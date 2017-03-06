
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IteratedLocalSearch {
    private Job[] parameterJobs;
    private Chromosome initialSolution;
    private Chromosome solution;
    private int m;
    private FitnessFunction fitnessFunction;
    private int strength;
    private int numIter;

    public IteratedLocalSearch(int m, Job[] parameterJobs, FitnessFunction fitnessFunction,int strength,int numIter) {
        this.parameterJobs = parameterJobs;
        this.m = m;
        this.fitnessFunction = fitnessFunction;
        this.strength = strength;
        this.numIter = numIter;
    }

    public void setInitialSolution(Chromosome initialSolution) {
        this.initialSolution = initialSolution;
    }

    public Chromosome getSolution() {
        return solution;
    }

    public void start() {
        List[] previousSchedule = null;
        List[] currentSchedule = null;
        List[] temp = null;

        List[] initialSchedule = createSchedule(initialSolution);

        currentSchedule = localSearchOnSingleProcessor(initialSchedule);
        currentSchedule = localSearchBetweenProcessor(currentSchedule);

        int cont = 0;
        Chromosome priorityList = null;
        do {
            previousSchedule = currentSchedule;
            currentSchedule = new List[m];
            for (int i = 0; i < m; ++i) {
                currentSchedule[i] = new LinkedList<Integer>(previousSchedule[i]);
            }

            priorityList = createPriorityList(currentSchedule);
            priorityList = perturbPriorityList(priorityList);
            currentSchedule = createSchedule(priorityList);
            temp = localSearchOnSingleProcessor(currentSchedule);
            temp = localSearchBetweenProcessor(temp);

            currentSchedule = better(temp, previousSchedule);
            cont++;
        } while (cont < numIter);
        solution = createPriorityList(currentSchedule);
        
    }

    private Chromosome localSearch(Chromosome initial){
        int chromosomeSize = initial.size();
        
        Chromosome best = (Chromosome)initial.clone();
        int bestFit = fitnessFunction.fitness(best);
        
        Chromosome temp = null;
        int tempFit = 0;
        
        for(int i = 0;  i < chromosomeSize; ++i){
            for(int j = i + 1;  j < chromosomeSize -1; ++j){
                temp = (Chromosome) initial.clone();
                tempFit = fitnessFunction.fitness(temp);
                
                if(tempFit > bestFit){
                    best = temp;
                    bestFit = tempFit;
                }
            }               
        }
        return best;
    }
    
    private Chromosome acceptanceCriterion(Chromosome c1, Chromosome c2){
        if(fitnessFunction.fitness(c1) > fitnessFunction.fitness(c2)){
            return (Chromosome)c1.clone();
        }
        
        return (Chromosome)c2.clone();         
    }
    private List[] better(List[] a, List[] b) {
        //print(a);
        //print(b);
        if (fitnessFunction.totalTardiness(a) < fitnessFunction.totalTardiness(b)) {
            return a;
        } else {
            return b;
        }
    }

    private Chromosome createPriorityList(List[] schedule) {
        Chromosome retval = (Chromosome) initialSolution.clone();
        int chromosomeSize = initialSolution.size();

        int time[] = new int[m];
        for (int i = 0; i < m; ++i) {
            time[i] = 0;
        }
        int currentMachine = 0;
        Integer currentJob;

        for (int i = 0; i < chromosomeSize; ++i) {
            currentJob = (Integer)schedule[currentMachine].remove(0);
            if (schedule[currentMachine].isEmpty()) {
                time[currentMachine] += 100000000;
            }

            retval.setGene(i, currentJob);
            time[currentMachine] += parameterJobs[currentJob].getProcessingTime();

            for (int j = 0; j < m; ++j) {
                if (time[j] < time[currentMachine]) {
                    currentMachine = j;
                }
            }
        }
        return retval;
    }

    private List[] createSchedule(Chromosome c) {
        List[] retval = new List[m];
        for (int i = 0; i < m; ++i) {
            retval[i] = new LinkedList<Integer>();
        }

        int time[] = new int[m];

        int chromosomeSize = c.size();
        int currentMachine = 0;
        int currentJob;
        for (int i = 0; i < chromosomeSize; ++i) {
            currentJob = c.getGene(i);
            time[currentMachine] += parameterJobs[currentJob].getProcessingTime();
            retval[currentMachine].add(currentJob);
            for (int j = 0; j < m; ++j) {
                if (time[j] < time[currentMachine]) {
                    currentMachine = j;
                }
            }
        }
        return retval;
    }

    private List[] localSearchOnSingleProcessor(List[] schedule) {
        List[] retval = new List[m];
        for (int i = 0; i < m; ++i) {
            retval[i] = SPTTP(schedule[i]);
        }
        return retval;
    }

    //effettua ricerca locale con 2-swao per il problema a singolo processore
    private List<Integer> SPTTP(List<Integer> scheduleOnSingleProcessor) {
        boolean find = false;
        List<Integer> bestSchedule = new ArrayList<Integer>(scheduleOnSingleProcessor);
        int bestTotalTardiness = fitnessFunction.totalTardinessOnSingleMachine(bestSchedule);

        ArrayList<Integer> temp = null;
        for (int i = 0;(i < scheduleOnSingleProcessor.size() - 1) /*&& (!find)*/; ++i) {
            for (int j = i + 1;(j < scheduleOnSingleProcessor.size()) /*&& (!find)*/; ++j) {
                temp = new ArrayList<Integer>(scheduleOnSingleProcessor);
                Collections.swap(temp, i, j);

                if (fitnessFunction.totalTardinessOnSingleMachine(temp) < bestTotalTardiness) {
                    bestSchedule = temp;
                    bestTotalTardiness = fitnessFunction.totalTardinessOnSingleMachine(temp);
                    find = true;
                }
            }
        }
        return bestSchedule;
    }

    private List[] localSearchBetweenProcessor(List[] schedule) {
        boolean find = false;
        int bestTotalTardiness = fitnessFunction.totalTardiness(schedule);
        List[] bestSchedule = new List[m];
        for (int l = 0; l < m; ++l) {
            bestSchedule[l] = new LinkedList<Integer>(schedule[l]);
        }

        List<Integer> scheduleOnSingleProcessorI = null;
        List<Integer> scheduleOnSingleProcessorK = null;
        int indexProcessorI;
        int indexProcessorK;
        Integer indexJobJ;
        Integer indexJobH;
        Integer jobJ;
        Integer jobH;

        List[] temp = null;

        for (int i = 0;(i < m)/*&&(!find)*/; ++i) {
            indexProcessorI = i;
            for (int j = 0; j < schedule[i].size()/*&&(!find)*/; ++j) {
                indexJobJ = j;
                for (int k = 0; (k < m)/*&&(!find)*/ ; ++k) {
                    indexProcessorK = k;
                    if (indexProcessorK != indexProcessorI) {
                        for (int h = 0; h < (schedule[k].size())/*&&(!find)*/; ++h) {
                            indexJobH = h;

                            temp = new List[m];
                            for (int l = 0; l < m; ++l) {
                                temp[l] = new LinkedList<Integer>(schedule[l]);
                            }
                            
                            jobJ = (Integer)temp[indexProcessorI].remove(indexJobJ.intValue());
                            jobH = (Integer)temp[indexProcessorK].remove(indexJobH.intValue());
                           
                            //jobJ = temp[indexProcessorI].remove(indexJobJ);
                            //jobH = 

                            
                            
                            temp[indexProcessorI].add(indexJobJ, jobH);
                            temp[indexProcessorK].add(indexJobH, jobJ);
                            if (fitnessFunction.totalTardiness(temp) < bestTotalTardiness) {
                                bestSchedule = temp;
                                bestTotalTardiness = fitnessFunction.totalTardiness(temp);
                                find = true;
                            }
                        }
                    }
                }
            }
        }
        return bestSchedule;
    }

    private Chromosome perturbPriorityList(Chromosome c) {
        Chromosome retval = (Chromosome) c.clone();
        //int strength = parameterJobs.length / 20;
        
        for (int i = 0; i < strength; ++i) {
            retval.swap();
        }
        return retval;
    }

    private void print(List[] schedule) {
        for (int i = 0; i < m; i++) {
            //System.out.println("schedule[i].size(): " + schedule[i].size());
            for (int j = 0; j < schedule[i].size(); ++j) {
                System.out.print(schedule[i].get(j) + " ");
            }
            System.out.println();
            System.out.println(fitnessFunction.totalTardinessOnSingleMachine(new ArrayList<Integer>(schedule[i])));
        }
        System.out.println();
    }
    public void pause(int time){
        try {
                Thread.sleep(time);
        } catch (InterruptedException ex) {
                Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
