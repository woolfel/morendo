package org.jamocha.rete.util;

import java.util.Comparator;

import org.jamocha.rete.BaseNode;

public class NodeComparator implements Comparator<Object> {

    public NodeComparator() {
    }

    public int compare(Object left, Object right) {
        if (left instanceof BaseNode && right instanceof BaseNode) {
            BaseNode lnode = (BaseNode)left;
            BaseNode rnode = (BaseNode)right;
            if (lnode.getNodeId() > rnode.getNodeId()) {
                return 1;
            }
            return -1;
        } else {
            return 0;
        }
    }

}
