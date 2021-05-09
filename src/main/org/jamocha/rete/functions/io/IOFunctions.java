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
package org.jamocha.rete.functions.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;


/**
 * @author Peter Lin
 *
 * IO Functions will initialize the IO related functions like printout,
 * batch, etc.
 */
public class IOFunctions implements FunctionGroup, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList funcs = new ArrayList();

	/**
	 * 
	 */
	public IOFunctions() {
		super();
	}

	public String getName() {
		return (IOFunctions.class.getSimpleName());
	}

	public void loadFunctions(Rete engine) {
		BatchFunction b = new BatchFunction();
		engine.declareFunction(b);
		funcs.add(b);
		BatchObjectsFunction bobj = new BatchObjectsFunction();
		engine.declareFunction(bobj);
		funcs.add(bobj);
		BatchStaticObjectsFunction bstobj = new BatchStaticObjectsFunction();
		engine.declareFunction(bstobj);
		funcs.add(bstobj);
        BuildFunction build = new BuildFunction();
        engine.declareFunction(build);
        funcs.add(build);
		LoadFactsFunction load = new LoadFactsFunction();
		engine.declareFunction(load);
		funcs.add(load);
		LoadStreamFunction stream = new LoadStreamFunction();
		engine.declareFunction(stream);
		funcs.add(stream);
		PrintFunction pf = new PrintFunction();
		engine.declareFunction(pf);
		funcs.add(pf);
		LoadGraphFunction lgf = new LoadGraphFunction();
		engine.declareFunction(lgf);
		funcs.add(lgf);
		ReadFunction read = new ReadFunction();
		engine.declareFunction(read);
		funcs.add(read);
		
	}

	public List listFunctions() {
		return funcs;
	}

}
