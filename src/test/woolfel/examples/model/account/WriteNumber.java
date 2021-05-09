package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteNumber implements WriteMacro {
    public WriteNumber() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setNumber((java.lang.String)value);
    }
}
