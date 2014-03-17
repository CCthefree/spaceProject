package programStructure;

import java.util.HashMap;
import java.util.Map;

/**
 * class of IRQ switches, record and set the state(open/close) of each IRQ
 * @author zengke.cai
 *
 */
public class IRQSwitch {
	private Map<String, Boolean> switches;
	
	public IRQSwitch(){
		this.switches = new HashMap<String, Boolean>();
		initialize();
	}
	
	
	private void initialize(){
		
	}
	
	
	/**
	 * get the state of certain IRQ
	 */
	public boolean setState(String IRQ){
		return this.switches.get(IRQ);
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
	
}
