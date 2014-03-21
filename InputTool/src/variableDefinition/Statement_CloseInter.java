package variableDefinition;


import java.util.List;

import util.Lexer;
import util.define;

/**
 * class of closeInter statement, in form of: close XX;
 * 
 * @author zengke.cai
 * 
 */
public class Statement_CloseInter extends Statement {

	public Statement_CloseInter(String root, String content) {
		this.root = root;
		this.content = content;
		this.errorInfo = "";
	}


	/**
	 * check the close interruption statement, include syntax check and semantic
	 * check
	 */
	public void check() {
		List<String> token = Lexer.lexer(this.content);
		int errorIndex = syntaxCheck(token);
		if (errorIndex == define.noError) {
			errorIndex = semanticCheck(token.get(1));
		}
		else
			this.errorInfo += "【处理程序】" + this.root + ": " + this.content + " 语法错误！\n";

		this.errorIndex = errorIndex;
	}


	/**
	 * 
	 */
	private int syntaxCheck(List<String> token) {
		if (token.size() != 3)
			return define.syntaxError;
		if (!(token.get(0).equals("close") && Lexer.isIRQ(token.get(1)) && token.get(2)
				.equals(";")))
			return define.syntaxError;

		return define.noError;
	}


	/**
	 * 
	 */
	private int semanticCheck(String str) {
		if(! Model.definedIRQ(str)){
			this.errorInfo += "【处理程序】" + this.root + "：未定义的中断号" + str + "!\n";
			return define.semanticError;
		}
		
		return define.noError;
	}

}
