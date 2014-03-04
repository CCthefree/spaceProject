
package util;

/**
 * class to handle error information in model provide static methods
 * 
 * @author zengke.cai
 * 
 */
public class ErrorInfo{

	private static String info;
	private static int index;

	/**
	 * clear error information
	 */
	public static void clear(){
		ErrorInfo.info = "";
		ErrorInfo.index = 0;
	}


	/**
	 * add a new error information
	 */
	public static void add(String errorInfo){
		ErrorInfo.info += (++ErrorInfo.index) + " ";
		ErrorInfo.info += (errorInfo + "\n");
	}


	public static String getInfo(){
		return ErrorInfo.info;
	}
}
