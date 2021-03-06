/*
 * Copyright 2002-2009 Jamocha
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

import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 *
 */
public class CompileEvent extends AbstractEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int type = -1;

	private String message = "";

	private Rule rule = null;

	/**
	 * @param source
	 */
	public CompileEvent(Object source, int eventType) {
		super(source);
		this.type = eventType;
	}

	public int getEventType() {
		return this.type;
	}

	public void setEventType(int eventType) {
		this.type = eventType;
	}

	public void setMessage(String text) {
		this.message = text;
	}

	public String getMessage() {
		if (this.rule != null) {
			return this.rule.getName() + " " + this.message;
		} else {
			return this.message;
		}
	}

	public void setRule(Rule theRule) {
		this.rule = theRule;
	}

	public Rule getRule() {
		return this.rule;
	}
}
