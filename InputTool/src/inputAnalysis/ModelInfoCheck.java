
package inputAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import userInterface.MainFrame;
import util.ErrorInfo;
import util.Notifier;
import util.define;
import variableDefinition.ControlVariable;
import variableDefinition.Interruption;
import variableDefinition.Interval;
import variableDefinition.Model;
import variableDefinition.ShareResource;
import variableDefinition.Statement;
import variableDefinition.Task;
import variableDefinition.TaskSequence;


/**
 * class of source analysis, provide function to check the total model
 * information
 * 
 * @author zengke.cai
 * 
 */

public class ModelInfoCheck{

	public static boolean computeInfoCorrect;	//计算所需的信息是否正确的标签，包括中断及处理程序信息和子过程信息
	public static boolean otherInfoCorrect;		//其它信息是否正确的标签
	//在检查子过程是否循环调用中记录出栈次序的列表，隐含了子过程的调用拓扑关系

	//////////////////////////////////////////////全局检查//////////////////////////////////////////////////
	/**
	 * check all the model data, render errors in tables
	 * 
	 */
	public static boolean totalAnalysis(){
		ErrorInfo.clear(); //clear error information
		computeInfoCorrect = true;
		otherInfoCorrect = true;

		Map<Integer, ArrayList<Integer>> cvLocations = new HashMap<Integer, ArrayList<Integer>>();
		Map<Integer, ArrayList<Integer>> taskLocations = new HashMap<Integer, ArrayList<Integer>>();
		Map<Integer, ArrayList<Integer>> srLocations = new HashMap<Integer, ArrayList<Integer>>();
		Map<Integer, ArrayList<Integer>> interLocations = new HashMap<Integer, ArrayList<Integer>>();
		Map<Integer, ArrayList<Integer>> intervalLocations = new HashMap<Integer, ArrayList<Integer>>();

		//////////////////////////////////////////////对模型基本信息的检查
		/**控制变量**/
		cvLocations.clear();	
		int index = 0;
		for (ControlVariable cv : Model.controlVariableArray){
			if(cv.contentCheck() != define.noError){
				otherInfoCorrect = false;
				ArrayList<Integer> errorIndices = cv.getErrorList();
				if(errorIndices.size() > 0)
					cvLocations.put(index, errorIndices);
			}
			index++;
		}

		if(!cvLocations.isEmpty()){	//进行渲染
			cvLocations = mapReverse(cvLocations);
		}
		MainFrame.renderer(MainFrame.cvariableTable, cvLocations);

		
		/**共享资源**/
		index = 0;
		srLocations.clear();
		for (ShareResource sr : Model.shareResourceArray){
			if(sr.contentCheck() != define.noError){
				otherInfoCorrect = false;
				ArrayList<Integer> errorIndices = sr.getErrorList();
				if(errorIndices.size() > 0)
					srLocations.put(index, errorIndices);
			}
			index++;
		}

		if(!srLocations.isEmpty()){
			srLocations = mapReverse(srLocations);
		}
		MainFrame.renderer(MainFrame.shareResourceTable, srLocations);


		/**子过程**/
		index = 0;
		taskLocations.clear();
		for (Task task : Model.taskArray){
			if(task.contentCheck() != define.noError){
				computeInfoCorrect = false;
				ArrayList<Integer> errorIndices = task.getErrorList();
				if(errorIndices.size() > 0)
					taskLocations.put(index, errorIndices);
			}
			index++;
		}

		if(!taskLocations.isEmpty()){
			taskLocations = mapReverse(taskLocations);
		}
		updateTaskInfo();	//更新子过程信息
		
		MainFrame.renderer(MainFrame.taskTable, taskLocations);


		/**中断/系统任务**/
		index = 0;
		interLocations.clear();
		for (Interruption inter : Model.interArray){
			if(inter.contentCheck() != define.noError){
				computeInfoCorrect = false;
				ArrayList<Integer> errorIndices = inter.getErrorList();
				if(errorIndices.size() > 0)
					interLocations.put(index, errorIndices);
			}
			index++;
			
			//检查中断处理程序
			Statement st = inter.proc.statement;
			st.check();
			if(st.errorIndex != define.noError){
				computeInfoCorrect = false;
				ErrorInfo.add(st.errorInfo);
			}
		}

		if(!interLocations.isEmpty()){
			interLocations = mapReverse(interLocations);
		}
		else{	//若基本内容正确，检查周期性系统任务的性质
			if(checkSysTask() == false)
				otherInfoCorrect = false;
		}
		MainFrame.renderer(MainFrame.interTable, interLocations);


		/**时序逻辑**/
		for (TaskSequence seq : Model.taskSequences){
			int errorIndex = seq.contentCheck();
			if(errorIndex != define.noError)
				otherInfoCorrect = false;
		}
		
		/**中断间隔*/
		index = 0;
		intervalLocations.clear();
		for (Interval interval : Model.intervalArray){
			if(interval.contentCheck() != define.noError){
				otherInfoCorrect = false;
				ArrayList<Integer> errorIndices = interval.getErrorList();
				if(errorIndices.size() > 0)
					intervalLocations.put(index, errorIndices);
			}
			index++;
		}

		if(!intervalLocations.isEmpty()){
			intervalLocations = mapReverse(intervalLocations);
		}
		MainFrame.renderer(MainFrame.intervalTable, intervalLocations);
		
			
		
		/////////////////////////////////////////////////对模型信息的分析
		if (computeInfoCorrect == true) {
			/** 分析每个控制变量被哪些procedure访问 **/
			containedCV();
			/** 分析每个子过程被哪些procedure调用 **/
			containedTask();
			/** 分析每个共享资源被哪些子过程读、写 **/
			containedSR();
		}
		
		
		////////////////////////////////////////////界面显示检查结果，更新WCET
		MainFrame.showErrorInfo();
		MainFrame.updateSET();
		MainFrame.updateWCET();

		return computeInfoCorrect && otherInfoCorrect;
	}





