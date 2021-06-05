# morendo
RETE inference rule engine written in Java inspired by CLIPS and JESS. Morendo is meant for research, learning and experimentation.

## Features
Morendo uses CLIPS language and is mostly compatible with rules written for CLIPS and JESS. There are some differences to note. Morendo does not support ordered facts. Although ordered facts are useful for quick prototyping and experimentation, it can lead to bad habits. It is better to design your model and use Java objects or deftemplates. I've seen real projects use ordered facts that had to be completely rewritten.

MOLAP cubes is commonly used in financial applications. Morendo has MOLAP cubes and cube queries to make it easier to reason over financial data.

Network topology cost calculates the the cost of a rule and gives you an idea of how different implementations of rules change the cost.

Java macros provide a way to eliminate the cost of Java reflection when reasoning over POJO.

Graph query provides a generic way to load concept graphs and use it in rules.

Morendo provides some temporal logic features to make it easier to reason over temporal data and define temporal patterns.

Two features from Haley enterprise ONLY and MULTIPLE adds some second order logic support.

## Acknowledgements
Morendo wouldn't be possible without the work by Dr. Forgy, Paul Haley, Gary Riley and Ernest Friedman-Hill. Even though morendo is a clean room implementation of RETE, the lessons learned from OPS5, CLIPS, JESS and half dozen other RETE rule engines influenced the implementation. The rule engine is open source, so that anyone can learn from it.
