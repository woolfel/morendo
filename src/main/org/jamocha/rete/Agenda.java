/*
 * Copyright 2002-2008 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.jamocha.rete.util.ProfileStats;

/**
 * @author Peter Lin
 *
 * The design of the agenda is based on CLIPS, which uses modules to
 * contain different rulesets. When a new activation is added to the
 * agenda, it is added to a specific module. By default, the rule
 * engine creates a main module. If no additional modules are created,
 * all activations are added to the main module. If there are multiple
 * modules, the activation is added to the activation list of that
 * given module.
 * Only the activations of the current module will be fired.
 */
public class Agenda implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
	 * The ArrayList for the modules.
	 */
	protected Map<?, ?> modules = null;

	private Rete engine = null;

	private boolean watch = false;

	private boolean profAdd = false;

	private boolean profRm = false;

	private boolean startReset = false;
	
	/**
	 * The agenda takes an instance of Rete. the agenda needs a
	 * handle to the engine to do work.
	 */
	public Agenda(Rete engine) {
		super();
		this.engine = engine;
		this.modules = engine.newLocalMap();
	}

	public void setWatch(boolean w) {
		this.watch = w;
	}

	public boolean watch() {
		return this.watch;
	}

	public void setProfileAdd(boolean prof) {
		this.profAdd = prof;
	}

	public boolean profileAdd() {
		return this.profAdd;
	}

	public void setProfileRemove(boolean prof) {
		this.profRm = prof;
	}

	public boolean profileRemove() {
		return this.profRm;
	}

	/**
	 * Add an activation to the agenda. If the engine is current being
	 * reset, no activations are added
	 * @param actv
	 */
	public void addActivation(Activation actv) {
		if (!this.startReset) {
			// the implementation should get the current focus from Rete
			// and then add the activation to the Module.
			if (profAdd) {
				addActivationWProfile(actv);
			} else {
				if (watch) {
					engine.writeMessage("=> " + actv.toPPString() + Constants.LINEBREAK, "t");
				}
				actv.getRule().getModule().addActivation(actv);
			}
		}
	}

	/**
	 * if profiling is turned on, the method is called to add
	 * new activations to the agenda
	 * @param actv
	 */
	public void addActivationWProfile(Activation actv) {
		ProfileStats.startAddActivation();
		actv.getRule().getModule().addActivation(actv);
		ProfileStats.endAddActivation();
	}

	/**
	 * Method is called to remove an activation from the agenda.
	 * @param actv
	 */
	public void removeActivation(Activation actv) {
		if (profRm) {
			removeActivationWProfile(actv);
		} else {
			if (watch) {
				engine.writeMessage("<= " + actv.toPPString() + Constants.LINEBREAK, "t");
			}
			actv.getRule().getModule().removeActivation(actv);
		}
	}

	/**
	 * if the profiling is turned on for remove, the method is
	 * called to remove activations.
	 * @param actv
	 */
	public void removeActivationWProfile(Activation actv) {
		ProfileStats.startRemoveActivation();
		actv.getRule().getModule().removeActivation(actv);
		ProfileStats.endRemoveActivation();
	}

	/**
	 * Clear will clear all the modules and remove all activations
	 */
	public void clear() {
		Iterator<?> itr = this.modules.keySet().iterator();
		while (itr.hasNext()) {
			Object key = itr.next();
			Module mod = (Module) this.modules.get(key);
			mod.clear();
		}
		this.modules.clear();
	}
	
	public void startReset() {
		this.startReset = true;
	}
	
	public void endReset() {
		this.startReset = false;
	}
}
