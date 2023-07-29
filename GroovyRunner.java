import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
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
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

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
      try {
         GroovyRunner runner = new GroovyRunner();
         Map<String, Object> bindings = new HashMap<String, Object>();
         bindings.put("name", "Jeremy");
         File f = new File("./runme.groovy");
         String scriptText = Files.readString(f.toPath(), StandardCharsets.UTF_8);
         RemotableGroovyScript rgs = new RemotableGroovyScript(scriptText, bindings);
         IgniteConfiguration cfg = new IgniteConfiguration();
         cfg.setClientMode(true);
         cfg.setPeerClassLoadingEnabled(true);
         IgniteLogger logger = new Log4J2Logger("config/ignite-log4j.xml");
         cfg.setGridLogger(logger);
         TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
         ipFinder.setAddresses(Collections.singletonList("192.168.0.117:49500..49520"));
         cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));
         System.out.println("Starting Ignite Client..");
         Ignite ignite = Ignition.start(cfg);
         IgniteCompute compute = ignite.compute();
         String[] words = {"Petrichor","Serendipity","Quixotic","Mellifluous","Ephemeral",
                           "Limerence", "Lugubrious", "Apricity", "Taciturn", "Egregious", "Callipygian"};
         for (String word : words) {
            bindings.put("name", word);
            rgs = new RemotableGroovyScript(scriptText, bindings);
            res = compute.apply(runner, rgs);
            System.out.println("Client side result back from task is: " + res);
         }
      } catch (Exception e) {
         res = e;
      }
      System.out.println("End result is: "+res);
   }

}