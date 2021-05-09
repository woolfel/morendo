package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteTitle implements WriteMacro {
    public WriteTitle() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setTitle((java.lang.String)value);
    }
}
