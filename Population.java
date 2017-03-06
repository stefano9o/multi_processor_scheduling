
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Population
implements Cloneable{
	private static Random random;
	private Chromosome population[];
	private int fitness[];
	private double mutationProb[];
	private double mutationProbDef;
	private int populationSize;
	private Chromosome best;
	private int chromosomeSize;
	private FitnessFunction fitnessFunction;
	private double fitnessMax;
	private double fitnessTot;
	private double fitnessAve;
        private boolean adaptive;
	
	public Population(int populationSize,int chromosomeSize, double mutationProbDef, FitnessFunction fitnessFunction,boolean adaptive){
		random = new Random();
		this.populationSize = populationSize;
		this.mutationProbDef = mutationProbDef;
		this.fitnessFunction = fitnessFunction;
		this.chromosomeSize = chromosomeSize;
                this.adaptive = adaptive;
		
		
		population = new Chromosome[populationSize];
		fitness = new int[populationSize];
		mutationProb = new double[populationSize];
		
		for(int i = 0; i < populationSize; ++i){
			population[i] = new Chromosome(chromosomeSize);
			
		}
		
		evaluate();
	}
	
	public Chromosome getChromosome(int index){
		if(	(index >= size())||
			(index < 0) ){
				throw new IndexOutOfBoundsException("index must be from 0 to " + (size() - 1));
		}
		else{
			return population[index];
		}		
	}
	
	public void setChromosome(int index, Chromosome c){
		if(	(index >= size())||
			(index < 0) ){
				throw new IndexOutOfBoundsException("index must be from 0 to " + (size() - 1));
		}
		else{
			population[index] = c;
		}	
		
	}
	
	public void mute(){
		for(int i = 0; i < populationSize; ++i){
			if(random.nextDouble() < mutationProb[i]){
				population[i].swap(random.nextInt(chromosomeSize), random.nextInt(chromosomeSize));
			}
		}
		
		evaluate();
	}
	
	public void evaluate(){
		fitnessTot = 0;
		fitnessMax = 0;
		int indFitnessMax = -1;
		for(int i = 0; i < size(); ++i){
			fitness[i] = fitnessFunction.fitness(population[i]);
			fitnessTot += fitness[i];
			if(fitness[i] > fitnessMax){
				fitnessMax = fitness[i];
				indFitnessMax = i;
			}
		}
		fitnessAve = fitnessTot/size();
		best = population[indFitnessMax];
		
		updateMutProb();
	}
	
	public void updateMutProb(){
		double prob;
		for(int i = 0; i < size(); ++i){
                    if(adaptive){
			if(fitness[i] >= fitnessAve){
				if(fitness[i] == fitnessMax){
					prob = mutationProbDef;
				}
				else{
					prob = 0.5*((fitnessMax - fitness[i])/(fitnessMax - fitnessAve));
				}
			}
			else{
				prob = 0.5;
			}
			mutationProb[i] = prob;
                    }
                    else{
                        mutationProb[i] = mutationProbDef;
                    }
		}		
	}
	
	public double getFitnessMax(){
		return fitnessMax;
	}

	public double getFitnessAve(){
		return fitnessAve;
	}

	public double getFitnessTot(){
		return fitnessTot;
	}
	
	public Chromosome bestChromosome(){
		return best;
	}
	
	public int getFitness(int index){
		if(	(index >= size())||
			(index < 0) ){
				throw new IndexOutOfBoundsException("index must be from 0 to " + (size() - 1));
		}
		else{
			return fitness[index];
		}
	}
	
	public int size(){
		return populationSize;
	}
  
  	public int chromosomeSize(){
  		return chromosomeSize;
  	}
  	
    @Override
	public Object clone(){
            try{
		Population populationCopy = (Population) super.clone();
		populationCopy.population = (Chromosome[]) population.clone();
		populationCopy.fitness = (int[]) fitness.clone();
		return populationCopy;
            }
            catch (CloneNotSupportedException ex) {
                Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
	}
	
    @Override
    public String toString(){
    	String retString = new String();
    	for(int i = 0; i < size(); ++i){
      		retString += population[i];
    		retString += fitnessFunction.totalTardiness(population[i]);
    		retString += "\n";
      		//retString += mutationProb[i];
    		//retString += "\n";
    	}
    	return retString;
    }

}
