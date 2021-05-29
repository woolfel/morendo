/*
 * Copyright 2002-2007 Peter Lin
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
package org.jamocha.rete.util;

import org.jamocha.rete.BindValue;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Fact;

public class NodeUtils {
    /**
     * get the values from the left side for nodes that do not have
     * joins with !=
     * @param facts
     * @return
     */
    public static Object[] getLeftValues(Binding[] binds, Fact[] facts) {
        Object[] vals = new Object[binds.length];
        for (int idx = 0; idx < binds.length; idx++) {
            vals[idx] = facts[binds[idx].getLeftRow()].getSlotValue(binds[idx]
                    .getLeftIndex());
        }
        return vals;
    }

    /**
     * convienance method for getting the values based on the bindings
     * for nodes that do not have !=
     * @param ft
     * @return
     */
    public static Object[] getRightValues(Binding[] binds, Fact ft) {
        Object[] vals = new Object[binds.length];
        for (int idx = 0; idx < binds.length; idx++) {
            vals[idx] = ft.getSlotValue(binds[idx].getRightIndex());
        }
        return vals;
    }

    /**
     * convienance method for getting the values based on the
     * bindings
     * @param ft
     * @return
     */
    public static BindValue[] getRightBindValues(Binding[] binds, Fact ft) {
        BindValue[] vals = new BindValue[binds.length];
        for (int idx=0; idx < binds.length; idx++) {
            vals[idx] = new BindValue(
                ft.getSlotValue(binds[idx].getRightIndex()),
                binds[idx].negated());
        }
        return vals;
    }
    
    /**
     * get the values from the left side
     * @param facts
     * @return
     */
    public static BindValue[] getLeftBindValues(Binding[] binds, Fact[] facts) {
        BindValue[] vals = new BindValue[binds.length];
        for (int idx=0; idx < binds.length; idx++) {
            vals[idx] = new BindValue(
                facts[binds[idx].getLeftRow()].
                getSlotValue(binds[idx].getLeftIndex()),
                binds[idx].negated());
        }
        return vals;
    }
}
