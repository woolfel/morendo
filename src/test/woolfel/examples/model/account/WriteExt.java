package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteExt implements WriteMacro {
    public WriteExt() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setExt((java.lang.String)value);
    }
}
