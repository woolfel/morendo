package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteUsername implements WriteMacro {
    public WriteUsername() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setUsername((java.lang.String)value);
    }
}
