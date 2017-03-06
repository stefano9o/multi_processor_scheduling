
import java.util.*;
import java.io.*;

public class Parser{
	private Scanner myScanner = null;
	private FileInputStream myFile = null;
	private int nMachines;
	private int nJobs;
	
	public Parser(String fileName){
		try{
			myFile = new FileInputStream(fileName);
		}
		catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
		}
		
		myScanner = new Scanner(myFile);	
	}

	public int numberMachines(){
		return nMachines;
	}
	
	public int numberJobs(){
		return nJobs;
	}
	
	public Job[] parse(){
		LinkedList<Integer> tokens = lexer();
		Job[] jobs = parser(tokens);
		
		return jobs;
	}

	
	private LinkedList<Integer> lexer(){
		LinkedList<Integer> tokens = new LinkedList<Integer>();
		
		while(myScanner.hasNext()){
			if(myScanner.hasNext("[0-9]+")){
				tokens.add(myScanner.nextInt());
			}
			else{
				myScanner.next();
			}
		}
		
			
		return tokens;		
	}
	
	private Job[] parser(LinkedList<Integer> tokens){
		ListIterator<Integer> it = tokens.listIterator(); 
		int n = tokens.get(0);
		int m = tokens.get(1);
		
		nJobs = n;
		nMachines = m;
		
		Job jobs[] = new Job[n];
		Job job = null;
		int j = 0;
		for(int i = 2; i < tokens.size(); i = i + 3){
			job = new Job(tokens.get(i),tokens.get(i+1),tokens.get(i+2));
			jobs[j] = job;
			++j;
		}
		return jobs;
	}
}


