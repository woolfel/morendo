/*
 * Copyright 2002-2010 Peter Lin
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
package org.jamocha.rete.query;

import org.jamocha.rete.Slot;

/**
 * @author Peter Lin
 * 
 * BaseAlpha is the baseAlpha node for all 1-input nodes.
 */
public abstract class QueryBaseAlphaCondition extends QueryBaseAlpha {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QueryBaseAlphaCondition(int id) {
		super(id);
	}
	
	public abstract void setSlot(Slot sl);
	
	public abstract void setOperator(int operator);
}