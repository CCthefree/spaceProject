package programStructure;


import java.util.ArrayList;
import java.util.List;

import util.BoolExpr;
import util.Lexer;
import util.define;
import fileOperator.PPTest;

/**
 * class of procedure, in form of ProgramPoint array
 * 
 * @author ting.rong
 * 
 */
public class Procedure {

	private String desc;

	private ArrayList<ProgramPoint> points; // 程序点数组


	/**
	 * constructor, calculate the program point according to the description of
	 * procedure
	 */
	public Procedure(String desc) {
		this.desc = desc;
		this.points = new ArrayList<ProgramPoint>();

		seqStat(this.desc, 0);
		
//		sortPoints();
		test();
	}
	
	
	/**
	 * program point analysis of sequence statement
	 */
	private int seqStat(String str, int startPoint){
		String remainStr = new String(str).trim();
		while(!remainStr.equals("")){
			int index = Lexer.statIndex(remainStr);
			String statStr = remainStr.substring(0, index+1);
			remainStr = remainStr.substring(index+1).trim();
			
			if(statStr.startsWith("{")){	//组合语句或列序语句
				statStr = statStr.substring(1, index).trim();
				if(statStr.startsWith("["))
					startPoint = compStat(statStr, startPoint);
				else
					startPoint = seqStat(statStr, startPoint);
				
			}
			else if(statStr.startsWith("if")){		//TODO 先添加IF部分，再添加ELSE部分造成当前IF语句NEXTPOINT错误
				ArrayList<BoolExpr> exprs = exprPart(statStr);
				index = Lexer.statIndex(remainStr);
				statStr = remainStr.substring(0, index+1);
				remainStr = remainStr.substring(index+1).trim();
				startPoint = ifPart(statStr, startPoint, exprs);
				
				if(remainStr.startsWith("else")){
					remainStr = remainStr.substring(4).trim();
					index = Lexer.statIndex(remainStr);
					statStr = remainStr.substring(0, index+1);
					remainStr = remainStr.substring(index+1).trim();
					startPoint = elsePart(statStr, startPoint);
				}
			}
			else{
				if(statStr.contains("="))
					startPoint = assignStat(statStr, startPoint);
				else if(statStr.contains("("))
					startPoint = callStat(statStr, startPoint);
				else if(statStr.startsWith("open") || statStr.startsWith("close"))
					startPoint = open_closeStat(statStr, startPoint);
			}
		}
		
		return startPoint;
	}
	


	/**
	 * program point analysis of assign statement
	 */
	private int assignStat(String str, int startPoint) {// ///////////赋值语句
		ArrayList<String> token = Lexer.lexer(str);
		String cvName = token.get(0);
		int assignValue = Integer.parseInt(token.get(2));
		int endPoint = startPoint + 1;
		this.points.add(new ProgramPoint(startPoint, endPoint, define.assign, cvName, assignValue));// 添加新的程序点

		return endPoint;
	}


	/**
	 * program point analysis of call statement
	 */
	private int callStat(String str, int startPoint) {
		ArrayList<String> token = Lexer.lexer(str);
		String taskName = token.get(0);
		int endPoint = startPoint + 1;
		this.points.add(new ProgramPoint(startPoint, endPoint, define.call, taskName));

		return endPoint;
	}
	
	
	/**
	 * program point analysis of open/close statement
	 */
	private int open_closeStat(String str, int startPoint){
		ArrayList<String> token = Lexer.lexer(str);
		String irq = token.get(1);
		int endPoint = startPoint + 1;
		if(token.get(0).equals("open"))
			this.points.add(new ProgramPoint(startPoint, endPoint, define.open, irq));
		else 
			this.points.add(new ProgramPoint(startPoint, endPoint, define.close, irq));
		return endPoint;
	}
	
	
	/**
	 * program point analysis of compound statement
	 */
	private int compStat(String str, int startPoint){
		ArrayList<String> token = Lexer.lexer(str);
		int endPoint = startPoint + 1;
		
		return endPoint;
	}


	/**
	 * program point analysis of if statement(conditional expressions part)
	 */
	public ArrayList<BoolExpr> exprPart(String str) {
		int leftBucket = str.indexOf("(");
		int rightBucket = str.indexOf(")");
		List<String> boolExprToken = Lexer.lexer(str.substring(leftBucket+1, rightBucket));
		ArrayList<BoolExpr> exprs = new ArrayList<BoolExpr>();
		while (!boolExprToken.isEmpty()) {
			String cvName = boolExprToken.get(0);
			String op = boolExprToken.get(1);
			int value = Integer.parseInt(boolExprToken.get(2));
			exprs.add(new BoolExpr(cvName, op, value));

			if (boolExprToken.size() >= 4)
				boolExprToken = boolExprToken.subList(4, boolExprToken.size());
			else
				break;
		}
		
		return exprs;
	}

	
	/**
	 * program point analysis of if statement(if part)
	 */
	private int ifPart(String str, int startPoint, ArrayList<BoolExpr> exprs){
		int endPoint = seqStat(str, startPoint+1);
		this.points.add(new ProgramPoint(startPoint, startPoint+1, define.IF, exprs, endPoint));
		return endPoint;
	}
	
	
	/**
	 * program point analysis of if statement(else part)
	 */
	private int  elsePart(String str, int startPoint){
		int endPoint = seqStat(str, startPoint);
		
		for (ProgramPoint pp : this.points) {
			if (pp.getNextPoint() == startPoint)
				pp.setNextPoint(endPoint);
			else if (pp.getElsePoint() == startPoint)
				pp.setElsePoint(endPoint);
		}
		return endPoint;
	}
		


	/**
	 * sort the program point according to current point, increase
	 */
	public void sortPoints() {
		int size = this.points.size();
		while (size > 0) {
			// find the minimum offset
			int index = 0;
			for (int i = 1; i < size; i++) {
				if (this.points.get(i).getPoint() < this.points.get(index).getPoint())
					index = i;
			}
			// remove and push behind
			ProgramPoint temp = this.points.remove(index);
			this.points.add(temp);

			size--;
		}
	}


	/**
	 * get the program point at 'index'
	 * 
	 * @return null if index is illegal
	 */
	public ProgramPoint getPP(int index) {
		if (index < 0 || index >= this.points.size()) {
			return null;
		}
		else
			return this.points.get(index);
	}


	/**
	 * get the end point of this procedure
	 */
	public int getEndPnt() {
		return this.points.size();
	}


	/**
	 * TEST test function of procedure
	 */
	public void test() {
		PPTest.append(desc + "\r\n");
		for (ProgramPoint pp : this.points)
			PPTest.append(pp.getPoint() + "  " + pp.getNextPoint() + "  " + pp.getType() + "	"
					+ pp.getElsePoint() + "\r\n");
		PPTest.append("\r\n");
	}

}
