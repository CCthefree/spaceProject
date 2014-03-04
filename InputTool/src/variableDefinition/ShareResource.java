
package variableDefinition;

import java.util.ArrayList;

import util.ErrorInfo;
import util.Lexer;
import util.define;


/**
 * class of share variable, extends variable
 */

public class ShareResource{

	//column labels displayed on share resource table
	public static String[] labels = { "资源名", "调用的子过程(读)", "调用的子过程(写)" };
	//original parameter size of share resource, exclude that generated('readTaskNames','writeTaskNames') 
	public static int paraSize = 1;

	//基本信息
	public String name;

	//衍生信息
	private ArrayList<String> readTaskNames; //the names of tasks that read the share resource
	private ArrayList<String> writeTaskNames; //the names of tasks that write the share resource
	private ArrayList<Integer> errorList;


	/**
	 * constructor
	 */
	public ShareResource() {
		this.name = "";
		this.readTaskNames = new ArrayList<String>();
		this.writeTaskNames = new ArrayList<String>();
		this.errorList = new ArrayList<Integer>();
	}


	/**
	 * constructor with parameter
	 */
	public ShareResource(String name) {
		this.name = name;
		this.readTaskNames = new ArrayList<String>();
		this.writeTaskNames = new ArrayList<String>();
		this.errorList = new ArrayList<Integer>();
	}


	/**
	 * assign value to members of share resource
	 */
	public void setValue(String str){
		this.name = str;
	}


	/**
	 * function of content check, include syntax check and semantic check
	 */
	public int contentCheck(){
		this.errorList.clear();
		
		syntaxCheck();
		if(this.errorList.isEmpty())
			return semanticCheck();
		else
			return define.syntaxError;
	}


	/**
	 * function of syntax check
	 */
	private void syntaxCheck(){
		if(!Lexer.isStructuredVar(this.name))
			this.errorList.add(0);
	}


	/**
	 * function of semantic check
	 */
	private int semanticCheck(){
		int flag = -1; //-1 for no error, -2 for duplication of variable name
		if(hasSameName()){
			ErrorInfo.add("【共享资源】 " + this.name + ": 已存在同名的共享资源!");
			flag = -2;
		}
		return flag == -1 ? define.noError : define.semanticError;
	}


	/**
	 * function to check whether there has the same name in share resource list
	 */
	public boolean hasSameName(){
		for (ShareResource sr : Model.shareResourceArray){
			if(!sr.equals(this) && sr.name.equals(this.name)){
				return true;
			}
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
	 * clear content of 'readTaskNames'
	 */
	public void clearReadTasks(){
		this.readTaskNames.clear();
	}


	/**
	 * add a task name into the 'readTaskNames'
	 */
	public void addReadTaskName(String name){
		if(this.readTaskNames.contains(name))
			return; 
		this.readTaskNames.add(name);
	}


	/**
	 * get all the task names in 'readTaskNames', in string form
	 */
	public String getReadTaskNames(){
		String result = "";

		if(this.readTaskNames.isEmpty())
			;
		else{
			int len = this.readTaskNames.size();
			for (int j = 0; j < len - 1; j++)
				result += this.readTaskNames.get(j) + ",";
			result += this.readTaskNames.get(len - 1);
		}
		return result;
	}


	/**
	 * clear content of 'writeTaskNames'
	 */
	public void clearWriteTasks(){
		this.writeTaskNames.clear();
	}


	/**
	 * add a task name into the 'writeTaskNames'
	 */
	public void addWriteTaskName(String name){
		if(this.writeTaskNames.contains(name))
			return ;
		this.writeTaskNames.add(name);
	}


	/**
	 * get all the task names in 'writeTaskNames', in string form
	 */
	public String getWriteTaskNames(){
		String result = "";

		if(this.writeTaskNames.isEmpty())
			;
		else{
			int len = this.writeTaskNames.size();
			for (int j = 0; j < len - 1; j++)
				result += this.writeTaskNames.get(j) + ",";
			result += this.writeTaskNames.get(len - 1);
		}
		return result;
	}

	
	/**
	 * get the content of a given shareResource
	 * in table order
	 * @return a string array of each attribute
	 */
	public static String[] getContent(ShareResource sr){
		return new String[]{sr.name};
	}
}
