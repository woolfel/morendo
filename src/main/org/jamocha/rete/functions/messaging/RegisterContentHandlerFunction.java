package org.jamocha.rete.functions.messaging;

import org.jamocha.messaging.ContentHandler;
import org.jamocha.messaging.ContentHandlerRegistry;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class RegisterContentHandlerFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String REGISTER_CONTENT_HANDLER = "register-content-handler";

	public RegisterContentHandlerFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		boolean register = false;
		if (params != null && params.length > 0) {
			for (int i=0; i < params.length; i++) {
				String name = params[i].getStringValue();
				try {
					Class clzz = Class.forName(name);
					ContentHandler handler = (ContentHandler)clzz.newInstance();
					String[] types = handler.getMessageTypes();
					for (int x=0; x < types.length; x++) {
						ContentHandlerRegistry.registerHandler(types[x], handler);
					}
					register = true;
				} catch (ClassNotFoundException e) {
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(register));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return REGISTER_CONTENT_HANDLER;
	}

	public Class[] getParameter() {
		return new Class[]{String.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(register-content-handler <classname>)";
	}

}
