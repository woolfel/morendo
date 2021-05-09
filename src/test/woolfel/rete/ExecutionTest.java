package woolfel.rete;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.functions.io.BatchFunction;

import junit.framework.TestCase;

public class ExecutionTest extends TestCase {

	public ExecutionTest() {
	}

	public ExecutionTest(String name) {
		super(name);
	}

	public void testExecution() {
		org.jamocha.rete.Rete engine = new org.jamocha.rete.Rete();
		engine.loadRuleset("./test.clp");
		Function batch = engine.findFunction(BatchFunction.BATCH);
		Parameter[] parameters = new Parameter[]{new ValueParam(Constants.STRING_TYPE,"./data.clp")};
		batch.executeFunction(engine, parameters);
		
		engine.fire();
	}
}
