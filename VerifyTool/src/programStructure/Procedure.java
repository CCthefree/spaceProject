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
	 * 
	 * @param desc
	 */
	public Procedure(String desc) {
		this.desc = desc;
		this.points = new ArrayList<ProgramPoint>();

		List<String> token = Lexer.lexerAnalyse(desc);
		int startPoint = 0;

		while (!token.isEmpty()) {
			if (token.get(0).equals("if")) { // if语句
				int index = (token.indexOf("else") != -1) ? token.indexOf("else") + 1 : token
						.indexOf(")") + 1;
				List<String> ifToken = token.subList(0, index + 1);// if语句的token
				token = token.subList(index + 1, token.size());

				startPoint = if_statement(ifToken, startPoint);
			}
			else if (token.get(1).equals("(")) { // 子过程
				List<String> subProcToken = token.subList(0, 4);
				token = token.subList(4, token.size());

				startPoint = subProc(subProcToken, startPoint);
			}
			else if (token.get(1).equals("=")) { // 赋值语句
				List<String> assignToken = token.subList(0, 4);
				token = token.subList(4, token.size());

				startPoint = cvAssignment(assignToken, startPoint);
			}

		}

		sortPoints();
	}


	/**
	 * program point analysis of assign statement
	 * 
	 * @param token
	 * @param startPoint
	 * @return
	 */
	public int cvAssignment(List<String> token, int startPoint) {// ///////////赋值语句
		int endPoint;
		String cvName = token.get(0);
		int assignValue = Integer.parseInt(token.get(2));
		endPoint = startPoint + 1;
		this.points.add(new ProgramPoint(startPoint, endPoint, define.assign, cvName, assignValue));// 添加新的程序点

		return endPoint;
	}


	/**
	 * program point analysis of call statement
	 * 
	 * @param token
	 * @param startPoint
	 * @return
	 */
	public int subProc(List<String> token, int startPoint) { // ////////子过程
		int endPoint;
		String taskName = token.get(0);
		endPoint = startPoint + 1;
		this.points.add(new ProgramPoint(startPoint, endPoint, define.call, taskName));

		return endPoint;
	}


	/**
	 * program point analysis of if statement
	 * 
	 * @param token
	 * @param startPoint
	 * @return
	 */
	public int if_statement(List<String> token, int startPoint) {// ///////////////if语句
		int endPoint = 0;

		int boolExprIndex = token.indexOf(")");
		List<String> boolExprToken = token.subList(2, boolExprIndex); // 布尔表达式的token
		ArrayList<BoolExpr> exprs = new ArrayList<BoolExpr>();// *******************//
		while (!boolExprToken.isEmpty()) {// 获取布尔表达式
			String cvName = boolExprToken.get(0);
			String op = boolExprToken.get(1);
			int value = Integer.parseInt(boolExprToken.get(2));
			exprs.add(new BoolExpr(cvName, op, value));

			if (boolExprToken.size() >= 4)
				boolExprToken = boolExprToken.subList(4, boolExprToken.size());
			else
				break;
		}

		int elseIndex = token.indexOf("else");
		token = token.subList(boolExprIndex + 1, token.size());// 去掉bool表达式部分
		if (elseIndex == -1) { // 没有else语句
			int temp = startPoint;
			endPoint = if_branch(token.get(0), startPoint + 1);
			this.points.add(new ProgramPoint(temp, temp + 1, define.IF, exprs, endPoint));

		}
		else { // 有else语句
			int temp = startPoint;
			int elsePoint = if_branch(token.get(0), startPoint + 1);
			endPoint = if_branch(token.get(2), elsePoint);

			for (ProgramPoint pp : this.points) {
				if (pp.getNextPoint() == elsePoint)
					pp.setNextPoint(endPoint);
				else if (pp.getElsePoint() == elsePoint)
					pp.setElsePoint(endPoint);
			}
			this.points.add(new ProgramPoint(temp, temp + 1, define.IF, exprs, elsePoint));
		}

		return endPoint;
	}


	/**
	 * program point analysis of compound statement in if statement
	 * 
	 * @param str
	 * @param startPoint
	 * @return
	 */
	public int if_branch(String str, int startPoint) {
		int endPoint = 0;
		String stats = str.substring(1, str.length() - 1);
		List<String> token = Lexer.lexerAnalyse(stats);
		while (!token.isEmpty()) {
			if (token.get(0).equals("if")) { // if语句
				int index = (token.indexOf("else") != -1) ? token.indexOf("else") + 1 : token
						.indexOf(")") + 1;
				List<String> ifToken = token.subList(0, index + 1);// if语句的token
				token = token.subList(index + 1, token.size());

				startPoint = if_statement(ifToken, startPoint);
			}
			else if (token.get(1).equals("(")) { // 子过程
				List<String> subProcToken = token.subList(0, 4);
				token = token.subList(4, token.size());

				startPoint = subProc(subProcToken, startPoint);
			}
			else if (token.get(1).equals("=")) { // 赋值语句
				List<String> assignToken = token.subList(0, 4);
				token = token.subList(4, token.size());

				startPoint = cvAssignment(assignToken, startPoint);
			}

		}

		endPoint = startPoint;
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
	 * @param index
	 * @return null if index is illegal
	 */
	public ProgramPoint getPP(int index) {
		if (index >= this.points.size() || index < 0) {
			return null;
		}
		else
			return this.points.get(index);
	}


	/**
	 * get the end point of this procedure
	 * 
	 * @return
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
