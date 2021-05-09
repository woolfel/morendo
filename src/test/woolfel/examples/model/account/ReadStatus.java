package woolfel.examples.model.account;

import org.jamocha.rete.macro.ReadMacro;

public class ReadStatus implements ReadMacro {
    public ReadStatus() {}

    public Object getProperty(Object instance) {
        return ((woolfel.examples.model.Account)instance).getStatus();
    }
}
