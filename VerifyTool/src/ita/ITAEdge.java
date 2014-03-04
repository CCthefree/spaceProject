package ita;

/**
 * class of ITA Edge
 * 
 * @author zengke.cai
 * 
 */
public class ITAEdge {

	private int fromLoc; // index of from location (index of location in
							// location array,

	// not the label of location)

	private int toLoc; // index of to location (index of location in location
						// array, not

	// the label of location)

	private int interIndex; // corresponding interruption's index

	// transition constrain;
	private String op;

	private int value;
	
	private boolean reset;


	/**
	 * constructor
	 */
	public ITAEdge(int from, int to, int inter, String op, int value, boolean reset) {
		this.fromLoc = from;
		this.toLoc = to;
		this.interIndex = inter;
		this.op = op;
		this.value = value;
		this.reset = reset;
	}


	public int getFromLoc() {
		return fromLoc;
	}


	public int getToLoc() {
		return toLoc;
	}


	public int getInterIndex() {
		return interIndex;
	}


	public String getOp() {
		return this.op;
	}


	public int getValue() {
		return this.value;
	}
	
	public boolean getReset(){
		return this.reset;
	}

}
