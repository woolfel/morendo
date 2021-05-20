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
package org.jamocha.rete.functions.java;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;
import org.jamocha.rete.functions.MemberFunction;

public class JavaFunctions implements FunctionGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private ArrayList funcs = new ArrayList();
	
	public JavaFunctions() {
		super();
	}
	
	public String getName() {
		return (JavaFunctions.class.getSimpleName());
	}

	@SuppressWarnings("unchecked")
	public void loadFunctions(Rete engine) {
		ClassnameResolver classnameResolver = new ClassnameResolver(engine);
		CallMethodFunction callm = new CallMethodFunction();
		engine.declareFunction(callm);
		funcs.add(callm);
		DefclassFunction defcls = new DefclassFunction();
		engine.declareFunction(defcls);
		funcs.add(defcls);
		DefinstanceFunction defins = new DefinstanceFunction();
		engine.declareFunction(defins);
		funcs.add(defins);
		LoadPackageFunction loadpkg = new LoadPackageFunction(classnameResolver);
		engine.declareFunction(loadpkg);
		funcs.add(loadpkg);
		GetMemberFunction getm = new GetMemberFunction();
		engine.declareFunction(getm);
		funcs.add(getm);
		NewFunction nf = new NewFunction(classnameResolver);
		engine.declareFunction(nf);
		funcs.add(nf);
		MemberFunction mf = new MemberFunction(classnameResolver);
		engine.declareFunction(mf);
		funcs.add(mf);
		InstanceofFunction iof = new InstanceofFunction(classnameResolver);
		engine.declareFunction(iof);
		funcs.add(iof);
		SetMemberFunction setm = new SetMemberFunction();
		engine.declareFunction(setm);
		funcs.add(setm);
	}

	@SuppressWarnings("rawtypes")
	public List listFunctions() {
		return funcs;
	}


}
