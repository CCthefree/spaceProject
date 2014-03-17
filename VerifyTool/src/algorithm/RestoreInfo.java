package algorithm;

/**
 * class to record the restore information of an event
 * 
 * @author zengke.cai
 * 
 */
class RestoreInfo {

	int type; // type of event

	// info for transition event
	int ITAIndex; // index of ITA
	int preLoc; // present location

	// info for push event
	int procIndex; // index of push in procedure

	// info for pop event
	int preProc;

	// shared info for move/pop event
	int prePnt; // present program point
	String cvName; // control variable name, use when statement at prePnt is assign statement
	int preValue; // present value of control variable
	String IRQ;	//record the operated IRQ


	public RestoreInfo(int type) {
		this.type = type;
		this.cvName = "";
		this.IRQ = "";
	}

}