package woolfel.rete;

import java.util.ArrayList;

import org.jamocha.rete.Rete;

import woolfel.examples.model.Account4;

import junit.framework.TestCase;

public class JavaObjectTest extends TestCase {

	public JavaObjectTest() {
		super();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testDeclareObject() {
		Rete engine = new Rete();
		engine.declareObject(Account4.class);
		Account4 acc = new Account4();
		ArrayList objs = new ArrayList();
		objs.add(acc);
		try {
			engine.assertObjects(objs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testAssertObject() {
		Rete engine = new Rete();
		engine.declareObject(Account4.class);
		engine.addPrintWriter("sysout", new java.io.PrintWriter(System.out));
		engine.loadRuleset("./samples/java_example4.clp");
		int rules = engine.getCurrentFocus().getRuleCount();
		assertTrue(1 == rules);
		Account4 acc = new Account4();
		ArrayList objs = new ArrayList();
		objs.add(acc);
		try {
			engine.assertObjects(objs);
			int fired = engine.fire();
			System.out.println("rules fired=" + fired);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
