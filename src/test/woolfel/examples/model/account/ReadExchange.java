package woolfel.examples.model.account;

import org.jamocha.rete.macro.ReadMacro;

public class ReadExchange implements ReadMacro {
    public ReadExchange() {}

    public Object getProperty(Object instance) {
        return ((woolfel.examples.model.Account)instance).getExchange();
    }
}
