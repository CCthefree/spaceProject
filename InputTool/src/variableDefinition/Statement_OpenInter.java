package variableDefinition;


import java.util.List;

import util.Lexer;
import util.define;

/**
 * class of closeInter statement, in form of: open XX;
 * 
 * @author zengke.cai
 * 
 */
public class Statement_OpenInter extends Statement {

	public Statement_OpenInter(String root, String content) {
		this.root = root;
		this.content = content;
		this.errorInfo = "";
		this.errorIndex = analysis();
	}


	/**
	 * check the open interruption statement, include syntax check and semantic
	 * check
	 */
	private int analysis() {
		List<String> token = Lexer.lexer(this.content);
		int errorIndex = syntaxCheck(token);
		if (errorIndex == define.noError) {
			semanticCheck(token.get(1));
		}
		else
			this.errorInfo += "【处理程序】" + this.root + ": " + this.content + " 语法错误！\n";

		return errorIndex;
	}


	/**
	 * 
	 */
	private int syntaxCheck(List<String> token) {
		if (token.size() != 3)
			return define.syntaxError;
		if (!(token.get(0).equals("open") && token.get(1).matches("[a-z]+[0-9]+") && token.get(2)
				.equals(";")))
			return define.syntaxError;

		return define.noError;
	}


	/**
	 * 
	 */
	private int semanticCheck(String str) {

		return define.noError;
	}
}
