package programStructure;

import java.util.HashMap;
import java.util.Map;
import fileOperator.Logger;

/**
 * class of control variable map(name-value)
 * 
 * @author zengke.cai
 * 
 */
public class CVMap {

	private Map<String, Integer> cvMap; // mapping from control variable name to
										// value


	/**
	 * constructor
	 */
	public CVMap() {
		this.cvMap = new HashMap<String, Integer>();
	}


	/**
	 * add a new pair of control variable '<name, value>' into cvMap
	 */
	public void add(String name, int value) {
		this.cvMap.put(name, value);
	}


	/**
	 * set the value of control variable 'name' as newValue
	 * 
	 * @return false if no such variable name
	 */
	public boolean setValue(String name, int newValue) {
		if (this.cvMap.containsKey(name)) {
			this.cvMap.remove(name);
			this.cvMap.put(name, newValue);
			return true;
		}
		else
			return false;
	}


	/**
	 * get the value of 'name'
	 * 
	 * @return -1 if no such variable name
	 */
	public int getValue(String name) {
		return this.cvMap.containsKey(name) ? this.cvMap.get(name) : -1;
	}


	/**
	 * TEST function to print all the element in 'cvMap'
	 */
	public void printAllEle() {
		for (String key : this.cvMap.keySet()) {
			Logger.append("<" + key + ", " + this.cvMap.get(key) + ">; ");
		}
	}
}
