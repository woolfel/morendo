/*
 * Copyright 2002-2010 Jamocha
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
package org.jamocha.rete.functions.messaging;

import java.util.Iterator;

import org.jamocha.messaging.ContentHandler;
import org.jamocha.messaging.ContentHandlerRegistry;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class PrintContentHandlersFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PRINT_CONTENT_HANDLERS = "print-content-handlers";

	public PrintContentHandlersFunction() {
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Iterator iterator = ContentHandlerRegistry.keyIterator();
		while (iterator.hasNext()) {
			String key = (String)iterator.next();
			ContentHandler handler = ContentHandlerRegistry.findHandler(key);
			engine.writeMessage(key + ":" + handler.getClass().getName() + Constants.LINEBREAK, "t");
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return PRINT_CONTENT_HANDLERS;
	}

	public Class[] getParameter() {
		return new Class[0];
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(print-content-handlers)";
	}

}
