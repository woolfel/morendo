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
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Defclass;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;


/**
 * Call method for invoking arbitrary java methods. The function uses defclass to find
 * the method, since that is the entry point for java classes.
 * 
 * @author Peter Lin
 */
public class CallMethodFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CALL_MEMBER = "call";

	/**
	 * 
	 */
	public CallMethodFunction() {
		super();
	}

	/**
	 * By default, the function returns Object type. Since the function
	 * can be used to call any number of getXXX methods and we wrap
	 * all primitives in their object equivalent, returning Object type
	 * makes the most sense.
	 */
	public int getReturnType() {
		return Constants.OBJECT_TYPE;
	}

	/**
	 * 
	 */
		public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Object rtn = null;
		DefaultReturnVector drv = new DefaultReturnVector();
		DefaultReturnValue rvalue = null;
		if (engine != null && params != null && params.length >= 2) {
			BoundParam bp = (BoundParam) params[0];
			ValueParam slot = (ValueParam) params[1];
			ArrayList<Object> callparam = new ArrayList<Object>();
			if (params.length > 2) {
				for (int idx=2; idx < params.length; idx++) {
					if (params[idx] instanceof BoundParam) {
						BoundParam bp2 = (BoundParam)params[idx];
						Object resolvedValue = null;
						if (bp2.isObjectBinding()) {
							resolvedValue = bp2.getValue(engine, Constants.OBJECT_TYPE);
						} else {
							resolvedValue = engine.getBinding(bp2.getVariableName());
						}
						if (resolvedValue.getClass().isArray()) {
							Object[] arr = (Object[])resolvedValue;
							callparam.add(arr);
						} else {
							callparam.add(resolvedValue);
						}
					} else if (params[idx] instanceof ValueParam) {
						callparam.add(params[idx].getValue());
					}
				}
			}
			Object instance = bp.getValue(engine, Constants.OBJECT_TYPE);
			Defclass dc = engine.findDefclass(instance);
			// we check to make sure the Defclass exists
			if (dc != null) {
				Method callm = dc.getCallMethod(slot.getStringValue(),callparam.toArray());
				if (callm != null) {
					try {
						rtn = callm.invoke(instance, callparam.toArray());
						int rtype = getMethodReturnType(callm);
						rvalue = new DefaultReturnValue(rtype,
								rtn);
					} catch (IllegalAccessException e) {
						rvalue = new DefaultReturnValue(Constants.STRING_TYPE,
								"IllegalAccessException: could not invoke the method due to access privledge");
					} catch (InvocationTargetException e) {
						rvalue = new DefaultReturnValue(Constants.STRING_TYPE,
								"InvocationTargetException: could not invoke the method");
					}
				}
			}
		}
		drv.addReturnValue(rvalue);
		return drv;
	}

	public String getName() {
		return CALL_MEMBER;
	}

	/**
	 * The current implementation expects 3 parameters in the following
	 * sequence:<br/>
	 * BoundParam - the bound object
	 * StringParam - the slot name
	 * ValueParam - the value to set the field
	 * <br/>
	 * Example: (set-member ?objectVariable slotName value)
	 */
	public Class<?>[] getParameter() {
		return new Class[] { BoundParam.class, ValueParam.class, ValueParam[].class };
	}

	/**
	 * For now, this utility method is here, but maybe I should move it
	 * to some place else later.
	 * @param m
	 * @return
	 */
	public int getMethodReturnType(Method m) {
		if (m.getReturnType() == String.class) {
			return Constants.STRING_TYPE;
		} else if (m.getReturnType() == int.class
				|| m.getReturnType() == Integer.class) {
			return Constants.INT_PRIM_TYPE;
		} else if (m.getReturnType() == short.class
				|| m.getReturnType() == Short.class) {
			return Constants.SHORT_PRIM_TYPE;
		} else if (m.getReturnType() == long.class
				|| m.getReturnType() == Long.class) {
			return Constants.LONG_PRIM_TYPE;
		} else if (m.getReturnType() == float.class
				|| m.getReturnType() == Float.class) {
			return Constants.FLOAT_PRIM_TYPE;
		} else if (m.getReturnType() == double.class
				|| m.getReturnType() == Double.class) {
			return Constants.DOUBLE_PRIM_TYPE;
		} else {
			return Constants.OBJECT_TYPE;
		}
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(call <binding> <methodName> <parameter>)";
		}
	}
}
