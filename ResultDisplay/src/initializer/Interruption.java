package initializer;


public class Interruption {

	private String name; // interruption name

	private int priority; // priority

	private int index;	//index of interruption used in event


	/**
	 * constructor with parameter
	 */
	public Interruption(String name, int prio, int index) {
		this.name = name;
		this.priority = prio;
		this.index = index;
	}


	
	public String getName() {
		return name;
	}


	
	public int getPriority() {
		return priority;
	}


	
	public int getIndex() {
		return index;
	}
}