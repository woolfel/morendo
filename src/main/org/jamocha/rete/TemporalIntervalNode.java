package org.jamocha.rete;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.util.NodeUtils;

/**
 * Temporal Interval Node is used for temporal patterns that use a set
 * interval. It is different than TemporalEqNode, which propogates
 * partial matches down the network immediately.
 * 
 * As the name suggests, the node will collect the partial matches for
 * a set interval. The node does this lazily and does not use a timer
 * or thread to trigger propogation.
 * @author woolfel
 *
 */
public class TemporalIntervalNode extends AbstractTemporalNode {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int interval = 0;
    private long lastTime = 0;
    private long nextTime = 0;
   	private Map<Object, Object> partialMatches = null;
    private Function function = null;
    private BigDecimal count = new BigDecimal(0);
    private ValueParam p1 = new ValueParam();
    private Parameter[] params = null;
    
    @SuppressWarnings("unchecked")
	public TemporalIntervalNode(int id, Rete engine) {
        super(id);
        partialMatches = (Map<Object, Object>) engine.newLinkedHashmap(String.valueOf(getNodeId()));
        this.lastTime = System.currentTimeMillis();
        this.nextTime = lastTime + interval;
    }

	@SuppressWarnings("unchecked")
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
            throws AssertException {
        long time = getRightTime();
        Map<Index, Index> leftmem = (Map<Index, Index>) mem.getBetaLeftMemory(this);
        leftmem.put(linx, linx);
        EqHashIndex inx = new EqHashIndex(NodeUtils.getLeftValues(this.binds,linx.getFacts()));
        TemporalHashedAlphaMem rightmem = (TemporalHashedAlphaMem) mem
                .getBetaRightMemory(this);
        Iterator<?> itr = rightmem.iterator(inx);
        if (itr != null) {
            while (itr.hasNext()) {
                Fact vl = (Fact) itr.next();
                if (vl != null) {
                    if (vl.timeStamp() > time) {
                    	Index newindx = linx.add(vl);
                        this.partialMatches.put(newindx,newindx);
                    } else {
                        removeFromPartialMatches(vl);
                        rightmem.removePartialMatch(inx, vl);
                    }
                }
            }
        }
        // the node should only propogate if it has reached the interval
        this.propogateAssert(engine,mem);
    }

	public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
            throws AssertException {
        long time = getLeftTime();
        TemporalHashedAlphaMem rightmem = (TemporalHashedAlphaMem) mem.getBetaRightMemory(this);
        EqHashIndex inx = new EqHashIndex(NodeUtils.getRightValues(this.binds,
                rfact));
        rightmem.addPartialMatch(inx, rfact, engine);
        // now that we've added the facts to the list, we
        // proceed with evaluating the fact
        Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
        // since there may be key collisions, we iterate over the
        // values of the HashMap. If we used keySet to iterate,
        // we could encounter a ClassCastException in the case of
        // key collision.
        Iterator<?> itr = leftmem.values().iterator();
        while (itr.hasNext()) {
            Index linx = (Index) itr.next();
            if (this.evaluate(linx.getFacts(), rfact, time)) {
                // now we propogate
            	Index newindx = linx.add(rfact);
                this.partialMatches.put(newindx,newindx);
            } else {
                removeFromPartialMatches(linx);
            	leftmem.remove(linx);
            }
        }
        this.propogateAssert(engine,mem);
    }

    /**
     * retract will evaluate and propogate retract immediately
     */
	public void retractLeft(Index linx, Rete engine, WorkingMemory mem)
            throws RetractException {
        Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
        leftmem.remove(linx);
        EqHashIndex eqinx = new EqHashIndex(NodeUtils.getLeftValues(this.binds,linx.getFacts()));
        TemporalHashedAlphaMem rightmem = (TemporalHashedAlphaMem) mem
                .getBetaRightMemory(this);

        // now we propogate the retract. To do that, we have
        // merge each item in the list with the Fact array
        // and call retract in the successor nodes
        Iterator<?> itr = rightmem.iterator(eqinx);
        if (itr != null) {
            while (itr.hasNext()) {
                propagateRetract(linx.add((Fact) itr.next()), engine, mem);
            }
        }
    }

