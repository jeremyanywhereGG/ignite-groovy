import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.logger.log4j2.Log4J2Logger;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.spi.collision.jobstealing.JobStealingCollisionSpi;
import org.apache.ignite.spi.failover.jobstealing.JobStealingFailoverSpi;
import org.apache.ignite.spi.loadbalancing.weightedrandom.WeightedRandomLoadBalancingSpi;

class GroovyRunner implements IgniteClosure<RemotableGroovyScript, Object> {
   @IgniteInstanceResource
   Ignite ignite;
   public Object apply(RemotableGroovyScript remoteableScript) {
      System.out.println("Starting Groovy Script in Ignite Task:");
      Object res;
      try {
            GroovyShell shell = new GroovyShell(remoteableScript.getBindings());
            String scriptText = remoteableScript.getScript();
            Script script = shell.parse(scriptText);
            res = script.run();
            System.out.println("Script Done." + res);
        } catch (Exception e) {
            // Handling any exceptions
            e.printStackTrace();
            return e.getMessage();
      }
      return res;
   }
   

   public static void main (String[] args) {
      Object res = "";
      long startTime = System.currentTimeMillis();
      ArrayList<ArrayList<ArrayList<String>>> solutions = new ArrayList<ArrayList<ArrayList<String>>>();
      try {
         GroovyRunner runner = new GroovyRunner();
         
         File f = new File("./CubeSolver.groovy");
         String scriptText = Files.readString(f.toPath(), StandardCharsets.UTF_8);
         RemotableGroovyScript rgs;// = new RemotableGroovyScript(scriptText, bindings);
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
         jscspi.setStealingEnabled(true);
         // Enable `JobStealingFailoverSpi`
         JobStealingFailoverSpi failoverSpi = new JobStealingFailoverSpi();

         IgniteConfiguration cfg = new IgniteConfiguration();
         cfg.setCollisionSpi(jscspi);
         cfg.setFailoverSpi(failoverSpi);
         cfg.setClientMode(true);
         cfg.setPeerClassLoadingEnabled(true);
         IgniteLogger logger = new Log4J2Logger("config/ignite-log4j.xml");
         cfg.setGridLogger(logger);
         // TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
         // ipFinder.setAddresses(Collections.singletonList("192.168.0.117:49500..49520"));
         // cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));
         System.out.println("Starting Ignite Client..");
         Ignite ignite = Ignition.start(cfg);
         IgniteCompute compute = ignite.compute();

         // create source and target for "fit_edge_bottom_to_side"
         int[] source = CubeUtilities.moveFitEdgeBottomToSide().get(0);
         int[] target = CubeUtilities.moveFitEdgeBottomToSide().get(1);

         int t = 0;
         Collection<IgniteFuture<Object>> groovyTasks = new ArrayList<>();      
         //start with all permutations of two moves (filtering out equal ones)      
         for (int i = 0; i < CubeUtilities.moves.length; i++) {
            for (int j = 0; j < CubeUtilities.moves.length; j++) {
               if (i != j) { // Avoid pairs with equal elements
                  String[] pair = {CubeUtilities.moves[i], CubeUtilities.moves[j]};
                  Map<String, Object> bindings = new HashMap<String, Object>();
                  bindings.put("pPair", pair);
                  bindings.put("pSource", source);
                  bindings.put("pTarget", target);
                  bindings.put("pTaskNo", t);
                  rgs = new RemotableGroovyScript(scriptText, bindings);
                  System.out.println("Firing off task no."+t++ +" with "+ pair[0] +" "+pair[1]);
                  groovyTasks.add(compute.applyAsync(runner, rgs));
                  // System.out.println("Client side result back from task "+ t++ +" is: " + res); 
               } 
               //Thread.sleep(200);
            }
       
         }
         for (IgniteFuture<Object> future:groovyTasks) { 
               res = future.get();
               if (res!= null) {
               if (((ArrayList<ArrayList<String>>)res).size() > 0) {
                  solutions.add((ArrayList<ArrayList<String>>)res);
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
      for (ArrayList<ArrayList<String>> moveListList : solutions) {
         System.out.println("results:");
         for (ArrayList<String> moveList : moveListList) {
               System.out.println("'" +moveList+"' ");
         }
         System.out.println("");
      }
      System.out.println("Elapsed Time: " + minutes + " minutes, " + seconds + " seconds");
      System.exit(0);
   }

}