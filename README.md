# bool-sat

>Assignment 2  
>CISC352: Artificial Intelligence  
>Sean Nesdoly & Mary Hoekstra  
>February 14th, 2017

This repository implements the algorithms required for solving the following three problems related to boolean satisfiability (*CNF-SAT*):
- Conversion of a formula in propositional logic to *Conjunctive Normal Form*
- *Proof by Refutation* - does a conclusion logically follow from a set of premises? This problem is solved using an implementation of the DPLL algorithm, which decides the satisfiability of a set of propositional logic formulae in CNF
- *Three-Colouring Problem* - given a set of edges in a graph, is it possible to colour the vertices such that no two vertices that are connected by an edge have the same colour? This is solved using an implementation of the DPLL algorithm

## Build Process

Choose one of the following commands to compile and run a problem:

```bash
cd path/to/bool-sat
ant -Darg=NUM // where NUM is the desired problem number to run
```

```bash
cd path/to/bool-sat
ant compile
java -jar build/jar/bool-sat.jar NUM // where NUM is the desired problem number to run
```

## Conversion to CNF
**TODO**: README for running solution

## Proof by Refutation
**TODO**: README for running solution

## Three-Colouring Problem
**TODO**: README for running solution
