# bool-sat

>Assignment 2  
>CISC352: Artificial Intelligence  
>Sean Nesdoly & Mary Hoekstra  
>February 14th, 2017

This repository implements the algorithms required for solving the following three problems related to boolean satisfiability (*CNF-SAT*):
- Conversion of a formula in propositional logic to *Conjunctive Normal Form*
- *Proof by Refutation* - does a conclusion logically follow from a set of premises? This problem is solved using an implementation of the DPLL algorithm, which decides the satisfiability of a set of propositional logic formulae in CNF
- *Three-Colouring Problem* - given a set of edges in a graph, is it possible to colour the vertices such that no two vertices that are connected by an edge have the same colour? This is solved using an implementation of the DPLL algorithm

---

### Program Input/Output

To specify program *input* to any of the problems, simply create a ***.txt** file in the `bool-sat` folder with the required input. Any filename will work, as long as it has the ***.txt** extension. If there is more than one file with a ***.txt** extension, the first file encountered will be used.

Program output is written to the file **out.txt** in the `bool-sat` folder. This file is overwritten for every run of the program. If the file is not there, it will be created.

Thus, on each run of a program, **out.txt** should be deleted. This prevents it from being used as the input file.

### Conversion to CNF

```
cd path/to/bool-sat
java -jar build/bool-sat.jar 1
```

### Proof by Refutation

```
cd path/to/bool-sat
java -jar build/bool-sat.jar 2
```

### Three-Colouring Problem

```
cd path/to/bool-sat
java -jar build/bool-sat.jar 3
```

---

### Build Process

Choose one of the following commands to compile and run a problem:

```bash
cd path/to/bool-sat
ant -Darg=NUM
```

```bash
cd path/to/bool-sat
ant compile
java -jar build/bool-sat.jar NUM
```

where NUM specifies the desired problem number to run!
