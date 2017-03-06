
import java.util.*;

public class ChromosomeComparator
implements Comparator<Chromosome>{
	private FitnessFunction fitnessFunction;
	
	public ChromosomeComparator(FitnessFunction fitnessFunction){
		this.fitnessFunction = fitnessFunction;
	}
	
	public int compare(Chromosome c1, Chromosome c2){
		return -(fitnessFunction.fitness(c1) - fitnessFunction.fitness(c2));
	}
}
