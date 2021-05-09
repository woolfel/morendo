package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteRegionCode implements WriteMacro {
    public WriteRegionCode() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setRegionCode((java.lang.String)value);
    }
}
