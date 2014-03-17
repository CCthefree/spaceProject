
package algorithm;

import initializer.Model;
import ita.ITA;
import ita.ITAEdge;
import programStructure.CPUStack;
import programStructure.CVMap;
import programStructure.IRQSwitch;
import programStructure.InterruptVector;
import programStructure.Interruption;
import programStructure.ProgramPoint;
import programStructure.SRArray;
import util.define;
import fileOperator.Logger;


/**
 * class of global state
 * 
 * @author zengke.cai
 * 
 */
public class GlobalState{

	private int[] locVec; //vector of ITANet location
	private IRQSwitch switches;	 // state of each IRQ
	private CPUStack stack; //CPU stack
	private InterruptVector interVec; //interruption vector
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

		this.cvMap = Model.getCVMap(); //share 'cvMap' with Model
		this.srArray = Model.getSRArray();	//share 'srArray' with Model 
		this.stack = new CPUStack();
		this.interVec = new InterruptVector();
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
			int curLoc = this.locVec[ITAIndex];
			ITA ita = Model.getITAAt(ITAIndex);
			ITAEdge edge = ita.getLocation(curLoc).getEdge();
			int nextLoc = edge.getToLoc();

			if(edge.getInterIndex() != -1){ //set interruption vector, if there is interruption on edge
				if(this.interVec.getValue(edge.getInterIndex()) == true){			
					return edge.getInterIndex();
				}
				else
					this.interVec.setTrue(edge.getInterIndex());
			}
			/**须先置中断向量表再置自动机位置向量，否则中断丢失的情况下中断向量表不置位，而自动机位置置位，造成不一致**/
			this.locVec[ITAIndex] = nextLoc; //set location vector of ITANet
			
		}

		 /**push in new procedure event**/
		else if(e.getType() == define.push){
			int procIndex = e.getProcIndex();
			this.interVec.setFalse(procIndex);
			this.stack.push(procIndex, 0);
		}

		/**procedure goes on event, can be type 2 or 3**/
		else{
			//get procedure and programPoint at top of CPU stack
			Interruption inter = Model.getInterAt(this.stack.getTopProc());
			ProgramPoint pp = inter.getIP().getPP(this.stack.getTopPnt());
			int nextPoint = e.getPnt(); //next program point

			if(pp.getType() == define.assign){ //statement at this point is assign statement 
				String cvName = pp.getCvName();
				int value = pp.getAssignValue();
				this.cvMap.setValue(cvName, value);

			}
			else{ //statement at this point is call/if statement

			}

			if(e.getType() == define.pop)	//pop event, pop out procedure
				this.stack.pop();
			else
				this.stack.setPnt(nextPoint);//set next program point
					
		}
		
		return -1;
	}
	
	
	


	public int[] getLocVec(){
		return locVec;
	}


	public CPUStack getStack(){
		return stack;
	}


	public InterruptVector getInterVec(){
		return interVec;
	}


	public CVMap getCvMap(){
		return cvMap;
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
		for (int i = 0; i < this.interVec.getSize(); i++){
			if(this.interVec.getValue(i) == true)
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
	}
}
