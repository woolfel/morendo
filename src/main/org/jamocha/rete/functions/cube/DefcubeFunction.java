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

import java.io.Serializable;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Cube;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Defcube;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defrule;

/**
 * 
 * @author Peter Lin
 */
public class DefcubeFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DEFCUBE = "defcube";

	public DefcubeFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean add = Boolean.FALSE;
		
		if (params != null && params.length == 1) {
			Defcube cube = (Defcube)params[0].getValue(engine, Constants.OBJECT_TYPE);
			add = cube.compileCube(engine);
			if (add) {
				Defrule rule = cube.getUpdateRule();
				// we have to add the update rule, so the engine will
				// populate the cube
				add = engine.getRuleCompiler().addRule(rule);
				engine.declareCube(cube);
				engine.addCube(cube);
				try {
					engine.assertObject(cube, null, false, true);
				} catch (AssertException e) {
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, add);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return DEFCUBE;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{Cube.class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return null;
	}

}
