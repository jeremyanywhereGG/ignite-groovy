
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.logger.log4j2.Log4J2Logger;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.JobContextResource;
import org.apache.ignite.spi.collision.jobstealing.JobStealingCollisionSpi;
import org.apache.ignite.spi.failover.jobstealing.JobStealingFailoverSpi;
import org.apache.ignite.spi.loadbalancing.weightedrandom.WeightedRandomLoadBalancingSpi;



public class PlainTaskTest implements IgniteClosure<String, Object> {
   @IgniteInstanceResource
   Ignite ignite;
   @JobContextResource
    private org.apache.ignite.compute.ComputeJobContext jobContext;
   public static int LOOP_SIZE = 1600000000;
   public Object apply(String parm) {
      System.out.println("Starting Ignite Task:");
      try {
            float f = 7.0f;
            for (int i=0; i< LOOP_SIZE; i++)
            {
               f = f * 2;
               if (f==0.0) {
                  System.out.println ("this will never happen");
               }
               if (i == (int)LOOP_SIZE/2) {
                  System.out.println("Java - Task [" +parm + "] halfway done." + (int)LOOP_SIZE/2);
               }
            }
            System.out.println("Task " +parm + " complete");
            
        } catch (Exception e) {
            // Handling any exceptions
            e.printStackTrace();
            return e.getMessage();
      }
      String[] parts = (""+ignite.cluster().localNode().consistentId()).split(",");
      String retVal = " " + parts[parts.length - 1] + " "+ parm;

      return "return val from:" + retVal;  
   }
   

   public static void main(String[] args) {
      Scanner scanner = new Scanner(System.in);
      try {
         WeightedRandomLoadBalancingSpi lbspi = new WeightedRandomLoadBalancingSpi();
         lbspi.setUseWeights(true);
         JobStealingCollisionSpi jscspi = new JobStealingCollisionSpi();

         jscspi.setWaitJobsThreshold(10);

         // Configure message expire time (in milliseconds).
         jscspi.setMessageExpireTime(1000);

         // Configure stealing attempts number.
         jscspi.setMaximumStealingAttempts(10);

         // Configure number of active jobs that are allowed to execute
         // in parallel. This number should usually be equal to the number
         // of threads in the pool (default is 100).
         jscspi.setActiveJobsThreshold(50);

         // Enable stealing.
         jscspi.setStealingEnabled(false);
         // Enable `JobStealingFailoverSpi`
         JobStealingFailoverSpi failoverSpi = new JobStealingFailoverSpi();

         IgniteConfiguration cfg = new IgniteConfiguration();
         cfg.setCollisionSpi(jscspi);
         cfg.setFailoverSpi(failoverSpi);
         cfg.setLoadBalancingSpi(lbspi);
         cfg.setClientMode(true);
         cfg.setPeerClassLoadingEnabled(true);
         IgniteLogger logger = new Log4J2Logger("config/ignite-log4j.xml");
         cfg.setGridLogger(logger);
         // TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
         // ipFinder.setAddresses(Collections.singletonList("192.168.0.117:49500..49520"));
         // cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));
         System.out.println("Starting Ignite Client..");
         Ignite ignite = Ignition.start(cfg);
         while(true) {
            PlainTaskTest.runIt(ignite);
            System.out.println("Press Enter to run job again..");
            scanner.nextLine();
         }
      } catch (Exception e) {
         System.out.println("Loop ended artificially"); 
         scanner.close();
         System.exit(0);
      }

   }
   private static void runIt(Ignite ignite) {
      long startTime = System.currentTimeMillis();
      Object res = "";
      ArrayList<String> solutions = new ArrayList<String>();
      try {
         IgniteCompute compute = ignite.compute();
         String[] moves = {"front_anticlock", "left_col_rot", "back_anticlock", "front_180", "top_row_rot",
         "middle_row_rot", "right_col_rot", "back_180", "front_clock", "middle_col_rot",
         "bottom_row_rot", "back_clock"};
         // create source and target for "fit_edge_bottom_to_side"
         int[] source = CubeUtilities.geFullWildcardPosition();
         int[] squares = {0, 1, 2, 21, 22, 23, 9, 10, 11, 30, 31, 32, 20, 41, 3, 4, 5, 24, 25, 26, 19, 40, 7};
         CubeUtilities.fillSpecificSquares(source,squares);
         int[] target = Arrays.copyOf(source, source.length);
         target[5] = source[7];
         target[7] = '?';
         int t = 0;
         Collection<IgniteFuture<Object>> groovyTasks = new ArrayList<>();            
         for (int i = 0; i < moves.length; i++) {
            for (int j = 0; j < moves.length; j++) {
               if (i != j) { // Avoid pairs with equal elements
                  String parm ="--> TASK ["+t+"]";
                  System.out.println("Firing off task no."+t++ +" with "+ parm);
                  groovyTasks.add(compute.applyAsync(new PlainTaskTest(), parm));
                  // System.out.println("Client side result back from task "+ t++ +" is: " + res); 
               } 
               //Thread.sleep(200);
            }
       
         }
         for (IgniteFuture<Object> future:groovyTasks) { 
               res = future.get();
               if (res!= null) {
               if (((String)res).length() > 0) {
                  solutions.add((String)res);
               }
            }
         }

         // iterate over future.. wait.. 
      } catch (Exception e) {
         e.printStackTrace(System.out);
      }
      long endTime = System.currentTimeMillis();

      // Calculate elapsed time in milliseconds
      long elapsedTimeMs = endTime - startTime;

      // Convert elapsed time to minutes and seconds
      long minutes = elapsedTimeMs / (60 * 1000);
      long seconds = (elapsedTimeMs % (60 * 1000)) / 1000;
      for (String sol : solutions) {
         System.out.println("Return:-> " + sol);
      }
      System.out.println("Elapsed Time: " + minutes + " minutes, " + seconds + " seconds");
      
   }

}

