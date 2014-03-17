package initializer;

import fileOperator.ITATest;
import fileOperator.PPTest;
import ita.ITA;

import java.util.ArrayList;

import programStructure.CVMap;
import programStructure.Interruption;
import programStructure.IntervalMap;
import programStructure.Procedure;
import programStructure.SRArray;
import programStructure.Task;

/**
 * class of the whole model
 * 
 * @author zengke.cai
 * 
 */
public class Model {
	private static CVMap cvMap = new CVMap(); // control variable map
	
	private static SRArray srArray = new SRArray();	//share resource array
	
	private static IntervalMap intervalMap = new IntervalMap(); 	//interval map

	private static ArrayList<Interruption> interArray = new ArrayList<Interruption>(); // interruption array

	private static ArrayList<Task> taskArray = new ArrayList<Task>();	//task array
	
	private static ArrayList<ITA> ITANet = new ArrayList<ITA>(); // net of interruption timed automata

	private static long commuBound;	//time bound of interruption that interrupt communication task
	
/////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * add a new control variable into model
	 */
	public static void addCV(String name, String value) {
		Model.cvMap.add(name, Integer.parseInt(value));
	}


	/**
	 * add a new share resource into model
	 */
	public static void addSR(String name) {
		Model.srArray.addSR(name);
	}
	
	
	/**
	 * add a new interval into model
	 */
	public static void addInterval(String IRQ, String value){
		Model.intervalMap.add(IRQ, Long.parseLong(value));
	}
	
	
	/**
	 * add new ITA
	 */
	public static void addITA(ITA ita) {
		Model.ITANet.add(ita);
	}


	/**
	 * add a new interruption into model
	 * 
	 * @param : data get from input file, all in string form
	 */
	public static void addInter(String name, String prio, String IRQ, String type, String period, 
			String offset, String ubd, String proc) {

		Interruption inter = new Interruption(name, prio, IRQ, type, period, offset, ubd, proc);
		Model.interArray.add(inter);
	}


	/**
	 * add a new task into model
	 */
	public static void addTask(String name, String lb, String ub, String ft,
			String readSR, String writeSR, String commFlag) {

		Task task = new Task(name, lb, ub, ft, readSR, writeSR, commFlag);
		Model.taskArray.add(task);
	}
	
	
	/**
	 * initialize commuBound info
	 */
	public static void initCommuBound(String bound){
		Model.commuBound = Long.parseLong(bound);
	}

////////////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * get ITA at index 'index'
	 *
	 * @return null if index is illegal
	 */
	public static ITA getITAAt(int index) {
		if (index < 0 || index >= Model.ITANet.size())
			return null;
		else 
			return Model.ITANet.get(index);
	}


	/**
	 * get interruption at index 'index'
	 * 
	 * @return null if index is illegal
	 */
	public static Interruption getInterAt(int index) {
		if (index < 0 || index >= Model.interArray.size())
			return null;
		else
			return Model.interArray.get(index);
	}
	
	
	/**
	 * get procedure at index 'index'
	 * 
	 * @return null if index is illegal
	 */
	public static Procedure getProcAt(int index) {
		if (index < 0 || index >= Model.interArray.size())
			return null;
		else
			return Model.interArray.get(index).getIP();
	}
	
	
	/**
	 * get the statement of procedure at certain program point
	 * 
	 * @return "" if parameters are illegal
	 */
	public static String getStatement(int procIndex, int pnt) {
		String result = "";

		Procedure proc = Model.getProcAt(procIndex);
		if (proc != null && proc.getPP(pnt) != null)
			result = proc.getPP(pnt).getStatement();
		return result;
	}
	
	
	/**
	 * get task with given name
	 * 
	 * @return null if no such task
	 */
	public static Task getTask(String name) {
		for (Task task : Model.taskArray)
			if (task.getName().equals(name))
				return task;
		return null;
	}
	
	
	/**
	 * get the interval of given IRQ
	 * 
	 * @return -1 if intervalMap doesn't contains key 'IRQ'
	 */
	public static long getInterval(String IRQ){
		return Model.intervalMap.valueof(IRQ);
	}
	
	
	/**
	 * get index of the interruption with name 'name'
	 * 
	 * @return -1 if no such interruption
	 */
	public static int getInterIndexByName(String name) {
		for (int i = 0; i < Model.interArray.size(); i++)
			if (Model.getInterAt(i).getName().equals(name))
				return i;
		return -1;
	}


	/**
	 * get the index of given name of share resource
	 * 
	 * @return -1 if no such share resource
	 */
	public static int getSRIndex(String name) {
		return Model.srArray.getIndex(name);
	}


	public static CVMap getCVMap() {
		return Model.cvMap;
	}


	public static SRArray getSRArray() {
		return Model.srArray;
	}


	public static int getITACount() {
		return Model.ITANet.size();
	}


	public static int getInterCount() {
		return Model.interArray.size();
	}
	
	
	public static long getCommuBound(){
		return Model.commuBound;
	}


	


	/**
	 * TEST ITANet test function
	 */
	public static void ITANettest() {
		for (int i = 0; i < Model.ITANet.size(); i++) {
			ITA ita = Model.ITANet.get(i);
			ITATest.append("ITA " + i + ": \r\n");
			ita.ITAtest();
			ITATest.append("\r\n");
		}
	}


	/**
	 * TEST interruption test function
	 */
	public static void interTest() {
		for (int i = 0; i < Model.interArray.size(); i++) {
			Interruption inter = Model.getInterAt(i);
			ITATest.append("Interruption " + i + ": \r\n");
			inter.test();
		}
	}


	/**
	 * TEST program point test
	 */
	public static void ppTest() {
		for (int i = 0; i < Model.interArray.size(); i++) {
			Interruption inter = Model.getInterAt(i);
			PPTest.append("procedure " + i + ":\r\n");
			inter.getIP().test();
		}
	}
}
