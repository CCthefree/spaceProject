package initializer;

public class Event {

	private int type; // indicate the type of event;

	private int timeStamp;	//time stamp when the event happens

	private int index; // index of this event in event path

	private int procIndex; // index of procedure, for all events
	
	private String statement;	//processing statement, for move event


	/**
	 * constructor, parameters that not used set as '-1'
	 */
	public Event(int type, int index, int timeStamp, int procIndex, String stat) {
		this.type = type;
		this.index = index;
		this.timeStamp = timeStamp;
		this.procIndex = procIndex;
		this.statement = stat;
	}


	/**
	 * generate event labels
	 * @return
	 */
	public String genLabel() {
		String result = "";
		//interruption fire event
		if (this.type == define.trans && this.procIndex != -1)
			result += Result.getInterName(this.procIndex) + "触发";
		//procedure pop out event
		else if (this.type == define.pop)
			result += Result.getInterName(this.procIndex) + "出栈";
		//statement process event
		else{ 
			 if (this.type == define.push)
				result += Result.getInterName(this.procIndex) + "进栈,";
			 else 
				result += Result.getInterName(this.procIndex);
			
			if(statement.contains("==")||statement.contains("!=")||statement.contains(">")||
					statement.contains(">=")||statement.contains("<")||statement.contains("<="))
				result += "判断条件" + this.statement;
			else 
				result += "执行语句" + this.statement;
			
		}	
		return result;
	}


	public int getType() {
		return type;
	}


	public int getTimeStamp() {
		return timeStamp;
	}


	public int getIndex() {
		return index;
	}


	public int getProcIndex() {
		return procIndex;
	}
	
	
	public String getStatement(){
		return statement;
	}
}
