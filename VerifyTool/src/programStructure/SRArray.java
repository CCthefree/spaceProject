package programStructure;

import java.util.ArrayList;

/**
 * class of share resource array structure
 * 
 * @author zengke.cai
 * 
 */
public class SRArray {

	private ArrayList<String> SRs; // list of share resource names

	private int[] nRead; // array of reading count on each SR

	private int[] nWrite; // array of writing count on each SR


	/**
	 * constructor
	 */
	public SRArray() {
		this.SRs = new ArrayList<String>();
	}


	/**
	 * add a new share resource into structure
	 * 
	 * @param name
	 */
	public void addSR(String name) {
		this.SRs.add(name);
	}


	/**
	 * initialization operation, initialize the reading and writing count array
	 * according to SR amount
	 */
	public void initialize() {
		int size = this.SRs.size();

		this.nRead = new int[size];
		this.nWrite = new int[size];
	}


	/**
	 * increase read number of SR at 'index'
	 * 
	 * @param index
	 * @return false if index is illegal
	 */
	public boolean incReadAt(int index) {
		if (index < 0 || index >= this.nRead.length)
			return false;
		else {
			this.nRead[index]++;
			return true;
		}
	}


	/**
	 * increase write number of SR at 'index'
	 * 
	 * @param index
	 * @return false if index is illegal
	 */
	public boolean incWriteAt(int index) {
		if (index < 0 || index >= this.nWrite.length)
			return false;
		else {
			this.nWrite[index]++;
			return true;
		}
	}


	/**
	 * decrease read number of SR at 'index'
	 * 
	 * @param index
	 * @return false if index is illegal or the count is <= 0
	 */
	public boolean decReadAt(int index) {
		if (index < 0 || index >= this.nRead.length)
			return false;
		else if (this.nRead[index] <= 0)
			return false;
		else {
			this.nRead[index]--;
			return true;
		}
	}


	/**
	 * decrease write number of SR at 'index'
	 * 
	 * @param index
	 * @return false if index is illegal or the count is <= 0
	 */
	public boolean decWriteAt(int index) {
		if (index < 0 || index >= this.nWrite.length)
			return false;
		else if (this.nWrite[index] <= 0)
			return false;
		else {
			this.nWrite[index]--;
			return true;
		}
	}


	/**
	 * get the index of a given share resource name
	 * 
	 * @param name
	 * @return -1 if no such share resource
	 */
	public int getIndex(String name) {
		return this.SRs.indexOf(name);
	}

	public String getNameAt(int index) {
		return this.SRs.get(index);
	}

	public ArrayList<String> getSRs() {
		return SRs;
	}


	public int[] getnRead() {
		return nRead;
	}


	public int[] getnWrite() {
		return nWrite;
	}
}
