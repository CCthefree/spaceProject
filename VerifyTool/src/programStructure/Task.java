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

	private int lowerBound; // static processing lowerBound

	private int upperBound; // static processing upperBound

	private int finishTime; // max allowed time of dynamic processing

	private ArrayList<Integer> readSR; // list of read share resource indexes

	private ArrayList<Integer> writeSR; // list of write share resource indexes


	/**
	 * constructor
	 */
	public Task(String name, int lb, int ub, int finishTime, ArrayList<Integer> read,
			ArrayList<Integer> write) {
		this.name = name;
		this.lowerBound = lb;
		this.upperBound = ub;
		this.finishTime = finishTime;

		this.readSR = read;
		this.writeSR = write;
	}


	public String getName() {
		return this.name;
	}


	public int getLowerBound() {
		return this.lowerBound;
	}


	public int getUpperBound() {
		return this.upperBound;
	}


	public int getFinishTime() {
		return this.finishTime;
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

}
