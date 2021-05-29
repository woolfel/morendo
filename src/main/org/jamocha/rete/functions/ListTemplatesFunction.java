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
package org.jamocha.rete.functions;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.Template;


/**
 * @author Peter Lin
 * 
 * ListTemplates will list all the templates and print them out.
 */
public class ListTemplatesFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String LISTTEMPLATES = "list-deftemplates";
	public static final String TEMPLATES = "templates";

	/**
	 * 
	 */
	public ListTemplatesFunction() {
		super();
	}

	public String getName() {
		return LISTTEMPLATES;
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	/**
	 * The current implementation will print out all the templates in
	 * no specific order. The function does basically the same thing
	 * as CLIPS (list-deftemplates)
	 */
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Collection<?> templates = engine.getCurrentFocus().getTemplates();
		int count = templates.size();
		Iterator<?> itr = templates.iterator();
		while (itr.hasNext()) {
			Template r = (Template)itr.next();
			engine.writeMessage(r.getName() + Constants.LINEBREAK, "t");
		}
		engine.writeMessage("for a total of " + count + Constants.LINEBREAK,"t");
		DefaultReturnVector rv = new DefaultReturnVector();
		return rv;
	}

	public Class<?>[] getParameter() {
		return new Class[] { String.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(list-deftemplates)";
		}
	}
}
