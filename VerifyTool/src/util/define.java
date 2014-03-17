package util;


public class define {
	
	//define type of event
	public static final int trans = 0;
	public static final int push = 1;
	public static final int move = 2;
	public static final int pop = 3;
	
	//define type of program point
	public static final int call = 10;
	public static final int assign = 11;
	public static final int open = 12;
	public static final int close = 13;
	public static final int comp = 14;
	public static final int IF = 15;
	
	//define error type of checking result
	public static final int noError = 100;
	public static final int interLost = 101;
	public static final int SRconflict = 102;
	public static final int taskOT = 103;
	public static final int procOT = 104;
}
