import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
/**
 * Test runner for the Groovy Script.. runs locally with, but the real thing, obviously, will be compute tasks. 
 */
public class TestGroovyRunner {
   public static void main (String[] args) throws IOException {
      GroovyRunner gr = new GroovyRunner();
      Map<String, Object> bindings = new HashMap<String, Object>();
      RemotableGroovyScript rgs; 
      String[] moves = {"front_anticlock", "left_col_rot", "back_anticlock", "front_180", "top_row_rot",
      "middle_row_rot", "right_col_rot", "back_180", "front_clock", "middle_col_rot",
      "bottom_row_rot", "back_clock"};
       List<String[]> firstMovePairs = new ArrayList<>();

      for (int i = 0; i < moves.length; i++) {
         for (int j = 0; j < moves.length; j++) {
               if (i != j) { // Avoid pairs with equal elements
                  String[] pair = {moves[i], moves[j]};
                  firstMovePairs.add(pair);
               } 
         }
      }
      // step through these and fire off a thread for each one. 
      int j = 0;
      String  scriptText="";
      try {
         File f = new File("./runme.groovy");
         scriptText = Files.readString(f.toPath(), StandardCharsets.UTF_8);

         } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
         }

      for (String[] item : firstMovePairs) {
         System.out.println("running for "+item);
         bindings.put("firstMovePair", item);
         GroovyShell shell = new GroovyShell(new Binding(bindings));
         Script script = shell.parse(scriptText);
         Object res = script.run();
         System.out.println("Execution result: " + res);
      }

      //String[] stringArray = {"Jeremy", "Jezmeister", "The Enjezerator"};

      // // Access and use the elements of the string array
      // for (String str : stringArray) {
      //    bindings.put("name", str);
      //    bindings.put("ignite", stringArray);
      //    rgs = new RemotableGroovyScript(scriptText, bindings);
      //    gr.apply(rgs);
      // }
   }
}


class TaskRunnable implements Runnable {
   String[] params;
   public TaskRunnable(String[] params) {
      this.params = params;
   }
   public void run() {
         //GroovyRunner runner = new GroovyRunner();

         
   }
}

