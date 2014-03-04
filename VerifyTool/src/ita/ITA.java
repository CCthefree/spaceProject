package ita;

import java.util.ArrayList;

import fileOperator.ITATest;

/**
 * class of interruption timed automata
 * 
 * @author zengke.cai
 * 
 */
public class ITA {

	private int initLoc; // initial location index,(index in locations, not the
							// label of location)

	private ArrayList<ITALocation> locations; // ITA locations


	public ITA() {
		this.locations = new ArrayList<ITALocation>();
	}


	public void addLocation(ITALocation l) {
		this.locations.add(l);
	}


	public void setInitLoc(int i) {
		this.initLoc = i;
	}


	/**
	 * get location at 'index'
	 * 
	 * @param index
	 * @return null if index is illegal
	 */
	public ITALocation getLocation(int index) {
		if (index >= this.locations.size() || index < 0) {
			return null;
		}
		else
			return this.locations.get(index);
	}


	/**
	 * get index of initialize location
	 */
	public int getInitLoc() {
		return this.initLoc;
	}


	/**
	 * TEST test function of ITA
	 */
	public void ITAtest() {
		for (int i = 0; i < this.locations.size(); i++) {
			ITALocation loc = this.locations.get(i);
			ITAEdge edge = loc.getEdge();
			ITATest.append("location :" + i + ", lable " + loc.getLabel() + ", ");
			ITATest.append("invariant :" + loc.getOp() + loc.getValue() + "\r\n");
			ITATest.append(" outgoing edge : from " + edge.getFromLoc() + " to " + edge.getToLoc()
					+ ", interIndex : " + edge.getInterIndex() + ", constrain: " + edge.getOp()
					+ edge.getValue() + ", reset: " + edge.getReset()+ "\r\n");
		}
	}
}
