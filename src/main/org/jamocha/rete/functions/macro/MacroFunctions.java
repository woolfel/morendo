package org.jamocha.rete.functions.macro;

import java.util.List;
import java.util.ArrayList;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class MacroFunctions implements FunctionGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String MACRO_FUNCTIONS = "Macro Functions";
	protected ArrayList<Function> funcs = new ArrayList<Function>();

	public MacroFunctions() {
		super();
	}

	public String getName() {
		return MACRO_FUNCTIONS;
	}

	public List<Function> listFunctions() {
		return funcs;
	}

	public void loadFunctions(Rete engine) {
		UseMacroFunction usemacro = new UseMacroFunction();
		funcs.add(usemacro);
		engine.declareFunction(usemacro);
		//CompileClassMacroFunction compilemacro = new CompileClassMacroFunction();
		//funcs.add(compilemacro);
		//engine.declareFunction(compilemacro);
		GenerateMacroFunction generate = new GenerateMacroFunction();
		funcs.add(generate);
		engine.declareFunction(generate);
		//JarClassMacroFunction jarmacro = new JarClassMacroFunction();
		//funcs.add(jarmacro);
		//engine.declareFunction(jarmacro);
	}

}
