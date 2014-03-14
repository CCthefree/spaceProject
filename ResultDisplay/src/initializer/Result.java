package initializer;

import java.util.ArrayList;

/**
 * checking result structure
 * 
 * @author zengke.cai
 * 
 */
public class Result {

	private String result; // the total result("YES" or "NO")

	private static ArrayList<Interruption> interArray;

	private static ArrayList<Event> path;

	private static ArrayList<Integer> readRecord;

	private static ArrayList<Integer> writeRecord;

	private static int type; // fault type(1 for interruption lost, 2 for
								// interruption over time, 3 for task over time
								// 4 for SR conflict)

	private static String name; // the name of fault interruption/task/SR

	private static int upbnd; // the upperBound of fault interruption/task


	/**
	 * constructor
	 */
	public Result() {
		Result.interArray = new ArrayList<Interruption>();
		Result.path = new ArrayList<Event>();
		Result.readRecord = new ArrayList<Integer>();
		Result.writeRecord = new ArrayList<Integer>();
	}


	/**
	 * generate the fault information to display on UI
	 * 
	 * @return
	 */
	public static String genFaultInfo() {
		String result = "";
		if (Result.type == define.interLost)
			result += "中断" + Result.name + "丢失 ";
		else if (Result.type == define.procOT)
			result = "中断处理程序" + Result.name + "超出时间上界: " + Result.upbnd;
		else if (Result.type == define.taskOT)
			result = "子过程" + Result.name + "超出时间上界: " + Result.upbnd;
		else if (Result.type == define.SRconflict)
			result = "共享资源" + Result.name + "访问冲突";

		return result;
	}


	/**
	 * add a interruption to result structure
	 */
	public void addInter(String name, String prio, String index) {
		Interruption inter = new Interruption(name, Integer.parseInt(prio), Integer.parseInt(index));

		Result.interArray.add(inter);
	}


	/**
	 * add a event to event path
	 */
	public void addEvent(String type, String index, String time, String procIndex, String statement) {
		Event event = new Event(Integer.parseInt(type), Integer.parseInt(index),
				Integer.parseInt(time), Integer.parseInt(procIndex), statement);

		Result.path.add(event);
	}


	/**
	 * add new read record
	 */
	public void addReadRec(int readCount) {
		Result.readRecord.add(readCount);
	}


	/**
	 * add new write record
	 */
	public void addWriteRec(int writeCount) {
		Result.writeRecord.add(writeCount);
	}


	/**
	 * sort interruption according to their priority, decrease
	 * 
	 * @param interruption
	 *            list
	 */
	public void sortInter() {
		int size = Result.interArray.size();
		while (size > 0) {
			int index = 0;
			for (int i = 1; i < size; i++) {
				if (Result.interArray.get(i).getPriority() < Result.interArray.get(index)
						.getPriority())
					index = i;
			}
			// remove and push behind
			Interruption temp = Result.interArray.remove(index);
			Result.interArray.add(temp);

			size--;
		}
	}


	/**
	 * get the new index of a interruption after sorted(this index used for
	 * paint)
	 * 
	 * @param index
	 *            : index of the interruption before sorted(this index used in
	 *            event)
	 * @return -1 if no such index of interruption
	 */
	public static int getNewIndex(int index) {
		int result = -1;
		for (int i = 0; i < Result.interArray.size(); i++)
			if (Result.interArray.get(i).getIndex() == index) {
				result = i;
				break;
			}

		return result;
	}


	/**
	 * get the name of a interruption with given index
	 * 
	 * @param index
	 *            : index used in event
	 * @return "" if no such index of interruption
	 */
	public static String getInterName(int index) {
		String result = "";
		for (int i = 0; i < Result.interArray.size(); i++) {
			if (Result.interArray.get(i).getIndex() == index) {
				result = Result.interArray.get(i).getName();
				break;
			}
		}

		return result;
	}


	public static int getInterCount() {
		return Result.interArray.size();
	}


	/**
	 * get the max time stamp of this path
	 * 
	 * @return
	 */
	public static int getMaxTime() {
		return Result.path.get(path.size() - 1).getTimeStamp();
	}


	/**
	 * get the read record at 'index'
	 */
	public static int getReadRecAt(int index) {
		if (index < 0 || index >= Result.readRecord.size())
			return -1;
		else
			return Result.readRecord.get(index);
	}


	/**
	 * get the write record at 'index'
	 */
	public static int getWriteRecAt(int index) {
		if (index < 0 || index >= Result.writeRecord.size())
			return -1;
		else
			return Result.writeRecord.get(index);
	}


	public void setResult(String str) {
		this.result = str;
	}


	public void setFault(int type, String name, int upbnd) {
		Result.type = type;
		Result.name = name;
		Result.upbnd = upbnd;
	}


	public String getResult() {
		return result;
	}


	public static String getName() {
		return name;
	}


	public static int getType() {
		return type;
	}


	public static ArrayList<Interruption> getInterArray() {
		return interArray;
	}


	public static ArrayList<Event> getPath() {
		return path;
	}

}
