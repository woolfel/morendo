package org.jamocha.service;

import java.util.ArrayList;
import java.util.List;

/**
 * RuleApplicationBean is used to read and write the Rule application data to JSON format.
 * RuleApplicationImpl then reads the values from the bean and has the methods to handle
 * the initialization logic.
 * 
 * @author peter
 *
 */
public class RuleApplicationBean implements Configuration {

	private int initialPool = 1;
	private int maxPool = 10;
	private int minPool = 1;
	private String name;
	private List<ObjectModel> models;
	private List<FunctionPackage> functionGroups = new ArrayList<FunctionPackage>();
	private List<ClipsRuleset> rulesets = new ArrayList<ClipsRuleset>();
	private List<ObjectData> objectData = new ArrayList<ObjectData>();
	private List<ClipsInitialData> clipsData = new ArrayList<ClipsInitialData>();
	private List<JSONData> jsonData = new ArrayList<JSONData>();
	
	public RuleApplicationBean() {
	}

	public int getInitialPool() {
		return initialPool;
	}

	public void setInitialPool(int initialPool) {
		this.initialPool = initialPool;
	}

	public int getMaxPool() {
		return maxPool;
	}

	public void setMaxPool(int maxPool) {
		this.maxPool = maxPool;
	}

	public int getMinPool() {
		return minPool;
	}

	public void setMinPool(int minPool) {
		this.minPool = minPool;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ObjectModel> getModels() {
		return models;
	}

	public void setModels(List<ObjectModel> models) {
		this.models = models;
	}

	public List<FunctionPackage> getFunctionGroups() {
		return functionGroups;
	}

	public void setFunctionGroups(List<FunctionPackage> functionGroups) {
		this.functionGroups = functionGroups;
	}

	public List<ClipsRuleset> getRulesets() {
		return rulesets;
	}

	public void setRulesets(List<ClipsRuleset> rulesets) {
		this.rulesets = rulesets;
	}

	public List<ObjectData> getObjectData() {
		return objectData;
	}

	public void setObjectData(List<ObjectData> initialData) {
		this.objectData = initialData;
	}

	public List<ClipsInitialData> getClipsData() {
		return clipsData;
	}

	public void setClipsData(List<ClipsInitialData> clipsData) {
		this.clipsData = clipsData;
	}

	public List<JSONData> getJsonData() {
		return jsonData;
	}

	public void setJsonData(List<JSONData> jsonData) {
		this.jsonData = jsonData;
	}
}
