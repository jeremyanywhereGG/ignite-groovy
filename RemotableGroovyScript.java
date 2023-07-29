import java.util.Map;
import groovy.lang.Binding;

public class RemotableGroovyScript {
   private String script;
   private Binding bindings;
   public RemotableGroovyScript(String script, Map<String,Object> bindings) {
      this.script = script;
      this.bindings = new Binding(bindings);
   }
   public String getScript() {
      return this.script;
   }
   public Binding getBindings() {
      return this.bindings;
   }
   public void setVariable(String variable, Object val) {
      bindings.setVariable(variable, val);
   }
}
