/*
 * Copyright 2002-2009 Jamocha
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
package org.jamocha.rete;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.functions.cube.CubeAddDataFunction;
import org.jamocha.rete.functions.cube.CubeDeleteDataFunction;
import org.jamocha.rete.measures.Measure;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.FunctionAction;
import org.jamocha.rule.ObjectCondition;

/**
 * 
 * @author Peter Lin
 *
 */
public class Defcube implements Cube {

	public static final String RULE_PREFIX = "generated_cube_rule_";
	private CubeDimension[] dimensions;
	private Defmeasure[] defmeasures;
	private Measure[] measures;
	private String description;
	private String name;
	private List objectConditionList = null;
	private Map bindings = null;
	private Map bindingsByName = null;
	private Deftemplate[] templates = null;
	private Map dataset = null;
	private Map dimensionMap = null;
	private Map dimensionMapByBinding = null;
	private Map measureMap = null;
	private Defrule updateRule = null;
	private boolean compileSuccessful = true;
	private boolean profile = false;
	private boolean profileIndex = false;
	
	public Defcube(Rete engine) {
		super();
		bindings = engine.newLocalMap();
		bindingsByName = engine.newLocalMap();
		dataset = engine.newMap();
		dimensionMap = engine.newLocalMap();
		dimensionMapByBinding = engine.newLocalMap();
		measureMap = engine.newLocalMap();
	}
	
	public CubeDimension[] getDimensions() {
		return this.dimensions;
	}

	public Measure[] getMeasures() {
		return this.measures;
	}

	public String getDescription() {
		return this.description;
	}

	public String getName() {
		return this.name;
	}

	public void setDescription(String text) {
		this.description = text;
	}

	public void setDimensions(CubeDimension[] dimensions) {
		this.dimensions = dimensions;
	}
	
	public CubeDimension getDimension(String name) {
		return (CubeDimension)this.dimensionMap.get(name);
	}

