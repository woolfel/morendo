package org.jamocha.rete.functions.text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

/**
 * TextFunctions contains functions for text parsing like stop words and doing
 * a simple word count. These are generic base functions for processing text
 * before you do analytics.
 * 
 * @author peter
 *
 */
public class TextFunctions implements FunctionGroup, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Function> funcs = new ArrayList<Function>();
	
	public TextFunctions() {
		super();
	}

	public String getName() {
		return TextFunctions.class.getSimpleName();
	}

	public List<Function> listFunctions() {
		return funcs;
	}

	public void loadFunctions(Rete engine) {
		StopwordFunction swf = new StopwordFunction();
		funcs.add(swf);
		engine.declareFunction(swf);
		TokenizeFunction tnf = new TokenizeFunction();
		funcs.add(tnf);
		engine.declareFunction(tnf);
		TokenMatchFunction tm = new TokenMatchFunction();
		funcs.add(tm);
		engine.declareFunction(tm);
	}

}
