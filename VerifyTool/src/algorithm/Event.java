package algorithm;

import fileOperator.Logger;
import initializer.Model;
import ita.ITALocation;

import java.util.ArrayList;

import programStructure.BoolExpr;
import programStructure.CVMap;
import programStructure.Interruption;
import programStructure.Procedure;
import programStructure.ProgramPoint;
import util.define;

/**
 * class of event of the running model
 * 
 * @author zengke.cai
 * 
 */
public class Event {

	private int type; // indicate the type of event;

	private int ITAIndex; // index of transition ITA, for transition event
	
	private boolean reset; //indicate whether the ITA transition reset clock, for transition event

	private int procIndex; // index of interruption, for all events

	private int pnt; // result program point of procedure, for move event


	/**
	 * constructor, parameters that not used set as '-1'
	 */
	public Event(int type, int ITAIndex, int procIndex, int pnt) {
		this.type = type;
		this.ITAIndex = ITAIndex;
		this.pnt = pnt;
		this.procIndex = procIndex;
		this.reset = false;
	}



	/**
	 * calculate all the potential events at global state 'gs'
	 * 
	 * @param gs  : current global state
	 * @return: list of all the potential events
	 */
	public static ArrayList<Event> potentialEvents(GlobalState gs) {
		ArrayList<Event> eventList = new ArrayList<Event>();
		Event newEvent;

		//successor of interruption vector 
		Interruption highestInter = Model.getInterAt(gs.getInterVec().getHighestInter());
		Interruption topInter = Model.getInterAt(gs.getStack().getTopProc());

		int prioOfVec = (highestInter == null) ? Integer.MAX_VALUE : highestInter.getPriority();
		int prioOfStack = (topInter == null) ? Integer.MAX_VALUE : topInter.getPriority();
		if (prioOfVec < prioOfStack) { // there has higher priority interruption
										// in interVec, return only this event
			/** 事件1: 高级中断进入CPU栈，此时候选事件唯一 **/
			newEvent = new Event(define.push, -1, gs.getInterVec().getHighestInter(), -1);
			eventList.add(newEvent);

			return eventList;
		}

		// successor of CPU stack procedure
		if (!gs.getStack().isEmpty()) {

			Procedure topIp = Model.getProcAt(gs.getStack().getTopProc());
			int topPnt = gs.getStack().getTopPnt();

			ProgramPoint pp = topIp.getPP(topPnt);
			int newPnt = pp.getNextPoint();
			if (pp.getType() == 'I') { // current point is if statement,
										// test the boolExpr and set
										// correct newPnt
				if (testExpr(pp.getExprs(), gs.getCvMap()) == false)
					newPnt = pp.getElsePoint();
			}

			if (newPnt == topIp.getEndPnt()) {	//新的程序点为procedure的结束点
				/*** 事件3：procedure执行并出栈 **/
				newEvent = new Event(define.pop, -1, gs.getStack().getTopProc(), -1);
			}
			else
				/*** 事件2：procedure继续执行 **/
				newEvent = new Event(define.move, -1, gs.getStack().getTopProc(), newPnt);
			
			eventList.add(newEvent);
		}

		// successor event of ITA
		{
			for (int i = 0; i < Model.getITACount(); i++) {

				ITALocation loc = Model.getITAAt(i).getLocation(gs.getLocVec()[i]);
				if (loc.getEdge() != null) {	//自动机当前位置有出边
					int interIndex = loc.getEdge().getInterIndex();
					/** 事件0：自动机事件 **/
					newEvent = new Event(define.trans, i, interIndex, -1);
					//如果转换边重制时钟，在event中标记
					if(loc.getEdge().getReset() == true)
						newEvent.setReset(true);
					
					eventList.add(newEvent);
				}
			}
		}

		// sort(eventList);
		return eventList;
	}


	/**
	 * test the value of boolean expressions
	 * 
	 * @param exprs
	 *            connect by '&&'
	 * @return
	 */
	public static boolean testExpr(ArrayList<BoolExpr> exprs, CVMap cvMap) {
		if (exprs == null || exprs.isEmpty()) {
			return true; // TODO
		}

		for (BoolExpr expr : exprs) {
			boolean flag = false;

			String cv = expr.getCvName();
			String op = expr.getOp();
			int testValue = expr.getValue();
			int realValue = cvMap.getValue(cv);

			if (op.equals(">") && realValue > testValue)
				flag = true;
			else if (op.equals(">=") && realValue >= testValue)
				flag = true;
			else if (op.equals("<") && realValue < testValue)
				flag = true;
			else if (op.equals("<=") && realValue <= testValue)
				flag = true;
			else if (op.equals("==") && realValue == testValue)
				flag = true;
			else if (op.equals("!=") && realValue != testValue)
				flag = true;

			if (flag == false)// one false, all false
				return false;
		}
		return true;
	}


	/**
	 * sort event according to their type, decrease
	 */
	public static void sort(ArrayList<Event> events) {
		int size = events.size();
		while (size > 0) {
			int index = 0;
			for (int i = 1; i < size; i++) {
				if (events.get(i).type > events.get(index).type)
					index = i;
			}
			// remove and push behind
			Event temp = events.remove(index);
			events.add(temp);

			size--;
		}
	}


	public void setReset(boolean r){
		this.reset = r;
	}
	
	
	public int getType() {
		return type;
	}


	public int getITAIndex() {
		return ITAIndex;
	}


	public int getProcIndex() {
		return procIndex;
	}


	public int getPnt() {
		return pnt;
	}
	
	
	public boolean getReset(){
		return reset;
	}


	/**
	 * TEST function to print event information
	 */
	public void printEvent() {
		Interruption inter = Model.getInterAt(this.procIndex);
		String proc = (inter == null) ? "" : inter.getName();
		
		if (this.type == 0) {
			Logger.append("ITA " + this.ITAIndex + " goes to next locaiton;");
			if (this.procIndex != -1){
				Logger.append(" interruption " + proc + " fired;\r\n");
			}
			else
				Logger.append("\r\n");

		}
		else if (this.type == 1) {
			String statement = Model.getStatement(procIndex, 0);
			Logger.append("new procedure " + proc + " pushed into stack, executing " + statement + ";\r\n");
		}
		else if (this.type == 2) {
			String statement = Model.getStatement(procIndex, pnt);
			Logger.append("procedure " + proc + " executing " + statement + ";\r\n");
		}
		else
			Logger.append("procedure " + proc + " poped out;\r\n");
	}

}
