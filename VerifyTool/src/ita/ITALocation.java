package ita;

/**
 * class of ITA location
 * 
 * @author zengke.cai
 * 
 */
public class ITALocation {

	private String label; // name of location

	private ITAEdge edge; // edge outgoing

	// op and value express the constraint of location
	private String op;

	private int value;


	/**
	 * constructor
	 */
	public ITALocation(String label, String op, int value) {
		this.label = label;
		this.op = op;
		this.value = value;
	}


	public String getLabel() {
		return label;
	}


	public String getOp() {
		return this.op;
	}


	public int getValue() {
		return this.value;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public ITAEdge getEdge() {
		return edge;
	}


	public void setEdge(ITAEdge edge) {
		this.edge = edge;
	}

}