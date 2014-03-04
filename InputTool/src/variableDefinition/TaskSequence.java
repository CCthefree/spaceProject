
package variableDefinition;

import java.util.ArrayList;

import util.ErrorInfo;
import util.Lexer;
import util.define;


/**
 * class of sequential logic, describe the logical order of tasks, to be
 * verified
 */
public class TaskSequence{

	//基本信息
	public String content; // record the content of logic sequences
	public int indexOfSeq; //表示第几个时序逻辑

	//衍生信息
	private ArrayList<String> seqTasks; // record the task sequences, only the task name


	/**
	 * 
	 * @param index
	 */
	public TaskSequence(int index) {
		this.content = "";
		this.indexOfSeq = index;
		this.seqTasks = new ArrayList<String>();
	}


	/**
	 * check the delimiter and format of task sequence
	 */
	public int inputCheck(String str){
		ArrayList<String> token = Lexer.lexer(str);
		int size = token.size();

		if(size % 2 != 0 || size < 2) //size = 2*n, n > 0
			return define.syntaxError;
		else{
			if(!token.get(size - 1).equals(";"))
				return define.syntaxError;
			for (int i = 1; i < size - 1; i = i + 2)
				if(!token.get(i).equals(","))
					return define.syntaxError;
		}

		return define.noError;
	}


	/**
	 * assign value to members of task sequences
	 */
	public void setValue(String str){
		this.content = str;

		if(inputCheck(this.content) != -1)
			this.seqTasks = null;
		else{
			ArrayList<String> token = Lexer.lexer(str);
			ArrayList<String> taskNames = new ArrayList<String>();
			for (int i = 0; i < token.size(); i = i + 2){
				taskNames.add(token.get(i));
			}
			this.seqTasks = taskNames;
		}
	}


	/**
	 * include syntax and semantic check return error index, -1 for no error
	 */
	public int contentCheck(){
		int errorIndex = syntaxCheck();
		return (errorIndex == define.noError) ? semanticCheck() : errorIndex;
	}


	/**
	 * syntax check of task sequences
	 */
	public int syntaxCheck(){

		if(this.seqTasks == null){
			ErrorInfo.add("【时序任务】" + (this.indexOfSeq + 1) + ": 任务逻辑序列格式错误!");
			return define.syntaxError;
		}
		else if(this.seqTasks.size() < 2){ //length of sequence less than two
			ErrorInfo.add("【时序任务】" + (this.indexOfSeq + 1) + ": 不合法的序列长度（小于2）!");
			return define.syntaxError;
		}

		for (String str : this.seqTasks)
			//elements of sequence are not variable 
			if(!Lexer.isVariableName(str)){
				ErrorInfo.add("【时序任务】" + (this.indexOfSeq + 1) + ": 含有不合法的变量名!");
				return define.syntaxError;
			}

		return define.noError;
	}


	/**
	 * semantic check of task sequences
	 */
	public int semanticCheck(){
		int flag = -1; 
		if(hasUndefinedTask())
			flag = -2;
		else if(hasSameName())
			flag = -3;
		if(hasSameSequence())
			flag = -4;

		switch (flag) {
			case -2:
				ErrorInfo.add("【时序任务】" + (this.indexOfSeq + 1) + " : 含有未定义的子过程名!");
				break;
			case -3:
				ErrorInfo.add("【时序任务】" + (this.indexOfSeq + 1) + " : 同一任务序列中包含相同的子过程!");
				break;
			case -4:
				ErrorInfo.add("【时序任务】" + (this.indexOfSeq + 1) + " : 包含相同的任务序列!");
				break;
			default:
				break;
		}
		return flag == -1 ? define.noError : define.semanticError;
	}


	/**
	 * check whether there are undefined task in task sequences
	 */
	public boolean hasUndefinedTask(){
		for (String str : this.seqTasks)
			if(!isTaskName(str))
				return true;

		return false;
	}


	/**
	 * check whether a given string is a task name
	 */
	public boolean isTaskName(String str){
		for (Task task : Model.taskArray)
			if(task.name.equals(str))
				return true;

		return false;
	}


	/**
	 * check whether a task has appeared twice in a sequence
	 * 
	 * @return true if appeared twice, otherwise false
	 */
	public boolean hasSameName(){
		if(this.seqTasks.size() < 2)
			return false;
		else{
			for (int i = 0; i < this.seqTasks.size() - 1; i++){
				String name1 = this.seqTasks.get(i);
				for (int j = i + 1; j < this.seqTasks.size(); j++){
					if(this.seqTasks.get(j).equals(name1))
						return true;
				}
			}
			return false;
		}
	}


	/**
	 * check whether are there two same task sequences
	 * 
	 * @return true if has, otherwise false
	 */
	public boolean hasSameSequence(){
		if(Model.taskSequences.size() < 1)
			return false;
		for (TaskSequence ts : Model.taskSequences){
			if(ts.equals(this) || ts.seqTasks == null)
				continue;

			ArrayList<String> taskSeq2 = ts.seqTasks;
			if(this.seqTasks.size() != taskSeq2.size())
				continue;
			else{
				for (int k = 0; k < this.seqTasks.size(); k++){
					if(!this.seqTasks.get(k).equals(taskSeq2.get(k)))
						break;
					else if(k == this.seqTasks.size() - 1 && this.seqTasks.get(k).equals(taskSeq2.get(k)))
						return true;
				}
			}
		}
		return false;
	}


	/**
	 * check whether is there contradiction in different task sequences
	 * 
	 * @return true if has, otherwise false;
	 */
	public boolean hasContradiction(){

		return false;
	}


	/**
	 * update all the task sequences according to a text calling in MainFrame
	 * 
	 * @param text
	 */
	public static void updateTaskSeqs(String text){
		Model.taskSequences.clear(); //清空原来的任务序列

		String[] seqs = text.split(";"); //将文本内容用";"分隔为字符串数组
		for (int i = 0; i < seqs.length; i++){
			if(!seqs[i].equals("") && !seqs[i].matches("[\\s]+")){ //空字符串跳过
				TaskSequence taskSeq = new TaskSequence(i);

				//为字符串补充 原有 的分号 （split函数去掉了）
				if(i < seqs.length - 1 || (i == seqs.length - 1 && text.endsWith(";")))
					taskSeq.setValue(seqs[i] + ";"); //直接赋值，检查留给 totalAnalysis
				else
					taskSeq.setValue(text);

				Model.taskSequences.add(taskSeq);
			}
		}
	}

}
