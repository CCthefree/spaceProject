
package inputAnalysis;

import java.util.HashMap;
import java.util.Map;

import variableDefinition.Interruption;
import variableDefinition.Model;


/**
 * class of computing execute time of interruption according to intervals
 * defined in class "Interval"
 * 
 */

public class ExecuteTime{

	//data structure to record the WCET compute details
	private static Map<String, String> computeDetail = new HashMap<String, String>();
	private static String desc = "";


	/**
	 * function to update WCET of all the interruptions, must compute in order
	 * of priority
	 */
	public static void updateWCET(){
		//clear data before recompute WCET
		computeDetail.clear();

		//update WCET of procedure first
		for (Interruption inter : Model.interArray)
			inter.proc.computeExecTime();

		//compute and update total WCET
		for (Interruption inter : Model.interArray){
			desc = "";
			inter.worstTime = computeWCET(inter);
			computeDetail.put(inter.name, desc);
		}
	}



	/**
	 * function to compute the worst execute time of a given interruption
	 */
	public static long computeWCET(Interruption inter){
		//主程序,不计算WCET
		if(inter.intPrio == 256)
			return -1;

		else{
			long worstTime = inter.proc.worstTime;
			desc += "处理程序静态执行最坏时间: " + worstTime + ";\n";
			desc += "\n最坏情况下，被如下中断打断 : \n";
			long maxInterTime = computeMaxInterTime(inter);
			return (worstTime == -1 || maxInterTime == -1) ? -1 : worstTime + maxInterTime;
		}
	}


	/**
	 * function to compute the max time that the given interruption can be
	 * interrupt by higher interruptions
	 */
	public static long computeMaxInterTime(Interruption currentInter){
		long maxInterTime = 0;
		long tempTime = 0;
		int flag = 0; //mark whether the time of any higher interruption is not computable

		//for those interruptions with higher priority
		for (Interruption inter : Model.interArray){
			if(inter.intPrio < currentInter.intPrio)
				tempTime = interTime(inter, currentInter.longUBD);

			if(tempTime == -1)
				flag = 1;
			else
				maxInterTime += tempTime;
		}

		return (flag == 1) ? -1 : maxInterTime;
	}


	/**
	 * function to compute the interrupt time by a higher interruption in a
	 * given duration
	 */
	public static long interTime(Interruption higherInter, long duration){
		long happenTimes = 0;

		//if the higher interruption is periodical
		if(higherInter.type.equals("periodical")){
			if(higherInter.repeat.equals("-1"))
				return -1;
			else{
				happenTimes = duration / higherInter.longRepeat;
				happenTimes += (duration % higherInter.longRepeat == 0) ? 0 : 1;
			}
		}
		else{
			//compute happenTimes
			happenTimes = duration / higherInter.longInterval;
			happenTimes += (duration % higherInter.longInterval == 0) ? 0 : 1;
		}

		desc += higherInter.name + "	" + happenTimes + " 次;\n";
		//return interrupt time
		return (higherInter.proc.worstTime == -1) ? -1 : happenTimes * higherInter.proc.worstTime;
	}


	/**
	 * function to get the WCET computation information
	 */
	public static String getComputeDetail(String interName){
		for (String key : computeDetail.keySet()){
			if(key.equals(interName))
				return computeDetail.get(key);
		}
		return "";
	}

}
