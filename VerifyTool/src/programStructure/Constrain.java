package programStructure;

import java.util.ArrayList;

import fileOperator.Logger;

/**
 * constrain class, constrains are in form of <fVar - sVar op value> or
 * <fArray - sArray op value>
 * 
 * @author ting.rong
 * 
 */
public class Constrain {

	//parameters of the first form
	private int firstVar;	

	private int secondVar;		

	//parameters of the second form
	private int[] firstArray;	

	private int[] secondArray;

	private String op; // == or <= or >= or < or >

	private int value;	//constrain value


	/**
	 * constructor of first form of constrain
	 */
	public Constrain(int firstVar, int secondVar, String op, int value) {
		this.firstVar = firstVar;
		this.secondVar = secondVar;
		this.op = op;
		this.value = value;
	}


	/**
	 * constructor of second form of constrain
	 */
	public Constrain(int[] firstArray, int[] secondArray, String op, int value) {
		this.firstArray = firstArray;
		this.secondArray = secondArray;
		this.op = op;
		this.value = value;
	}


	/**
	 * constructor
	 * 
	 * @param exprs
	 *            : list of subExpr defined in SubExpr class
	 */
	public Constrain(ArrayList<SubExpr> exprs, String op, int value) {
		this.firstArray = new int[exprs.size()];
		this.secondArray = new int[exprs.size()];

		for (int i = 0; i < exprs.size(); i++) {
			this.firstArray[i] = exprs.get(i).getFirstPara();
			this.secondArray[i] = exprs.get(i).getSecondPara();
		}

		this.op = op;
		this.value = value;
	}


	public int getFirstVar() {
		return this.firstVar;
	}


	public int getSecondVar() {
		return this.secondVar;
	}


	public int[] getFirstArray() {
		return this.firstArray;
	}


	public int[] getSecondArray() {
		return this.secondArray;
	}


	public String getOp() {
		return this.op;
	}


	public int getValue() {
		return this.value;
	}


	/**
	 * TEST print constrain function
	 */
	public void printCons() {
		if (this.firstArray == null) {
			Logger.append(this.firstVar + " - " + this.secondVar + this.op + this.value + "\r\n");
		}
		else {
			String fp = "{";
			String sp = "{";
			for (int i = 0; i < this.firstArray.length; i++) {
				fp += this.firstArray[i] + " ";
				sp += this.secondArray[i] + " ";
			}
			fp += "}";
			sp += "}";
			Logger.append(fp + " - " + sp + this.op + this.value + "\r\n");
		}
	}
}
