package woolfel.examples.model.account;

import org.jamocha.rete.macro.ReadMacro;

public class ReadMiddle implements ReadMacro {
    public ReadMiddle() {}

    public Object getProperty(Object instance) {
        return ((woolfel.examples.model.Account)instance).getMiddle();
    }
}
