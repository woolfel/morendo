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
package org.jamocha.rete.functions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.FunctionException;

/**
 * @author Peter Lin
 * 
 * RuleEngineFunction is responsible for loading all the rule functions
 * related to engine operation.
 */
public class RuleEngineFunctions implements FunctionGroup, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Function> funcs = new ArrayList<Function>();
	
	public RuleEngineFunctions() {
		super();
	}
	
	public String getName() {
		return (RuleEngineFunctions.class.getSimpleName());
	}
	
	public void loadFunctions(Rete engine) {
		try {
			AndFunction and = new AndFunction();
			engine.declareFunction(and);
			funcs.add(and);
			AssertFunction assrt = new AssertFunction();
			engine.declareFunction(assrt);
			funcs.add(assrt);
			AssertTemporalFunction assertTemporal = new AssertTemporalFunction();
			engine.declareFunction(assertTemporal);
			funcs.add(assertTemporal);
			AnyEqFunction anyeq = new AnyEqFunction();
			engine.declareFunction(anyeq);
			funcs.add(anyeq);
			BindFunction bindf = new BindFunction();
			engine.declareFunction(bindf);
			funcs.add(bindf);
			ClearFunction clr = new ClearFunction();
			engine.declareFunction(clr);
			funcs.add(clr);
            DefglobalFunction defgbl = new DefglobalFunction();
            engine.declareFunction(defgbl);
            funcs.add(defgbl);
			DefmoduleFunction dmod = new DefmoduleFunction();
			engine.declareFunction(dmod);
			funcs.add(dmod);
			DefruleFunction drule = new DefruleFunction();
			engine.declareFunction(drule);
			funcs.add(drule);
	        DefstrategyFunction dstrat = new DefstrategyFunction();
	        engine.declareFunction(dstrat);
	        funcs.add(dstrat);
			DeftemplateFunction dtemp = new DeftemplateFunction();
			engine.declareFunction(dtemp);
			funcs.add(dtemp);
			DuplicateFunction dupe = new DuplicateFunction();
			engine.declareFunction(dupe);
			funcs.add(dupe);
			EchoFunction efunc = new EchoFunction();
			engine.declareFunction(efunc);
			funcs.add(efunc);
			EqFunction eq = new EqFunction();
			engine.declareFunction(eq);
			funcs.add(eq);
			EvalFunction eval = new EvalFunction();
			engine.declareFunction(eval);
			funcs.add(eval);
	        ExitFunction ext = new ExitFunction();
	        engine.declareFunction(ext);
	        funcs.add(ext);
	        FactCountFunction fcount = new FactCountFunction();
	        engine.declareFunction(fcount);
	        funcs.add(fcount);
	        FactIdFunction fidfun = new FactIdFunction();
	        engine.declareFunction(fidfun);
	        funcs.add(fidfun);
	        FactsFunction ffun = new FactsFunction();
	        engine.declareFunction(ffun);
	        funcs.add(ffun);
			FireFunction fire = new FireFunction();
			engine.declareFunction(fire);
			funcs.add(fire);
			FocusFunction focus = new FocusFunction();
			engine.declareFunction(focus);
			funcs.add(focus);
			GetCurrentModuleFunction gcmf = new GetCurrentModuleFunction();
			engine.declareFunction(gcmf);
			funcs.add(gcmf);
	        GetStrategyFunction gsf = new GetStrategyFunction();
	        engine.declareFunction(gsf);
	        funcs.add(gsf);
			LazyAgendaFunction laf = new LazyAgendaFunction();
			engine.declareFunction(laf);
			funcs.add(laf);
			ListDirectoryFunction ldir = new ListDirectoryFunction();
			engine.declareFunction(ldir);
			funcs.add(ldir);
			ListFunctionsFunction lffnc = new ListFunctionsFunction();
			engine.declareFunction(lffnc);
			engine.declareFunction(ListFunctionsFunction.FUNCTIONS,lffnc);
			funcs.add(lffnc);
			ListMeasuresFunction listmsr = new ListMeasuresFunction();
			engine.declareFunction(listmsr);
			funcs.add(listmsr);
			ListTemplatesFunction listTemp = new ListTemplatesFunction();
			engine.declareFunction(listTemp);
			engine.declareFunction(ListTemplatesFunction.TEMPLATES,listTemp);
			funcs.add(listTemp);
			LoadFunctionsFunction loadfunc = new LoadFunctionsFunction();
			engine.declareFunction(loadfunc);
			funcs.add(loadfunc);
			LoadFunctionGroupFunction loadfg = new LoadFunctionGroupFunction();
			engine.declareFunction(loadfg);
			funcs.add(loadfg);
			UsageFunction usage = new UsageFunction();
			engine.declareFunction(usage);
			funcs.add(usage);
			ModifyFunction mod = new ModifyFunction();
			engine.declareFunction(mod);
			funcs.add(mod);
			ModulesFunction modules = new ModulesFunction();
			engine.declareFunction(modules);
			funcs.add(modules);
			OrFunction or = new OrFunction();
			engine.declareFunction(or);
			funcs.add(or);
			PPrintRuleFunction pprule = new PPrintRuleFunction();
			engine.declareFunction(pprule);
			funcs.add(pprule);
			PPrintTemplateFunction pptemp = new PPrintTemplateFunction();
			engine.declareFunction(pptemp);
			funcs.add(pptemp);
	        PrintProfileFunction pproff = new PrintProfileFunction();
	        engine.declareFunction(pproff);
	        funcs.add(pproff);
	        ProfileFunction proff = new ProfileFunction();
	        engine.declareFunction(proff);
	        funcs.add(proff);
	        ResetFunction resetf = new ResetFunction();
	        engine.declareFunction(resetf);
	        funcs.add(resetf);
	        ResetFactsFunction resetff = new ResetFactsFunction();
	        engine.declareFunction(resetff);
	        funcs.add(resetff);
	        ResetObjectsFunction resetof = new ResetObjectsFunction();
	        engine.declareFunction(resetof);
	        funcs.add(resetof);
	        RetractFunction rtract = new RetractFunction();
	        engine.declareFunction(rtract);
	        funcs.add(rtract);
			RulesFunction rf = new RulesFunction();
			engine.declareFunction(rf);
			engine.declareFunction(RulesFunction.RULES,rf);
			funcs.add(rf);
			SaveFactsFunction savefacts = new SaveFactsFunction();
			engine.declareFunction(savefacts);
			funcs.add(savefacts);
			SetFocusFunction setfoc = new SetFocusFunction();
			engine.declareFunction(setfoc);
			funcs.add(setfoc);
	        SetStrategyFunction setstrat = new SetStrategyFunction();
	        engine.declareFunction(setstrat);
	        funcs.add(setstrat);
			SpoolFunction spool = new SpoolFunction();
			engine.declareFunction(spool);
			engine.declareFunction(SpoolFunction.DRIBBLE, spool);
			funcs.add(spool);
			UnDefruleFunction udrule = new UnDefruleFunction();
			engine.declareFunction(udrule);
			funcs.add(udrule);
			UnDeftemplateFunction udt = new UnDeftemplateFunction();
			engine.declareFunction(udt);
			funcs.add(udt);
			UnWatchFunction uwatchf = new UnWatchFunction();
			engine.declareFunction(uwatchf);
	        funcs.add(uwatchf);
	        UnProfileFunction uproff = new UnProfileFunction();
	        engine.declareFunction(uproff);
	        funcs.add(uproff);
	        VersionFunction ver = new VersionFunction();
	        engine.declareFunction(ver);
	        funcs.add(ver);
	        ViewFunction view = new ViewFunction();
	        engine.declareFunction(view);
	        funcs.add(view);
			WatchFunction watchf = new WatchFunction();
			engine.declareFunction(watchf);
			funcs.add(watchf);
			IsNilFunction inf = new IsNilFunction();
			engine.declareFunction(inf);
	        funcs.add(inf);
	        IsNotNilFunction innf = new IsNotNilFunction();
	        engine.declareFunction(innf);
	        funcs.add(innf);
		} catch (FunctionException e) {
			engine.writeMessage(e.getMessage());
		}
	}

	public List<Function> listFunctions() {
		return funcs;
	}

}
