package programStructure;

/**
 * tuple of boolean expression
 * 
 * @author zengke.cai
 */
public class BoolExpr {

	private String cvName;	//control variable name
	
	private int value;	//the value cvName comparing with

	private String op; // allow '>','>=','<','<=','==','!='


	/**
	 * constructor
	 */
	public BoolExpr(String name, String op, int value) {
		this.cvName = name;
		this.op = op;
		this.value = value;
	}


	/**
	 * get the whole expression in string form
	 * 
	 * @return
	 */
	public String getWholeExpr() {
		return this.cvName + this.op + this.value;
	}


	public String getCvName() {
		return cvName;
	}


	public int getValue() {
		return value;
	}


	public String getOp() {
		return op;
	}

}