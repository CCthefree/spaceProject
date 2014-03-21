package variableDefinition;

import java.util.List;

import util.Lexer;
import util.define;

/**
 * class of assignment statement, with the form: variable = integer;
 * 
 * @author zengke.cai
 * 
 */
public class Statement_Assign extends Statement {

	public ControlVariable cv;
	public int value;


	public Statement_Assign(String root, String content) {
		this.root = root;
		this.content = content;
		this.errorInfo = "";
	}


	/**
	 * function of statement check, include syntax check and semantic check set
	 */
	public void check() {
		List<String> token = Lexer.lexer(this.content);
		int errorIndex = syntaxCheck(token);
		if (errorIndex == define.noError) {
			setValue(token.get(0), Lexer.toInt(token.get(2)));
			errorIndex = semanticCheck(token.get(0));
		}
		else {
			this.errorInfo += "【处理程序】" + this.root + ": " + this.content + " 语法错误！\n";
		}

		this.errorIndex = errorIndex;
	}


	/**
	 * set value of the assign statement(variable and value)
	 */
	public void setValue(String name, int value) {
		this.cv = Model.getCV(name);
		this.value = value;
		
		if(this.cv != null)
			Model.getProcedure(this.root).addCV(this.cv);
	}


	/**
	 * function of syntax check
	 */
	private int syntaxCheck(List<String> token) {
		if (token.size() < 4) {
			return define.syntaxError;
		}
		if (!(Lexer.isStructuredVar(token.get(0)) && token.get(1).equals("=")
				&& token.get(2).matches("[0-9]+") && token.get(3).equals(";")))
			return define.syntaxError;

		return define.noError;
	}


	/**
	 * function of semantic check
	 */
	private int semanticCheck(String variable) {
		int flag = -1;
		if (this.cv == null) {
			flag = -2;
		}
		else if (this.value == Integer.MIN_VALUE) {
			flag = -3;
		}
		else if (!inLegalRange()) {
			flag = -4;
		}

		switch (flag) {
		case -2:
			this.errorInfo += "【处理程序】" + this.root + ": 变量" + variable + "不是控制变量，不能对其赋值!\n";

			Procedure proc = Model.getProcedure(this.root);
			if (proc != null)
				proc.addUndefinedCVs(variable);
			break;
		case -3:
			this.errorInfo += "【处理程序】" + this.root + ": 赋给变量" + variable + "的值超出int范围!\n";
			break;
		case -4:
			this.errorInfo += "【处理程序】" + this.root + ": 赋给变量" + variable + "的值超出其定义范围!\n";
			break;
		default:
			break;
		}

		return flag == -1 ? define.noError : define.semanticError;
	}



	/**
	 * function to check whether the assigning value is in the Range of the
	 * control variable
	 * 
	 * @return true if in, otherwise false
	 */
	private boolean inLegalRange() {
		if (this.cv.intLBD == -1 || this.cv.intUBD == -1)
			return true;
		else if (this.value >= this.cv.intLBD && this.value <= this.cv.intUBD)
			return true;

		return false;
	}

}
