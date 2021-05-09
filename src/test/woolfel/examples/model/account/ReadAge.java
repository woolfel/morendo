package woolfel.examples.model.account;

import org.jamocha.rete.macro.ReadMacro;

public class ReadAge implements ReadMacro {
    public ReadAge() {}

    public Object getProperty(Object instance) {
        return ((woolfel.examples.model.Account)instance).getAge();
    }
}
