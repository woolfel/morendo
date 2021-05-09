package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteAccountId implements WriteMacro {
    public WriteAccountId() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setAccountId((java.lang.String)value);
    }
}