    /**
     * retract will evaluate and propogate retract immediately
     */
	public void retractRight(Fact rfact, Rete engine, WorkingMemory mem)
            throws RetractException {
        long time = getLeftTime();
        EqHashIndex inx = new EqHashIndex(NodeUtils.getRightValues(this.binds,rfact));
        TemporalHashedAlphaMem rightmem = (TemporalHashedAlphaMem) mem
                .getBetaRightMemory(this);
        // first we remove the fact from the right
        rightmem.removePartialMatch(inx, rfact);
        // now we see the left memory matched and remove it also
        Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
        Iterator<?> itr = leftmem.values().iterator();
        while (itr.hasNext()) {
            Index linx = (Index) itr.next();
            if (this.evaluate(linx.getFacts(), rfact,time)) {
                propagateRetract(linx.add(rfact), engine, mem);
            }
        }
    }

    public boolean evaluate(Fact[] leftlist, Fact right, Rete engine){
        boolean eval = true;
        // we iterate over the binds and evaluate the facts
        for (int idx=0; idx < this.binds.length; idx++){
            Binding bnd = binds[idx];
            if (bnd instanceof Binding2) {
                eval = ((Binding2)bnd).evaluate(leftlist,right,engine);
            } else {
                eval = bnd.evaluate(leftlist, right);
            }
            if (!eval) {
                break;
            }
        }
        return eval;
    }
     
	protected void propogateAssert(Rete engine, WorkingMemory mem) 
    throws AssertException {
        long now = System.currentTimeMillis();
        // if we have partial matches and the current time is greater than
        // the last time + interval
        if (now > nextTime) {
        	List<?> proplist = new ArrayList<Object>();
        	// if the function is not null, we do additional filter
        	if (this.function != null) {
        		((ValueParam)params[1]).setValue(new ArrayList<Object>(this.partialMatches.values()));
        		ReturnVector rv = this.function.executeFunction(engine, params);
        		proplist = (List<?>)rv.firstReturnValue().getValue();
        	}
        	if (proplist.size() > 0) {
        		for (int idx=0; idx < proplist.size(); idx++) {
            		this.propagateAssert((Index)proplist.get(idx), engine, mem);
        		}
        	}
        	proplist.clear();
        	this.partialMatches.clear();
            // at the end we set the last time to now and set the next time
            this.lastTime = now;
            this.nextTime = now + interval;
        }
    }
    
    /**
     * method will iterate over the partial matches and remove any that are expired
     * @param index
     */
	protected void removeFromPartialMatches(Index index) {
    	Collection<?> c = this.partialMatches.values();
    	Iterator<?> itr = c.iterator();
    	while (itr.hasNext()) {
    		Index pindex = (Index)itr.next();
    		if (pindex.partialMatch(index)) {
    			this.partialMatches.remove(pindex);
    		}
    	}
    }
    
	protected void removeFromPartialMatches(Fact fact) {
    	Collection<?> c = this.partialMatches.values();
    	Iterator<?> itr = c.iterator();
    	while (itr.hasNext()) {
    		Index pindex = (Index)itr.next();
    		if (pindex.partialMatch(fact)) {
    			this.partialMatches.remove(pindex);
    		}
    	}
    }
    
    public String toPPString() {
        StringBuffer buf = new StringBuffer();
        buf.append("TemporalIntervalNode-" + this.nodeID + "> ");
        buf.append("interval " + this.interval/1000 + " s, ");
        buf.append("left=" + this.leftElapsedTime/1000 + " s, right=" + 
                this.rightElapsedTime/1000 + " s - ");
        for (int idx = 0; idx < this.binds.length; idx++) {
            if (idx > 0) {
                buf.append(" && ");
            }
            if (this.binds[idx] != null) {
                buf.append(this.binds[idx].toPPString());
            }
        }
        return buf.toString();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("TemporalIntervalNode-" + this.nodeID + "> ");
        buf.append("interval " + this.interval + " ms, ");
        buf.append("left=" + this.leftElapsedTime + " ms, right=" + this.rightElapsedTime + " ms - ");
        for (int idx = 0; idx < this.binds.length; idx++) {
            if (idx > 0) {
                buf.append(" && ");
            }
            if (this.binds[idx] != null) {
                buf.append(this.binds[idx].toPPString());
            }
        }
        return buf.toString();
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
		this.params = new Parameter[2];
		ValueParam p2 = new ValueParam();
		params[0] = p1;
		params[1] = p2;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

	public BigDecimal getCount() {
		return count;
	}

	public void setCount(BigDecimal count) {
		this.count = count;
		this.p1.setValue(this.count);
	}
}
