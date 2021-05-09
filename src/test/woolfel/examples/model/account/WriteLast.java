package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteLast implements WriteMacro {
    public WriteLast() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setLast((java.lang.String)value);
    }
}
