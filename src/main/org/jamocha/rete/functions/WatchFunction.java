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

import java.io.Serializable;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * @author Peter Lin
 * 
 * WatchFunction allows users to watch different engine processes, like
 * activations, facts and rules.
 */
public class WatchFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String WATCH = "watch";
	
	/**
	 * 
	 */
	public WatchFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
        if (params != null) {
            // the params are not null, now check the parameter count
            if (params.length > 0) {
                for (int idx=0; idx < params.length; idx++) {
                    String cmd = params[idx].getStringValue();
                    setWatch(engine,cmd);
                }
            } else {
                // we do nothing, maybe we should return a message
            }
        }
        DefaultReturnVector ret = new DefaultReturnVector();
        return ret;
	}
    
    protected void setWatch(Rete engine, String cmd) {
        if (cmd.equals("all")) {
            engine.setWatch(Rete.WATCH_ALL);
        } else if (cmd.equals("facts")) {
            engine.setWatch(Rete.WATCH_FACTS);
        } else if (cmd.equals("activations")) {
            engine.setWatch(Rete.WATCH_ACTIVATIONS);
        } else if (cmd.equals("rules")) {
            engine.setWatch(Rete.WATCH_RULES);
        }
    }

	public String getName() {
		return WATCH;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(watch activations|all|facts|rules)\n" +
			"Function description:\n" +
			"\tAllows users to watch engine processes (activations, facts, rules).";
	}

}
