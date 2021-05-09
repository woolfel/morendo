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
package org.jamocha.rete.functions.analysis;

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
public class AnalysisFunctions implements FunctionGroup, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList funcs = new ArrayList();
	
	public AnalysisFunctions() {
		super();
	}
	
	public String getName() {
		return (AnalysisFunctions.class.getSimpleName());
	}
	
	public void loadFunctions(Rete engine) {
		AverageCostFunction aveCost = new AverageCostFunction();
		engine.declareFunction(aveCost);
		funcs.add(aveCost);
		GenerateFactsFunction genff = new GenerateFactsFunction();
		engine.declareFunction(genff);
		funcs.add(genff);
		PartialMatchCostFunction pmcost = new PartialMatchCostFunction();
		engine.declareFunction(pmcost);
		funcs.add(pmcost);
		SetDistinctCount setCount = new SetDistinctCount();
		engine.declareFunction(setCount);
		funcs.add(setCount);
		TestRuleFunction trfunc = new TestRuleFunction();
		engine.declareFunction(trfunc);
		funcs.add(trfunc);
        TopologyCostFunction topcostfunc = new TopologyCostFunction();
        engine.declareFunction(topcostfunc);
        funcs.add(topcostfunc);
        TopologyCostAllFunction topCostAll = new TopologyCostAllFunction();
        engine.declareFunction(topCostAll);
        funcs.add(topCostAll);
        ValidateRuleFunction vrf = new ValidateRuleFunction();
        engine.declareFunction(vrf);
        funcs.add(vrf);
	}

	public List listFunctions() {
		return funcs;
	}

}
