/*
 * Copyright 2002-2010 Peter Lin
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

package org.jamocha.service.servlet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.service.EngineContext;

public class ServletEngineContext implements EngineContext {

	private RuleStartupService ruleService = null;
	private org.jamocha.rete.Rete engine = null;
	private String applicationName = null;
	private String version = null;
	private long startTime = 0;
	private long endTime = 0;
	private List<Object> objectList = new ArrayList<Object>();
	private ServletContext servletContext = null;
	
	protected ServletEngineContext() {
	}
	
	public ServletEngineContext(RuleStartupService service, org.jamocha.rete.Rete engine, String applicationName, String version, ServletContext servletContext) {
		this.ruleService = service;
		this.engine = engine;
		this.applicationName = applicationName;
		this.version = version;
		this.servletContext = servletContext;
		if (startTime == 0) {
			startTime = System.currentTimeMillis();
		}
	}

	public Rete getRuleEngine() {
		return this.engine;
	}
	
	public void assertObject(Object data, boolean isStatic, boolean isShadowed)
			throws AssertException {
		try {
			objectList.add(data);
			engine.assertObject(data, null, isStatic, isShadowed);
		} catch (AssertException e) {
			servletContext.log("ServletEngineContext error asserting Object:", e);
			throw e;
		}
	}

	public void asssertObjects(List<?> data, boolean isStatic, boolean isShadowed)
			throws AssertException {
		objectList.addAll(data);
		Iterator<?> itr = data.iterator();
		while (itr.hasNext()) {
			try {
				engine.assertObject(itr.next(), null, isStatic, isShadowed);
			} catch (AssertException e) {
				servletContext.log("ServletEngineContext error asserting Object:", e);
				throw e;
			}
		}
	}

	public void close() {
		this.endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		int count = engine.getRulesFiredCount();
		this.ruleService.updateStatistics(elapsedTime, count);
		Iterator<Object> itr = objectList.iterator();
		while (itr.hasNext()) {
			try {
				this.engine.retractObject(itr.next());
			} catch (Exception e) {
				servletContext.log("Error closing ServletEngineContext", e);
			}
		}
		this.ruleService.queueEngine(this.applicationName, this.version, this.engine);
		this.engine = null;
	}

	public void executeRules() {
		engine.fire();
	}

	public String getApplicationName() {
		return this.applicationName;
	}

	public List<?> getObjects() {
		return engine.getObjects();
	}

	public void modifyObject(Object data) throws AssertException,
			RetractException {
		try {
			engine.modifyObject(data);
		} catch (AssertException e) {
			servletContext.log("ServletEngineContext error modifying Object:",e);
			throw e;
		} catch (RetractException e) {
			servletContext.log("ServletEngineContext error modifying Object:",e);
			throw e;
		}
	}

	public void modifyObjects(List<?> data) throws AssertException,
			RetractException {
		Iterator<?> itr = data.iterator();
		while (itr.hasNext()) {
			try {
				engine.modifyObject(itr.next());
			} catch (AssertException e) {
				servletContext.log("ServletEngineContext error modifying Object:",e);
				throw e;
			} catch (RetractException e) {
				servletContext.log("ServletEngineContext error modifying Object:",e);
				throw e;
			}
		}
	}

	public void removeObject(Object data) throws RetractException {
		try {
			engine.retractObject(data);
		} catch (RetractException e) {
			servletContext.log("ServletEngineContext error removing Object:",e);
			throw e;
		}
	}

	public void removeObjects(List<?> data) throws RetractException {
		Iterator<?> itr = data.iterator();
		while (itr.hasNext()) {
			try {
				engine.retractObject(itr.next());
			} catch (RetractException e) {
				servletContext.log("ServletEngineContext error removing Object:",e);
				throw e;
			}
		}
	}

	public void reset() {
		engine.resetObjects();
	}

}
