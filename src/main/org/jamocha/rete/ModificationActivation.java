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
package org.jamocha.rete;

import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rule.Action;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 *
 * LinkedActivation is different than BasicActivation in a couple of
 * ways. LinkedActivation makes it easier to remove Activations from
 * an ActivationList, without having to iterate over the activations.
 * When the activation is executed or removed, it needs to make sure
 * it checks the previous and next and set them correctly.
 */
public class ModificationActivation extends LinkedActivation {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ModificationActivation(Rule rule, Index inx) {
		super(rule, inx);
	}

	/**
	 * Method overrides the base implementation and gets the modificationActions
	 * instead of the actions. The rest of the methods are the same.
	 */
	public void executeActivation(Rete engine) throws ExecuteException {
		remove(engine);
		try {
            getRule().setTriggerFacts(getFacts());
			Action[] actions = getRule().getModificationActions();
			for (int idx = 0; idx < actions.length; idx++) {
				if (actions[idx] != null) {
					actions[idx].executeAction(engine, getFacts());
				} else {
					throw new ExecuteException(ExecuteException.NULL_ACTION);
				}
			}
		} catch (ExecuteException e) {
			throw e;
		}
	}
    
    public ModificationActivation clone() {
        ModificationActivation la = new ModificationActivation(getRule(),getIndex());
        return la;
    }
}
