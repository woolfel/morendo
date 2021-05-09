package woolfel.examples.model.account;

import org.jamocha.rete.macro.ReadMacro;

public class ReadFirst implements ReadMacro {
    public ReadFirst() {}

    public Object getProperty(Object instance) {
        return ((woolfel.examples.model.Account)instance).getFirst();
    }
}
