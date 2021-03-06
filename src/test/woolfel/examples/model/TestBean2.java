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
package woolfel.examples.model;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * @author Peter Lin
 *
 * Alternate version of TestBean that does implement add/remove
 * PropertyChangeListener. It doesn't implement the necessary
 * calls in the set methods to notify the rule engine
 */
public class TestBean2 {

    protected String attr1 = null;
    protected int attr2;
    protected short attr3;
    protected long attr4;
    protected float attr5;
    protected double attr6;
    
    @SuppressWarnings("rawtypes")
	protected ArrayList listeners = new ArrayList();
    
	/**
	 * 
	 */
	public TestBean2() {
		super();
	}

    public void setAttr1(String val){
        this.attr1 = val;
    }
    
    public String getAttr1(){
        return this.attr1;
    }
    
    public void setAttr2(int val){
        this.attr2 = val;
    }
    
    public int getAttr2(){
        return this.attr2;
    }
    
    public void setAttr3(short val){
        this.attr3 = val;
    }
    
    public short getAttr3(){
        return this.attr3;
    }
    
    public void setAttr4(long val){
        this.attr4 = val;
    }
    
    public long getAttr4(){
        return this.attr4;
    }
    
    public void setAttr5(float val){
        this.attr5 = val;
    }
    
    public float getAttr5(){
        return this.attr5;
    }
    
    public void setAttr6(double val){
        this.attr6 = val;
    }
    
    public double getAttr6(){
        return this.attr6;
    }
    
    @SuppressWarnings("unchecked")
	public void addPropertyChangeListener(PropertyChangeListener listener){
        this.listeners.add(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener){
        this.listeners.remove(listener);
    }
}
