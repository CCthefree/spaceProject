package programStructure;

import java.util.ArrayList;

/**
 * class of program point
 * 
 * @author zengke.cai
 */
public class ProgramPoint {

	// common info
	private int point; // current point

	private char type; // type of statement at this
						// point,'A'表示赋值语句，'C'表示子过程，'I'表示if语句

	private String statement; // the statement at this point

	private int nextPoint; // next point

	// assign statement info
	private String cvName;

	private int assignValue;

	// call statement info
	private String taskName;

	// if statement info
	private ArrayList<BoolExpr> exprs;

	private int elsePoint;


	/**
	 * constructor of assign statement
	 * 
	 */
	public ProgramPoint(int point, int nextPoint, char type, String cvName, int assignValue) {
		this.point = point;
		this.nextPoint = nextPoint;
		this.type = type;
		this.cvName = cvName;
		this.assignValue = assignValue;

		genStatement();
	}


	/**
	 * constructor of call statement
	 * 
	 */
	public ProgramPoint(int point, int nextPoint, char type, String taskName) {
		this.point = point;
		this.nextPoint = nextPoint;
		this.type = type;
		this.taskName = taskName;

		genStatement();
	}


	/**
	 * constructor of if statement
	 * 
	 */
	public ProgramPoint(int point, int nextPoint, char type, ArrayList<BoolExpr> exprs,
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
		if (this.type == 'A') {
			result += this.cvName + " = " + this.assignValue;
		}
		else if (this.type == 'C') {
			result += this.taskName + "()";
		}
		else {
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


	public char getType() {
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


	public ArrayList<BoolExpr> getExprs() {
		return exprs;
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
