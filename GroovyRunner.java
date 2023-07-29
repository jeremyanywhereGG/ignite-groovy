import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.logger.log4j2.Log4J2Logger;
import org.apache.ignite.resources.IgniteInstanceResource;

class GroovyRunner implements IgniteClosure<RemotableGroovyScript, Object> {
   @IgniteInstanceResource
   Ignite ignite;
   public Object apply(RemotableGroovyScript remoteableScript) {
      System.out.println("Remoting Script -: '" + remoteableScript.getScript().substring(0,10)+"...'");
      Object res;
      try {
            GroovyShell shell = new GroovyShell(remoteableScript.getBindings());
            String scriptText = remoteableScript.getScript();
            Script script = shell.parse(scriptText);
            res = script.run();
            System.out.println("Execution result: " + res);
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
      try {
         GroovyRunner runner = new GroovyRunner();
         Map<String, Object> bindings = new HashMap<String, Object>();
         File f = new File("./CubeSolver.groovy");
         String scriptText = Files.readString(f.toPath(), StandardCharsets.UTF_8);
         RemotableGroovyScript rgs = new RemotableGroovyScript(scriptText, bindings);
         IgniteConfiguration cfg = new IgniteConfiguration();
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
         for (int i = 0; i < moves.length; i++) {
            for (int j = 0; j < moves.length; j++) {
                  if (i != j) { // Avoid pairs with equal elements
                     String[] pair = {moves[i], moves[j]};
                     bindings.put("pPair", pair);
                     bindings.put("pSource", source);
                     bindings.put("pTarget", target);
                     rgs = new RemotableGroovyScript(scriptText, bindings);
                     res = compute.apply(runner, rgs);
                     System.out.println("Client side result back from task "+i++ +" is: " + res);
                  } 
            }
         }
      } catch (Exception e) {
         res = e;
      }
      long endTime = System.currentTimeMillis();

      // Calculate elapsed time in milliseconds
      long elapsedTimeMs = endTime - startTime;

      // Convert elapsed time to minutes and seconds
      long minutes = elapsedTimeMs / (60 * 1000);
      long seconds = (elapsedTimeMs % (60 * 1000)) / 1000;

      System.out.println("Elapsed Time: " + minutes + " minutes, " + seconds + " seconds");
      System.out.println("End result is: "+res);
   }

}