	/**
	 * check the constrains of system tasks, specially periodical task
	 */
	public static boolean checkSysTask(){ //检查系统任务的allowTime是否大于upbound
		Map<Long, ArrayList<Interruption>> interMap = classifyInter();

		//check data constrain of cycle system tasks
		for (Long key : interMap.keySet()){
			ArrayList<Interruption> interList = interMap.get(key);
			if(checkCycleInter(interList) == false)
				return false;
		}
		return true;
	}


	/**
	 * function to classify the system tasks according to their cycle
	 * 
	 * @return a map from cycle to interruption list
	 */
	public static Map<Long, ArrayList<Interruption>> classifyInter(){
		Map<Long, ArrayList<Interruption>> interMap = new HashMap<Long, ArrayList<Interruption>>();
		for (Interruption inter : Model.interArray){
			//search from the interruptions which are system procedure and have cycle
			if(inter.intPrio == 255 && inter.longRepeat != -1){
				long cycle = inter.longRepeat; //get cycle
				// find in hashMap, add if certain cycle exist
				if(interMap.keySet().contains(cycle)){
					for (Long key : interMap.keySet()){
						if(key == cycle){
							interMap.get(key).add(inter);
							break;
						}
					}
				}
				//otherwise, add a new entry
				else{
					ArrayList<Interruption> interList = new ArrayList<Interruption>();
					interList.add(inter);
					interMap.put(cycle, interList);
				}
			}
		}
		return interMap;
	}


