/*
 * Copyright 2002-2006 Peter Lin
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 *
 * Functional will load a List<Object> from binary format and assert each
 * object. I assumes the data file is in binary format and the root
 * object is List<Object>.
 */
public class BatchObjectsFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String BATCH = "batch-objects";

	/**
	 * 
	 */
	public BatchObjectsFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	/**
	 * method will attempt to load one or more files. If batch is called without
	 * any parameters, the function does nothing and just returns.
	 */
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector rv = new DefaultReturnVector();
		if (params != null && params.length > 0) {
			for (int idx = 0; idx < params.length; idx++) {
				try {
                    String input = params[idx].getStringValue();
                    InputStream inStream;
                    // Check for a protocol indicator at the beginning of the
                    // String. If we have one use a URL.
                    if (input.matches("^[a-zA-Z]+://.*")) {
                        URL url = new URL(input);
                        inStream = url.openConnection().getInputStream();
                        // Otherwise treat it as normal file on the Filesystem
                    } else {
                        inStream = new FileInputStream(new File(input));
                    }
                    this.parse(engine, inStream, rv);
                    inStream.close();
                } catch (FileNotFoundException e) {
					// we should report the error
					rv.addReturnValue(new DefaultReturnValue(
							Constants.BOOLEAN_OBJECT, Boolean.FALSE));
                    engine.writeMessage(e.getMessage() + Constants.LINEBREAK,Constants.DEFAULT_OUTPUT);
                } catch (IOException e) {
                    rv.addReturnValue(new DefaultReturnValue(
                            Constants.BOOLEAN_OBJECT, Boolean.FALSE));
                    engine.writeMessage(e.getMessage() + Constants.LINEBREAK,Constants.DEFAULT_OUTPUT);
				}
			}
		}
		return rv;
	}

	/**
	 * method does the actual work of creating a CLIPSParser and parsing
	 * the file.
	 * @param engine
	 * @param ins
	 * @param rv
	 */
	@SuppressWarnings("unchecked")
	public void parse(Rete engine, InputStream ins, DefaultReturnVector rv) {
		try {
			ObjectInputStream ois = new ObjectInputStream(ins);
			List<Object> data = (List<Object>)ois.readObject();
			for (Object obj: data) {
				Deftemplate templ = (Deftemplate)engine.findDeftemplate(obj.getClass());
				engine.assertObject(obj, templ.getName(), false, true);
			}
			if (rv != null) {
				rv.addReturnValue(new DefaultReturnValue(
						Constants.BOOLEAN_OBJECT, Boolean.FALSE));
			}
		} catch (Exception e) {
            engine.writeMessage(e.getMessage() + Constants.LINEBREAK,Constants.DEFAULT_OUTPUT);
		}
	}

	public String getName() {
		return BATCH;
	}

	public Class<?>[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(batch-objects");
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					buf.append(" \"" + params[idx].getStringValue() + "\"");
				}
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(batch-objects <filename>)\n" +
					"Command description:\n" +
					"\tLoads and executes the file <filename>.";
		}
	}
}
