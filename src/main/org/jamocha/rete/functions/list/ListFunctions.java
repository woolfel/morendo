package org.jamocha.rete.functions.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class ListFunctions implements FunctionGroup, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList funcs = new ArrayList();
	
	public ListFunctions() {
		super();
	}

	public String getName() {
		return ListFunctions.class.getSimpleName();
	}

	public List listFunctions() {
		return funcs;
	}

	public void loadFunctions(Rete engine) {
		BottomFunction btm = new BottomFunction();
		funcs.add(btm);
		engine.declareFunction(btm);
		TopFunction top = new TopFunction();
		funcs.add(top);
		engine.declareFunction(top);
		// list modification functions
		CreateMSlotFunction cms = new CreateMSlotFunction();
		funcs.add(cms);
		engine.declareFunction(cms);
		DeleteRangeFunction drf = new DeleteRangeFunction();
		funcs.add(drf);
		engine.declareFunction(drf);
		// insert function
		InsertValueFunction insert = new InsertValueFunction();
		funcs.add(insert);
		engine.declareFunction(insert);
		// length function
		LengthFunction length = new LengthFunction();
		funcs.add(length);
		engine.declareFunction(length);
		// member test function
		MemberTestFunction mbrtest = new MemberTestFunction();
		funcs.add(mbrtest);
		engine.declareFunction(mbrtest);
		// nth function
		NthFunction nth = new NthFunction();
		funcs.add(nth);
		engine.declareFunction(nth);
		MapContainsFunction mapct = new MapContainsFunction();
		funcs.add(mapct);
		engine.declareFunction(mapct);
		CreateSetFunction cset = new CreateSetFunction();
		funcs.add(cset);
		engine.declareFunction(cset);
		SetContainsFunction setct = new SetContainsFunction();
		funcs.add(setct);
		engine.declareFunction(setct);
		EagerSetContainsFunction esetct = new EagerSetContainsFunction();
		funcs.add(esetct);
		engine.declareFunction(esetct);
		// First function
		FirstFunction first = new FirstFunction();
		funcs.add(esetct);
		engine.declareFunction(first);
		// rest function
		RestFunction rest = new RestFunction();
		funcs.add(esetct);
		engine.declareFunction(rest);
	}

}
