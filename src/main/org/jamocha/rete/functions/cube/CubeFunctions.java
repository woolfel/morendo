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
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.FunctionException;

/**
 * @author Peter Lin
 * 
 * RuleEngineFunction is responsible for loading all the rule functions
 * related to engine operation.
 */
public class CubeFunctions implements FunctionGroup, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private ArrayList funcs = new ArrayList();
	
	public CubeFunctions() {
		super();
	}
	
	public String getName() {
		return (CubeFunctions.class.getSimpleName());
	}
	
	@SuppressWarnings("unchecked")
	public void loadFunctions(Rete engine) {
		try {
	        CubeAddDataFunction upcube = new CubeAddDataFunction();
	        engine.declareFunction(upcube);
	        funcs.add(upcube);
	        CubeDeleteDataFunction delcube = new CubeDeleteDataFunction();
	        engine.declareFunction(delcube);
	        funcs.add(delcube);
			DefcubeFunction defcb = new DefcubeFunction();
			engine.declareFunction(defcb);
			funcs.add(defcb);
			IndexDimensionFunction indexDim = new IndexDimensionFunction();
			engine.declareFunction(indexDim);
			funcs.add(indexDim);
			ListCubesFunction listcubes = new ListCubesFunction();
			engine.declareFunction(listcubes);
			engine.declareFunction(ListCubesFunction.CUBES, listcubes);
			funcs.add(listcubes);
	        LoadMeasureFunction loadmsr = new LoadMeasureFunction();
	        engine.declareFunction(loadmsr);
	        funcs.add(loadmsr);
	        LoadMeasureGroupFunction loadmgrp = new LoadMeasureGroupFunction();
	        engine.declareFunction(loadmgrp);
	        funcs.add(loadmgrp);
	        ProfileCubeFunction prof = new ProfileCubeFunction();
	        engine.declareFunction(prof);
	        funcs.add(prof);
	        ProfileCubeIndexFunction pcifunction = new ProfileCubeIndexFunction();
	        engine.declareFunction(pcifunction);
	        funcs.add(pcifunction);
	        PPrintDefcubeFunction ppdcube = new PPrintDefcubeFunction();
	        engine.declareFunction(ppdcube);
	        funcs.add(ppdcube);
	        PrintProfileCubeIndexFunction ppci = new PrintProfileCubeIndexFunction();
	        engine.declareFunction(ppci);
	        funcs.add(ppci);
	        UnProfileCubeFunction unprof = new UnProfileCubeFunction();
	        engine.declareFunction(unprof);
	        funcs.add(unprof);
	        UnProfileCubeIndexFunction unprofind = new UnProfileCubeIndexFunction();
	        engine.declareFunction(unprofind);
	        funcs.add(unprofind);
		} catch (FunctionException e) {
		}
	}

	@SuppressWarnings("rawtypes")
	public List listFunctions() {
		return funcs;
	}

}
