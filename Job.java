
public class Job{
	private int number;
	private int dueDate;
	private int processingTime;
	private static int nMachines;
	
	public Job(int number, int processingTime, int dueDate){
		this.number = number - 1;
		this.processingTime = processingTime;
		this.dueDate = dueDate;	
	}
	
	public static void setNMachines(int _nMachines){
		nMachines = _nMachines;
	}
	
	public static int getNMachines(){
		return nMachines;
	}
	
	public int getNumber(){
		return number;
	}
	
	public int getDueDate(){
		return dueDate;
	}
	
	public int getProcessingTime(){
		return processingTime;
	}
	

    @Override
	public String toString(){
		return ("n = " + number + "; pt = " + processingTime + "; dd = " + dueDate);
	}
}
