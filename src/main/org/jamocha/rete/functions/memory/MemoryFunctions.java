/*
 * Copyright 2002-2009 Peter Lin
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
package org.jamocha.rete.functions.memory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

/**
 * @author Peter Lin
 * 
 * RuleEngineFunction is responsible for loading all the rule functions
 * related to engine operation.
 */
public class MemoryFunctions implements FunctionGroup, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("rawtypes")
	private ArrayList funcs = new ArrayList();
	
	public MemoryFunctions() {
		super();
	}
	
	public String getName() {
		return (MemoryFunctions.class.getSimpleName());
	}
	
	@SuppressWarnings("unchecked")
	public void loadFunctions(Rete engine) {
		GarbageCollectFunction gcf = new GarbageCollectFunction();
		engine.declareFunction(gcf);
		funcs.add(gcf);
		MatchesFunction mf = new MatchesFunction();
		engine.declareFunction(mf);
		funcs.add(mf);
		MemoryFreeFunction mff = new MemoryFreeFunction();
		engine.declareFunction(mff);
		funcs.add(mff);
		MemoryTotalFunction mtf = new MemoryTotalFunction();
		engine.declareFunction(mtf);
		funcs.add(mtf);
		MemoryUsedFunction musd = new MemoryUsedFunction();
		engine.declareFunction(musd);
		funcs.add(musd);
        RightMatchesFunction rmfunc = new RightMatchesFunction();
        engine.declareFunction(rmfunc);
        funcs.add(rmfunc);
        RuleMatchesFunction rmatch = new RuleMatchesFunction();
        engine.declareFunction(rmatch);
        funcs.add(rmatch);
	}

	@SuppressWarnings("rawtypes")
	public List listFunctions() {
		return funcs;
	}

}
