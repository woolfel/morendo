# morendo
RETE inference rule engine written in Java inspired by CLIPS and JESS. Morendo is meant for research, learning and experimentation.

## Features
Morendo uses CLIPS language and is mostly compatible with rules written for CLIPS and JESS. There are some differences to note. Morendo does not support ordered facts. Although ordered facts are useful for quick prototyping and experimentation, it can lead to bad habits. It is better to design your model and use Java objects or deftemplates. I've seen real projects use ordered facts that had to be completely rewritten.

**MOLAP** multi-dimensional OLAP cube is commonly used in financial applications. Morendo has MOLAP cubes and cube queries to make it easier to reason over financial data. There's examples in the sample folder.

**Network topology cost** calculates the the cost of a rule. The function counts the nodes for the rule to estimate a relative cost. Alpha nodes for literal constraints cost is 1.  Join nodes have a cost of 4. Rules with higher cost will use more memory. The cost function gives you more information to make design decisions on how to implement the rules.

**Java macros** provide a way to eliminate the cost of Java reflection when reasoning over POJO.

**Graph query** provides a generic way to load concept graphs and use it in rules.

Morendo provides some **temporal logic features** to make it easier to reason over temporal data and define temporal patterns. Temporal activation will check if the facts are expired before adding the activation to the agenda. If any facts are expired, the engine will retract the expired facts and not add the rule to the agenda.

**Event Driven Rules** Applications that process event streams can set the rule property no-agenda to true. This will skip the agenda and execute the action of the rules immediately.

Two features from Haley enterprise **ONLY and MULTIPLE** adds some second order logic support.

## Acknowledgements
Morendo wouldn't be possible without the work by Dr. Forgy, Paul Haley, Gary Riley and Ernest Friedman-Hill. Even though morendo is a clean room implementation of RETE, the lessons learned from OPS5, CLIPS, JESS and half dozen other RETE rule engines influenced the implementation. The rule engine is open source, so that anyone can learn from it.
