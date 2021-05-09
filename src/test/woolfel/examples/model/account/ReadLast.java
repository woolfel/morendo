package woolfel.examples.model.account;

import org.jamocha.rete.macro.ReadMacro;

public class ReadLast implements ReadMacro {
    public ReadLast() {}

    public Object getProperty(Object instance) {
        return ((woolfel.examples.model.Account)instance).getLast();
    }
}
