package programStructure;

import java.util.HashMap;
import java.util.Map;

import fileOperator.Logger;

/**
 * class of IRQ switches, record and set the state(open/close) of each IRQ
 * @author zengke.cai
 *
 */
public class IRQSwitch {
	private Map<String, Boolean> switches;
	
	/**
	 * constructor
	 */
	public IRQSwitch(){
		this.switches = new HashMap<String, Boolean>();
		initialize();
	}
	
	
	private void initialize(){
		//TODO
	}
	
	
	/**
	 * get the state of certain IRQ
	 */
	public boolean getState(String IRQ){
		return this.switches.containsKey(IRQ) ? this.switches.get(IRQ) : true;
	}
	
	
	/**
	 * open certain IRQ
	 */
	public void open(String IRQ){
		this.switches.remove(IRQ);
		this.switches.put(IRQ, true);
	}
	
	
	/**
	 * close certain IRQ
	 */
	public void close(String IRQ){
		this.switches.remove(IRQ);
		this.switches.put(IRQ, false);
	}
	
	
	/**
	 * reverse the value of given IRQ
	 */
	public void reverse(String IRQ){
		if(!this.switches.containsKey(IRQ))
			;
		else{
			boolean value = this.switches.get(IRQ);
			this.switches.remove(IRQ);
			this.switches.put(IRQ, !value);
		}
	}
	
	
	/**
	 * TEST print the IRQ switches
	 */
	public void print(){
		for (String key : this.switches.keySet()) {
			Logger.append("<" + key + ", " + this.switches.get(key) + ">; ");
		}
	}
}
