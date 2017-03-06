
import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Scheduler{
	public static void main(String[] argv){
		Random random = new Random(System.currentTimeMillis());
		
		String inputFilename = argv[0];
        int nRun = Integer.parseInt(argv[1]);
		int dimPop = Integer.parseInt(argv[2]);
		int executionTime = Integer.parseInt(argv[3]);
        boolean adaptive = Boolean.parseBoolean(argv[4]);
		double crossoverProbability = Double.parseDouble(argv[5]);
		double mutationProbabilityDef = Double.parseDouble(argv[6]);
        int strength = Integer.parseInt(argv[7]);
        int numIter = Integer.parseInt(argv[8]);
        boolean print = Boolean.parseBoolean(argv[9]);
                
		//System.out.println(adaptive);
		Parser par = new Parser(inputFilename);
		Job[] jobs = par.parse();
		
		int n = par.numberJobs();
		int m = par.numberMachines();
		
		FitnessFunction fitnessFunction = new FitnessFunction(jobs,m);
		IteratedLocalSearch ils = new IteratedLocalSearch(m,jobs,fitnessFunction,strength,numIter);
			
		long start = System.currentTimeMillis();
		long finish;
		
		Population initialPopulation = new Population(dimPop,n,mutationProbabilityDef,fitnessFunction,adaptive);
//		System.out.println(initialPopulation);
//		pause(1000);
				
		SelectionFunction selectionFunction = new SelectionFunction(fitnessFunction);
		CrossoverFunction crossoverFunction = new CrossoverFunction(fitnessFunction,adaptive,crossoverProbability);

		Population temp = (Population)initialPopulation.clone();
		//LinkedList<Chromosome> bestChromosomes = new LinkedList<Chromosome>();
		//LinkedList<Integer> bestTotalTardiness = new LinkedList<Integer>();
//		System.out.println(temp);
//		pause(1000);
		
		Chromosome best = null;
		int bestTotalTardiness = 0;
		long time = 0;
                int step = 0;
		do{
			selectionFunction.setOldPopulation(temp);
			selectionFunction.generatePopulation();
			temp = (Population)selectionFunction.getNewPopulation().clone();
//			System.out.println(temp);
//			pause(1000);
			
			crossoverFunction.setOldPopulation(temp);
			crossoverFunction.generatePopulation();
			temp = (Population)crossoverFunction.getNewPopulation().clone();
                        if(print){
                            System.out.println(temp);
                        }
			
//			pause(1000);
			
			temp.mute();
			
			if(best == null){
				best = temp.bestChromosome();
				bestTotalTardiness = fitnessFunction.totalTardiness(best);
				time = System.currentTimeMillis() - start;
			}
			else if(fitnessFunction.totalTardiness(temp.bestChromosome()) < bestTotalTardiness){
				best = temp.bestChromosome();
				bestTotalTardiness = fitnessFunction.totalTardiness(best);
				time = System.currentTimeMillis() - start;				
			}
				
			for(int i = 0; i < temp.size(); ++i){
				temp.getChromosome(i);
				ils.setInitialSolution(temp.getChromosome(i));
                                //ils.setInitialSolution(temp.bestChromosome());
				ils.start();
				if(fitnessFunction.totalTardiness(ils.getSolution()) < bestTotalTardiness){
					best = ils.getSolution();
					bestTotalTardiness = fitnessFunction.totalTardiness(ils.getSolution());
					time = System.currentTimeMillis() - start;
					
//					System.out.print(bestTotalTardiness + " ");
				}
			}
//			System.out.println();
			//bestChromosomes.add(temp.bestChromosome());
			//bestTotalTardiness.add(fitnessFunction.totalTardiness(temp.bestChromosome()));
//			System.out.println(fitnessFunction.totalTardiness(temp.bestChromosome()));			
			finish = System.currentTimeMillis();
                        ++step;
		}while((finish - start) <= executionTime);
		
		System.out.println(argv[0] + "-" + argv[1] + ": " + bestTotalTardiness + " trovata in " + time + " e " + step + " step");

		FileWriter fw = null;
		String outputFilename = inputFilename.replace("istanze", "soluzioni");
                outputFilename = outputFilename.replace(".dat", "-" + nRun + ".dat");
		
		try{
			fw = new FileWriter(outputFilename);			
		
			fw.write(String.valueOf(bestTotalTardiness) + "\n" + time + "\n" + best);
		
			fw.flush();
			fw.close();	
		}
                catch (IOException ex) {
                    Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
                }

	}
	
	public static void pause(int time){
            try {
                Thread.sleep(time);
            } catch (InterruptedException ex) {
                Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
}
