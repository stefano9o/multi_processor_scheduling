
import java.util.*;

public class Chromosome
implements Cloneable{
	private int genes[];
	private int chromosomeSize;
	
	private static Random random = new Random();
	
	public Chromosome(int chromosomeSize){
		this.chromosomeSize = chromosomeSize;
		genes = new int[size()];
		
		for(int i = 0; i < size(); ++i){
			genes[i] = i;
		}
		
		for(int i = 1; i < size(); ++i){
			int j = random.nextInt(i);
			
			genes[i] = genes[j];
			genes[j] = i;
		}
	}
	
	public void swap(int index1, int index2){
		if(	(index1 >= size())||
			(index2 >= size())||
			(index1 < 0)||
			(index2 < 0) ){
				throw new IndexOutOfBoundsException("index must be from 0 to " + (size() - 1));
		}
		else{
			int temp = genes[index1];
			genes[index1] = genes[index2];
			genes[index2] = temp;
		}
	}
	
	public void swap(){
		int index1 = random.nextInt(size());
		int index2 = random.nextInt(size());
		swap(index1, index2);
	}	
	
	public int getGene(int index){
		if(	(index >= size())||
			(index < 0) ){
				throw new IndexOutOfBoundsException("index must be from 0 to " + (size() - 1));
		}
		else{
			return genes[index];
		}	
	}
	
	public void setGene(int index, int value){
		if(	(index >= size())||
			(index < 0) ){
				throw new IndexOutOfBoundsException("index must be from 0 to " + (size() - 1));
		}
		else{
			genes[index] = value;
		}	
	}
	
	public int size(){
		return chromosomeSize;
	}

	public Object clone() {
		try {
			Chromosome chromosomeCopy = (Chromosome) super.clone();
			chromosomeCopy.genes = (int[]) genes.clone();
			//chromosomeCopy.chromosomeSize = chromosomeSize;
			
			return chromosomeCopy;
		} 
		catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public String toString(){
		String retString = new String();
		
		for(int i = 0 ; i < size(); ++i){
			retString += String.valueOf(genes[i]);
			retString += " ";
		}

		
		return retString;
	}
}
