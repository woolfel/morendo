package woolfel.examples.model;

import java.lang.reflect.Method;

import junit.framework.TestCase;

public class ReflectionTest extends TestCase {

	public ReflectionTest() {
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testLookup1() {
		String methd = "setCashCountry";
		Class[] params = new Class[2];
		params[0] = java.lang.Double.class;
		params[1] = java.lang.String.class;
		Class account4 = Account4.class;
		try {
			Method m = account4.getMethod(methd, params);
			assertNull(m);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(true);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(true);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testLookup2() {
		String methd = "setCashCountry";
		Class[] params = new Class[2];
		params[0] = double.class;
		params[1] = java.lang.String.class;
		Class account4 = Account4.class;
		try {
			Method m = account4.getMethod(methd, params);
			assertNotNull(m);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
}
