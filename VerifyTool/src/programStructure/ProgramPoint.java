package programStructure;

import java.util.ArrayList;

import util.BoolExpr;
import util.define;

/**
 * class of program point
 * 
 * @author zengke.cai
 */
public class ProgramPoint {

	// common info
	private int point; // current point

	private int type; // type of statement at this

	private String statement; // the statement at this point

	private int nextPoint; // next point

	// assign statement info
	private String cvName;

	private int assignValue;

	// call statement info
	private String taskName;
	
	// open/close statement info
	private String IRQ;

	// if statement info
	private ArrayList<BoolExpr> exprs;

	private int elsePoint;


	/**
	 * constructor of assign statement
	 * 
	 */
	public ProgramPoint(int point, int nextPoint, int type, String cvName, int assignValue) {
		this.point = point;
		this.nextPoint = nextPoint;
		this.type = type;
		this.cvName = cvName;
		this.assignValue = assignValue;

		genStatement();
	}


	/**
	 * constructor of call statement; open/close statement
	 * 
	 */
	public ProgramPoint(int point, int nextPoint, int type, String name) {
		this.point = point;
		this.nextPoint = nextPoint;
		this.type = type;
		
		if(type == define.call)
			this.taskName = name;
		else if(type == define.open || type == define.close)
			this.IRQ = name;

		genStatement();
	}


	/**
	 * constructor of if statement
	 * 
	 */
	public ProgramPoint(int point, int nextPoint, int type, ArrayList<BoolExpr> exprs,
			int elsePoint) {
		this.point = point;
		this.nextPoint = nextPoint;
		this.type = type;
		this.exprs = exprs;
		this.elsePoint = elsePoint;

		genStatement();
	}


	/**
	 * generate statement of current program point
	 */
	public void genStatement() {
		String result = "";
		if (this.type == define.assign) {
			result += this.cvName + " = " + this.assignValue;
		}
		else if (this.type == define.call) {
			result += this.taskName + "()";
		}
		else if(this.type == define.open){
			result += "open " + this.IRQ;
		}
		else if(this.type == define.close){
			result += "close " + this.IRQ;
		}
		else if(this.type == define.IF){
			int i = 0;
			for (; i < this.exprs.size() - 1; i++)
				result += this.exprs.get(i).getWholeExpr() + "&&";
			result += this.exprs.get(i).getWholeExpr();
		}

		this.statement = result;
	}


	public int getPoint() {
		return point;
	}


	public int getType() {
		return type;
	}


	public int getNextPoint() {
		return nextPoint;
	}


	public String getCvName() {
		return cvName;
	}


	public int getAssignValue() {
		return assignValue;
	}


	public String getTaskName() {
		return taskName;
	}

	
	public String getIRQ(){
		return IRQ;
	}

	
	public ArrayList<BoolExpr> getExprs() {
		return exprs;
	}

	
	public void setExprs(ArrayList<BoolExpr> exp){
		this.exprs = exp;
	}

	public int getElsePoint() {
		return elsePoint;
	}


	public String getStatement() {
		return this.statement;
	}


	public void setNextPoint(int nextPoint) {
		this.nextPoint = nextPoint;
	}


	public void setElsePoint(int elsePoint) {
		this.elsePoint = elsePoint;
	}
}
