
package algorithm;

import fileOperator.Logger;
import initializer.Model;
import ita.ITAEdge;

import java.util.ArrayList;

import programStructure.CPUStack;
import programStructure.CVMap;
import programStructure.IRQSwitch;
import programStructure.Interruption;
import programStructure.ProgramPoint;
import programStructure.SRArray;
import util.BoolExpr;
import util.define;


/**
 * class of global state
 * 
 * @author zengke.cai
 * 
 */
public class GlobalState{

	private int[] locVec; //vector of ITANet location
	private boolean[] interVec; //interruption vector
	private IRQSwitch switches;	 // state of each IRQ
	private CPUStack stack; //CPU stack
	private CVMap cvMap; //control variable map
	private SRArray srArray;	//share resource list

	
	/**
	 * constructor, initialize global state with model data
	 */
	public GlobalState() {
		//initialize ITANet location vector
		int ITACount = Model.getITACount();
		this.locVec = new int[ITACount];
		for (int i = 0; i < ITACount; i++){
			this.locVec[i] = Model.getITAAt(i).getInitLoc();
		}

		//initialize interruption vector
		int interCount = Model.getInterCount();
		this.interVec = new boolean[interCount];
		for(int i = 0; i < interCount; i++){
			this.interVec[i] = false;
		}
		
		this.cvMap = Model.getCVMap(); //share 'cvMap' with Model
		this.srArray = Model.getSRArray();	//share 'srArray' with Model 
		this.stack = new CPUStack();
		this.switches = new IRQSwitch();
	}


	/**
	 * calculate the next state of current state on event e
	 * 
	 * @return the index of lost interruption, -1 for no interruption lost
	 */
	public int successor(Event e){
		/**ITA transition event**/
		if(e.getType() == define.trans){ 
			int ITAIndex = e.getITAIndex();
			ITAEdge edge = Model.getITAAt(ITAIndex).getLocation(this.locVec[ITAIndex]).getEdge();

			if(edge.getInterIndex() != -1){ //set interruption vector, if there is interruption on edge
				if(this.interVec[edge.getInterIndex()] == true)			
					return edge.getInterIndex();
				else
					this.interVec[edge.getInterIndex()] = true;
			}
			/**须先置中断向量表再置自动机位置向量，否则中断丢失的情况下中断向量表不置位，而自动机位置置位，造成不一致**/
			this.locVec[ITAIndex] = edge.getToLoc(); //set location vector of ITANet
		}

		 /**push in new procedure event**/
		else if(e.getType() == define.push){
			int procIndex = e.getProcIndex();
			this.interVec[procIndex] = false;
			this.stack.push(procIndex, 0);
		}

		/**procedure goes on event, can be type 2 or 3**/
		else{
			//get procedure and programPoint at top of CPU stack
			Interruption inter = Model.getInterAt(this.stack.getTopProc());
			ProgramPoint pp = inter.getIP().getPP(this.stack.getTopPnt());

			if(pp.getType() == define.assign){ //statement at this point is assign statement 
				this.cvMap.setValue(pp.getCvName(), pp.getAssignValue());
			}
			else if(pp.getType() == define.open){	//open statement
				this.switches.open(pp.getIRQ());
			}
			else if(pp.getType() == define.close){	//close statement
				this.switches.close(pp.getIRQ());
			}
			else{ //statement at this point is call/if statement

			}

			if(e.getType() == define.pop)	//pop event, pop out procedure
				this.stack.pop();
			else
				this.stack.setPnt(e.getPnt());//set next program point
					
		}
		
		return -1;
	}
	
	
	/**
	 * get the interruption with highest priority that answerable
	 */
	public int highestInterIndex(){
		int index = -1;
		for (int i = 0; i < this.interVec.length; i++) {
			if (index == -1 && this.interVec[i] == true 
					&& this.getSwitchState(Model.getInterAt(i).getIRQ()) == true)
				index = i;
			else if(this.interVec[i] == true &&
						this.getSwitchState(Model.getInterAt(i).getIRQ()) == true &&
							Model.getInterAt(i).getPriority() < Model.getInterAt(index).getPriority())
				index = i;
		}
		return index;
	}
	
	
	/**
	 * check the result of boolean expression
	 * 
	 * @param exprs connect with '&&' only
	 */
	public boolean checkBoolExpr(ArrayList<BoolExpr> exprs){
		if (exprs == null || exprs.isEmpty()) {
			return true;
		}

		for (BoolExpr expr : exprs) {
			boolean flag = false;

			String op = expr.getOp();
			int testValue = expr.getValue();
			int realValue = this.cvMap.getValue(expr.getCvName());
			
			if (op.equals(">") && realValue > testValue)
				flag = true;
			else if (op.equals(">=") && realValue >= testValue)
				flag = true;
			else if (op.equals("<") && realValue < testValue)
				flag = true;
			else if (op.equals("<=") && realValue <= testValue)
				flag = true;
			else if (op.equals("==") && realValue == testValue)
				flag = true;
			else if (op.equals("!=") && realValue != testValue)
				flag = true;

			if (flag == false)// one false, all false
				return false;
		}
		return true;
	}
	
	
	/**
	 * get the switch state of given IRQ
	 */
	public boolean getSwitchState(String IRQ){
		return this.switches.getState(IRQ);
	}
	
	
	/**
	 * reverse the switch state
	 */
	public void reverse(String IRQ){
		this.switches.reverse(IRQ);
	}

	
	/**
	 * get the current location of given ITA index
	 */
	public int getITALoc(int ITAIndex){
		if(ITAIndex < 0 || ITAIndex > Model.getITACount())
			return -1;
		else
			return this.locVec[ITAIndex];
	}
	
	
	/**
	 * set the location of given ITA index
	 */
	public void setITALoc(int ITAIndex, int loc){
		if(ITAIndex < 0 || ITAIndex > Model.getITACount())
			;
		else
			this.locVec[ITAIndex] = loc;
	}
	
	
	/**
	 * get the value of inteVec element at 'index'
	 */
	public boolean getInterFlag(int index){
		if (index < 0 || index >= this.interVec.length)
			return false; 
		else
			return this.interVec[index];
	}
	
	
	/**
	 * set the value of interVec element at 'index' as 'value'
	 */
	public void setInterFlag(int index, boolean value){
		if (index < 0 || index >= this.interVec.length)
			;
		else
			this.interVec[index] = value;
	}


	/**
	 * get value of control variable of given name
	 */
	public int getCVValue(String name){
		return this.cvMap.getValue(name);
	}
	
	
	/**
	 * set value of control variable as 'value'
	 */
	public void setCVValue(String name, int value){
		this.cvMap.setValue(name, value);
	}
	
	
	
	public CPUStack getStack(){
		return stack;
	}
	
	
	public SRArray getSRArray(){
		return srArray;
	}


	/**
	 * TEST
	 * function to print global state
	 */
	public void printGS(){
		Logger.append("    ITANet locations:	");
		for (int i = 0; i < this.locVec.length; i++)
			Logger.append(this.locVec[i] + " ");
		Logger.append("\r\n");

		Logger.append("    Interruption vector:	");
		for(int i = 0; i < this.interVec.length; i++){
			if(this.interVec[i] == true)
				Logger.append("true ");
			else
				Logger.append("false ");
		}
		Logger.append("\r\n");

		Logger.append("    CPUStack:	");
		this.stack.printAllEle();
		Logger.append("\r\n");

		Logger.append("    Control variable:	");
		this.cvMap.printAllEle();
		Logger.append("\r\n");
		
		Logger.append("    IRQSwitch:     ");
		this.switches.print();
		Logger.append("\r\n");
	}
}
