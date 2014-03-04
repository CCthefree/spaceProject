
package variableDefinition;

import java.util.ArrayList;

import util.ErrorInfo;
import util.Lexer;
import util.define;


/**
 * class of interruption
 */
public class Interruption{

	//column labels displayed on interruption table
	public static String[] labels = { "中断名", "优先级", "中断号", "中断类型", "重复频率", "Offset", "最小时间间隔", "最长时间要求", "备注", "静态执行时间区间", "WCET/UPBOUND" };
	//original parameter size of interruption, exclude that generated(WCET/UPDOUND) 
	public static int paraSize = 9;

	//基本信息
	public String name;
	public String prio; //优先级
	public String IRQ; //中断号
	public String type; //周期or随机
	public String repeat; //重复频率
	public String offset; //循环任务相对开始时间点
	public String interval; //最小时间间隔
	public String upperBound; //最长时间要求
	public String remark;	//备注
	
	public Procedure proc; //对应的中断处理程序
	
	//衍生信息
	public int intPrio; 
	public long longRepeat;
	public long longOffset;
	public long longUBD;
	public long longInterval;
	
	public long worstTime;
	public long bestTime;
	private ArrayList<Integer> errorList;


	/**
	 * constructor without parameter
	 * 
	 * @param info is used to semantic check
	 */
	public Interruption() {
		this.name = "";
		this.prio = "-1";
		this.IRQ = "no";
		this.type = "";
		this.repeat = "-1";
		this.offset = "-1";
		this.interval = "-1";
		this.upperBound = "-1";
		this.remark = "";
		
		this.proc = new Procedure();

		this.longInterval = -1;
		this.longOffset = -1;
		this.intPrio = -1;
		this.longRepeat = -1;
		this.longUBD = -1;
		this.errorList = new ArrayList<Integer>();
	}


	/**
	 * assign to members of interruption
	 * 
	 * @param variableList
	 */
	public void setValue(String[] variableList){
		this.name = variableList[0];
		this.prio = variableList[1];
		this.IRQ = variableList[2];
		this.type = variableList[3];
		this.repeat = variableList[4];
		this.offset = variableList[5];
		this.interval = variableList[6];
		this.upperBound = variableList[7];
		this.remark = variableList[8];
			
		this.proc.name = this.name;
	}
	


	/**
	 * function of content check, include syntax and semantic check.
	 */
	public int contentCheck(){
		this.errorList.clear();
		
		syntaxCheck();
		if(this.errorList.isEmpty()){
			return semanticCheck();
		}
		else
			return define.syntaxError;
	}


	/**
	 * function of syntax check
	 */
	private void syntaxCheck(){
		if(!Lexer.isVariableName(this.name))
			this.errorList.add(0);
		
		//数值类型的变量
		this.intPrio = Lexer.toInt(this.prio);
		if(this.intPrio == Integer.MIN_VALUE || this.intPrio < -1)
			this.errorList.add(1);
		
		if(!this.IRQ.matches("no|[a-z]+[0-9]+"))
			this.errorList.add(2);
		
		if(!(this.type.equals("periodical") || this.type.equals("random")))
			this.errorList.add(3);
		
		this.longRepeat = Lexer.toLong(this.repeat);
		if(this.longRepeat == Long.MIN_VALUE || this.longRepeat < -1)
			this.errorList.add(4);
		
		this.longOffset = Lexer.toLong(this.offset);
		if(this.longOffset == Long.MIN_VALUE || this.longOffset < -1)
			this.errorList.add(5);
		
		this.longInterval = Lexer.toLong(this.interval);
		if(this.longInterval == Long.MIN_VALUE || this.longInterval < -1)
			this.errorList.add(6);
		
		this.longUBD = Lexer.toLong(this.upperBound);
		if(this.longUBD == Long.MIN_VALUE || this.longUBD < -1)
			this.errorList.add(7);
	}


	/**
	 * function of semantic check
	 */
	private int semanticCheck(){
		int flag = -1; //-1 for no error

		if(hasSameName())
			flag = -2;
		else if(this.intPrio > 256 || this.intPrio < -1)
			flag = -3;
		else if((this.type.equals("periodical") && this.longRepeat == -1) || (this.type.equals("random") && this.longRepeat != -1))
			flag = -4;
		else if(this.type.equals("periodical") && this.longInterval != -1)
			flag = -5;
		else if(this.intPrio < 255 && this.type.equals("random") && this.longInterval == -1)
			flag = -6;
		else if(this.type.equals("random") && this.longOffset != -1)
			flag = -7;
		else if(checkIRQ())
			flag = -8;

		switch (flag) {
			case -2:
				ErrorInfo.add("【中断/任务】 " + this.name + ": 已存在同名的中断!");
				break;
			case -3:
				ErrorInfo.add("【中断/任务】" + this.name + ": 优先级超出定义范围（-1-256）!");
				break;
			case -4:
				ErrorInfo.add("【中断/任务】 " + this.name + ": 中断类型与中断重复频率相冲突!");
				break;
			case -5:
				ErrorInfo.add("【中断/任务】" + this.name + ": 周期中断不能有中断间隔!");
				break;
			case -6:
				ErrorInfo.add("【中断/任务】 " + this.name + ": 缺少中断间隔!");
				break;
			case -7:
				ErrorInfo.add("【中断/任务】" + this.name + ": 随机中断不能有offset!");
				break;
			case -8:
				ErrorInfo.add("【中断/任务】 " + this.name + ": 存在相同中断号但不同优先级的中断!");
				break;
			default:
				break;
		}
		return flag == -1 ? define.noError : define.semanticError;
	}


	/**
	 * function to check whether there exist duplication of the name in
	 * interruption list
	 * 
	 * @return true if two interruption have the same name
	 */
	private boolean hasSameName(){
		for (Interruption inter : Model.interArray){
			if(!inter.equals(this) && inter.name.equals(this.name)){
				return true;
			}
		}
		return false;
	}


	/**
	 * function to check whether the interruptions with the same IRQ have the
	 * different priority
	 * 
	 * @return true if they have different priority
	 */
	private boolean checkIRQ(){
		for (Interruption inter : Model.interArray){
			if(!this.IRQ.equals("no") && !inter.equals(this) && inter.IRQ.equals(this.IRQ))
				if(!inter.prio.equals(this.prio))
					return true;
		}
		return false;
	}


	/**
	 * 
	 */
	public ArrayList<Integer> getErrorList(){
		return this.errorList;
	}

	
	/**
	 * get the content of a given interruption
	 * in table order
	 * @return a string array of each attribute
	 */
	public static String[] getContent(Interruption inter){
		return new String[] { inter.name, inter.prio, inter.IRQ, inter.type,
				 inter.repeat, inter.offset, inter.interval, inter.upperBound, inter.remark
				};
	}
	
}
