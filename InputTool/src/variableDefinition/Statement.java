package variableDefinition;

import java.util.ArrayList;

/**
 * class of statement, used to describe the process of procedure.
 * @author zengke.cai
 *
 */
public abstract class Statement{
	public String root;
	public String content;
	public int errorIndex;
	public String errorInfo;
	public long worstTime;
	public long bestTime;

	
	public void computeExecTime(){
	}
	
	
	public void check(){
		
	}
	
	
	public ArrayList<Statement> getChild(){
		return null;
	}

}
