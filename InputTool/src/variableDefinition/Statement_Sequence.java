package variableDefinition;


import java.util.ArrayList;

import util.Lexer;
import util.define;


/**
 * class of sequence statements, consist of statements of all types
 * @author zengke.cai
 *
 */
public class Statement_Sequence extends Statement{
	private ArrayList<Statement> childStats;
	
	
	public Statement_Sequence(String root, String str){
		this.root = root;
		this.content = str;
		this.errorInfo = "";
		this.childStats = new ArrayList<Statement>();
		this.errorIndex = analysis();
		
		if(this.errorIndex == define.syntaxError)
			this.errorInfo += "【处理程序】" + this.root + ": " + this.content + " 语法错误!\n";
	}
	
	
	/**
	 * analysis the sequence statement
	 *
	 */
	private int analysis(){
		String remainStr = new String(this.content).trim();
		while (!remainStr.equals("")){
			int index = Lexer.statIndex(remainStr);
			if(index == Integer.MIN_VALUE)
				return define.noError;
			else if(index == -1){
				return define.syntaxError;
			}
			
			String statStr = remainStr.substring(0, index+1);
			remainStr = remainStr.substring(index+1).trim();
			if(statStr.startsWith("{")){
				statStr = statStr.substring(1, index).trim();
				if(statStr.startsWith("[")){
					Statement_Compound compoundStat = new Statement_Compound(this.root, statStr);
					this.childStats.add(compoundStat);
				}
				else{
					Statement_Sequence seqStat = new Statement_Sequence(this.root, statStr);
					this.childStats.add(seqStat);
				}
			}
			else if(statStr.startsWith("if")){
				Statement_If ifStat = new Statement_If(this.root, statStr);
				index = Lexer.statIndex(remainStr);
				statStr = remainStr.substring(0, index+1);
				remainStr = remainStr.substring(index+1).trim();
				ifStat.addIfStat(statStr);
				
				if(remainStr.startsWith("else")){
					remainStr = remainStr.substring(4).trim();
					index = Lexer.statIndex(remainStr);
					statStr = remainStr.substring(0, index+1);
					remainStr = remainStr.substring(index+1).trim();
					ifStat.addElseStat(statStr);
				}
				this.childStats.add(ifStat);
			}
			else{
				if(statStr.contains("=")){
					Statement_Assign assignStat = new Statement_Assign(this.root, statStr);
					this.childStats.add(assignStat);
				}
				else if(statStr.contains("(")){
					Statement_Call callStat = new Statement_Call(this.root, statStr);
					this.childStats.add(callStat);
				}
				else if(statStr.startsWith("open")){
					Statement_OpenInter openStat = new Statement_OpenInter(this.root, statStr);
					this.childStats.add(openStat);
				}
				else if(statStr.startsWith("close")){
					Statement_CloseInter closeStat = new Statement_CloseInter(this.root, statStr);
					this.childStats.add(closeStat);
				}
				else
					return define.syntaxError;
			}
		}
		return define.noError;
	}

	
	
	/**
	 * check the error type and get error info of the whole statement tree
	 */
	public void check(){
		if(this.childStats.isEmpty())
			;
		else{
			for(Statement stat : this.childStats){
				stat.check();
				if(stat.errorIndex > this.errorIndex)
					this.errorIndex = stat.errorIndex;
				if(!this.errorInfo.contains(stat.errorInfo))
					this.errorInfo += stat.errorInfo;
			}
		}
	}
	
		
	/**
	 * compute the execute time of the sequence statement, by add all execute time of
	 * child statements
	 */
	public void computeExecTime(){
		this.bestTime = 0;
		this.worstTime = 0;

		if(this.childStats.isEmpty())
			return ;
		for(Statement stat : this.childStats){
			stat.computeExecTime();
			if(stat.bestTime < 0 || stat.worstTime < 0){	//处理包含未定义子过程的情况
				this.bestTime = 0;
				this.worstTime = -1;
				return ;
			}
			else {
				this.bestTime += stat.bestTime;
				this.worstTime += stat.worstTime;
			}
		}
	}


}
