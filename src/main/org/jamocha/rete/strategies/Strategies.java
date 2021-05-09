/*
 * Copyright 2002-2008 Peter Lin
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
package org.jamocha.rete.strategies;

import java.util.HashMap;

import org.jamocha.rete.Strategy;

/**
 * Strategy is where new strategies are registered and where the functions
 * find the strategy class to set the strategy.
 * @author woolfel
 *
 */
public class Strategies {
    public static Strategy DEPTH = new DepthStrategy();
    public static Strategy BREADTH = new BreadthStrategy();
    public static Strategy RECENCY = new RecencyStrategy();
    private static HashMap registry = new HashMap();
    static {
        registry.put(DEPTH.getName(), DEPTH);
        registry.put(BREADTH.getName(), BREADTH);
        registry.put(RECENCY.getName(), RECENCY);
    };
    
    public static void register(Strategy strat) {
        if (strat != null) {
            registry.put(strat.getName(), strat);
        }
    }
    
    public static Strategy getStrategy(String key) {
        return (Strategy)registry.get(key);
    }
}
