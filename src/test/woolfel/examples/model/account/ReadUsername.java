package woolfel.examples.model.account;

import org.jamocha.rete.macro.ReadMacro;

public class ReadUsername implements ReadMacro {
    public ReadUsername() {}

    public Object getProperty(Object instance) {
        return ((woolfel.examples.model.Account)instance).getUsername();
    }
}
