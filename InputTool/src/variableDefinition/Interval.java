package variableDefinition;


import java.util.ArrayList;

import util.ErrorInfo;
import util.Lexer;
import util.define;

public class Interval {

	public static String[] labels = new String[] { "中断号", "最小中断间隔" };

	// 基本信息
	public String IRQ; // 中断号
	public String leastInterval;// 最小间隔

	// 衍生信息
	public long longLeastInterval;
	private ArrayList<Integer> errorList;


	public Interval() {
		this.IRQ = "";
		this.leastInterval = "-1";

		this.longLeastInterval = -1;
		this.errorList = new ArrayList<Integer>();
	}


	/**
	 * 将参数列表赋值给interval的各个参数
	 * 
	 * @param variableList
	 */
	public void setValue(String[] variableList) {
		this.IRQ = variableList[0];
		this.leastInterval = variableList[1];
	}


	/**
	 * 内容检查，包括语法检查与语义检查
	 * 
	 * @return
	 */
	public int contentCheck() {
		this.errorList.clear();
		
		syntaxCheck();
		if (this.errorList.isEmpty()) {
			return semanticCheck();
		}
		else
			return define.syntaxError;
	}


	/**
	 * 语法检查
	 */
	private void syntaxCheck() {
		if (!Lexer.isIRQ(this.IRQ))
			this.errorList.add(0);

		this.longLeastInterval = Lexer.toLong(this.leastInterval);
		if(this.longLeastInterval == Long.MIN_VALUE || this.longLeastInterval < -1)
			this.errorList.add(1);
	}


	/**
	 * 语义检查
	 * 
	 * @return
	 */
	private int semanticCheck() {
		int flag = -1;

		if (hasSameIRQ())
			flag = -2;

		switch (flag) {
		case -2:
			ErrorInfo.add("【中断间隔】 " + this.IRQ + ": 已存在同名的中断号!");
			break;
		default:
			break;
		}

		return flag == -1 ? define.noError : define.semanticError;
	}


	/**
	 * function to check whether there exist duplication of IRQ in interval list
	 */
	private boolean hasSameIRQ() {
		for (Interval interval : Model.intervalArray)
			if (!interval.equals(this) && interval.IRQ.equals(this.IRQ))
				return true;
		return false;
	}


	/**
	 * 获取给定的中断间隔的各个参数
	 * 
	 * @param interval
	 * @return
	 */
	public static String[] getContent(Interval interval) {
		return new String[] { interval.IRQ, interval.leastInterval };
	}


	/**
	 * 获取错误的列号
	 * 
	 * @return
	 */
	public ArrayList<Integer> getErrorList() {
		return this.errorList;
	}

}
