
import java.util.*;

public class SelectionFunction{
	private Population oldPopulation = null;
	private Population newPopulation = null;
	
	private FitnessFunction fitnessFunction;
	
	private static Random random = new Random(System.currentTimeMillis());
	
	public SelectionFunction(FitnessFunction fitnessFunction){
		this.fitnessFunction = fitnessFunction;
	}	
	
	public void setOldPopulation(Population oldPopulation){
		this.oldPopulation = oldPopulation;
	}
	
	public Population getOldPopulation(){
		return oldPopulation;
	}
	
	public Population getNewPopulation(){
		if(newPopulation == null){
			throw new RuntimeException("it must call generatePopulation before calling getNewPopulation()");
		}
		return newPopulation;
	}
	
	public void generatePopulation(){
		if(oldPopulation == null){
			throw new RuntimeException("it must set oldPopulation using setOldPopulation()  before calling generatePopulation()");
		}
		
		newPopulation = (Population)oldPopulation.clone();
		double[] wheelValue = new double[oldPopulation.size()];
		int oldPopulationSize = oldPopulation.size();
		double[] fitnessValue = new double[oldPopulationSize];
		
		double totalFitness = 0;
		/* si calcolano i valori di fitness dei cromosomi appartenenti alla vecchia popolazione e la fitness totale */
		for (int i = 0; i < oldPopulationSize; i++) {
			totalFitness += oldPopulation.getFitness(i);
		}
		
		double tempValue = 0;
		double wheelTemp = 0;
		/* si definisce la distribuzione di probabilità discreta */
		for (int i = 0; i < oldPopulationSize; i++) {
			tempValue = oldPopulation.getFitness(i) / totalFitness;
			wheelTemp += tempValue;
			wheelValue[i] = wheelTemp;
		}
		
		double point = 0;
		int chromosomeIndex = 0;
		/* si effetua una realizzazione della variabile aleatorea "lancio pallina roulette" */
		for (int i = 0; i < oldPopulationSize; i++) {
			point = random.nextDouble();
			for (int j = 0; j < oldPopulation.size(); j++) {
				if (wheelValue[j] > point) {
					chromosomeIndex = j;
					break;
				}
				chromosomeIndex = 0;
			}

			Chromosome choosenChromosome = oldPopulation.getChromosome(chromosomeIndex);
			newPopulation.setChromosome(i,choosenChromosome);
		}
		newPopulation.evaluate();
	}
}
