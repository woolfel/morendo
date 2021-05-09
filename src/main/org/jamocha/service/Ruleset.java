package org.jamocha.service;

public interface Ruleset {
	void setContents(String content);
	String getContents();
	
	void setURL(String url);
	String getURL();
	
	boolean loadRuleset(org.jamocha.rete.Rete engine);
	boolean reloadRuleset(org.jamocha.rete.Rete engine);
}
