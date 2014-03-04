package ita;

import java.util.ArrayList;

import variableDefinition.Interruption;

/**
 * class of interruption timed automata, construct three types of ITA according
 * to parameters
 * 
 * @author zengke.cai
 * 
 */
public class ITA {

	private int initLoc; // index of initial location

	private String clock; // the name of the clock of this ITA(only one)

	private ArrayList<ITALocation> locations; // ITA locations

	private ArrayList<ITAEdge> edges; // ITA edges


	
	/**
	 * construct ITA of interruption
	 * 
	 * @param inter
	 *            : random or periodical interruption
	 * 
	 */
	public ITA(Interruption inter) {
		this.initLoc = -1;
		this.clock = "x";
		this.locations = new ArrayList<ITALocation>();
		this.edges = new ArrayList<ITAEdge>();
		
		// random interruption
		if (inter.type.equals("random")) {
			ITALocation initLoc = new ITALocation("s0", "", -1);// 随机中断的location无约束
			ITAEdge edge = new ITAEdge(0, 0, inter.name, ">=", inter.longInterval, true);
			initLoc.setEdge(edge);

			this.locations.add(initLoc);
			this.edges.add(edge);
			this.initLoc = 0;
		}
		else { // periodical interruption
			ITALocation initLoc = new ITALocation("s0", "<=", inter.longRepeat);
			ITALocation loc1 = new ITALocation("s1", "<=", inter.longRepeat);// location
																			// 1的约束为“<=周期”
			ITAEdge edge0 = new ITAEdge(0, 1, inter.name, "", -1, true);
			ITAEdge edge1 = new ITAEdge(1, 1, inter.name, "==", inter.longRepeat, true);
			initLoc.setEdge(edge0);
			loc1.setEdge(edge1);

			this.locations.add(initLoc);
			this.locations.add(loc1);
			this.edges.add(edge0);
			this.edges.add(edge1);
			this.initLoc = 0;
		}

		initCoordinate();
	}


	/**
	 * construct ITA of periodical system task
	 * 
	 * @param sysTasks
	 *            : list of system tasks with the same period
	 * 
	 */
	public ITA(ArrayList<Interruption> sysTasks) {
		this.initLoc = -1;
		this.clock = "x";
		this.locations = new ArrayList<ITALocation>();
		this.edges = new ArrayList<ITAEdge>();

		sort(sysTasks);

		int i = 0;
		// one location, one edge; the edge fire the task with same index
		for (; i < sysTasks.size(); i++) {
			Interruption inter = sysTasks.get(i);
			ITALocation loci = new ITALocation("s" + i, "<=", inter.longOffset);
			ITAEdge edgei = new ITAEdge(i, i + 1, inter.name, "==", inter.longOffset, false);
			loci.setEdge(edgei);

			this.locations.add(loci);
			this.edges.add(edgei);
		}
		// end location of this cycle, point back to the 1th location(initLoc is
		// the 0th), fire 0th task
		ITALocation loc = new ITALocation("s" + i, "<=", sysTasks.get(0).longRepeat);
		ITAEdge edge = new ITAEdge(i, 1, sysTasks.get(0).name, "==", sysTasks.get(0).longRepeat,
				true);
		loc.setEdge(edge);

		this.locations.add(loc);
		this.edges.add(edge);
		this.initLoc = 0;

		initCoordinate();
	}


	/**
	 * assign coordinate value to each location and edge
	 */
	public void initCoordinate() {
		int xStep = 100; // 各状态之间横向间隔
		int y = 100; // 状态水平线纵坐标值
		int xStart = 50; // 初始状态横坐标值
		//状态的形状大小值
		int locWidth = 30;	
		int locHeight = 30;

		/** 状态坐标设置**/
		for (int locIndex = 0; locIndex < this.locations.size(); locIndex++) {
			locations.get(locIndex).setShape(locWidth, locHeight);
			locations.get(locIndex).setCoordinate(xStart + xStep * locIndex, y);
		}
		
		/** 边上点的坐标设置**/
		for (int edgeIndex = 0; edgeIndex < this.edges.size(); edgeIndex++){
			int from = edges.get(edgeIndex).getFromLoc();
			int to = edges.get(edgeIndex).getToLoc();
			
			int startX = locations.get(from).xValue + locWidth/2;
			int startY = locations.get(from).yValue;
			int endX = locations.get(to).xValue - locWidth/2;
			int endY = startY;
			
			this.edges.get(edgeIndex).addPoint(startX, startY);
			
			if(from < to){	//顺序指向， 没有转折点
					
			}
			else{	//循环,增加两个转折点
				int x1 = locations.get(from).xValue + 50;
				int y1 = locations.get(from).yValue - 50;
				
				int x2 = locations.get(to).xValue - 50; 
				int y2 = locations.get(to).yValue - 50;
				
				this.edges.get(edgeIndex).addPoint(x1, y1);
				this.edges.get(edgeIndex).addPoint(x2, y2);
			}
			
			this.edges.get(edgeIndex).addPoint(endX, endY);
		}
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
	 * get edge at 'index'
	 * 
	 * @param index
	 * @return
	 */
	public ITAEdge getEdge(int index) {
		if (index >= this.edges.size() || index < 0)
			return null;
		else
			return this.edges.get(index);
	}


	/**
	 * get number of locations
	 * 
	 * @return
	 */
	public int getLocCount() {
		return this.locations.size();
	}


	/**
	 * get number of edges
	 * 
	 * @return
	 */
	public int getEdgeCount() {
		return this.edges.size();
	}


	/**
	 * get index of initialize location
	 */
	public int getInitLoc() {
		return this.initLoc;
	}


	public String getClock() {
		return this.clock;
	}


	/**
	 * sort system tasks according to their offset, increase
	 * 
	 * @param sysTasks
	 */
	public void sort(ArrayList<Interruption> sysTasks) {
		int size = sysTasks.size();
		while (size > 0) {
			// find the minimum offset
			int index = 0;
			for (int i = 1; i < size; i++) {
				if (sysTasks.get(i).longOffset < sysTasks.get(index).longOffset)
					index = i;
			}
			// remove and push behind
			Interruption temp = sysTasks.remove(index);
			sysTasks.add(temp);

			size--;
		}
	}
}
