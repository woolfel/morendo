package org.jamocha.rete.sc;

import org.jamocha.rete.Engine;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * Interface defining a statically compiled 1-input node. Unlike the
 * interpreted version, there will be one or more classes that
 * implement the interface.
 * 
 * @author Peter Lin
 *
 */
public interface OneInputNode {
	/**
	 * Simple conditions that match for a literal
	 * value. For example:
	 * (name "bob")
	 */
	public static final int LITERAL_CONSTRAINT = 1;
	/**
	 * Terminal nodes indicate a rule was matched
	 * fully and is a candidate for firing.
	 */
	public static final int TERMINAL = 2;
	/**
	 * Simple conditions that use a function to test for
	 * a defined constraint. For example: 
	 * (name ?name&:(index-of ?name "bob") )
	 */
	public static final int FUNCTION_TEST = 3;
	public static final int OBJECT_TYPE = 4;
	
	void assertObject(OOFact value, Engine engine, WorkingMemory mem) throws AssertException;
	void retractObject(OOFact value, Engine engine, WorkingMemory mem) throws RetractException;
	void addOneInputNode(OneInputNode node);
	void addTwoInputNode(TwoInputNode node);
	int getNodeType();
}
