
import java.util.*;

public class FitnessFunction{
	private final int C = 10000;

	private Job[] parameterJobs;
	private int m;	
	
	public FitnessFunction(Job parameterJobs[], int m){
		this.parameterJobs = parameterJobs;
		this.m = m;
	}

	public int totalTardiness(Chromosome c){
		int currentMachine = 0;
		int currentJob;
		int time[] = new int[m];

		int totalTardiness = 0;
		for(int i = 0; i < m; ++i){
			time[i] = 0;
		}
				
		for(int i = 0; i < c.size(); ++i){
			currentJob = c.getGene(i);
			time[currentMachine] += parameterJobs[currentJob].getProcessingTime();
			
			if(time[currentMachine] > parameterJobs[currentJob].getDueDate()){
				totalTardiness += time[currentMachine] - parameterJobs[currentJob].getDueDate();
			}
			
			//currentMachine = 0;
			for(int j = 0; j < m; ++j){
				if (time[j] < time[currentMachine]){
					currentMachine = j;
				} 
			}
		}
		
		return totalTardiness;
	
	}
	
	public int fitness(Chromosome c){
		return (C - totalTardiness(c));
	}
	
	public int totalTardinessOnSingleMachine(List<Integer> s){
		int currentJob;
		int time = 0;

		int totalTardiness = 0;
				
		for(int i = 0; i < s.size(); ++i){
			currentJob = s.get(i);
			time += parameterJobs[currentJob].getProcessingTime();
			if(time > parameterJobs[currentJob].getDueDate()){
				totalTardiness += time - parameterJobs[currentJob].getDueDate();
			}
		}
		
		return totalTardiness;
	}
	
	public int totalTardiness(List[] schedule){
		int tt = 0;
		
		for(int i = 0; i < schedule.length; ++i){
			tt += totalTardinessOnSingleMachine(new ArrayList<Integer>(schedule[i]));
		}
		
		return tt;
	
	}
}