	public void setMeasures(Measure[] measures) {
		this.measures = measures;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Defmeasure[] getDefmeasures() {
		return defmeasures;
	}

	public void setDefmeasures(Defmeasure[] defmeasures) {
		this.defmeasures = defmeasures;
	}

	public Defmeasure getMeasure(String name) {
		return (Defmeasure)measureMap.get(name);
	}
	
	public void setDimensions(List list) {
		CubeDimension[] dimens = new CubeDimension[list.size()];
		this.dimensions = (CubeDimension[])list.toArray(dimens);
	}
	
	public void setDefmeasures(List list) {
		Defmeasure[] mrs = new Defmeasure[list.size()];
		this.defmeasures = (Defmeasure[])list.toArray(mrs);
	}
	
	public void setDeftemplates(List list) {
		objectConditionList = list;
	}
	
	public void setProfileQuery(boolean profile) {
		this.profile = profile;
	}
	
	public boolean profileQuery() {
		return profile;
	}
	
	public void setProfileIndex(boolean profile) {
		this.profileIndex = profile;
		for (int idx=0; idx < this.dimensions.length; idx++) {
			this.dimensions[idx].setProfile(profile);
		}
	}
	
	public boolean profileIndex() {
		return this.profileIndex;
	}
	
	/**
	 * Add data to the cube. It expects a fact array from the
	 * generated cube rule.
	 */
	public void addData(Fact[] data, Rete engine) {
		Index index = new Index((Fact[])data);
		dataset.put(index, index);
		indexData(data, engine);
	}
	
	/**
	 * Remove data from the cube.
	 */
	public void removeData(Fact[] data) {
		Index index = new Index((Fact[])data);
		dataset.remove(index);
	}
	
	/**
	 * Method iterates over the dimensions and indexes the data.
	 * First is creates a new instance of Index with the fact
	 * array, then it calls Dimension.indexData(Fact,Rete).
	 * @param data
	 * @param engine
	 */
	protected void indexData(Fact[] data, Rete engine) {
		Index index = new Index(data);
		for (int idx=0; idx < dimensions.length; idx++) {
			if (dimensions[idx].isAutoIndex()) {
				dimensions[idx].indexData(index, engine);
			}
		}
	}
	
	public Defrule getUpdateRule() {
		return this.updateRule;
	}
	
	/**
	 * Method is responsible for 
	 */
	public boolean compileCube(Rete engine) {
		ArrayList templatelist = new ArrayList();
		for (int idx=0; idx < objectConditionList.size(); idx++) {
			ObjectCondition oc = (ObjectCondition)this.objectConditionList.get(idx);
			Deftemplate templ = (Deftemplate)engine.getCurrentFocus().getTemplate(oc.getTemplateName());
			if (templ == null) {
				return false;
			}
			oc.setTemplate(templ);
			templatelist.add(templ);
		}
		Deftemplate[] tpl = new Deftemplate[this.objectConditionList.size()];
		this.templates = (Deftemplate[])templatelist.toArray(tpl);
		compileBindings(engine);
		if (this.compileSuccessful) {
			configureDimensions(engine);
		}
		if (this.compileSuccessful) {
			configureMeasures(engine);
		}
		
		// after we've configured the bindings, dimensions and measures,
		// we need to generate the rule to update the dimension.
		if (this.compileSuccessful) {
			createUpdateRule(engine);
		}
		return compileSuccessful;
	}
	
	/**
	 * compileObject bindings is responsible for iterating over the
	 * ObjectConditions and creating the bindings
	 */
	protected void compileBindings(Rete engine) {
		int counter = 0;
		for (int idx=0; idx < this.objectConditionList.size(); idx++) {
			ObjectCondition oc = (ObjectCondition)this.objectConditionList.get(idx);
			Constraint[] constraints = oc.getConstraints();
			Deftemplate template = templates[idx];
			for (int cx=0; cx < constraints.length; cx++) {
				if (constraints[cx] instanceof BoundConstraint) {
					BoundConstraint bc = (BoundConstraint)constraints[cx];
					CubeBinding binding = new CubeBinding();
					binding.setTemplateName(template.getName());
					binding.setVarName(bc.getVariableName());
					BaseSlot slot = template.getSlot(bc.getName());
					binding.setLeftIndex(slot.getId());
					binding.setLeftRow(idx);
					binding.setSlotName(bc.getName());
					addBinding(binding);
				}
				counter++;
			}
		}
	}
	
	public CubeBinding getBinding(String variableName) {
		return (CubeBinding)bindings.get(variableName);
	}
	
	public CubeBinding getBindingBySlot(String slotName) {
		return (CubeBinding)bindingsByName.get(slotName);
	}
	
	protected void addBinding(CubeBinding binding) {
		CubeBinding b = (CubeBinding)bindings.get(binding.getVarName());
		if (b == null) {
			bindings.put(binding.getVarName(), binding);
			bindingsByName.put(binding.getSlotName(), binding);
		} else {
			b.setJoin(true);
		}
	}
	
	protected void addMeasureBinding(CubeBinding binding) {
		bindingsByName.put(binding.getSlotName(), binding);
	}
	
	protected void configureDimensions(Rete engine) {
		for (int idx=0; idx < dimensions.length; idx++) {
			CubeDimension dimension = dimensions[idx];
			CubeBinding binding = (CubeBinding)bindings.get(dimension.getVariableName());
			if (binding != null) {
				dimension.setBinding(binding);
				dimension.setJoined(binding.isJoin());
				binding.setRightIndex(idx);
			} else {
				this.compileSuccessful = false;
				engine.writeMessage("Dimension " + dimension.getName() + 
						" has an invalid binding " + dimension.getVariableName() + Constants.LINEBREAK);
				break;
			}
			dimensionMap.put(dimension.getName(), dimension);
			dimensionMapByBinding.put(dimension.getVariableName(), dimension);
		}
	}
	
	protected void configureMeasures(Rete engine) {
		int dimensionsize = this.dimensions.length;
		for (int idx=0; idx < this.defmeasures.length; idx++) {
			Defmeasure dmeasure = defmeasures[idx];
			Measure msr = engine.findMeasure(dmeasure.getMeasureName());
			if (msr != null) {
				dmeasure.setMeasure(msr);
				CubeDimension dimension = (CubeDimension)dimensionMapByBinding.get(dmeasure.getVariableName());
				if (dimension != null) {
					dmeasure.setDimension(dimension);
				}
				CubeBinding dbinding = (CubeBinding)this.bindings.get(dmeasure.getVariableName());
				CubeBinding binding = new CubeBinding();
				binding.setTemplateName(this.name);
				binding.setSlotName(dmeasure.getMeasureLabel());
				binding.setMeasure(true);
				binding.setRightIndex(idx + dimensionsize);
				binding.setLeftIndex(dbinding.leftIndex);
				binding.setLeftRow(dbinding.leftrow);
				measureMap.put(dmeasure.getMeasureLabel(), dmeasure);
				addMeasureBinding(binding);
			} else {
				this.compileSuccessful = false;
				engine.writeMessage("Measure " + dmeasure.getMeasureName() +
						" does not exist. Please double check."
						+ Constants.LINEBREAK);
				break;
			}
		}
	}
	
	/**
	 * method is responsible for creating a rule for updating the cube
	 * @param engine
	 */
	protected void createUpdateRule(Rete engine) {
		updateRule = new Defrule();
		updateRule.setName(RULE_PREFIX + name);
		// we set salient to 10,000 so that it will always fire first
		updateRule.setSalience(10000);
		updateRule.setNoAgenda(true);
		// first we add the conditions
		for (int idx=0; idx < this.objectConditionList.size(); idx++) {
			Condition c = (Condition)this.objectConditionList.get(idx);
			updateRule.addCondition(c);
		}
		// generate the action to call cube-add-data
		FunctionAction functionAction = new FunctionAction();
		functionAction.setFunctionName(CubeAddDataFunction.CUBE_ADD_DATA);
		ValueParam p1 = new ValueParam();
		p1.setValue(this.name);
		Parameter[] params = new Parameter[]{ p1 };
		functionAction.setParameters(params);
		updateRule.addAction(functionAction);
		
		// generate the modification action to call cube-delete-data
		FunctionAction functionAction2 = new FunctionAction();
		functionAction2.setFunctionName(CubeDeleteDataFunction.CUBE_DELETE_DATA);
		ValueParam p2 = new ValueParam();
		p2.setValue(this.name);
		Parameter[] params2 = new Parameter[]{ p2 };
		functionAction2.setParameters(params2);
		updateRule.addModificationAction(functionAction2);
	}
	
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(" + this.name + Constants.LINEBREAK);
		for (int idx=0; idx < dimensions.length; idx++) {
			buf.append("  (" + dimensions[idx].toPPString() + ")" + Constants.LINEBREAK);
		}
		for (int idx=0; idx < defmeasures.length; idx++) {
			buf.append("  (" + defmeasures[idx].toPPString() + ")" + Constants.LINEBREAK);
		}
		buf.append(")" + Constants.LINEBREAK);
		buf.append("size - " + dataset.size() + Constants.LINEBREAK);
		return buf.toString();
	}
}
