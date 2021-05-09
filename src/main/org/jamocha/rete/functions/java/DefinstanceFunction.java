/*
 * Copyright 2002-2010 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.functions.java;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Defclass;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.exception.AssertException;


/**
 * @author Peter Lin
 *
 * Definstance will assert an object instance using Rete.assert(Object).
 */
public class DefinstanceFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DEFINSTANCE = "definstance";
	private Logger log = LogFactory.createLogger(DefinstanceFunction.class);

	public DefinstanceFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.STRING_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		String asrt = "";
		Object instance = null;
		if (params.length >= 1 && params[0] != null) {
			if (params[0] instanceof FunctionParam2) {
				FunctionParam2 func = (FunctionParam2)params[0];
				String classname = func.getFunctionName();
				try {
					Defclass defclass = engine.findDefclassByName(classname);
					if (defclass != null) {
						instance = defclass.getClassObject().newInstance();
						Parameter[] parameters = (Parameter[])func.getParameters();
						for (int idx=0; idx < parameters.length; idx++) {
							if (parameters[idx] instanceof FunctionParam2) {
								FunctionParam2 fp = (FunctionParam2)parameters[idx];
								String slotname = fp.getFunctionName();
								ValueParam paramVal = (ValueParam)fp.getParameters()[0];
								Class returnType = defclass.getReadMethod(slotname).getReturnType();
								Object value = getTypedValue(paramVal, returnType);
								defclass.getWriteMethod(slotname).invoke(instance, new Object[]{value});
							}
						}
					}
				} catch (InstantiationException e) {
					log.debug(e);
				} catch (IllegalAccessException e) {
					log.debug(e);
				} catch (IllegalArgumentException e) {
					log.debug(e);
				} catch (InvocationTargetException e) {
					log.debug(e);
				}
			} else if (params[0] instanceof BoundParam) {
				instance = ((BoundParam)params[0]).getValue(engine, Constants.OBJECT_TYPE);
			} else if (params[0] instanceof ValueParam) {
				String classname = params[0].getStringValue();
				Defclass defclass = engine.findDefclassByName(classname);
				if (defclass != null) {
					try {
						instance = defclass.getClassObject().newInstance();
					} catch (InstantiationException e) {
						log.debug(e);
					} catch (IllegalAccessException e) {
						log.debug(e);
					}
				}
			}
			String template = null;
			if (params.length == 2 && params[1].getStringValue() != null) {
				template = params[1].getStringValue();
			}
			try {
				engine.assertObject(instance, template, true, false);
				asrt = "true";
			} catch (AssertException e) {
				// we should log this and output an error
				asrt = "false";
			}
		} else {
			asrt = "false";
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.STRING_TYPE,
				asrt);
		ret.addReturnValue(rv);
		return ret;
	}
	
	protected Object getTypedValue(ValueParam param, Class clzz) {
		if (clzz == String.class) {
			return param.getStringValue();
		} else if (clzz == int.class || clzz == Integer.class) {
			return new Integer(param.getIntValue());
		} else if (clzz == short.class || clzz == Short.class) {
			return new Short(param.getShortValue());
		} else if (clzz == float.class || clzz == Float.class) {
			return new Float(param.getFloatValue());
		} else if (clzz == long.class || clzz == Long.class) {
			return new Long(param.getLongValue());
		} else if (clzz == double.class || clzz == Double.class) {
			return new Double (param.getDoubleValue());
		} else if (clzz == BigDecimal.class) {
			return param.getBigDecimalValue();
		} else if (clzz == BigInteger.class) {
			return param.getBigIntegerValue();
		} else {
			return param.getValue();
		}
	}

	public String getName() {
		return DEFINSTANCE;
	}

	/**
	 * The function expects a single BoundParam that is an object binding
	 */
	public Class[] getParameter() {
		return new Class[] { BoundParam.class, ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(definstance <object instance> <template name>)";
		}
	}
}
