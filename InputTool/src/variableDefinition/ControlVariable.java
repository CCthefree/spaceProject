package variableDefinition;

import java.util.ArrayList;

import util.ErrorInfo;
import util.Lexer;
import util.define;


/**
 * class of control variable
 */

public class ControlVariable {

	// column labels displayed on control variable table
	public static String[] labels = { "变量名", "下界", "上界", "初值", "顺序取值", "备注", "调用的程序" };
	// original parameter size of control variable, exclude that generated('procNames')
	public static int paraSize = 6;

	// 基本信息
	public String name; // 名称
	public String lowerBound; // 取值下界
	public String upperBound; // 取值上界
	public String initValue; // 初始值
	public String assignFlag; // 是否顺序取值的标记
	public String remark; // 备注

	// 衍生信息
	public int intLBD;
	public int intUBD;
	public int intInitValue;
	private ArrayList<String> procNames; // the names of procedure that access
											// this control variable
	private ArrayList<Integer> errorList; // record the index of syntax error of
											// parameters


	/**
	 * constructor without parameter
	 */
	public ControlVariable() {
		this.name = "";
		this.lowerBound = "0";
		this.upperBound = "-1";
		this.initValue = "0";
		this.assignFlag = "yes";
		this.remark = "";

		this.intInitValue = 0;
		this.intLBD = 0;
		this.intUBD = -1;
		this.procNames = new ArrayList<String>();
		this.errorList = new ArrayList<Integer>();
	}


	/**
	 * assign value to members of control variables
	 * 
	 * @param variableList
	 */
	public void setValue(String[] variableList) {
		this.name = variableList[0];
		this.lowerBound = variableList[1];
		this.upperBound = variableList[2];
		this.initValue = variableList[3];
		this.assignFlag = variableList[4];
		this.remark = variableList[5];
	}


	/**
	 * function of content check, include syntax and semantic check.
	 */
	public int contentCheck() {
		this.errorList.clear();

		syntaxCheck();

		if (this.errorList.isEmpty())
			return semanticCheck();
		else
			return define.syntaxError;
	}


	/**
	 * function of syntax check
	 */
	private void syntaxCheck() {
		if (!Lexer.isStructuredVar(this.name))
			this.errorList.add(0);

		// 数值类型的变量
		this.intLBD = Lexer.toInt(this.lowerBound);
		if (this.intLBD == Integer.MIN_VALUE || this.intLBD < 0)
			this.errorList.add(1);

		this.intUBD = Lexer.toInt(this.upperBound);
		if (this.intUBD == Integer.MIN_VALUE || this.intUBD < -1)
			this.errorList.add(2);

		this.intInitValue = Lexer.toInt(this.initValue);
		if (this.intInitValue == Integer.MIN_VALUE || this.intInitValue < 0)
			this.errorList.add(3);

		if (!this.assignFlag.equals("yes") && !this.assignFlag.equals("no"))
			this.errorList.add(4);
	}


	/**
	 * function of semantic check
	 */
	private int semanticCheck() {
		int flag = -1;

		if (hasSameName())
			flag = -2;
		else if (this.intUBD != -1 && this.intLBD > this.intUBD)
			flag = -3;
		else if (!isInRange())
			flag = -4;

		switch (flag) {
		case -2:
			ErrorInfo.add("【控制变量】 " + this.name + ": 已存在同名的变量!");
			break;
		case -3:
			ErrorInfo.add("【控制变量】 " + this.name + ": 下界超过上界!");
			break;
		case -4:
			ErrorInfo.add("【控制变量】 " + this.name + ": 初始值超出定义范围!");
			break;
		default:
			break;
		}
		return flag == -1 ? define.noError : define.semanticError;
	}


	/**
	 * test whether the 'initValue' is in the range of 'lowerBound' and
	 * 'upperBound'
	 * 
	 * @return
	 */
	private boolean isInRange() {
		// upperBound unknown, return true
		if (this.intUBD == -1)
			return true;
		else
			return (this.intInitValue >= this.intLBD && this.intInitValue <= this.intUBD);
	}


	/**
	 * function to check whether there exist duplication of the name in control
	 * variable list
	 * 
	 * @return true if two variable have the same name
	 */
	private boolean hasSameName() {
		for (ControlVariable cv : Model.controlVariableArray) {
			if (!cv.equals(this) && cv.name.equals(this.name)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * clear the parameter 'ProcNames'
	 */
	public void clearProcNames() {
		this.procNames.clear();
	}


	/**
	 * add a procedure name into the 'procNames'
	 */
	public void addProcName(String name) {
		// return if the name has already in
		for (String temp : this.procNames)
			if (temp.equals(name))
				return;

		this.procNames.add(name);
	}


	/**
	 * get all the procedure names in 'ProcNames', in string form, separated
	 * with delimiter
	 */
	public String getProcNames() {
		String result = "";
		String delimiter = ",";

		if (this.procNames.isEmpty())
			;
		else {
			int len = this.procNames.size();
			for (int j = 0; j < len - 1; j++)
				result += this.procNames.get(j) + delimiter;
			result += this.procNames.get(len - 1);
		}
		return result;
	}


	/**
	 * 
	 */
	public ArrayList<Integer> getErrorList() {
		return this.errorList;
	}


	/**
	 * get the content of a given controlVariable in table order
	 * 
	 * @return a string array of each attribute
	 */
	public static String[] getContent(ControlVariable cv) {
		return new String[] { cv.name, cv.lowerBound, cv.upperBound, cv.initValue, cv.assignFlag,
				cv.remark };
	}

}
