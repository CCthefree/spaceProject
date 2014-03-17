package programStructure;

import java.util.Stack;

import fileOperator.Logger;

/**
 * member of CPUStack
 * 
 */
class StackMember {

	int IPIndex; // index of procedure(in fact is interruption index)

	int point; // program point


	StackMember(int index, int pnt) {
		this.IPIndex = index;
		this.point = pnt;
	}
}


/**
 * class of CPU Stack
 * 
 * @author zengke.cai
 * 
 */
public class CPUStack {

	private Stack<StackMember> stack;


	/**
	 * constructor, initialize a stack of StackMember
	 */
	public CPUStack() {
		this.stack = new Stack<StackMember>();
	}


	/**
	 * push in new StackMember '<index, pnt>'
	 */
	public void push(int index, int pnt) {
		StackMember sm = new StackMember(index, pnt);
		this.stack.push(sm);
	}


	/**
	 * pop out top element
	 * 
	 * @return null if stack is empty
	 */
	public StackMember pop() {
		if (this.stack.isEmpty())
			return null;
		else {
			return this.stack.pop();
		}
	}


	/**
	 * get the procedure index at top of CPU stack
	 * 
	 * @return -1 if stack is empty
	 */
	public int getTopProc() {
		if (this.stack.isEmpty())
			return -1;
		else
			return this.stack.peek().IPIndex;
	}


	/**
	 * get the program point at top of CPU stack
	 * 
	 * @return -1 if stack is empty
	 */
	public int getTopPnt() {
		if (this.stack.isEmpty())
			return -1;
		else
			return this.stack.peek().point;
	}


	/**
	 * set the program point of procedure at top of CPU stack
	 * 
	 * @return false if stack is empty
	 */
	public boolean setPnt(int newPnt) {
		if (this.stack.isEmpty())
			return false;
		else {
			this.stack.peek().point = newPnt;
			return true;
		}
	}


	/**
	 * get stack size
	 */
	public int getSize() {
		return this.stack.size();
	}


	/**
	 * get interruption index of the element at 'index'
	 * 
	 * @param index
	 * @return -1 if index is illegal
	 */
	public int getProcIndexAt(int index) {
		if (index < 0 || index >= this.stack.size())
			return -1;

		return this.stack.elementAt(index).IPIndex;
	}


	/**
	 * get the program point at 'index'
	 * 
	 * @return -1 if index is illegal
	 */
	public int getPntAt(int index) {
		if (index < 0 || index >= this.stack.size())
			return -1;

		return this.stack.elementAt(index).point;
	}


	/**
	 * TEST function to print all the element in CPU stack
	 */
	public void printAllEle() {
		for (int i = 0; i < this.stack.size(); i++) {
			Logger.append(" <" + this.stack.elementAt(i).IPIndex + ", "
					+ this.stack.elementAt(i).point + ">; ");
		}
	}

}
