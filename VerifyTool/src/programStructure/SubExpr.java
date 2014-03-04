package programStructure;

/**
 * class of subtract expression class, in form of <firstPara, secondPara>
 * 
 * @author zengke.cai
 * 
 */
public class SubExpr {

	private int firstPara; // minuend

	private int secondPara; // subtractor


	/**
	 * constructor
	 */
	public SubExpr(int f, int s) {
		this.firstPara = f;
		this.secondPara = s;
	}


	public int getFirstPara() {
		return firstPara;
	}


	public int getSecondPara() {
		return secondPara;
	}


	public void setFirstPara(int f) {
		this.firstPara = f;
	}

}
