package programStructure;

import fileOperator.ITATest;

/**
 * class of interruption
 * 
 * @author zengke.cai
 * 
 */
public class Interruption {

	private String name;

	private int priority;
	
	private String IRQ;

	private char type;

	private long period;// period of periodical interruption, interval for random
						// interruption

	private long offset; // offset of system task

	private long ubd; // upperBound

	private Procedure IP; // corresponding procedure


	/**
	 * constructor with parameter
	 */
	public Interruption(String name, String prio, String IRQ, String type, String period, 
			String offset, String ubd, String proc) {

		this.name = name;
		this.IRQ = IRQ;
		this.priority = Integer.parseInt(prio);
		this.period = Long.parseLong(period);
		this.offset = Long.parseLong(offset);
		this.ubd = Long.parseLong(ubd);

		this.type = ' ';
		if (this.priority >= 255)
			this.type = 'S'; // system task;
		else if (type.equals("random"))
			this.type = 'R'; // random interruption
		else
			this.type = 'P'; // periodical interruption

		this.IP = new Procedure(proc);
	}


	public String getName() {
		return name;
	}


	public int getPriority() {
		return priority;
	}

	
	public String getIRQ(){
		return IRQ;
	}

	public char getType() {
		return type;
	}


	public long getPeriod() {
		return period;
	}


	public long getOffset() {
		return offset;
	}


	public Procedure getIP() {
		return IP;
	}


	public long getUBD() {
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
