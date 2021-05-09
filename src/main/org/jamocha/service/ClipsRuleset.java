package org.jamocha.service;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Function;
import org.jamocha.rule.Defrule;

public class ClipsRuleset implements Ruleset {
	
	private transient Logger log = null;
	private String contents;
	private String URL;
	
	public ClipsRuleset() {
		super();
		log = LogFactory.createLogger(ClipsRuleset.class);
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	/**
	 * for clips rulesets, the URL is the location where the ruleset
	 * file is located.
	 */
	public String getURL() {
		return URL;
	}

	public void setURL(String url) {
		URL = url;
	}
	
	/**
	 * Since the batch function in Jamocha handles http, and file,
	 * there's no need to write a whole new implementation.
	 */
	public boolean loadRuleset(org.jamocha.rete.Rete engine) {
		if (log == null) {
			log = LogFactory.createLogger(ClipsRuleset.class);
		}
		boolean loaded = false;
		if (this.URL != null) {
			try {
				engine.loadRuleset(this.URL);
				loaded = true;
			} catch (Exception e) {
				// we should log this
				log.fatal(e);
			}
		} else if (this.contents != null) {
			java.io.StringReader reader = new java.io.StringReader(this.contents);
			CLIPSParser parser = new CLIPSParser(engine, reader);
			Object expr = null;
			try {
				while ((expr = parser.basicExpr()) != null) {
					if (expr instanceof Defrule) {
						Defrule rl = (Defrule) expr;
						engine.getRuleCompiler().addRule(rl);
					} else if (expr instanceof Deftemplate) {
						Deftemplate dft = (Deftemplate) expr;
						engine.getCurrentFocus().addTemplate(dft, engine,
								engine.getWorkingMemory());
					} else if (expr instanceof Function) {
						Function fnc = (Function) expr;
						fnc.executeFunction(engine, null);
					}
				}
			} catch (Exception e) {
				// we need to log the error
				log.fatal(e);
			}
			loaded = true;
		}
		return loaded;
	}
	
	public boolean reloadRuleset(org.jamocha.rete.Rete engine) {
		boolean reload = false;
		engine.clearRules();
		reload = this.loadRuleset(engine);
		return reload;
	}
}
