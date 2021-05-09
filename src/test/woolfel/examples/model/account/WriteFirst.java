package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteFirst implements WriteMacro {
    public WriteFirst() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setFirst((java.lang.String)value);
    }
}
