package variableDefinition;

import java.util.ArrayList;

import util.Lexer;
import util.Notifier;

/**
 * class of model data
 * @author zengke.cai
 *
 */
public class Model{


	// list of entity objects
	public static ArrayList<Task> taskArray = new ArrayList<Task>();
	public static ArrayList<Interruption> interArray = new ArrayList<Interruption>();
	public static ArrayList<ControlVariable> controlVariableArray = new ArrayList<ControlVariable>();
	public static ArrayList<ShareResource> shareResourceArray = new ArrayList<ShareResource>();
	public static ArrayList<TaskSequence> taskSequences = new ArrayList<TaskSequence>();
	public static ArrayList<Interval> intervalArray = new ArrayList<Interval>();
	public static long commuTaskBound = -1;



	/**
	 * add a new task with given name
	 */
	public static Task addNewTask(String name) {
		Task task = new Task();
		task.name = name;
		Model.taskArray.add(task);
		return task;
	}
	
	
	/**
	 * add a new control variable with given name
	 */
	public static ControlVariable addNewCV(String name) {
		ControlVariable cv = new ControlVariable();
		cv.name = name;
		Model.controlVariableArray.add(cv);
		return cv;
	}

	
	/**
	 * add a new share resource
	 * @param name: the name is input by user, maybe illegal
	 */
	public static ShareResource addNewSR(String name){
		if(!Lexer.isStructuredVar(name)){
			Notifier.ErrorPrompt(name + "不是合法的变量名！");
			return null;
		}
		else if(existSameName(name)){
			Notifier.ErrorPrompt("已存在同名的共享资源: " + name);
			return null;
		}
		else{
			ShareResource sr = new ShareResource(name);
			Model.shareResourceArray.add(sr);
			return sr;
		}
	}
	
	
	/**
	 * test whether there exists same name share resource
	 * 
	 * @return true if exists
	 */
	public static boolean existSameName(String name){
		for (ShareResource sr : Model.shareResourceArray){
			if(sr.name.equals(name)){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * get share resource with given name return null if no such share resource
	 */
	public static ShareResource getSR(String name) {
		for (ShareResource sr : Model.shareResourceArray)
			if (sr.name.equals(name))
				return sr;
		return null;
	}
	
	
	/**
	 * get the control variable with the given name
	 * 
	 * @param name
	 *            of control variable
	 */
	public static ControlVariable getCV(String str) {
		for (ControlVariable cv : Model.controlVariableArray) {
			if (cv.name.equals(str))
				return cv;
		}
		return null;
	}
	
	
	/**
	 * get the procedure with given name
	 */
	public static Procedure getProcedure(String name) {
		for (Interruption inter : Model.interArray) {
			if (inter.name.equals(name))
				return inter.proc;
		}
		for(Task task : Model.taskArray){
			if(task.name.equals(name))
				return task.proc;
		}
		return null;
	}
	
	
	/**
	 * get the task with the given name
	 */
	public static Task getTask(String str) {
		for (Task task : Model.taskArray) {
			if (task.name.equals(str))
				return task;
		}
		return null;
	}
}
