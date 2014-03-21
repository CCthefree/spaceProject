package variableDefinition;

import java.util.List;

import util.Lexer;
import util.define;

/**
 * class of task call statement, in form of: TaskXX();
 * 
 * @author zengke.cai
 * 
 */
public class Statement_Call extends Statement {

	public Task task;
	
	
	public Statement_Call(){
		
	}
	
	public Statement_Call(String root, String content) {
		this.root = root;
		this.content = content;
		this.errorInfo = "";
	}


	/**
	 * function of statement check, include syntax check and semantic check, set
	 * value if no error
	 */
	public void check() {
		List<String> token = Lexer.lexer(this.content);
		int errorIndex = syntaxCheck(token);
		if (errorIndex == define.noError) {
			errorIndex = semanticCheck(token.get(0));
			if (errorIndex == define.noError){
				this.task = Model.getTask(token.get(0));
				Model.getProcedure(this.root).addTask(this.task);
			}
		}
		else
			this.errorInfo += "【处理程序】" + this.root + ": " + this.content + " 语法错误！\n";

		this.errorIndex = errorIndex;
	}


	/**
	 * function of syntax check
	 */
	public int syntaxCheck(List<String> token) {
		if (token.size() != 4)
			return define.syntaxError;
		if (!(Lexer.isVariableName(token.get(0)) && token.get(1).equals("(")
				&& token.get(2).equals(")") && token.get(3).equals(";")))
			return define.syntaxError;

		return define.noError;
	}


	/**
	 * function of semantic check
	 */
	private int semanticCheck(String variable) {
		if (!isTaskName(variable)) {
			this.errorInfo += "【处理程序】" + this.root + "：调用未定义的子过程" + variable + "!\n";

			Procedure proc = Model.getProcedure(this.root);
			if (proc != null)
				proc.addUndefinedTasks(variable);
		
			return define.semanticError;
		}

		return define.noError;
	}


	/**
	 * test whether a string is a task name
	 * 
	 * @return true if it is, otherwise false
	 */
	private boolean isTaskName(String str) {
		for (Task task : Model.taskArray) {
			if (task.name.equals(str))
				return true;
		}
		return false;
	}


	/**
	 * compute the execute time of task call
	 */
	public void computeExecTime() {
		if(this.task == null){	//处理未定义的子过程
			this.bestTime = 0;
			this.worstTime = -1;
		}
		else {
			this.bestTime = this.task.longLBD;
			this.worstTime = this.task.longUBD;
		}
	}
}
