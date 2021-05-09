package woolfel.examples.model.account;

import org.jamocha.rete.macro.ReadMacro;

public class ReadRegionCode implements ReadMacro {
    public ReadRegionCode() {}

    public Object getProperty(Object instance) {
        return ((woolfel.examples.model.Account)instance).getRegionCode();
    }
}
