package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteAreaCode implements WriteMacro {
    public WriteAreaCode() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setAreaCode((java.lang.String)value);
    }
}
