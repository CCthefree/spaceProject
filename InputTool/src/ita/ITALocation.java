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

	// op and value express the location invariant
	private String op;

	private long value;

	// the location info of this state when display on edit UI
	public int xValue;

	public int yValue;

	public int height;

	public int width;


	/**
	 * constructor
	 */
	public ITALocation(String label, String op, long value) {
		this.label = label;
		this.op = op;
		this.value = value;
	}


	/**
	 * set the coordinate of the location
	 */
	public void setCoordinate(int x, int y) {
		this.xValue = x;
		this.yValue = y;
	}


	/**
	 * set the width and height of this location
	 */
	public void setShape(int w, int h) {
		this.width = w;
		this.height = h;
	}


	public String getLabel() {
		return label;
	}


	public String getOp() {
		return this.op;
	}


	public long getValue() {
		return this.value;
	}


	public ITAEdge getEdge() {
		return edge;
	}


	public void setEdge(ITAEdge edge) {
		this.edge = edge;
	}

}