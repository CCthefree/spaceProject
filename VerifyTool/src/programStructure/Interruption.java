package programStructure;

import fileOperator.ITATest;

/**
 * class of interruption
 * 
 * @author zengke.cai
 * 
 */
public class Interruption {

	private String name; // interruption name

	private int priority; // priority

	private char type; // type

	private int period;// period of periodical interruption, interval for random
						// interruption

	private int offset; // offset of system task

	private int lbd; // lowerBound

	private int ubd; // upperBound

	private Procedure IP; // corresponding procedure


	/**
	 * constructor with parameter
	 */
	public Interruption(String name, int prio, char type, int period, int offset, int lbd, int ubd,
			Procedure proc) {
		this.name = name;
		this.priority = prio;
		this.type = type;
		this.period = period;
		this.offset = offset;
		this.lbd = lbd;
		this.ubd = ubd;
		this.IP = proc;
	}


	public String getName() {
		return name;
	}


	public int getPriority() {
		return priority;
	}


	public char getType() {
		return type;
	}


	public int getPeriod() {
		return period;
	}


	public int getOffset() {
		return offset;
	}


	public Procedure getIP() {
		return IP;
	}


	public int getLBD() {
		return this.lbd;
	}


	public int getUBD() {
		return this.ubd;
	}


	/**
	 * TEST test function
	 */
	public void test() {
		ITATest.append("name " + this.name + ", type " + this.type + ", prio " + this.priority
				+ ", period " + this.period + ", offset " + this.offset + ", upbnd " + this.ubd
				+ "\r\n");
	}
}
