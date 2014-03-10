package variableDefinition;


import java.util.ArrayList;

import util.Lexer;
import util.define;


/**
 * class of compound statement that used to describe the compound tasks, 
 * in form of: "{[timeInterval; readSR; writeSR;] taskCallSequence}"
 * @author zengke.cai
 *
 */
public class Statement_Compound extends Statement{
	public ArrayList<Statement> childStats;
	
	public Statement_Compound(String root, String content){
		this.root = root;
		this.content = content;
		this.errorInfo = "";
		this.childStats = new ArrayList<Statement>();
		this.errorIndex = analysis();
		
		if(this.errorIndex == define.syntaxError)
			this.errorInfo += "【处理程序】" + this.root + ": " + this.content + " 语法错误！\n";
	}
	
	
	/**
	 * analysis the compound statement, consisted by information analysis and statement analysis
	 */
	private int analysis(){
		String wholeStr = new String(this.content).trim();
		int infoIndex = wholeStr.indexOf("]");
		if(infoIndex == -1){
			return define.syntaxError;
		}
		else{
			int flag;
			//取'[',']'之间的字符串用于信息分析，余下的串构建语句序列
			String infoStr = wholeStr.substring(1, infoIndex).trim();
			String statStr = wholeStr.substring(infoIndex+1).trim();
			
			//调用语句序列分析
			if(statementCheck(statStr) == define.syntaxError)
				return define.syntaxError;
			
			//分析时间区间信息
			int timeIndex = Lexer.statIndex(infoStr);
			if(timeIndex == -1 || timeIndex == Integer.MIN_VALUE)
				return define.syntaxError;
			else{
				if(timeInfoCheck(infoStr.substring(0, timeIndex+1)) == define.syntaxError)
					return define.syntaxError;
			}
			
			//分析读资源信息
			infoStr = infoStr.substring(timeIndex+1).trim();
			int readIndex = Lexer.statIndex(infoStr);
			if(readIndex == -1 || readIndex == Integer.MIN_VALUE)
				return define.syntaxError;
			else{
				flag = readInfoCheck(infoStr.substring(0, readIndex+1));
				if(flag == define.syntaxError)
					return define.syntaxError;
			}
			
			//分析写资源信息
			infoStr = infoStr.substring(readIndex+1);
			return writeInfoCheck(infoStr) > flag ? writeInfoCheck(infoStr) : flag;
		}
	}
	
	
	/**
	 * check the call statements, only syntax check
	 */
	private int statementCheck(String str){
		while(!str.equals("")){
			int index = Lexer.statIndex(str);
			String statStr = str.substring(0, index+1);
			str = str.substring(index+1).trim();
			
			ArrayList<String> token = Lexer.lexer(statStr);
			Statement_Call callStat = new Statement_Call();
			if(callStat.syntaxCheck(token) == define.syntaxError){
				this.errorInfo += "【处理程序】" + this.root + ": " + statStr + " 语法错误！\n";
				return define.syntaxError;
			}
		}
		
		return define.noError;
	}
	
	
	/**
	 * check time interval info, in form of: LBD, UBD;
	 */
	private int timeInfoCheck(String str){
		ArrayList<String> token = Lexer.lexer(str);
		if(token.size() != 4)
			return define.syntaxError;
		else if(!(token.get(0).matches("[0-9]+") && token.get(2).matches("[0-9]+") && token.get(1).equals(",") && token.get(3).equals(";")))
			return define.syntaxError;
		else{
			this.bestTime = Lexer.toLong(token.get(0));
			this.worstTime = Lexer.toLong(token.get(2));
		}
		
		return define.noError;
	}
	
	
	/**
	 * check the read source info, in form of: read : XX, XX, ...;
	 */
	private int readInfoCheck(String str){
		ArrayList<String> token = Lexer.lexer(str);
		int size = token.size();
		if(size % 2 != 0 && size != 3)
			return define.syntaxError;
		else{
			if(! (token.get(0).equals("read") && token.get(1).equals(":") && token.get(size-1).equals(";")))
				return define.syntaxError;
			if(size != 3){
				for(int i = 2; i < size; i = i + 2){
					if(! Lexer.isStructuredVar(token.get(i)))
						return define.syntaxError;
					if(i+2 != size && !token.get(i+1).equals(","))
						return define.syntaxError;
				}
				
				Procedure proc = Model.getProcedure(this.root);
				for(int i = 2; i < size; i = i + 2){
					if(! isShareResource(token.get(i))){
						this.errorInfo += "【处理程序】" + this.root + ": " + str + " 访问未定义的共享变量！\n";
						
						if(proc != null)
							proc.addUndefinedRSRs(token.get(i));
						return define.semanticError;
					}
					else{
						proc.addReadSR(token.get(i));
					}
				}
			}
		}
		
		return define.noError;
	}
	
	
	/**
	 * check the write source info, in form of: write : XX, XX, ...;
	 */
	private int writeInfoCheck(String str){
		ArrayList<String> token = Lexer.lexer(str);
		int size = token.size();
		if(size % 2 != 0 && size != 3)
			return define.syntaxError;
		else{
			if(! (token.get(0).equals("write") && token.get(1).equals(":") && token.get(size-1).equals(";")))
				return define.syntaxError;
			if(size != 3){
				for(int i = 2; i < size; i = i + 2){
					if(! Lexer.isStructuredVar(token.get(i)))
						return define.syntaxError;
					if(i+2 != size && !token.get(i+1).equals(","))
						return define.syntaxError;
				}
				
				Procedure proc = Model.getProcedure(this.root);
				for(int i = 2; i < size; i = i + 2){
					if(! isShareResource(token.get(i))){
						this.errorInfo += "【处理程序】" + this.root + ": " + str + " 访问未定义的共享变量！\n";
						
						if(proc != null)
							proc.addUndefinedWSRs(token.get(i));
						return define.semanticError;
					}
					else{
						proc.addWriteSR(token.get(i));
					}
				}
			}
		}
		
		return define.noError;
	}
	
	
	/**
	 * check the error type and get error info of this compound statement
	 */
//	public void check(){
//		if (this.childStat != null) {
//			this.childStat.check();
//			//XXX 组合语句允许调用未定义的子过程，所以只考虑语法错误
//			if(this.childStat.errorIndex == define.syntaxError){
//				this.errorIndex = define.syntaxError;
//				if(! this.errorInfo.contains(childStat.errorInfo))
//					this.errorInfo += this.childStat.errorInfo;
//			}
//		}
//	}
	
	
	/**
	 * 
	 */
	private boolean isShareResource(String name){
		for(ShareResource sr : Model.shareResourceArray){
			if(sr.name.equals(name))
				return true;
		}
		return false;
	}
	

}
