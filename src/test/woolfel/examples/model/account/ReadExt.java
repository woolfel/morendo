package woolfel.examples.model.account;

import org.jamocha.rete.macro.ReadMacro;

public class ReadExt implements ReadMacro {
    public ReadExt() {}

    public Object getProperty(Object instance) {
        return ((woolfel.examples.model.Account)instance).getExt();
    }
}
