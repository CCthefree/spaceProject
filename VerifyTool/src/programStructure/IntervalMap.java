package programStructure;

import java.util.HashMap;
import java.util.Map;

/**
 * class of interval map(IRQ-leastInterval)
 * @author zengke.cai
 *
 */
public class IntervalMap {
	private Map<String, Long> intervalMap;	//map from IRQ to least interval
	
	public IntervalMap(){
		this.intervalMap = new HashMap<String, Long>();
	}
	
	
	/**
	 * add a mapping <IRQ, value> into interval map
	 */
	public void add(String IRQ, long value){
		this.intervalMap.put(IRQ, value);
	}
}
