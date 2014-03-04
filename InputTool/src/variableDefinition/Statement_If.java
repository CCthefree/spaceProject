package variableDefinition;

import java.util.ArrayList;
import java.util.List;

import util.Lexer;
import util.define;

/**
 * class of if statement, with the form: if(boolExpr){statement;*}
 * else{statement;*} (else part is optional)
 * 
 * @author zengke.cai
 * 
 */
public class Statement_If extends Statement {

	List<String> boolExpr;

	public Statement_Sequence ifStat;
	public Statement_Sequence elseStat;


	/**
	 * 
	 */
	public Statement_If(String root, String content) {
		this.root = root;
		this.content = content;
		this.errorInfo = "";
		this.boolExpr = new ArrayList<String>();
		this.errorIndex = expressionCheck();
		
		if (this.errorIndex == define.syntaxError)
			this.errorInfo += "【处理程序】" + this.root + ": " + this.content + " 语法错误！\n";
	}


	/**
	 * append the if-part as a new sequence statement
	 * 
	 * @param str
	 */
	public void addIfStat(String str) {
		this.content += " " + str;
		this.ifStat = new Statement_Sequence(this.root, str);
	}


	/**
	 * append the else-part as a new sequence statement
	 * 
	 * @param str
	 */
	public void addElseStat(String str) {
		this.content += " else " + str;
		this.elseStat = new Statement_Sequence(this.root, str);
	}


	/**
	 * check the conditional expression, include syntax check and semantic check
	 * 
	 * @return
	 */
	private int expressionCheck() {
		List<String> token = Lexer.lexer(this.content);
		int index = token.indexOf(")");
		if (token.size() < 6 || !token.get(1).equals("(") || index != token.size()-1) { 
			return define.syntaxError;
		}

		List<String> boolExprToken = token.subList(2, index); // boolExpr
																// elements
		// check boolean Expression
		int size = boolExprToken.size();
		if (size % 4 != 3) { // size = 4*n+3, n>=0
			return define.syntaxError;
		}
		else {
			for (int i = 0; i < size; i = i + 4) {
				if (!(Lexer.isVariableName(boolExprToken.get(i))
						&& boolExprToken.get(i + 1).matches("<=|>=|<|>|==|!=") && boolExprToken
						.get(i + 2).matches("[0-9]+")))
					return define.syntaxError;
				if (i + 3 != size && !boolExprToken.get(i + 3).equals("&&"))
					return define.syntaxError;
			}
		}
		this.boolExpr = boolExprToken;
		
		for (int i = 0; i < size; i = i + 4) {
			Procedure proc = Model.getProcedure(this.root);
			
			if (!isControlVariable(boolExprToken.get(i))) {
				this.errorInfo += "【处理程序】" + this.root + ": 变量" + boolExprToken.get(i) + "不是控制变量!\n";
				// add undefined control variable into 'undefineCVs';
				if (proc != null)
					proc.addUndefinedCVs(boolExprToken.get(i));
				return define.semanticError;
			}
			else{
				proc.addCV(Model.getCV(boolExprToken.get(i)));
			}
		}
		
		return define.noError;
	}


	/**
	 * check the error type and get error info of the whole if statement
	 */
	public void check() {
		this.ifStat.check();
		if(this.ifStat.errorIndex > this.errorIndex)
			this.errorIndex = this.ifStat.errorIndex;
		if(!this.errorInfo.contains(ifStat.errorInfo))
			this.errorInfo += ifStat.errorInfo;

		if (this.elseStat != null) {
			this.elseStat.check();
			if(this.elseStat.errorIndex > this.errorIndex)
				this.errorIndex = this.elseStat.errorIndex;
			if(!this.errorInfo.contains(elseStat.errorInfo))
				this.errorInfo += this.elseStat.errorInfo;
		}
	}


	/**
	 * function to check whether a string is a name of control variable
	 * 
	 * @return true if it is, otherwise false
	 */
	private boolean isControlVariable(String str) {
		for (ControlVariable cv : Model.controlVariableArray) {
			if (cv.name.equals(str))
				return true;
		}
		return false;
	}


	/**
	 * compute the execute time of if statement
	 */
	public void computeExecTime() {
		this.ifStat.computeExecTime();
		if(ifStat.worstTime == -1){		//处理IF中有未定义的子过程
			this.bestTime = 0;
			this.worstTime = -1;
			return;
		}
		else {
			this.bestTime = this.ifStat.bestTime;
			this.worstTime = this.ifStat.worstTime;
		}

		if (this.elseStat != null) {
			this.elseStat.computeExecTime();
			if(elseStat.worstTime == -1){		//处理ELSE中有未定义的子过程
				this.bestTime = 0;
				this.worstTime = -1;
				return;
			}
			else {
				if (this.elseStat.worstTime > this.worstTime)
					this.worstTime = this.elseStat.worstTime;
				if (this.elseStat.bestTime < this.bestTime)
					this.bestTime = this.elseStat.bestTime;
			}
		}
	}

}
