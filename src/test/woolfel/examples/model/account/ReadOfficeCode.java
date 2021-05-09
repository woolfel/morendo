package woolfel.examples.model.account;

import org.jamocha.rete.macro.ReadMacro;

public class ReadOfficeCode implements ReadMacro {
    public ReadOfficeCode() {}

    public Object getProperty(Object instance) {
        return ((woolfel.examples.model.Account)instance).getOfficeCode();
    }
}
