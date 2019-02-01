# Birds-of-a-feather
Research project, a simulation tool to numerically replicate a theoretical results on random graphs.

In the article Birds of a feather or opposites attract - effects in network modelling, Mia Deijfen and Robert Fitzner study properties of some standard network models when the population is split into two different types and the connection pattern between the types is varied. The studied models are generalizations of the Erdos-Renyi graph, the configuration model and a preferential attachment graph.

For the Erdos-Renyi graph and the configuration model, the focus is on the component structure. We derive expressions for the critical parameter indicating when there is a giant component in the graph, and study the size of the largest component by aid of simulations. When the expected degrees in the graph are fixed and the connections are shifted so that more edges connect vertices of different types, we find that the critical parameter decreases. Creating a giant component is hence easier in a population where vertices tend to connect to their own type. The size of the largest component in the supercritical regime can be both increasing and decreasing as the connections change, depending on the combination of types. 

For the preferential attachment model, we analyze the degree distributions of the two types and derive explicit expressions for the degree exponents. The exponents are confirmed by simulations that also illustrate other properties of the degree structure.

This repository contains the source code that has been used sample to the random graph. The source code is written in Java. Each graph model is included in a separate package, which contains a file starter. This file contains code that starts the sampling of the example shown in the article.
