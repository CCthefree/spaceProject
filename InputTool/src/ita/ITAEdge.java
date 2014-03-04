package ita;

import java.util.ArrayList;

/**
 * class of ITA Edge
 * 
 * @author zengke.cai
 * 
 */
public class ITAEdge {

	private int fromLoc; // index of from location

	private int toLoc; // index of to location

	private String interName; // corresponding interruption name

	// transition constrain;
	private String op;

	private long value;

	// reset clock flag
	private boolean reset;

	// coordinate of points on this edge
	public ArrayList<Integer> xPoints;

	public ArrayList<Integer> yPoints;


	/**
	 * constructor
	 */
	public ITAEdge(int from, int to, String inter, String op, long value, boolean reset) {
		this.fromLoc = from;
		this.toLoc = to;
		this.interName = inter;
		this.op = op;
		this.value = value;
		this.reset = reset;

		this.xPoints = new ArrayList<Integer>();
		this.yPoints = new ArrayList<Integer>();
	}


	/**
	 * add a new point coordinate
	 */
	public void addPoint(int x, int y) {
		this.xPoints.add(x);
		this.yPoints.add(y);
	}


	public int getFromLoc() {
		return fromLoc;
	}


	public int getToLoc() {
		return toLoc;
	}


	public String getInterName() {
		return interName;
	}


	public String getOp() {
		return this.op;
	}


	public long getValue() {
		return this.value;
	}


	public boolean getReset() {
		return this.reset;
	}

}
