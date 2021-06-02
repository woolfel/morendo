/*
 * Copyright 2002-2009 Jamocha
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
package org.jamocha.rete.functions.cube;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Cube;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.RuleFunction;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * CubeAddData function is used to update a cube and add more data
 * to it. Unlike other facts, updating a cube does not result in
 * retract + assert. This is done for performance reasons. If we
 * were to do a retract + assert, it could result in rules firing
 * more than needed.
 * 
 * One important thing is the engine should generate the rule for
 * updating cubes. The priority of the update cube rules should
 * always fire before other rules. That should be done by setting
 * the salience to 1000 or a value sufficiently high.
 * 
 * @author Peter Lin
 */
public class CubeAddDataFunction implements RuleFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CUBE_ADD_DATA = "cube-add-data";
	private Fact[] triggerFacts = null;

	public CubeAddDataFunction() {
		super();
	}

	public void setTriggerFacts(Fact[] facts) {
		this.triggerFacts = facts;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		boolean update = false;
		if (params != null && params.length == 1) {
			String name = (String)params[0].getValue(engine, Constants.STRING_TYPE);
			Cube c = engine.getCube(name);
			if (c != null && triggerFacts != null) {
				c.addData(this.triggerFacts, engine);
				update = true;
				try {
					engine.modifyObject(c);
				} catch (AssertException e) {
					// we should never get an exception
				} catch (RetractException e) {
					// we should never get an exception
				}
			}
		}
		
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.BOOLEAN_OBJECT,
				update);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return CUBE_ADD_DATA;
	}

	public Class<?>[] getParameter() {
		return new Class[]{String.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			return "(cube-add-data \"" + params[0].getStringValue() + "\")";
		} else {
			return "(cube-add-data <name>)";
		}
	}

}
