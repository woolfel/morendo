package org.jamocha.rete;

import java.util.Iterator;
import java.util.Map;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.util.NodeUtils;

public class TemporalEqNode extends AbstractTemporalNode {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TemporalEqNode(int id) {
        super(id);
    }

    public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
            throws AssertException {
        long time = getRightTime();
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        leftmem.put(linx, linx);
        EqHashIndex inx = new EqHashIndex(NodeUtils.getLeftValues(this.binds,linx.getFacts()));
        TemporalHashedAlphaMem rightmem = (TemporalHashedAlphaMem) mem
                .getBetaRightMemory(this);
        Iterator itr = rightmem.iterator(inx);
        if (itr != null) {
            try {
                while (itr.hasNext()) {
                    Fact vl = (Fact) itr.next();
                    if (vl != null) {
                        if (vl.timeStamp() > time) {
                            this.propagateAssert(linx.add(vl), engine, mem);
                        } else {
                            rightmem.removePartialMatch(inx, vl);
                            this.propagateRetract(linx.add(vl), engine, mem);
                        }
                    }
                }
            } catch (RetractException e) {
                // there shouldn't be any retract exceptions
            }
        }

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
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        // since there may be key collisions, we iterate over the
        // values of the HashMap. If we used keySet to iterate,
        // we could encounter a ClassCastException in the case of
        // key collision.
        Iterator itr = leftmem.values().iterator();
        try {
            while (itr.hasNext()) {
                Index linx = (Index) itr.next();
                if (this.evaluate(linx.getFacts(), rfact, time)) {
                    // now we propogate
                    this.propagateAssert(linx.add(rfact), engine, mem);
                } else {
                    this.propagateRetract(linx.add(rfact), engine, mem);
                }
            }
        } catch (RetractException e) {
            // we shouldn't get a retract exception. if we do, it's a bug
        }
    }

    public void retractLeft(Index linx, Rete engine, WorkingMemory mem)
            throws RetractException {
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        leftmem.remove(linx);
        EqHashIndex eqinx = new EqHashIndex(NodeUtils.getLeftValues(this.binds,linx.getFacts()));
        TemporalHashedAlphaMem rightmem = (TemporalHashedAlphaMem) mem
                .getBetaRightMemory(this);

        // now we propogate the retract. To do that, we have
        // merge each item in the list with the Fact array
        // and call retract in the successor nodes
        Iterator itr = rightmem.iterator(eqinx);
        if (itr != null) {
            while (itr.hasNext()) {
                propagateRetract(linx.add((Fact) itr.next()), engine, mem);
            }
        }
    }

    public void retractRight(Fact rfact, Rete engine, WorkingMemory mem)
            throws RetractException {
        long time = getLeftTime();
        EqHashIndex inx = new EqHashIndex(NodeUtils.getRightValues(this.binds,rfact));
        TemporalHashedAlphaMem rightmem = (TemporalHashedAlphaMem) mem
                .getBetaRightMemory(this);
        // first we remove the fact from the right
        rightmem.removePartialMatch(inx, rfact);
        // now we see the left memory matched and remove it also
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        Iterator itr = leftmem.values().iterator();
        while (itr.hasNext()) {
            Index linx = (Index) itr.next();
            if (this.evaluate(linx.getFacts(), rfact,time)) {
                propagateRetract(linx.add(rfact), engine, mem);
            }
        }
    }

    public String toPPString() {
        StringBuffer buf = new StringBuffer();
        buf.append("TemporalEqNode-" + this.nodeID + "> ");
        buf.append("left=" + this.leftElapsedTime/1000 + " s, right=" + this.rightElapsedTime/1000 + " s - ");
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

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("TemporalEqNode-" + this.nodeID + "> ");
        buf.append("left=" + this.leftElapsedTime/1000 + " s, right=" + this.rightElapsedTime/1000 + " s - ");
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

}
