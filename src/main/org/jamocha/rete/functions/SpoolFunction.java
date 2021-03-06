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
package org.jamocha.rete.functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * @author Peter Lin
 * 
 * The purpose of spool function is to capture the output to a file,
 * and make it easier to record what happens. This is inspired by
 * Oracle SqlPlus spool function.
 */
public class SpoolFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String SPOOL = "spool";
	public static final String DRIBBLE = "dribble";
	
	/**
	 * 
	 */
	public SpoolFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean sp = Boolean.TRUE;
		if (params != null && params.length >= 2) {
			String val = params[0].getStringValue();
			if (val.equals("off")) {
				// turn off spooling
				String name = params[1].getStringValue();
				PrintWriter writer = engine.removePrintWriter(name);
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			} else {
				// turn on spooling
				// we expected a file name
				String spname = params[0].getStringValue();
				String fname = params[1].getStringValue();
				try {
					File nfile = new File(fname);
					nfile.createNewFile();
					FileOutputStream fos = new FileOutputStream(nfile);
					PrintWriter writer = new PrintWriter(fos);
					engine.addPrintWriter(spname,writer);
				} catch (FileNotFoundException e) {
					// we should report it
					sp = Boolean.FALSE;
				} catch (IOException e) {
					sp = Boolean.FALSE;
				}
			}
		} else {
			sp = Boolean.FALSE;
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = 
			new DefaultReturnValue(Constants.BOOLEAN_OBJECT, sp);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return SPOOL;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(spool <name> <file>| off <name>)";
	}

}
