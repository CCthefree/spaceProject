package variableDefinition;

import java.util.ArrayList;

import util.ErrorInfo;
import util.Lexer;
import util.define;


/**
 * class of task
 */

public class Task {

	// column labels displayed on task table
	public static String[] labels = { "子过程名", "时间下界", "时间上界", "完成时间", "读资源", "写资源", "通信子过程", "备注",
			"调用的程序" };

	// record the original parameter size of task, exclude generated
	// parameters('procNames')
	public static int paraSize = 8;

	// 基本信息
	public String name; // 子过程名
	public String lowerBound; // 静态执行时间下界
	public String upperBound; // 静态执行时间上界
	public String finishTime; // 要求完成时间
	public ArrayList<String> readVariable; // 读取的资源列表
	public ArrayList<String> writeVariable; // 写的资源
	public String commFlag; // 通信子过程标记
	public String remark; // 备注
	public Procedure proc; // 可选，不是所有子过程都有处理程序

	// 衍生信息
	public long longLBD;
	public long longUBD;
	public long longFinish;
	private ArrayList<String> procNames; // the names of procedure that call
											// this task
	private ArrayList<Integer> errorList; // record the syntax error index in
											// parameter list


	/**
	 * constructor
	 */
	public Task() {
		this.name = "";
		this.lowerBound = "0";
		this.upperBound = "-1";
		this.finishTime = "-1";
		this.readVariable = new ArrayList<String>();
		this.writeVariable = new ArrayList<String>();
		this.commFlag = "no";
		this.remark = "";

		this.longFinish = -1;
		this.longLBD = 0;
		this.longUBD = -1;
		this.procNames = new ArrayList<String>();
		this.errorList = new ArrayList<Integer>();
	}


	/**
	 * assign value to members of task
	 * 
	 * @param variableList
	 */
	public void setValue(String[] variableList) {
		this.name = variableList[0];
		this.lowerBound = variableList[1];
		this.upperBound = variableList[2];
		this.finishTime = variableList[3];
		this.readVariable = variableSetAnalysis(variableList[4]);
		this.writeVariable = variableSetAnalysis(variableList[5]);
		this.commFlag = variableList[6];
		this.remark = variableList[7];
	}


	/**
	 * function to analysis the content of task, include syntax and semantic
	 * analysis
	 */
	public int contentCheck() {
		this.errorList.clear();
		// syntax check
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
		if (!Lexer.isVariableName(this.name))
			this.errorList.add(0);

		// 数值类型的变量
		this.longLBD = Lexer.toLong(this.lowerBound);
		if (this.longLBD == Long.MIN_VALUE || this.longLBD < 0)
			this.errorList.add(1);

		this.longUBD = Lexer.toLong(this.upperBound);
		if (this.longUBD == Long.MIN_VALUE || this.longUBD < -1)
			this.errorList.add(2);

		this.longFinish = Lexer.toLong(this.finishTime);
		if (this.longFinish == Long.MIN_VALUE || this.longFinish < -1)
			this.errorList.add(3);

		if (this.readVariable == null) // 读资源
			this.errorList.add(4);

		if (this.writeVariable == null) // 写资源
			this.errorList.add(5);

		if (!this.commFlag.equals("yes") && !this.commFlag.equals("no"))
			this.errorList.add(6);
	}


	/**
	 * function of semantic check
	 * 
	 * @return error flag
	 */
	private int semanticCheck() { // 判断语义
		int flag = -1;

		if (hasSameName())
			flag = -2;
		else if (this.longUBD != -1 && this.longLBD > this.longUBD)
			flag = -3;
		else if (!checkShareResource(this.readVariable) || !checkShareResource(this.writeVariable))
			flag = -4;

		switch (flag) {
		case -2:
			ErrorInfo.add("【子过程】 " + this.name + ": 已存在同名的子过程!");
			break;
		case -3:
			ErrorInfo.add("【子过程】 " + this.name + ": 时间下界超过上界!");
			break;
		case -4:
			ErrorInfo.add("【子过程】 " + this.name + ": 共享资源没有定义!");
			break;
		default:
			break;
		}
		return flag == -1 ? define.noError : define.semanticError;
	}


	/**
	 * function to check read/write variable set
	 * 
	 * @return the variable list, return null if existing syntax error
	 */
	private ArrayList<String> variableSetAnalysis(String list) {
		ArrayList<String> result = new ArrayList<String>();
		if (list.equals(""))
			;
		else {
			String[] variables = list.split(",");
			for (int i = 0; i < variables.length; i++) {
				if (Lexer.isStructuredVar(variables[i]))
					result.add(variables[i]);
				else
					return null;
			}
		}
		return result;
	}


