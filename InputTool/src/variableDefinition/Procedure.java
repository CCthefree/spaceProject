package variableDefinition;

import java.util.ArrayList;

import util.define;

/**
 * class of procedure. contains name and description. it proceeds as described,
 */

public class Procedure {

	// 基本信息
	public String name; // procedure name
	public String description; // procedure description

	// 衍生信息
	public long bestTime;
	public long worstTime;
	public Statement_Sequence statement;
	public ArrayList<String> undefineTasks;
	public ArrayList<String> undefineCVs;
	public ArrayList<String> undefineSRs;
	public ArrayList<Task> containedTasks;
	public ArrayList<ControlVariable> containedCVs;
	public ArrayList<String> readSRs;
	public ArrayList<String> writeSRs;


	/**
	 * constructor without parameter
	 */
	public Procedure() {
		this.name = "";
		this.description = "";
	}
	
	
	public Procedure(String name, String desc){
		setValue(name, desc);
	}


	/**
	 * function to set value of procedure
	 */
	public void setValue(String n, String desc) {
		this.name = n;
		this.description = desc;

		this.undefineCVs = new ArrayList<String>();	
		this.undefineTasks = new ArrayList<String>();
		this.undefineSRs = new ArrayList<String>();
		this.statement = new Statement_Sequence(n, desc);

		this.statement.check();

		if (this.statement.errorIndex == define.noError)
			computeExecTime();
	}


	/**
	 * compute the worst execute time of the procedure
	 */
	public void computeExecTime() {
		this.statement.computeExecTime();
		this.bestTime = this.statement.bestTime;
		this.worstTime = this.statement.worstTime;
	}


	/**
	 * add a task into the 'containedTasks' list
	 */
	public void addTask(Task task){
		if(this.containedTasks == null)
			this.containedTasks = new ArrayList<Task>();
		
		if(this.containedTasks.contains(task))
			return;
		else
			this.containedTasks.add(task);
	}
	
	
	/**
	 * add a controlVariable into the 'containedCVs' list
	 */
	public void addCV(ControlVariable cv){
		if(this.containedCVs == null)
			this.containedCVs = new ArrayList<ControlVariable>();
		
		if(this.containedCVs.contains(cv))
			return;
		else
			this.containedCVs.add(cv);
	}
	
	
	/**
	 * add a ShareResource name into the 'readSRs' list
	 */
	public void addReadSR(String sr){
		if(this.readSRs == null)
			this.readSRs = new ArrayList<String>();
		
		if(this.readSRs.contains(sr))
			return;
		else
			this.readSRs.add(sr);
	}
	
	
	/**
	 * add a shareResource name into the 'writeSRs' list
	 */
	public void addWriteSR(String sr){
		if(this.writeSRs == null)
			this.writeSRs = new ArrayList<String>();
		
		if(this.writeSRs.contains(sr))
			return;
		else
			this.writeSRs.add(sr);
	}



	/**
	 * add the name of a undefinedTask into 'undefineTasks'
	 */
	public void addUndefinedTasks(String name) {
		if (this.undefineTasks == null)
			this.undefineTasks = new ArrayList<String>();

		if (this.undefineTasks.contains(name))
			return;
		else
			this.undefineTasks.add(name);
	}


	/**
	 * add the name of a undefined control variable into 'undefineCVs'
	 */
	public void addUndefinedCVs(String name) {
		if (this.undefineCVs == null)
			this.undefineCVs = new ArrayList<String>();

		if (this.undefineCVs.contains(name))
			return;
		else
			this.undefineCVs.add(name);
	}
	
	
	/**
	 * add the name of a undefinedTask into 'undefineSRs'
	 */
	public void addUndefinedSRs(String name) {
		if (this.undefineSRs == null)
			this.undefineSRs = new ArrayList<String>();

		if (this.undefineSRs.contains(name))
			return;
		else
			this.undefineSRs.add(name);
	}

}
