package initializer;

import fileOperator.ITATest;
import fileOperator.PPTest;
import ita.ITA;

import java.util.ArrayList;

import programStructure.CVMap;
import programStructure.Interruption;
import programStructure.Procedure;
import programStructure.ProgramPoint;
import programStructure.SRArray;
import programStructure.Task;

/**
 * class of the whole model
 * 
 * @author zengke.cai
 * 
 */
public class Model {

	private static ArrayList<ITA> ITANet; // net of interruption timed automata

	// private static InterruptVector interVec; //interruption vector
	// private static CPUStack stack; //CPU stack
	private static CVMap cvMap; // control variable map

	private static ArrayList<Interruption> interArray; // interruption array

	private static ArrayList<Task> taskArray;

	private static SRArray srArray;


	/**
	 * constructor
	 */
	public Model() {
		Model.interArray = new ArrayList<Interruption>();
		Model.cvMap = new CVMap();
		Model.ITANet = new ArrayList<ITA>();
		Model.taskArray = new ArrayList<Task>();
		Model.srArray = new SRArray();
	}


	/**
	 * add a new interruption into model
	 * 
	 * @param : data get from input file, all in string form
	 */
	public void addInter(String name, String prio, String type, String period, String offset,
			String lbd, String ubd, String proc) {

		int intPrio = Integer.parseInt(prio);

		char typeForITA = ' ';
		if (intPrio >= 255)
			typeForITA = 'S'; // system task;
		else if (type.equals("random"))
			typeForITA = 'R'; // random interruption
		else
			typeForITA = 'P'; // periodical interruption

		int intPeriod = Integer.parseInt(period);

		int intOffset = Integer.parseInt(offset);

		int lowerBd = Integer.parseInt(lbd);

		int upperBd = Integer.parseInt(ubd);

		Procedure procedure = new Procedure(proc);

		Interruption inter = new Interruption(name, intPrio, typeForITA, intPeriod, intOffset,
				lowerBd, upperBd, procedure);
		Model.interArray.add(inter);
	}


	/**
	 * add a new control variable into model
	 * 
	 * @param : data get from input file, all in string form
	 */
	public void addCV(String name, String value) {
		int valueNum = Integer.parseInt(value);

		Model.cvMap.add(name, valueNum);
	}


	/**
	 * add a new share resource into model
	 * 
	 * @param name
	 */
	public void addSR(String name) {
		Model.srArray.addSR(name);
	}


	/**
	 * add a new task into model
	 */
	public void addTask(String name, String lb, String ub, String finishTime,
			ArrayList<String> read, ArrayList<String> write) {
		int lowerBound = Integer.parseInt(lb);
		int upperBound = Integer.parseInt(ub);
		int ft = Integer.parseInt(finishTime);

		// convert the name list of SR to the index list
		ArrayList<Integer> readIndex = new ArrayList<Integer>();
		ArrayList<Integer> writeIndex = new ArrayList<Integer>();
		for (String str : read) {
			readIndex.add(getSRIndex(str));
		}
		for (String str : write) {
			writeIndex.add(getSRIndex(str));
		}

		Task task = new Task(name, lowerBound, upperBound, ft, readIndex, writeIndex);
		Model.taskArray.add(task);
	}

	
	/**
	 * add new ITA
	 */
	public void addITA(ITA ita) {
		Model.ITANet.add(ita);
	}
	
	
	/**
	 * get ITA of array index 'index'
	 * 
	 * @param index
	 *            : the index ITA in array
	 * @return null if index is illegal
	 */
	public static ITA getITA(int index) {
		if (index >= Model.ITANet.size() || index < 0)
			return null;
		else {
			return Model.ITANet.get(index);
		}
	}


	/**
	 * get interruption of array index 'index'
	 * 
	 * @param index
	 *            : the index interruption in array
	 * @return null if index is illegal
	 */
	public static Interruption getInterAt(int index) {
		if (index >= Model.interArray.size() || index < 0)
			return null;
		else
			return Model.interArray.get(index);
	}
	
	
	/**
	 * 根据中断名获取中断号
	 * @param name
	 * @return
	 */
	public static int getInterIndexByName(String name) {
		for (int i = 0; i < Model.interArray.size(); i++)
			if (Model.getInterAt(i).getName().equals(name))
				return i;
		return -1;
	}


	/**
	 * get procedure according to procIndex
	 */
	public static Procedure getProcAt(int index) {
		if (index >= Model.interArray.size() || index < 0)
			return null;
		else
			return Model.interArray.get(index).getIP();
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
	 * get the index of given interruption in model
	 * 
	 * @param inter
	 * @return -1 if parameter is null or no such interruption
	 */
	public static int getInterIndex(Interruption inter) {
		if (inter == null)
			return -1;
		else
			return Model.interArray.indexOf(inter);
	}


	/**
	 * get the index of given name of share resource
	 * 
	 * @param name
	 * @return -1 if no such share resource
	 */
	public static int getSRIndex(String name) {
		return Model.srArray.getIndex(name);
	}


	/**
	 * get the statement of procedure at certain program point
	 * 
	 * @param procIndex
	 *            : index of procedure
	 * @param pnt
	 *            : program point
	 * @return "" if parameters are illegal
	 */
	public static String getStatement(int procIndex, int pnt) {
		String result = "";

		Interruption inter = Model.getInterAt(procIndex);
		if (inter != null) {
			ProgramPoint pp = inter.getIP().getPP(pnt);
			if (pp != null)
				result = pp.getStatement();
		}

		return result;
	}


	/**
	 * get ShareResource array
	 */
	public static SRArray getSRArray() {
		return Model.srArray;
	}



	public static int getITACount() {
		return Model.ITANet.size();
	}


	public static int getInterCount() {
		return Model.interArray.size();
	}


	public static CVMap getCVMap() {
		return Model.cvMap;
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
