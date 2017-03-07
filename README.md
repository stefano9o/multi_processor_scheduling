# Multi-Processor Scheduling
## Final project for the exam "Methods and model of decision support" prepared in 7/2014

### Problem Description
This program uses a Metaheuristic which is a mix of "Population-based" and "Local search" to solve the problem of minimising the total tardiness on identical parallel machines. In particular, it has been used a "Genetic Algorithm" with an "Iterated Local Search".

### Pre-requirement
it is necessary that a JDK was installed on the machine

### Parameter Description
1. instance name
2. # of the run (just to discriminate the output file name)
3. total execution time [s]
4. flag which indicates whether using an adaptive method or not
5. # of the population for the Genetic Algorithm
6. probability of crossover for the Genetic Algorithm
7. probability of mutation for the Genetic Algorithm
8. strength for the Iterated local search (the perturbation strength has to be sufficient to lead the trajectory to a different attraction basin leading to a different local optimum)
9. # iteration for the Iterated local search
10. flag which indicates whether using a verbose execution

### How to
1. Compile all the source file: `javac *.java.`
2. Execute the scheduler program with one of the instance inside the "istanze" folder `Java Scheduler "istanze/20_02_02_02_001.dat" 0 1150 10 false 0.7 0.001 2 100 false` (after a parameter tuning).
3. the solution is saved in "soluzioni/20_02_02_02_001-n.dat", where n is the second parameter which has to be passed when the program is run.

### Optimus 
inside the "ottimi" folder it can be found the real optimal solution of the related instance.
