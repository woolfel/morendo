package org.jamocha.rete.sc;

import org.jamocha.rete.Engine;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * Interface defines a statically compiled 2-input node. Unlike the
 * interpreted version, one or more classes will implement the
 * interface.
 * 
 * @author Peter Lin
 *
 */
public interface TwoInputNode {
	public static final int EQUAL_JOIN = 100;
	public static final int EXISTS_EQUAL_JOIN = 101;
	public static final int NOT_EQUAL_JOIN = 102;
	public static final int TEST_JOIN = 103;
	public static final int NOTEQUAL_JOIN = 104;
	public static final int EXISTS_NOTEQUAL_JOIN = 105;
	public static final int NOT_NOTEQUAL_JOIN = 106;
	
	void assertObject(OOFact value, Engine engine, WorkingMemory mem) throws AssertException;
	void retractObject(OOFact value, Engine engine, WorkingMemory mem) throws RetractException;
	void assertList(OOIndex index, Engine engine, WorkingMemory mem) throws AssertException;
	void retractList(OOIndex index, Engine engine, WorkingMemory mem) throws RetractException;
	void addOneInputNode(OneInputNode node);
	void addTwoInputNode(TwoInputNode node);
	int getNodeType();
}
