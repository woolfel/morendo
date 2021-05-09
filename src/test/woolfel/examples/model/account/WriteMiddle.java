package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteMiddle implements WriteMacro {
    public WriteMiddle() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setMiddle((java.lang.String)value);
    }
}
