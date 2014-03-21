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
	public ArrayList<String> undefineRSRs;
	public ArrayList<String> undefineWSRs;
	
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
		
		this.containedCVs = null;
		this.containedTasks = null;
		this.readSRs = null;
		this.writeSRs = null;
		this.undefineCVs = null;
		this.undefineTasks = null;
		this.undefineRSRs = null;
		this.undefineWSRs = null;
		
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
		
		if(!this.containedTasks.contains(task))
			this.containedTasks.add(task);
	}
	
	
	/**
	 * add a controlVariable into the 'containedCVs' list
	 */
	public void addCV(ControlVariable cv){
		if(this.containedCVs == null)
			this.containedCVs = new ArrayList<ControlVariable>();
		
		if(!this.containedCVs.contains(cv))
			this.containedCVs.add(cv);
	}
	
	
	/**
	 * add a ShareResource name into the 'readSRs' list
	 */
	public void addReadSR(String sr){
		if(this.readSRs == null)
			this.readSRs = new ArrayList<String>();
		
		if(!this.readSRs.contains(sr))
			this.readSRs.add(sr);	
	}
	
	
	/**
	 * add a shareResource name into the 'writeSRs' list
	 */
	public void addWriteSR(String sr){
		if(this.writeSRs == null)
			this.writeSRs = new ArrayList<String>();
		
		if(!this.writeSRs.contains(sr))
			this.writeSRs.add(sr);	
	}



	/**
	 * add the name of a undefinedTask into 'undefineTasks'
	 */
	public void addUndefinedTasks(String name) {
		if (this.undefineTasks == null)
			this.undefineTasks = new ArrayList<String>();

		if (!this.undefineTasks.contains(name))
			this.undefineTasks.add(name);	
	}


	/**
	 * add the name of a undefined control variable into 'undefineCVs'
	 */
	public void addUndefinedCVs(String name) {
		if (this.undefineCVs == null)
			this.undefineCVs = new ArrayList<String>();

		if (!this.undefineCVs.contains(name))
			this.undefineCVs.add(name);		
	}
	
	
	/**
	 * add the name of a undefined share resource into 'undefineRSRs'
	 */
	public void addUndefinedRSRs(String name) {
		if (this.undefineRSRs == null)
			this.undefineRSRs = new ArrayList<String>();

		if (!this.undefineRSRs.contains(name))
			this.undefineRSRs.add(name);
	}
	
	
	/**
	 * add the name of a undefined share resource into 'undefineWSRs'
	 */
	public void addUndefinedWSRs(String name) {
		if (this.undefineWSRs == null)
			this.undefineWSRs = new ArrayList<String>();

		if (!this.undefineWSRs.contains(name))
			this.undefineWSRs.add(name);
	}

}
