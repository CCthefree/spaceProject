package programStructure;

import initializer.Model;

import java.util.ArrayList;

/**
 * task class
 * 
 * @author ting.rong
 * 
 */
public class Task {

	private String name; // task name

	private long lowerBound; // static processing lowerBound

	private long upperBound; // static processing upperBound

	private long finishTime; // max allowed time of dynamic processing
	
	private boolean commFlag;	//flag indicates whether this task is communication task

	private ArrayList<Integer> readSR; // list of read share resource indexes

	private ArrayList<Integer> writeSR; // list of write share resource indexes


	/**
	 * constructor
	 */
	public Task(String name, String lb, String ub, String ft,
			String readSR, String writeSR, String flag) {
		this.name = name;
		this.lowerBound = Long.parseLong(lb);
		this.upperBound = Long.parseLong(ub);
		this.finishTime = Long.parseLong(ft);
		this.commFlag = flag.equals("yes") ? true : false;
		
		// convert the name list of SR to the index list
		this.readSR = new ArrayList<Integer>();
		this.writeSR = new ArrayList<Integer>();
		String[] read = readSR.split(",");
		String[] write = writeSR.split(",");
		for (String str : read) {
			this.readSR.add(Model.getSRIndex(str));
		}
		for (String str : write) {
			this.writeSR.add(Model.getSRIndex(str));
		}
	}

	/**
	 * check whether task read res
	 */
	public boolean hasRead(String res) {
		int index = Model.getSRArray().getIndex(res);
		if (readSR.contains(index))
			return true;
		else return false;
	}
	
	/**
	 * check whether task write res
	 */
	public boolean hasWritten(String res) {
		int index = Model.getSRArray().getIndex(res);
		if (writeSR.contains(index))
			return true;
		else return false;
	}
	

	public String getName() {
		return this.name;
	}


	public long getLowerBound() {
		return this.lowerBound;
	}


	public long getUpperBound() {
		return this.upperBound;
	}


	public long getFinishTime() {
		return this.finishTime;
	}

	public boolean getCommFlag(){
		return this.commFlag;
	}

	public ArrayList<Integer> getReadSR() {
		return readSR;
	}


	public ArrayList<Integer> getWriteSR() {
		return writeSR;
	}


	public void setReadSR(ArrayList<Integer> readSR) {
		this.readSR = readSR;
	}


	public void setWriteSR(ArrayList<Integer> writeSR) {
		this.writeSR = writeSR;
	}

}
