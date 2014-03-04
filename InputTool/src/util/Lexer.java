
package util;

import java.util.ArrayList;



/**
 * class of Lexer, do lexical word of this project
 */

public class Lexer{

	/**
	 * test whether a string is a reserved word
	 */
	public static boolean isRword(String str){
		if(str.equals("[task|interruption|controlVariable|shareResource|taskSequence|interval|commuTaskBound |close|open|read|write|if|else|yes|no]"))
			return true;
		else
			return false;
	}


	/**
	 * test whether a string is a variable name, start with a character and
	 * composed by digits、characters and '_'
	 */
	public static boolean isVariableName(String str){
		if(str.matches("[a-zA-Z][_0-9a-zA-Z]*"))
			return true;
		return false;
	}
	
	
	/**
	 * test whether a string is a structured variable name (variable names connected by '.')
	 */
	public static boolean isStructuredVar(String str){
		if(!str.contains(".") && isVariableName(str))
			return true;
		else if(str.startsWith(".") || str.endsWith("."))
			return false;
		else{
			String[] names = str.split("\\.");
			for(int i = 0; i < names.length; i++){
				if(!isVariableName(names[i]))
					return false;
			}
			return true;
		}
	}
	
	
	/**
	 * convert a string into integer
	 * @param str
	 * @return Integer.MInN_VALUE is failed
	 */
	public static int toInt(String str){
		int result = Integer.MIN_VALUE;
		try{
			result = Integer.parseInt(str);
		}
		catch(NumberFormatException e){
			
		}
		return result;
	}
	
	
	/**
	 * convert a string into long
	 * @param str
	 * @return Long.MInN_VALUE is failed
	 */
	public static long toLong(String str){
		long result = Long.MIN_VALUE;
		try{
			result = Long.parseLong(str);
		}
		catch(NumberFormatException e){
			
		}
		return result;
	}



	/**
	 * function to find the end of first statement in given string
	 * if the string begins with '{', the end is the matching '}'; 
	 * if the string begins with "if", the end is the conditional expression; 
	 * otherwise, end is ';'
	 */
	public static int statIndex(String str){
		//空白字符串或空串
		if(str.matches("[\\s]+") || str.equals(""))
			return Integer.MIN_VALUE;
		
		//去除字符串头部的空白字符
		while(str.substring(0, 1).matches("[\\s]"))
			str = str.substring(1);
		
		//大括号的情况
		if(str.startsWith("{")){
			int count = 1;
			for (int index = 1; index < str.length(); index++){
				if(str.charAt(index) == '{')
					count++;
				if(str.charAt(index) == '}'){
					count--;
					if(count == 0)
						return index;
				}
			}
			return -1;
		}
		//if表达式的情况
		else if(str.startsWith("if")){
			for(int index = 2; index < str.length(); index++){
				if(str.charAt(index) == ')')
					return index;
			}
			return -1;
		}
		//其它情况
		else{
			for(int index = 1; index < str.length(); index++){
				if(str.charAt(index) == ';')
					return index;
			}
			return -1;
		}
	}


	/**
	 * function of lexical analysis, analysis sentence, store result into
	 * tokenArray;
	 */
//	public static ArrayList<String> lexerAnalyse(String str){
//		ArrayList<String> result = new ArrayList<String>();
//		while (!str.equals("")){
//			int index = 1;
//
//			//去除字符串头部的空白符
//			if(str.substring(0, index).matches("[\\s]+")){
//				str = str.substring(index);
//				continue;
//			}
//
//			if(isDelimiter(str.substring(0, index))){ // 分隔符
//				result.add(str.substring(0, index));
//				str = str.substring(index);
//			}
//			else if(str.startsWith("{")){ // 大括号
//				//找到匹配的'}'的下标
//				int count = 1;
//				int i;
//				for (i = 1; i < str.length(); i++){
//					if(str.charAt(i) == '{')
//						count++;
//					if(str.charAt(i) == '}'){
//						count--;
//						if(count == 0)
//							break;
//					}
//				}
//				if(i < str.length()){
//					result.add(str.substring(0, i + 1));
//					str = str.substring(i + 1);
//				}
//				else{
//					str = "";
//				}
//			}
//			else if(isLegalToken(str.substring(0, index))){ //合法符号	TODO
//				while (index <= str.length() && isLegalToken(str.substring(0, index)))
//					index++;
//
//				result.add(str.substring(0, index - 1));
//				str = str.substring(index - 1);
//			}
//			else{ //未定义的字符，直接添加进结果列表
//				result.add(str.substring(0, index));
//				str = str.substring(index);
//			}
//		}
//
//		return result;
//	}
	
	
	/**
	 * 
	 */
	public static ArrayList<String> lexer(String str){
		ArrayList<String> result = new ArrayList<String>();
		int len = str.length();
		int state = 0;
		int begin = 0; 
		int end = begin;
		
		while(begin < len){
			char ch = str.charAt(end);
			
			if(state == 0){		//初始状态
				if(Character.isSpace(ch)){	//空白字符
					begin = end + 1;
					end = begin;
				}
				else if(Character.isLetter(ch)){
					state = 1;
					end++;
				}
				else if(Character.isDigit(ch)){
					state = 2;
					end++;
				}
				else if(ch == '>' || ch == '<' || ch == '='){
					state = 3;
					end++;
				}
				else if(ch == '&'){
					state = 4;
					end++;
				}
				else if(ch == '|'){
					state = 5;
					end++;
				}
				else if(ch == '!'){
					state = 6;
					end++;
				}
				else if(ch == '{' || ch == '}' || ch == '[' || ch == ']' || ch == ';' || ch == ':' 
						|| ch == '(' || ch == ')' || ch == ','){
					result.add(str.substring(begin, end+1));
					begin = end+1;
					end = begin;
				}
				else{
					result.add("error");
					break;
				}
			}
			else if(state == 1){	//标识符识别
				if(Character.isLetterOrDigit(ch))
					end++;
				else if(ch == '.'){
					state = 7;	
					end++;
				}
				else{
					result.add(str.substring(begin, end));
					begin = end;
					state = 0;
				}
			}
			else if(state == 2){	//数字识别
				if(Character.isDigit(ch))
					end++;
				else if(! Character.isLetter(ch)){
					result.add(str.substring(begin, end));
					begin = end;
					state = 0;
				}
				else{
					result.add("error");
					break;
				}
			}
			else if(state == 3){	//'>' '<' '=' [+'=']
				if(ch == '='){
					result.add(str.substring(begin, end+1));
					begin = end+1;
					end = begin;
					state = 0;
				}
				else{
					result.add(str.substring(begin, end));
					begin = end;
					state = 0;
				}
			}
			else if(state == 4){	//&&
				if(ch == '&'){
					result.add(str.substring(begin, end+1));
					begin = end+1;
					end = begin;
					state = 0;
				}
				else{
					result.add("error");
					break;
				}
			}
			else if(state == 5){	//||
				if(ch == '|'){
					result.add(str.substring(begin, end+1));
					begin = end+1;
					end = begin;
					state = 0;
				}
				else{
					result.add("error");
					break;
				}
			}
			else if(state == 6){	// !=
				if(ch == '='){
					result.add(str.substring(begin, end+1));
					begin = end+1;
					end = begin;
					state = 0;
				}
				else{
					result.add("error");
					break;
				}
			}
			else if(state == 7){
				if(Character.isLetter(ch)){
					state = 1;
					end++;
				}
				else{
					result.add("error");
					break;
				}
			}
		}
		
		return result;
	}
}