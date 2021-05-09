package woolfel.examples.model.account;

import org.jamocha.rete.macro.WriteMacro;

public class WriteOfficeCode implements WriteMacro {
    public WriteOfficeCode() {}

    public void setProperty(Object instance, Object value) {
        ((woolfel.examples.model.Account)instance).setOfficeCode((java.lang.String)value);
    }
}
