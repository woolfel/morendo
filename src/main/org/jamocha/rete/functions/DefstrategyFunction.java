/*
 * Copyright 2002-2007 Peter Lin
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
import java.lang.reflect.InvocationTargetException;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.Strategy;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.strategies.Strategies;

/**
 * Function is used to register a new strategy defined by the user. The user
 * must implement the Strategy interface
 * @author Peter Lin
 * 
 */
public class DefstrategyFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DEFSTRATEGY = "defstrategy";

	public DefstrategyFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean def = Boolean.TRUE;
		if (params.length == 1) {
			String clazz = params[0].getStringValue();
            Class clzz;
            try {
                clzz = Class.forName(clazz);
                Strategy strat = (Strategy)clzz.getDeclaredConstructor().newInstance();
                Strategies.register(strat);
                def = Boolean.TRUE;
            } catch (ClassNotFoundException e) {
                // for now we do nothing
            } catch (InstantiationException e) {
                // for now we do nothing
            } catch (IllegalAccessException e) {
                // for now we do nothing
            } catch (NoSuchMethodException e) {
            	// for now we do nothing
            } catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
		} else {
			def = Boolean.FALSE;
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, def);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return DEFSTRATEGY;
	}

	/**
	 * defclass function expects 3 parameters. (defclass classname,
	 * templatename, parenttemplate) parent template name is optional.
	 */
	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[] { ValueParam.class, ValueParam.class,
				ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(defstrategy");
			for (int idx = 0; idx < params.length; idx++) {
				buf.append(" " + params[idx].getStringValue());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(defstrategy [new classname])";
		}
	}
}
