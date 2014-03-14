package fileOperator;

import ita.ITA;
import ita.ITAEdge;
import ita.ITALocation;
import ita.ITANet;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import variableDefinition.ControlVariable;
import variableDefinition.Interruption;
import variableDefinition.Interval;
import variableDefinition.Model;
import variableDefinition.ShareResource;
import variableDefinition.Task;
import variableDefinition.TaskSequence;

/**
 * write model into a XML file
 * 
 * @author zengke.cai
 * 
 */
public class XMLFileWriter {
	private String fileName;
	private ArrayList<ITA> itaNet;
	
	
	/**
	 * constructor with parameter
	 * @param str: file name
	 */
	public XMLFileWriter(String str){
		this.fileName = str;
		
		//construct ITA net
		ITANet net = new ITANet();
		this.itaNet = net.generateITA();
	}

	
	/**
	 * save function, save the model information in the given file name, in XML
	 * form
	 */
	public void save() {
		try {
			// set output format
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(new FileWriter(this.fileName),
					format);

			// create XML document
			Document doc = constructDoc();

			writer.write(doc);
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * function to construct XML document
	 */
	public Document constructDoc() {
		Document doc = DocumentHelper.createDocument();

		Element root = doc.addElement("model");

		Element cv = root.addElement("controlVariable");
		constructCVNode(cv); // pass the element into function to create
								// sub-elements

		Element sr = root.addElement("shareResource");
		constructSRNode(sr);

		Element task = root.addElement("task");
		constructTaskNode(task);

		Element interruption = root.addElement("interruption");
		constructInterNode(interruption);

		Element taskSequence = root.addElement("taskSequence");
		constructTaskSeqNode(taskSequence);
		
		Element interval = root.addElement("interval");
		constructIntervalNode(interval);
		
		Element commuTaskBound = root.addElement("commuTaskBound");
		constructCommuTaskBoundNode(commuTaskBound);
		
		Element ITANet = root.addElement("ITANet");
		ITANet.addAttribute("amount", String.valueOf(this.itaNet.size()));
		constructITANetNode(ITANet);

		return doc;
	}




//////////////////////////////////////////////model data///////////////////////////////////////////////////

	/**
	 * construct element of control variable
	 */
	public void constructCVNode(Element ele) {
		for (ControlVariable cv : Model.controlVariableArray) {
			Element value = ele.addElement("value");
			value.addElement("name").addText(cv.name);
			value.addElement("lowerBound").addText(cv.lowerBound);
			value.addElement("upperBound").addText(cv.upperBound);
			value.addElement("initValue").addText(cv.initValue);
			value.addElement("assignFlag").addText(cv.assignFlag);
			value.addElement("remark").addText(cv.remark);
		}
	}

	
	/**
	 * construct element of share resource
	 */
	public void constructSRNode(Element ele) {
		for (ShareResource sr : Model.shareResourceArray) {
			Element value = ele.addElement("value");
			value.addElement("name").addText(sr.name);
		}
	}

	
	/**
	 * construct element of task
	 */
	public void constructTaskNode(Element ele) {
		for (Task task : Model.taskArray) {
			Element value = ele.addElement("value");
			value.addElement("name").addText(task.name);
			value.addElement("lowerBound").addText(task.lowerBound);
			value.addElement("upperBound").addText(task.upperBound);
			value.addElement("finishTime").addText(task.finishTime);
			value.addElement("readSource").addText(task.getReadResource());
			value.addElement("writeSource").addText(task.getWriteResource());
			value.addElement("commFlag").addText(task.commFlag);
			value.addElement("remark").addText(task.remark);
		}
	}

	
	/**
	 * construct element of interruption
	 */
	public void constructInterNode(Element ele) {
		for (Interruption inter : Model.interArray) {
			Element value = ele.addElement("value");
			value.addElement("name").addText(inter.name);
			value.addElement("priority").addText(inter.prio);
			value.addElement("IRQ").addText(inter.IRQ);
			value.addElement("type").addText(inter.type);
			value.addElement("repeat").addText(inter.repeat);
			value.addElement("offset").addText(inter.offset);
			value.addElement("interval").addText(inter.interval);
			value.addElement("upperBound").addText(inter.upperBound);
			value.addElement("remark").addText(inter.remark);
			value.addElement("procedure").addText(inter.proc.description);
		}
	}
	

	/**
	 * construct element of task sequence
	 */
	public void constructTaskSeqNode(Element ele) {
		for (TaskSequence taskSeq : Model.taskSequences) {
			ele.addElement("value").addText(taskSeq.content);
		}
	}
	
	/**
	 * construct element of interval
	 */
	public void constructIntervalNode(Element ele) {
		for (Interval interval: Model.intervalArray) {
			Element value = ele.addElement("value");
			value.addElement("IRQ").addText(interval.IRQ);
			value.addElement("leastInterval").addText(interval.leastInterval);
			
		}
		
	}
	
	/**
	 * construct element of commuTask bound
	 */
	public void constructCommuTaskBoundNode(Element ele) {
		ele.addElement("value").addText(String.valueOf(Model.commuTaskBound));
		
	}
	
	
////////////////////////////////////////////////ITA structure/////////////////////////////////////////////////
	
	/**
	 * construct ITA net node by construct each ITA node
	 * @param ele
	 */
	public void constructITANetNode(Element ele){
		for(int i = 0; i < this.itaNet.size(); i++){
			Element ITA = ele.addElement("ITA");
			constructITANode(ITA, i);
		}
	}
	
	
	/**
	 * construct ITA node
	 * @param ele
	 * @param index
	 */
	public void constructITANode(Element ele, int index){
		ITA ita = this.itaNet.get(index);
		
		/** construct location elements**/
		int locCount = ita.getLocCount(); 
		for(int locIndex = 0; locIndex < locCount; locIndex++){
			ITALocation loc = ita.getLocation(locIndex);
			String isInit = (locIndex == ita.getInitLoc()) ? "true" : "false";
			String invariant = loc.getOp().equals("") ? "" : 
				ita.getClock() + loc.getOp() + loc.getValue();
			
			Element state = ele.addElement("state");
			state.addAttribute("ID", String.valueOf(locIndex));
			state.addElement("name").addText(loc.getLabel());
			state.addElement("initial").addText(isInit);
			state.addElement("invariant").addText(invariant);
			
			Element constraint = state.addElement("constraint");
			constraint.addAttribute("x", String.valueOf(loc.xValue));
			constraint.addAttribute("y", String.valueOf(loc.yValue));
			constraint.addElement("height").addText(String.valueOf(loc.height));
			constraint.addElement("width").addText(String.valueOf(loc.width));
		}
		
		/** construct edge elements**/
		int edgeCount = ita.getEdgeCount();
		for(int edgeIndex = 0; edgeIndex < edgeCount; edgeIndex++){
			ITAEdge edge = ita.getEdge(edgeIndex);
			String reset = (edge.getReset() == false) ? "" : "x = 0";
			String guard = edge.getOp().equals("") ? "" : 
				ita.getClock() + edge.getOp() + edge.getValue();
			
			Element trans = ele.addElement("transition");
			trans.addElement("name").addText(edge.getInterName());
			trans.addElement("sourceid").addText(String.valueOf(edge.getFromLoc()));
			trans.addElement("targetid").addText(String.valueOf(edge.getToLoc()));
			trans.addElement("guard").addText(guard);
			trans.addElement("reset").addText(reset);
			
			Element bendpoints = trans.addElement("bendpoints");
			for (int i = 0; i < edge.xPoints.size(); i++) {
				bendpoints.addElement("point").addAttribute("x", String.valueOf(edge.xPoints.get(i)))
					.addAttribute("y", String.valueOf(edge.yPoints.get(i)));
			}
		}
	}
}
