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
package woolfel.rete;

import org.jamocha.rete.HashedEqBNode;
import org.jamocha.rete.Binding;
import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * Tests for binding class. The test will create some bindings
 * and create betaNodes.
 */
public class BindingTest extends TestCase {

	/**
	 * 
	 */
	public BindingTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public BindingTest(String arg0) {
		super(arg0);
	}

    public void setUp(){
        System.out.println("this test does not do any setup");
    }
    
    public void tearDown(){
        System.out.println("this test does not do any teardown");
    }
    
    public void testSingleBinding(){

        Binding bn = new Binding();
        bn.setLeftRow(0);
        bn.setLeftIndex(0);
        bn.setRightIndex(0);
        
        Binding[] binds = {bn};
        HashedEqBNode btnode = new HashedEqBNode(1);
        btnode.setBindings(binds);
        
        System.out.println("betaNode::" + btnode.toPPString());
        assertNotNull(btnode.toPPString());
    }
    
    public void testTwoBinding(){

        Binding bn = new Binding();
        bn.setLeftRow(0);
        bn.setLeftIndex(0);
        bn.setRightIndex(0);
        
        Binding bn2 = new Binding();
        bn2.setLeftRow(0);
        bn2.setLeftIndex(2);
        bn2.setRightIndex(2);
        
        Binding[] binds = {bn,bn2};
        HashedEqBNode btnode = new HashedEqBNode(1);
        btnode.setBindings(binds);
        
        System.out.println("betaNode::" + btnode.toPPString());
        assertNotNull(btnode.toPPString());
    }

    public void testThreeBinding(){

        Binding bn = new Binding();
        bn.setLeftRow(0);
        bn.setLeftIndex(0);
        bn.setRightIndex(0);
        
        Binding bn2 = new Binding();
        bn2.setLeftRow(0);
        bn2.setLeftIndex(2);
        bn2.setRightIndex(2);
        
        Binding bn3 = new Binding();
        bn3.setLeftRow(1);
        bn3.setLeftIndex(0);
        bn3.setRightIndex(0);

        Binding[] binds = {bn,bn2,bn3};
        HashedEqBNode btnode = new HashedEqBNode(1);
        btnode.setBindings(binds);
        
        System.out.println("betaNode::" + btnode.toPPString());
        assertNotNull(btnode.toPPString());
    }
    
    public void testThreeBinding2(){

        Binding bn = new Binding();
        bn.setLeftRow(0);
        bn.setLeftIndex(0);
        bn.setRightIndex(0);
        
        Binding bn2 = new Binding();
        bn2.setLeftRow(0);
        bn2.setLeftIndex(2);
        bn2.setRightIndex(2);
        
        Binding bn3 = new Binding();
        bn3.setLeftRow(0);
        bn3.setLeftIndex(0);
        bn3.setRightIndex(0);

        Binding[] binds = {bn,bn2,bn3};
        HashedEqBNode btnode = new HashedEqBNode(1);
        btnode.setBindings(binds);
        
        System.out.println("betaNode::" + btnode.toPPString());
        assertNotNull(btnode.toPPString());
    }
}