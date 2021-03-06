/*
 * Copyright 2002-2006 Peter Lin
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
package org.jamocha.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.Constants;
import org.jamocha.rete.ConversionUtils;


/**
 * @author Peter Lin
 *
 * Literal constraint is a comparison between an object field and a concrete
 * value. for example, account.name is equal to "Peter Lin". I originally,
 * named the class something else, but since CLIPS uses literal constraint,
 * I decided to change the name of the class. Even though I don't like the
 * term literal constraint, it doesn't make sense to fight existing
 * terminology.
 */
public class OrLiteralConstraint implements Constraint {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String name;
	protected ArrayList<MultiValue> value = new ArrayList<MultiValue>();
    protected boolean negated = false;
    
	
	public OrLiteralConstraint() {
		super();
	}

	/**
     * the name is the slot name
	 */
	public String getName() {
		return name;
	}

	/**
     * set the slot name as declared in the rule
	 */
	public void setName(String name) {
        this.name = name;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rule.Constraint#getValue()
	 */
	public Object getValue() {
		return value;
	}

	/**
     * Set the value of the constraint. It should be a concrete value and
     * not a binding.
	 */
	@SuppressWarnings("unchecked")
	public void setValue(Object val) {
		if (val instanceof ArrayList) {
	        this.value = (ArrayList<MultiValue>)val;
		}
	}
	
	public void addValue(MultiValue mv) {
		this.value.add(mv);
	}
	
	public void addValues(Collection<MultiValue> list) {
		this.value.addAll(list);
	}
	
	/**
	 * if the literal constraint is negated with a "~" tilda, call
	 * the method pass true.
	 * @param negate
	 */
	public void setNegated(boolean negate) {
		this.negated = negate;
	}
	
	/**
	 * if the literal constraint is negated, the method returns true
	 * @return
	 */
	public boolean getNegated() {
		return this.negated;
	}
	
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		Iterator<MultiValue> itr = this.value.iterator();
		buf.append("    (" + this.name + " ");
		int count = 0;
		while (itr.hasNext()) {
			MultiValue mv = (MultiValue)itr.next();
			if (count > 0) {
				buf.append("|");
			}
			if (mv.getNegated()) {
				buf.append("~" + ConversionUtils.formatSlot(mv.getValue()));
			} else {
				buf.append( ConversionUtils.formatSlot(mv.getValue()));
			}
			count++;
		}
		buf.append(")" + Constants.LINEBREAK);
		return buf.toString();
	}

	public void addValues(List<Object> andor) {
		// TODO Auto-generated method stub
		
	}
}
