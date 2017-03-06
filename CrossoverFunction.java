
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
		
		
		
		/*
		int chromosomeSize = Chromosome.size(); 

		int goP1[] = new int[chromosomeSize];
		int goP2[] = new int[chromosomeSize];
		int goS1[] = new int[chromosomeSize];
		int goS2[] = new int[chromosomeSize];
			
		int aP1[] = new int[chromosomeSize];
		int aP2[] = new int[chromosomeSize];
		int aS1[] = new int[chromosomeSize];
		int aS2[] = new int[chromosomeSize];
			
		Chromosome selected1 = null;
		Chromosome selected2 = null;
			
		for(int i = 0; i < oldPopulation.size(); i = i + 2){
			Chromosome p1 = oldPopulation.getChromosome(i);
			Chromosome p2 = oldPopulation.getChromosome(i + 1);
			int fP1 = oldPopulation.getFitnessValue(i);
			int fP2 = oldPopulation.getFitnessValue(i + 1);
			int fMax;
			if(fP1 >= fP2){
				fMax = fP1;
			}
			else{
				fMax = fP2;
			}
			
			double crossoverProbability;
			if(fMax >= oldPopulation.getFitnessAve()){
				crossoverProbability = ((oldPopulation.getFitnessMax() - fMax)/(oldPopulation.getFitnessMax() - oldPopulation.getFitnessAve()));
			}
			else{
				crossoverProbability = 1;
			}
			
//			System.out.println(p1);
//			System.out.println(p2);


//			System.out.println(crossoverProbability + " ");
//			try{
//				Thread.sleep(500);
//			}
//			catch(InterruptedException ie){
//				ie.printStackTrace();
//			}
			crossoverProbability = 0.6;
			
			double crossoverParameter = random.nextDouble();
			if(crossoverParameter < crossoverProbability){
				for(int j = 0; j < chromosomeSize; ++j){
					goP1[j] = p1.getGlobalOrdering(j);
					goP2[j] = p2.getGlobalOrdering(j); 
					aP1[j] = p1.getAssignement(j);
					aP2[j] = p2.getAssignement(j); 
				}
			
//				int crossPoint1 = 0;
//				int crossPoint2 = 0;
//	
//				do{
//					crossPoint1 = random.nextInt(chromosomeSize);
//					crossPoint2 = random.nextInt(chromosomeSize);		
//				}while(crossPoint1 >= crossPoint2);
		
				boolean mask[] = new boolean[chromosomeSize];
//				for(int j = 0; j < chromosomeSize; ++j){
//					if((j < crossPoint1)||(j >= crossPoint2)){
//						mask[j] = false;
//					}
//					else{
//						mask[j] = true;
//					}
//				}

				for(int j = 0; j < chromosomeSize; ++j){
					mask[j] = random.nextBoolean();
				}
				
//				System.out.println("crossPoint1: " + crossPoint1);
//				System.out.println("crossPoint2: " + crossPoint2);
			
				//pmxCrossover(goP1,goP2,goS1,goS2,crossPoint1,crossPoint2);
				//crossover1(goP1,goP2,goS1,goS2,aP1,aP2,aS1,aS2,crossPoint1,crossPoint2);
			
				orderderCrossover(goP1,goP2,goS1,goS2,mask);
				assignementCrossover(aP1,aP2,aS1,aS2,mask);

				Chromosome s1 = new Chromosome(goS1,aS1);
				Chromosome s2 = new Chromosome(goS2,aS2);
				Population tempPopulation = new Population(oldPopulation.getFitnessFunction());

			
				tempPopulation.addChromosome(p1);
				tempPopulation.addChromosome(p2);
				tempPopulation.addChromosome(s1);
				tempPopulation.addChromosome(s2);
				
				tempPopulation.evaluate();
			
				int index;
				selected1 = tempPopulation.getBestChromosome();
				if(selected1 == p1)
					index = 0;
				else if(selected1 == p2)
					index = 1;
				else if(selected1 == s1)
					index = 2;
				else if(selected1 == s2)
					index = 3;
				else
					index = -1;
		
			
				tempPopulation.removeChromosome(index);
				tempPopulation.evaluate();
			
				selected2 = tempPopulation.getBestChromosome();
			}
			else{
				selected1 = p1;
				selected2 = p2;
			}
			
			newPopulation.addChromosome(selected1);
			newPopulation.addChromosome(selected2);
		}
		newPopulation.evaluate();
	}
	
	private void orderderCrossover(int goP1[], int goP2[], int goS1[], int goS2[], boolean[] mask){
		int chromosomeSize = Chromosome.size();
		boolean bitVector1[] = new boolean[chromosomeSize];
		boolean bitVector2[] = new boolean[chromosomeSize];
		
		
		for(int i = 0; i < chromosomeSize; ++i){
			bitVector1[i] = false;
			bitVector2[i] = false;
//			System.out.print(mask[i] + " ");
		}
		
		//System.out.println();
		//System.out.println();
		
		LinkedList<Integer> s1 = new LinkedList<Integer>();
		LinkedList<Integer> s2 = new LinkedList<Integer>();
		
		for(int i = 0; i < chromosomeSize; ++i){
			if(mask[i] == true){
				goS1[i] = goP1[i];
				bitVector1[goP1[i]] = true;
				goS2[i] = goP2[i];
				bitVector2[goP2[i]] = true;
			}
			s2.add(goP1[i]);
			s1.add(goP2[i]);
		}
		
		int temp;
		ListIterator<Integer> it1 = s1.listIterator();
		while(it1.hasNext()){
			temp = it1.next();
			if(bitVector1[temp] == true){
//				System.out.print(temp + " ");
				it1.remove();
			}
		}
//		System.out.println();
//		System.out.println();
//		System.out.println("s1: " + s1);
//		System.out.println();
		ListIterator<Integer> it2 = s2.listIterator();
		while(it2.hasNext()){
			temp = it2.next();
			if(bitVector2[temp] == true){
//				System.out.print(temp + " ");
				it2.remove();
			}
		}
//		System.out.println();
//		System.out.println();
//		System.out.println("s2: " + s2);
		
		it1 = s1.listIterator();
		it2 = s2.listIterator();
		for(int i = 0; i < chromosomeSize; ++i){
			if(mask[i] == false){
				goS1[i] = it1.next();
				goS2[i] = it2.next();
			}
		}			
	
	}
	private void pmxCrossover(int goP1[], int goP2[], int goS1[], int goS2[], int crossPoint1, int crossPoint2){
		int chromosomeSize = Chromosome.size();
		
		boolean[] bitVector = new boolean[chromosomeSize];	//utilizzato per velocizzare la ricerca
		int valueToAdd;	// primo valore all'interno del padre2 che non è già stato aggiunto dal padre 1
		int indValue;	//poszione del valore sopra
		int k;			//valore dell'elemento in posizione indValue nel padre1
		int indK;		//posizione dell'elemento sopra nel padre2
		
		boolean isDone;	//utilizzata per ciclo
		
		for(int i = 0; i < chromosomeSize; ++i){
			bitVector[i] = false;	
			goS1[i] = -1;
		}	
		
		//System.out.println("crossPoint1: " + crossPoint1);
		//System.out.println("crossPoint2: " + crossPoint2);
		
		// Parte interna 
		for(int i = crossPoint1; i < crossPoint2; ++i){
				goS1[i] = goP1[i];
				bitVector[goP1[i]] = true;		
		}
			
		for(int i = crossPoint1; i < crossPoint2; ++i){
			if(bitVector[goP2[i]] == false){
				valueToAdd = goP2[i];
				indValue = i;
					
				isDone = false;
				while(!isDone){
					k = goP1[indValue];
					indK = search(goP2, k);
					
					if((indK < crossPoint1)||(indK >= crossPoint2)){
						isDone = true;
						indValue = indK;
					}
					else{
						indValue = indK;
					}
						
				}
				goS1[indValue] = valueToAdd;
			}
		}
		
		for(int i = 0; i < chromosomeSize; ++i){
			if(goS1[i] == -1){
				goS1[i] = goP2[i];
			}
		}
		
		for(int i = 0; i < chromosomeSize; ++i){
			bitVector[i] = false;	
			goS2[i] = -1;
		}	
		
		// Parte interna 
		for(int i = crossPoint1; i < crossPoint2; ++i){
				goS2[i] = goP2[i];
				bitVector[goP2[i]] = true;
					
		}
			
		for(int i = crossPoint1; i < crossPoint2; ++i){
			if(bitVector[goP1[i]] == false){
				valueToAdd = goP1[i];
				indValue = i;
					
				isDone = false;
				while(!isDone){
					k = goP2[indValue];
					indK = search(goP1, k);
					
					if((indK < crossPoint1)||(indK >= crossPoint2)){
						isDone = true;
						indValue = indK;
					}
					else{
						indValue = indK;
					}
						
				}
				goS2[indValue] = valueToAdd;
			}
		}
		
		for(int i = 0; i < chromosomeSize; ++i){
			if(goS2[i] == -1){
				goS2[i] = goP1[i];
			}
		}
		
	}


	private void crossover1(int goP1[], int goP2[], int goS1[], int goS2[], int aP1[], int aP2[], int aS1[], int aS2[], int crossPoint1, int crossPoint2){
		int chromosomeSize = Chromosome.size();
		
		boolean[] bitVector = new boolean[chromosomeSize];	//utilizzato per velocizzare la ricerca
		int valueToAdd;	// primo valore all'interno del padre2 che non è già stato aggiunto dal padre 1
		int indValue;	//poszione del valore sopra
		int k;			//valore dell'elemento in posizione indValue nel padre1
		int indK;		//posizione dell'elemento sopra nel padre2
		
		boolean isDone;	//utilizzata per ciclo
		
		
		//crossPoint1 = 4;
		//crossPoint2 = 14;
		
		for(int i = 0; i < chromosomeSize; ++i){
			bitVector[i] = false;	
			goS1[i] = -1;
		}	
		
		// Parte interna 
		for(int i = crossPoint1; i < crossPoint2; ++i){
				goS1[i] = goP1[i];
				aS1[i] = aP1[i];
				bitVector[goP1[i]] = true;		
		}
			
		for(int i = crossPoint1; i < crossPoint2; ++i){
			if(bitVector[goP2[i]] == false){
				valueToAdd = goP2[i];
				indValue = i;
					
				isDone = false;
				while(!isDone){
					k = goP1[indValue];
					indK = search(goP2, k);
					
					if((indK < crossPoint1)||(indK >= crossPoint2)){
						isDone = true;
						indValue = indK;
					}
					else{
						indValue = indK;
					}
						
				}
				goS1[indValue] = valueToAdd;
				aS1[indValue] = aP2[indValue];
			}
		}
		
		for(int i = 0; i < chromosomeSize; ++i){
			if(goS1[i] == -1){
				goS1[i] = goP2[i];
				aS1[i] = aP2[i];
			}
		}
		
		for(int i = 0; i < chromosomeSize; ++i){
			bitVector[i] = false;	
			goS2[i] = -1;
		}	
		
		// Parte interna 
		for(int i = crossPoint1; i < crossPoint2; ++i){
				goS2[i] = goP2[i];
				aS2[i] = aP2[i];
				bitVector[goP2[i]] = true;
					
		}
			
		for(int i = crossPoint1; i < crossPoint2; ++i){
			if(bitVector[goP1[i]] == false){
				valueToAdd = goP1[i];
				indValue = i;
					
				isDone = false;
				while(!isDone){
					k = goP2[indValue];
					indK = search(goP1, k);
					
					if((indK < crossPoint1)||(indK >= crossPoint2)){
						isDone = true;
						indValue = indK;
					}
					else{
						indValue = indK;
					}
						
				}
				goS2[indValue] = valueToAdd;
				aS2[indValue] = aP1[indValue];
			}
		}
		
		for(int i = 0; i < chromosomeSize; ++i){
			if(goS2[i] == -1){
				goS2[i] = goP1[i];
				aS2[i] = aP1[i];
			}
		}
		/*
		for(int i = 0; i < chromosomeSize; ++i){
			System.out.print(goS1[i] + " ");
		}
		System.out.println();
		for(int i = 0; i < chromosomeSize; ++i){
			System.out.print(aS1[i] + " ");
		}
		System.out.println();
		System.out.println();
		for(int i = 0; i < chromosomeSize; ++i){
			System.out.print(goS1[i] + " ");
		}
		System.out.println();
		for(int i = 0; i < chromosomeSize; ++i){
			System.out.print(aS1[i] + " ");
		}
		System.out.println();
		System.out.println();
		
	}
	
	public int search(int array[],int k){
		int i;
		for(i = 0; i < array.length; ++i){
			if (array[i] == k){
				return i;
			}	
		}
		return -1;
	}

	
	private void assignementCrossover(int aP1[], int aP2[], int aS1[], int aS2[], boolean mask[]){
		int chromosomeSize = Chromosome.size();
		for(int i = 0; i < chromosomeSize; ++i){
			if(mask[i] == true){
				aS1[i] = aP1[i];
				aS2[i] = aP2[i];
			}
			else{
				aS1[i] = aP2[i];
				aS2[i] = aP1[i];		
			}
		}
	}


	public Population getNewPopulation(){
		return newPopulation;
	}
}
*/

