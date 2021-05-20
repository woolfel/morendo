package org.jamocha.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

public class EngineContextImpl implements EngineContext {

	private Logger log = LogFactory.createLogger(EngineContextImpl.class);
	private RuleServiceImpl ruleService = null;
	private org.jamocha.rete.Rete engine = null;
	private String applicationName = null;
	private String version = null;
	private long startTime = 0;
	private long endTime = 0;
	@SuppressWarnings("rawtypes")
	private List objectList = new ArrayList();
	
	public EngineContextImpl(RuleServiceImpl service, org.jamocha.rete.Rete engine, String name, String version) {
		this.ruleService = service;
		this.engine = engine;
		this.applicationName = name;
		this.version = version;
		if (startTime == 0) {
			startTime = System.currentTimeMillis();
		}
	}
	
	public Rete getRuleEngine() {
		return this.engine;
	}

	public void assertObject(Object data, boolean isStatic, boolean isShadowed) throws AssertException {
		try {
			engine.assertObject(data, null, isStatic, isShadowed);
		} catch (AssertException e) {
			log.debug(e);
			throw e;
		}
	}

	@SuppressWarnings("rawtypes")
	public void asssertObjects(List data, boolean isStatic, boolean isShadowed) throws AssertException {
		Iterator itr = data.iterator();
		while (itr.hasNext()) {
			try {
				engine.assertObject(itr.next(), null, isStatic, isShadowed);
			} catch (AssertException e) {
				log.debug(e);
				throw e;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void close() {
		this.endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		int count = engine.getRulesFiredCount();
		this.ruleService.updateStatistics(elapsedTime, count);
		Iterator itr = objectList.iterator();
		while (itr.hasNext()) {
			try {
				this.engine.retractObject(itr.next());
			} catch (Exception e) {
				log.debug(e);
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

	@SuppressWarnings("rawtypes")
	public List getObjects() {
		return engine.getObjects();
	}

	public void modifyObject(Object data) throws AssertException, RetractException {
		try {
			engine.modifyObject(data);
		} catch (AssertException e) {
			log.debug(e);
			throw e;
		} catch (RetractException e) {
			log.debug(e);
			throw e;
		}
	}

	@SuppressWarnings("rawtypes")
	public void modifyObjects(List data) throws AssertException, RetractException {
		Iterator itr = data.iterator();
		while (itr.hasNext()) {
			try {
				engine.modifyObject(itr.next());
			} catch (AssertException e) {
				log.debug(e);
				throw e;
			} catch (RetractException e) {
				log.debug(e);
				throw e;
			}
		}
	}

	public void removeObject(Object data) throws RetractException {
		try {
			engine.retractObject(data);
		} catch (RetractException e) {
			log.debug(e);
			throw e;
		}
	}

	@SuppressWarnings("rawtypes")
	public void removeObjects(List data) throws RetractException {
		Iterator itr = data.iterator();
		while (itr.hasNext()) {
			try {
				engine.retractObject(itr.next());
			} catch (RetractException e) {
				log.debug(e);
				throw e;
			}
		}
	}

	public void reset() {
		engine.resetObjects();
	}
}
