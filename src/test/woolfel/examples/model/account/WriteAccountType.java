package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteAccountType implements WriteMacro {
    public WriteAccountType() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setAccountType((java.lang.String)value);
    }
}
