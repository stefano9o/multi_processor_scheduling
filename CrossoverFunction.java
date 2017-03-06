
import java.util.*;

public class CrossoverFunction{
	private static Random random = new Random(System.currentTimeMillis());

	private Population oldPopulation;
	private Population newPopulation;
	private FitnessFunction fitnessFunction;
    	private boolean adaptive;
	private double probability;
	
	public CrossoverFunction(FitnessFunction fitnessFunction,boolean adaptive,double probability){
		this.fitnessFunction = fitnessFunction;
                this.adaptive = adaptive;
                this.probability = probability;
	}
	
	public void setOldPopulation(Population oldPopulation){
		this.oldPopulation = (Population)oldPopulation.clone();
	}
	public Population getNewPopulation(){
		return newPopulation;
	}
	public void generatePopulation(){
		newPopulation = (Population)oldPopulation.clone();
		int oldPopulationSize = oldPopulation.size();
		double fp;
		for(int i = 0; i < oldPopulationSize; i = i + 2){

			Chromosome p1 = oldPopulation.getChromosome(i);
			Chromosome p2 = oldPopulation.getChromosome(i + 1);

                        if(adaptive){
                            if(fitnessFunction.fitness(p1) > fitnessFunction.fitness(p2)){
                                    fp = fitnessFunction.fitness(p1);
                            } 
                            else{
                                    fp = fitnessFunction.fitness(p2);
                            }

                            if(fp >= oldPopulation.getFitnessAve()){
                                    probability = ((oldPopulation.getFitnessMax() - fp)/(oldPopulation.getFitnessMax() - oldPopulation.getFitnessAve()));
                            }
                            else{
                                    probability = 1;
                            }
                        }
			
			if(random.nextDouble() < probability){		
				int chromosomeSize = p1.size();
				boolean mask[] = new boolean[chromosomeSize];
				for(int j = 0; j < chromosomeSize; ++j){
					mask[j] = random.nextBoolean();
				}
				
				Chromosome o1 = ox(p1,p2,mask,true);
				Chromosome o2 = ox(p1,p2,mask,false);
			
				PriorityQueue<Chromosome> nextGeneration = new PriorityQueue<Chromosome>(4,new ChromosomeComparator(fitnessFunction));
	//			nextGeneration.add(p1);
	//			nextGeneration.add(p1);
				nextGeneration.add(o1);
				nextGeneration.add(o2);
				
				Chromosome s1 = nextGeneration.poll();
				Chromosome s2 = nextGeneration.poll();		
					
				newPopulation.setChromosome(i,s1);
				newPopulation.setChromosome(i + 1,s2);			
			}
		}
		newPopulation.evaluate();
	}
		
	private Chromosome ox(Chromosome p1, Chromosome p2, boolean[] mask, boolean left){
		Chromosome retval = null;
		int chromosomeSize = p1.size();
		LinkedList<Integer> staticJobs = new LinkedList<Integer>();
		LinkedList<Integer> nonStaticJobs = new LinkedList<Integer>();
		
		if(left){
			retval = (Chromosome)p1.clone();
		}
		else{
			retval = (Chromosome)p2.clone();
		}
		//si aggiungono ad una coda i job statici del padre1/padre2
		for(int i = 0; i < chromosomeSize; ++i){
			if(mask[i] == true){
				if(left){
					staticJobs.add(p1.getGene(i));	
				}
				else{
					staticJobs.add(p2.getGene(i));
				}		
			}
		}
		//si rimuovono i job statici del padre1/padre2 dal padre2/padre
		int index;
		for(int i = 0; i < chromosomeSize; ++i){
			if(!staticJobs.isEmpty()){
				if(left)	index = staticJobs.indexOf(p2.getGene(i));	//ritorna -1 se non vi è occorrenza
				else		index = staticJobs.indexOf(p1.getGene(i));	//ritorna -1 se non vi è occorrenza
				
				if(index >= 0){
					staticJobs.remove(index);
				}	
				else{
					if(left)	nonStaticJobs.add(p2.getGene(i));
					else		nonStaticJobs.add(p1.getGene(i));
				}			
			}
			else{
				if(left)	nonStaticJobs.add(p2.getGene(i));
				else		nonStaticJobs.add(p1.getGene(i));			
			}
		}

		//si inseriscono i job nonstatici nel padre1/padre2 mantenendo l'odine del padre2/padre1
		for(int i = 0; i < chromosomeSize; ++i){
			if(nonStaticJobs.isEmpty()){
				break;
			}
			if(mask[i] == false){
				retval.setGene(i,nonStaticJobs.remove());
			}
		}		
		return retval;		
	}
}
