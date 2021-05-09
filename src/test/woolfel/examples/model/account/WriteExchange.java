package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteExchange implements WriteMacro {
    public WriteExchange() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setExchange((java.lang.String)value);
    }
}
