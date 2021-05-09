package org.jamocha.service;

import java.util.List;

/**
 * Configuration describes the models, functions, rules and
 * data for a given RuleApplication. For each one, there
 * could be one or more, so they all use lists.
 * 
 * the list could be text files, or other objects.
 * 
 * 
 * @author Peter Lin
 */
public interface Configuration {
	void setInitialPool(int initial);
	int getInitialPool();
	
	void setMaxPool(int max);
	int getMaxPool();
	
	void setMinPool(int min);
	int getMinPool();
	
	void setName(String name);
	String getName();
	
	void setModels(List<ObjectModel> models);
	List<ObjectModel> getModels();
	
	void setFunctionGroups(List<FunctionPackage> functionGroups);
	List<FunctionPackage> getFunctionGroups();
	
	void setRulesets(List<ClipsRuleset> rulesets);
	List<ClipsRuleset> getRulesets();
	
	void setObjectData(List<ObjectData> data);
	List<ObjectData> getObjectData();
	
	void setClipsData(List<ClipsInitialData> data);
	List<ClipsInitialData> getClipsData();
}
