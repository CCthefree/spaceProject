package ita;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import variableDefinition.Interruption;
import variableDefinition.Model;

/**
 * class of ITA network, construct each ITA with interruption(system task sequence)
 * @author zengke.cai
 *
 */

public class ITANet {

	private ArrayList<ITA> itaNet;


	public ITANet() {
		this.itaNet = new ArrayList<ITA>();
	}


	/**
	 * generate ITANet of model, new ITA generated according to interruptions
	 */
	public ArrayList<ITA> generateITA() {
		// map used to classify system tasks according to their period
		// mapping from period to interruption list(with the same period)
		Map<Long, ArrayList<Interruption>> interMap = new HashMap<Long, ArrayList<Interruption>>();

		for (Interruption inter : Model.interArray) {
			// system task, add to 'interMap', deal later
			if (inter.intPrio == 255) {
				long period = inter.longRepeat; // get period
				// find in hashMap, add if certain period exist
				if (interMap.keySet().contains(period)) {
					interMap.get(period).add(inter);
				}
				// otherwise, add a new entry
				else {
					ArrayList<Interruption> interList = new ArrayList<Interruption>();
					interList.add(inter);
					interMap.put(period, interList);
				}
			}
			// periodical interruption, generate ITA and add to model
			else if (inter.intPrio < 255 && inter.type.equals("periodical")) {
				ITA ita = new ITA(inter);
				this.itaNet.add(ita);
			}
			// random interruption, generate ITA and add to model
			else if (inter.intPrio < 255 && inter.type.equals("random")) {
				ITA ita = new ITA(inter);
				this.itaNet.add(ita);
			}
		}

		// generate ITA of system tasks
		for (long key : interMap.keySet()) {
			ITA ita = new ITA(interMap.get(key));
			this.itaNet.add(ita);
		}

		return this.itaNet;
	}
}
