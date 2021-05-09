package woolfel.examples.model.account;

import org.jamocha.rete.macro.ReadMacro;

public class ReadAccountId implements ReadMacro {
    public ReadAccountId() {}

    public Object getProperty(Object instance) {
        return ((woolfel.examples.model.Account)instance).getAccountId();
    }
}