	/**
	 * function to check whether the read/write resource is in share resource
	 * list
	 * 
	 * @return true if all the read/write resource is in, otherwise false
	 */
	private boolean checkShareResource(ArrayList<String> strList) {
		if (strList == null || strList.size() == 0)
			return true;

		int count = 0;
		for (String temp : strList) {
			for (ShareResource sr : Model.shareResourceArray) {
				if (temp.equals(sr.name)) {
					count++;
					break;
				}
			}
		}
		if (count == strList.size())
			return true;
		else
			return false;
	}


	/**
	 * function to analysis all the share resources read by the task, the
	 * analysis result is adding the task name into 'readTaskNames' of
	 * corresponding share resources
	 */
	public void containedReadSR() {
		if (this.readVariable != null && !this.readVariable.isEmpty()) {
			for (String name : this.readVariable) {
				ShareResource sr = Model.getSR(name);
				if (sr != null)
					sr.addReadTaskName(this.name);
			}
		}
	}


	/**
	 * function to analysis all the share resources written by the task, the
	 * analysis result is adding the task name into 'writeTaskNames' of
	 * corresponding share resources
	 */
	public void containedWriteSR() {
		if (this.writeVariable != null && !this.writeVariable.isEmpty()) {
			for (String name : this.writeVariable) {
				ShareResource sr = Model.getSR(name);
				if (sr != null)
					sr.addWriteTaskName(this.name);
			}
		}
	}


	/**
	 * function to check whether there exist duplication of the name in task
	 * list
	 * 
	 * @return true if two task have the same name, otherwise false
	 */
	private boolean hasSameName() {
		for (Task task : Model.taskArray) {
			if (!task.equals(this) && task.name.equals(this.name)) {
				return true;
			}
		}
		return false;
	}
	

	/**
	 * update information of the task according to its procedure
	 */
	public void setProc(Procedure newProc) {
		this.proc = newProc;
	}
	
	
	/**
	 * set lowerBound and upperBound of the task
	 */
	public void setBound(long lbd, long ubd){
		this.longLBD = lbd;
		this.longUBD = ubd;
		this.lowerBound = String.valueOf(lbd);
		this.upperBound = String.valueOf(ubd);
	}
	
	
	/**
	 * add read/write SRs into the list of the task
	 */
	public void addSR(ArrayList<String> read, ArrayList<String> write) {
		// add read SRs
		if(read != null)
			for (String str : read) {
				if (!this.readVariable.contains(str))
					this.readVariable.add(str);
			}

		// add write SRs
		if(write != null)
			for (String str : write) {
				if (!this.writeVariable.contains(str))
					this.writeVariable.add(str);
			}
	}
	
	
	public void removeProc(){
		this.proc = null;
	}


	/**
	 * clear the content of 'ProcNames'
	 */
	public void clearProcNames() {
		this.procNames.clear();
	}


	/**
	 * add a procedure name into the 'procNames'
	 */
	public void addProcName(String name) {
		// return if the name has already in
		if (this.procNames.contains(name))
			return;
		this.procNames.add(name);
	}


	/**
	 * get all the procedure names in 'ProcNames', in string form
	 */
	public String getProcNames() {
		String result = "";

		if (this.procNames.isEmpty())
			;
		else {
			int len = this.procNames.size();
			for (int j = 0; j < len - 1; j++)
				result += this.procNames.get(j) + ",";
			result += this.procNames.get(len - 1);
		}
		return result;
	}


	/**
	 * get all the read shareResources in a string form
	 */
	public String getReadResource() {
		String result = "";

		if (this.readVariable == null || this.readVariable.isEmpty())
			;
		else {
			int len = this.readVariable.size();
			for (int j = 0; j < len - 1; j++)
				result += this.readVariable.get(j) + ",";
			result += this.readVariable.get(len - 1);
		}
		return result;
	}


	/**
	 * get all the write shareResources in a string form
	 */
	public String getWriteResource() {
		String result = "";

		if (this.writeVariable == null || this.writeVariable.isEmpty())
			;
		else {
			int len = this.writeVariable.size();
			for (int j = 0; j < len - 1; j++)
				result += this.writeVariable.get(j) + ",";
			result += this.writeVariable.get(len - 1);
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
	 * get the content of a given task in table order
	 * 
	 * @return a string array of each attribute
	 */
	public static String[] getContent(Task task) {
		return new String[] { task.name, task.lowerBound, task.upperBound, task.finishTime,
				task.getReadResource(), task.getWriteResource(), task.commFlag, task.remark };
	}
}
