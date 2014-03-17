package programStructure;

import initializer.Model;

/**
 * class of interruption vector
 * 
 * @author zengke.cai
 * 
 */
public class InterruptVector {

	private int size; // max size of interrupt flags

	private boolean[] flags; // flag of interruption


	/**
	 * constructor, the number of flags equals the number of interruptions
	 */
	public InterruptVector() {
		this.size = Model.getInterCount(); // set the vector size the same with
											// amount of interruption
		this.flags = new boolean[this.size];

		// initialize all as false
		for (int i = 0; i < this.size; i++)
			this.flags[i] = false;
	}


	/**
	 * set flag at 'index' as true;
	 * 
	 * @param index
	 * @return false if index is illegal
	 */
	public boolean setTrue(int index) {
		if (index >= this.size || index < 0)
			return false;
		else {
			this.flags[index] = true;
			return true;
		}
	}


	/**
	 * set flag at 'index' as false;
	 * 
	 * @param index
	 * @return false if index is illegal
	 */
	public boolean setFalse(int index) {
		if (index >= this.size || index < 0)
			return false;
		else {
			this.flags[index] = false;
			return true;
		}
	}


	/**
	 * get the value at 'index'
	 * 
	 * @param index
	 * @return false if index is illegal
	 */
	public boolean getValue(int index) {
		if (index >= this.size || index < 0)
			return false; // TODO, error prompt
		else
			return this.flags[index];
	}


	/**
	 * get the index of highest priority interruption in interVec
	 * 
	 * @return -1 if no interruption in interVec
	 */
	public int getHighestInter() {
		int index = -1;
		for (int i = 0; i < this.size; i++) {
			if (this.flags[i] == true && index == -1)
				index = i;
			else if (this.flags[i] == true
					&& Model.getInterAt(i).getPriority() < Model.getInterAt(index).getPriority())
				index = i;
		}
		return index;
	}
	

	/**
	 * get the size of interruption vector
	 * 
	 */
	public int getSize() {
		return this.size;
	}
}
