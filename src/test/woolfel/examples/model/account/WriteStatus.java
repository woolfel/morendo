package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteStatus implements WriteMacro {
    public WriteStatus() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setStatus((java.lang.String)value);
    }
}
