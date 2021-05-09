package org.jamocha.rete;

import java.util.List;

import org.jamocha.messagerouter.MessageRouter;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * Interface defining Rule engine, with all the methods common to interpreted
 * and statically compiled versions.
 * 
 * @author Peter Lin
 *
 */
@SuppressWarnings("rawtypes")
public interface Engine {
	ActivationList getActivationList();
	Agenda getAgenda();
	Object getBinding(String name);
	Object getDefglobalValue(String name);
	MessageRouter getMessageRouter();
	int getRulesFiredCount();
	List getRulesFired();
	Strategy getStrategy();
	WorkingMemory getWorkingMemory();
	
	long nextFactId();
	
	void assertObject(Object value) throws AssertException;
	void assertObjects(List values) throws AssertException;
	void retractObject(Object value) throws RetractException;
	void retractObjects(List values) throws RetractException;
	void declareObject(Class obj);
	
	void resetAll();
	void writeMessage(String msg);
}
