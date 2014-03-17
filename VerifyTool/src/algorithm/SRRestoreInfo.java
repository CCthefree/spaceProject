package algorithm;

import java.util.ArrayList;

/**
 * class of restore information for share resource
 * 
 * @author zengke.cai
 * 
 */
class SRRestoreInfo {

	ArrayList<Integer> incRead; // list of indexes of SR that should increase
								// reading count

	 ArrayList<Integer> incWrite; // list of indexes of SR that should increase
									// writing count

	ArrayList<Integer> decRead; // list of indexes of SR that should decrease
								// reading count

	ArrayList<Integer> decWrite; // list of indexes of SR that should decrease
									// writing count

	SRRestoreInfo(){
		
	}
}
