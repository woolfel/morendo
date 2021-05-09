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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * @author Christian Ebert
 * 
 * Creates a Java Object and returns it.
 */


public class NewFunction implements Function, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String NEW = "new";
	
	private ClassnameResolver classnameResolver;
	
	public NewFunction(ClassnameResolver classnameResolver){
		super();
		this.classnameResolver = classnameResolver;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Object o = null;
		String classname = null;
		Object [] args = null;
		if (params != null) {
			if (params[0] instanceof ValueParam) {
				ValueParam n = (ValueParam) params[0];
				classname = n.getStringValue();
			}
			else if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam) params[0];
				classname = (String) engine.getBinding(bp
						.getVariableName());
			} else if (params[0] instanceof FunctionParam2) {
				FunctionParam2 n = (FunctionParam2) params[0];
				n.setEngine(engine);
				n.lookUpFunction();
				ReturnVector rval = (ReturnVector) n.getValue();
				classname = rval.firstReturnValue().getStringValue();
			}
			args = new Object[params.length-1];
			for (int idx = 1; idx < params.length; idx++) {
				if (params[idx] instanceof ValueParam) {
					ValueParam n = (ValueParam) params[idx];
					args[idx-1] = n.getValue();
				} else if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					args[idx-1] = engine.getBinding(bp.getVariableName());
				} else if (params[idx] instanceof FunctionParam2) {
					FunctionParam2 n = (FunctionParam2) params[idx];
					n.setEngine(engine);
					n.lookUpFunction();
					ReturnVector rval = (ReturnVector) n.getValue();
					args[idx-1] = rval.firstReturnValue().getValue();
				}
			} 
			try {
				Class classDefinition = classnameResolver.resolveClass(classname);
				Constructor foundConstructor = null;
				for(Constructor constructor : classDefinition.getConstructors() ) {
					Class[] parameterClasses = constructor.getParameterTypes();
					if(parameterClasses.length == args.length) {
						boolean match = true;
						for(int i=0; i<parameterClasses.length; ++i) {
							match &= (parameterClasses[i].isInstance(args[i]) || args[i] == null); 
						}
						if(match) {
							foundConstructor = constructor;
							break;
						}
					}
				}
				if(foundConstructor != null) {
					o = foundConstructor.newInstance(args);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.OBJECT_TYPE,
				o);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return NEW;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public int getReturnType() {
		return Constants.OBJECT_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(new");
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" " + params[idx].getStringValue());
				} else {
					buf.append(" " + params[idx].getStringValue());
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(new (<literal> | <binding> | <value>)+)\n";
		}
	}

}
