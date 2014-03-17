package util;

import java.util.ArrayList;


/**
 * class of lexical analysis, input a String, the result is token array;
 */

public class Lexer{

	/**
	 * test whether a string is a reserved word
	 */
	public static boolean isRword(String str){
		if(str.equals("tasks") || str.equals("interrupts") || str.equals("constrolVariable") || str.equals("shareVariable") || str.equals("if")
				|| str.equals("yes") || str.equals("no") || str.equals("else"))
			return true;
		else
			return false;
	}


	/**
	 * test whether a string is a variable name, start with a character and
	 * composed by digits and characters
	 */
	public static boolean isVariableName(String str){
		if(str.matches("[a-zA-Z][_0-9a-zA-Z]*"))
			return true;
		return false;
	}


	public static boolean isDelimiter(String str){
		if(str.matches("[;,.:()/}]"))
			return true;
		return false;
	}


	/**
	 * test whether a string is legal token, return true if so
	 */
	public static boolean isLegalToken(String str){
		if(str.matches("=|<|>|>=|<=|==|!|!=|-|&|&&")) // operator
			return true;
		else if(str.matches("-1|[0-9]+")) // digit
			return true;
		else if(isVariableName(str)) // variables
			return true;
		else if(isRword(str)) // reserved words
			return true;
		else
			return false;
	}


	/**
	 * function of lexical analysis, analysis sentence, store result into
	 * tokenArray;
	 */
	public static ArrayList<String> lexerAnalyse(String str){
		ArrayList<String> result = new ArrayList<String>();
		while (!str.equals("")){
			int index = 1;

			//去除字符串头部的空白符
			if(str.substring(0, index).matches("[\\s]+")){
				str = str.substring(index);
				continue;
			}

			if(isDelimiter(str.substring(0, index))){ // 分隔符
				result.add(str.substring(0, index));
				str = str.substring(index);
			}
			else if(str.startsWith("{")){ // 大括号
				//找到匹配的'}'的下标
				int count = 1;
				int i;
				for (i = 1; i < str.length(); i++){
					if(str.charAt(i) == '{')
						count++;
					if(str.charAt(i) == '}'){
						count--;
						if(count == 0)
							break;
					}
				}
				if(i < str.length()){
					result.add(str.substring(0, i + 1));
					str = str.substring(i + 1);
				}
				else{
					str = "";
				}
			}
			else if(isLegalToken(str.substring(0, index))){ //合法符号
				while (index <= str.length() && isLegalToken(str.substring(0, index)))
					index++;

				result.add(str.substring(0, index - 1));
				str = str.substring(index - 1);
			}
			else{ //未定义的字符，直接添加进结果列表
				result.add(str.substring(0, index));
				str = str.substring(index);
			}
		}

		return result;
	}
}

