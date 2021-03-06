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
package org.jamocha.rete.functions.io;

import java.io.Serializable;
import java.util.ArrayList;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 *
 * PrintFucntion is pretty simple. It can any number of parameters and
 * print it.
 */
public class PrintFunction implements Function, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PRINTOUT = "printout";
    
	/**
	 * 
	 */
	public PrintFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	/**
     * The implementation will call Rete.writeMessage(). This means that
     * if multiple output streams are set, the message will be printed to
     * all of them.
	 */
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
        // print out some stuff
		DefaultReturnVector rv = new DefaultReturnVector();
        if (params.length > 0) {
            String output = params[0].getStringValue();
            for (int idx=1; idx < params.length; idx++) {
            	if (params[idx] instanceof BoundParam) {
            		BoundParam bp = (BoundParam)params[idx];
            		Object v = engine.getBinding(bp.getVariableName());
            		if (v != null) {
            			if (v.getClass().isArray()) {
            				Object[] ary = (Object[])v;
            				writeArray(ary,engine,output,false);
            			} else if (v instanceof ArrayList) {
            				writeList((ArrayList<?>)v, engine, output, false);
            			} else {
            				engine.writeMessage(v.toString(),output);
            			}
            		} else {
            			rv.addReturnValue(new DefaultReturnValue(Constants.STRING_TYPE,
            					"Error: Variable " + bp.getVariableName() + " is not bound"));
            			rv.addReturnValue(new DefaultReturnValue(Constants.BOOLEAN_OBJECT,
            					Boolean.FALSE));
            		}
            	} else if (params[idx].getValue() != null &&
                		params[idx].getValue().equals(Constants.CRLF)) {
                    engine.writeMessage(Constants.LINEBREAK,output);
                } else {
                	Object val = params[idx].getValue();
                	if (val instanceof String) {
                        engine.writeMessage((String)val,output);
                	} else if (val.getClass().isArray()) {
                		Object[] ary = (Object[])val;
                		writeArray(ary,engine,output,true);
                	} else {
                		engine.writeMessage(val.toString(),output);
                	}
                }
            }
        }
		return rv;
	}

	public void writeArray(Object[] arry, Rete engine, String output, boolean linebreak) {
		for (int idz=0; idz < arry.length; idz++) {
			Object val = arry[idz];
			if (val instanceof Fact) {
				Fact f = (Fact)val;
				engine.writeMessage(f.toFactString() + " ",output);
			} else {
				engine.writeMessage(arry[idz].toString() + " ",output);
			}
			if (linebreak) {
				engine.writeMessage(Constants.LINEBREAK, output);
			}
		}
	}
	
	public void writeList(ArrayList<?> array, Rete engine, String output, boolean linebreak) {
		for (int i=0; i < array.size(); i++) {
			Object val = array.get(i);
			if (val instanceof Fact) {
				Fact f = (Fact)val;
				engine.writeMessage(f.toFactString() + " ",output);
			} else if (val.getClass().isArray()) {
				writeArray((Object[])val, engine, output, linebreak);
			} else {
				engine.writeMessage(array.get(i).toString() + " ",output);
			}
			if (linebreak) {
				engine.writeMessage(Constants.LINEBREAK, output);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#getName()
	 */
	public String getName() {
		return PRINTOUT;
	}

	/**
     * The implementation returns an array of size 1 with Parameter.class
     * as the only entry. Any function that can take an unlimited number
     * of Parameters should return new Class[] {Parameter.class}.
     * If a function doesn't take any parameters, the method should return
     * null instead.
	 */
	public Class<?>[] getParameter() {
		return new Class[] {ValueParam[].class};
	}

	/**
	 * Note: need to handle crlf correctly, for now leave it as is.
	 */
	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(print ");
			buf.append(params[0].getStringValue());
			for (int idx=1; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam)params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else {
					buf.append(" \"" + params[idx].getStringValue() + "\"");
				}
			}
			buf.append(" )");
			return buf.toString();
		} else {
			return "(print)";
		}
	}
}