	/**
	 * function to check the data constrain of cycle system tasks
	 * 
	 * constrain : the allow time of a interruption must bigger than its upper
	 * bound
	 * 
	 * @param interList: interruptions with the same cycle
	 * @return true if constrain is satisfied, otherwise false
	 */
	public static boolean checkCycleInter(ArrayList<Interruption> interList){
		//sort by the offset of each interruption
		int size = interList.size();
		while (size > 0){
			//find the minimum offset
			int index = 0;
			for (int i = 1; i < size; i++){
				if(interList.get(i).longOffset < interList.get(index).longOffset)
					index = i;
			}
			//remove and push behind
			Interruption temp = interList.remove(index);
			interList.add(temp);

			size--;
		}

		//compare upperBound and allowed time
		for (int k = 0; k < interList.size(); k++){
			Interruption inter = interList.get(k);
			long allowTime;
			if(k == interList.size() - 1)
				allowTime = inter.longRepeat - inter.longOffset;
			else{
				allowTime = interList.get(k + 1).longOffset - inter.longOffset;
			}

			if(inter.longUBD > allowTime){
				ErrorInfo.add("【中断/任务】 : " + inter.name + " 时间上界超过周期允许时间!");
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * function to update all the task information after read task description file
	 */
	private static void updateTaskInfo(){
		ArrayList<Task> checkedList = new ArrayList<Task>();
		if(checkLoopCall(checkedList) == true){	//存在循环调用，则弃用子过程附加文件中的所有信息
			for(Task task : Model.taskArray)	//TODO 如果多次读取附加文件导致循环调用，会删掉所有Task的定义
				task.removeProc();
		}
		else{		//否则， 按逆拓扑序更新子过程的上下界以及共享资源信息
			for(Task task : checkedList){
				if(task.proc != null){
					task.proc.computeExecTime();
					
					task.setBound(task.proc.bestTime, task.proc.worstTime);
					task.addSR(task.proc.readSRs, task.proc.writeSRs);
				}
			}
		}
		
		//更新三个表格的信息，因为可能有新的CV/SR加入模型
		MainFrame.setTaskContent();
		MainFrame.setCVContent();
		MainFrame.setSRContent();
	}
	
	
	/**
	 * check whether there exist loop call in task
	 * @return true if loop call exists
	 */
	private static boolean checkLoopCall(ArrayList<Task> checkedList){
		if(Model.taskArray.isEmpty())
			return false;

		Stack<Task> stack = new Stack<Task>();
		stack.push(Model.taskArray.get(0));
		
		while(!stack.isEmpty()){
			if(containsSameEle(stack)){
				String trace = "";
				for(int i = 0; i < stack.size(); i++)
					trace += stack.elementAt(i).name + "->";
				Notifier.ErrorPrompt("子过程中存在循环调用序列： " + trace);
				return true;
			}
			else{
				//不调用其它子过程，出栈
				if(stack.peek().proc == null || stack.peek().proc.containedTasks == null){
					checkedList.add(stack.pop());
				}
				//调用的子过程均检查过，出栈
				else if(checkedList.containsAll(stack.peek().proc.containedTasks)){
					checkedList.add(stack.pop());
				}
				else{	//将未检查过的子过程压栈
					for(Task task : stack.peek().proc.containedTasks)
						if(!checkedList.contains(task)){
							stack.push(task);
							break;
						}
				}
				
				//模型中还存在未检查的子过程,压栈
				if(stack.isEmpty()){
					for(Task task : Model.taskArray)
						if(! checkedList.contains(task)){
							stack.push(task);
							break;
						}
				}
			}
		}
		return false;
	}


	/**
	 * analysis to get the procedure list that access certain control variables
	 */
	private static void containedCV(){
		for (ControlVariable cv : Model.controlVariableArray){
			cv.clearProcNames();
		}
		
		//analysis contained control variables
		for (Interruption inter : Model.interArray){
			if(inter.proc.containedCVs != null)
				for(ControlVariable cv : inter.proc.containedCVs)
					cv.addProcName(inter.name);
		}
		for(Task task : Model.taskArray){
			if(task.proc != null && task.proc.containedCVs != null)
				for(ControlVariable cv : task.proc.containedCVs)
					cv.addProcName(task.name);
		}
		
		//write into control variable table
		for (int i = 0; i < Model.controlVariableArray.size(); i++){
			ControlVariable cv = Model.controlVariableArray.get(i);
			MainFrame.cvariableTable.setValueAt(cv.getProcNames(), i, ControlVariable.paraSize);
		}
	}


	/**
	 * analysis to get the procedure list that call certain task
	 */
	private static void containedTask(){
		for (Task task : Model.taskArray){
			task.clearProcNames();
		}
		//analysis contained tasks in interruption
		for (Interruption inter : Model.interArray){
			if(inter.proc.containedTasks != null)
				for(Task task : inter.proc.containedTasks)
					task.addProcName(inter.name);
		}
		for(Task task : Model.taskArray)
			if(task.proc != null && task.proc.containedTasks != null)
				for(Task subTask : task.proc.containedTasks)
					subTask.addProcName(task.name);
		
		//write into task table
		for (int i = 0; i < Model.taskArray.size(); i++){
			Task task = Model.taskArray.get(i);
			MainFrame.taskTable.setValueAt(task.getProcNames(), i, Task.paraSize);
		}
	}


	/**
	 * analysis to get the task list that read & write certain share resource
	 */
	private static void containedSR(){
		for (ShareResource sr : Model.shareResourceArray){
			sr.clearReadTasks();
			sr.clearWriteTasks();
		}
		//analysis contained share resources
		for (Task task : Model.taskArray){
			task.containedReadSR();
			task.containedWriteSR();
		}
		//write into share resource table
		for (int i = 0; i < Model.shareResourceArray.size(); i++){
			ShareResource sr = Model.shareResourceArray.get(i);
			MainFrame.shareResourceTable.setValueAt(sr.getReadTaskNames(), i, 1);
			MainFrame.shareResourceTable.setValueAt(sr.getWriteTaskNames(), i, 2);
		}
	}
	
	
	/**
	 * function to reverse a map, <row, columns> to <column, rows>
	 */
	private static Map<Integer, ArrayList<Integer>> mapReverse(Map<Integer, ArrayList<Integer>> sour){
		Map<Integer, ArrayList<Integer>> result = new HashMap<Integer, ArrayList<Integer>>();
		for (int row : sour.keySet())
			for (int col : sour.get(row)){
				if(result.containsKey(col)){ //if result map contains certain column, add rows into mapping list
					result.get(col).add(row);
				}
				else{ //else, add a new mapping
					ArrayList<Integer> rows = new ArrayList<Integer>();
					rows.add(row);
					result.put(col, rows);
				}
			}
		sour.clear();
		return result;
	}
	
	
	/**
	 * check whether there exist same element in the given stack
	 */
	private static boolean containsSameEle(Stack<Task> stack){
		if(stack.size() == 1)
			return false;
		else{
			for(int i = 0; i < stack.size()-1; i++)
				for(int j = i+1; j < stack.size(); j++)
					if(stack.elementAt(i).equals(stack.elementAt(j)))
						return true;
			return false;
		}
	}

}
