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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.parser.clips.ParseException;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.exception.AssertException;


/**
 * @author Peter Lin
 * 
 * LoadStream will loads facts 1 at a time as it reads the file.
 * In contrast, Batch function will read the entire file before 
 * it starts asserting the facts.
 */
public class LoadStreamFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String LOAD_STREAM = "load-stream";

	/**
	 * 
	 */
	public LoadStreamFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector rv = new DefaultReturnVector();
		boolean loaded = true;
		if (params != null && params.length > 0) {
			for (int idx = 0; idx < params.length; idx++) {
				String input = null;
				if (params[idx] instanceof ValueParam) {
					input = ((ValueParam) params[idx]).getStringValue();
				} else if (params[idx] instanceof BoundParam) {

				}
				if (input.indexOf('\\') > -1) {
					input.replaceAll("\\", "/");
				}
				// check to see if the path is an absolute windows path
				// or absolute unix path
				if (input.indexOf(":") < 0 && !input.startsWith("/")
						&& !input.startsWith("./")) {
					input = "./" + input;
				}
				try {
                    InputStream inStream = getInputStream(input);
					CLIPSParser parser = new CLIPSParser(inStream);
					Object data = null;
					while ((data = parser.streamExpr()) != null)  {
						ValueParam[] vp = (ValueParam[])data;
						Deftemplate tmpl = (Deftemplate) engine.getCurrentFocus().getTemplate(vp[0].getStringValue());
						Deffact fact = (Deffact) tmpl.createFact((Object[]) vp[1].getValue(), -1);
						engine.assertFact(fact);
					}
				} catch (FileNotFoundException e) {
					loaded = false;
					engine.writeMessage(e.getMessage() + Constants.LINEBREAK,Constants.DEFAULT_OUTPUT);
				} catch (ParseException e) {
					loaded = false;
					engine.writeMessage(e.getMessage() + Constants.LINEBREAK,Constants.DEFAULT_OUTPUT);
				} catch (AssertException e) {
					loaded = false;
					engine.writeMessage(e.getMessage() + Constants.LINEBREAK,Constants.DEFAULT_OUTPUT);
				} catch (IOException e) {
                    loaded = false;
                    engine.writeMessage(e.getMessage() + Constants.LINEBREAK,Constants.DEFAULT_OUTPUT);
                }
			}
		} else {
			loaded = false;
		}
		DefaultReturnValue drv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(loaded));
		rv.addReturnValue(drv);
		return rv;
	}

	public String getName() {
		return LOAD_STREAM;
	}

	public Class[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public static InputStream getInputStream(String input) throws
	FileNotFoundException, IOException {
        InputStream inStream = null;
        if (input.matches("^[a-zA-Z]+://.*")) {
            URL url = new URL(input);
            inStream = url.openConnection().getInputStream();
            // Otherwise treat it as normal file on the Filesystem
        } else {
            inStream = new FileInputStream(new File(input));
        }
        return inStream;
	}
	
	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(load-stream");
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
			return "(load-stream <filename>)\n" +
			"Command description:\n" +
			"\tLoad the file <filename>.";
		}
	}
}
