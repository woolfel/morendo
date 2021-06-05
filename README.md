# morendo
RETE inference rule engine written in Java inspired by CLIPS and JESS. Morendo is meant for research, learning and experimentation.

## Features
Morendo uses CLIPS language and is mostly compatible with rules written for CLIPS and JESS. There are some differences to note. Morendo does not support ordered facts. Although ordered facts are useful for quick prototyping and experimentation, it can lead to bad habits. It is better to design your model and use Java objects or deftemplates. I've seen real projects use ordered facts that had to be completely rewritten.

## Acknowledgements
Morendo wouldn't be possible without the work by Dr. Forgy, Paul Haley, Gary Riley and Ernest Friedman-Hill. Even though morendo is a clean room implementation of RETE, the lessons learned from OPS5, CLIPS, JESS and half dozen other RETE implementation influenced the implementation.
