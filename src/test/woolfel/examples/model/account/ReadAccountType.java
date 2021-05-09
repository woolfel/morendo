package woolfel.examples.model.account;

import org.jamocha.rete.macro.ReadMacro;

public class ReadAccountType implements ReadMacro {
    public ReadAccountType() {}

    public Object getProperty(Object instance) {
        return ((woolfel.examples.model.Account)instance).getAccountType();
    }
}
