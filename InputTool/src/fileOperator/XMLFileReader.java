package fileOperator;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import variableDefinition.ControlVariable;
import variableDefinition.Interruption;
import variableDefinition.Interval;
import variableDefinition.Model;
import variableDefinition.ShareResource;
import variableDefinition.Task;
import variableDefinition.TaskSequence;

/**
 * class of reading model information in XML file
 * @author zengke.cai
 *
 */
public class XMLFileReader {

	// used to restore model information if read failed
	private ArrayList<Task> taskArray;

	private ArrayList<Interruption> interArray;

	private ArrayList<ControlVariable> controlVariableArray;

	private ArrayList<ShareResource> shareResourceArray;

	private ArrayList<TaskSequence> taskSequences;
	
	private ArrayList<Interval> intervalArray;
	
	private long commuTaskBound;
	
	private String fileName;


	/**
	 * constructor, set read file name
	 */
	public XMLFileReader(String name) {
		this.fileName = name;
	}


	/**
	 * initialize mode information
	 */
	public boolean initModel() {
		/**store current model data, in case of reading failed**/
		backup();	

		SAXReader reader = new SAXReader();

		try {
			File file = new File(fileName);
			Document doc = reader.read(file);
			Element root = doc.getRootElement();

			// read control variable data
			initCV(root);

			// read share resource
			initSR(root);

			// read task
			initTask(root);

			// read interruption data
			initInterruption(root);

			// read task sequence
			initTaskSequence(root);
			
			//read interval
			initInterval(root);
			
			//read commuTaskBound
			initCommuTaskBound(root);

			return true;

		}
		catch (DocumentException e) {
			recover();

			JOptionPane.showMessageDialog(null, "找不到文件" + fileName);
			e.printStackTrace();
			return false;
		}
		catch (NullPointerException e) {
			recover();

			JOptionPane.showMessageDialog(null, "读取文件：" + fileName + "时发生空指针错");
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * initialize interruption information, include procedure
	 */
	public void initInterruption(Element root) {
		List inters = root.element("interruption").elements("value");
		if (inters == null)
			return;

		for (Iterator it = inters.iterator(); it.hasNext();) {
			Element interEle = (Element) it.next();
			String name = interEle.elementText("name");
			String prio = interEle.elementText("priority");
			String IRQ = interEle.elementText("IRQ");
			String type = interEle.elementText("type");
			String repeat = interEle.elementText("repeat");
			String offset = interEle.elementText("offset");
			String interval = interEle.elementText("interval");
			String ubd = interEle.elementText("upperBound");
			String remark = interEle.elementText("remark");

			String procStr = interEle.elementText("procedure");

			String values[] = new String[] { name, prio, IRQ, type, repeat, offset, interval, ubd,
					remark };
			Interruption inter = new Interruption();
			inter.setValue(values);
			Model.interArray.add(inter);
			inter.proc.setValue(name, procStr);
		}
	}


	/**
	 * initialize control variable information
	 * 
	 * @param model
	 * @param root
	 */
	public void initCV(Element root) {
		List cvs = root.element("controlVariable").elements("value");
		if (cvs == null)
			return;

		for (Iterator it = cvs.iterator(); it.hasNext();) {
			Element cvEle = (Element) it.next();
			String name = cvEle.elementText("name");
			String lbd = cvEle.elementText("lowerBound");
			String ubd = cvEle.elementText("upperBound");
			String initValue = cvEle.elementText("initValue");
			String assignFlag = cvEle.elementText("assignFlag");
			String remark = cvEle.elementText("remark");

			String values[] = new String[] { name, lbd, ubd, initValue, assignFlag, remark};
			ControlVariable cv = new ControlVariable();
			cv.setValue(values);
			Model.controlVariableArray.add(cv);
		}
	}


	/**
	 * initialize share resource information
	 * 
	 * @param model
	 * @param root
	 */
	public void initSR(Element root) {
		List srs = root.element("shareResource").elements("value");
		if (srs == null)
			return;

		for (Iterator it = srs.iterator(); it.hasNext();) {
			Element srEle = (Element) it.next();
			String name = srEle.elementText("name");

			ShareResource sr = new ShareResource();
			sr.setValue(name);
			Model.shareResourceArray.add(sr);
		}
	}


	/**
	 * initialize task
	 */
	public void initTask(Element root) {
		List tasks = root.element("task").elements("value");
		if (tasks == null)
			return;

		for (Iterator it = tasks.iterator(); it.hasNext();) {
			Element taskEle = (Element) it.next();
			String name = taskEle.elementText("name");
			String lbd = taskEle.elementText("lowerBound");
			String ubd = taskEle.elementText("upperBound");
			String finishTime = taskEle.elementText("finishTime");
			String read = getSRList(taskEle.element("readSource"));
			String write = getSRList(taskEle.element("writeSource"));
			String commFlag = taskEle.elementText("commFlag");
			String remark = taskEle.elementText("remark");

			String values[] = new String[] { name, lbd, ubd, finishTime, read, write, commFlag, remark};
			Task task = new Task();
			task.setValue(values);
			Model.taskArray.add(task);
		}
	}


	/**
	 * initialize task sequence
	 * 
	 * @param root
	 */
	public void initTaskSequence(Element root) {
		List seqs = root.element("taskSequence").elements("value");
		if (seqs == null)
			return;

		for (Iterator it = seqs.iterator(); it.hasNext();) {
			int index = 0;
			Element seqEle = (Element) it.next();
			String seq = seqEle.getText();

			TaskSequence taskSeq = new TaskSequence(index++);
			taskSeq.setValue(seq);
			Model.taskSequences.add(taskSeq);
		}
	}
	
	/**
	 * initialize interval
	 * @param root
	 */
	public void initInterval(Element root) {
		List intervals = root.element("interval").elements("value");
		if (intervals == null)
			return;
		
		for (Iterator it = intervals.iterator(); it.hasNext();) {
			Element intervalEle = (Element) it.next();
			String IRQ = intervalEle.elementText("IRQ");
			String leastInterval = intervalEle.elementText("leastInterval");

			String values[] = new String[] { IRQ, leastInterval};
			Interval interval = new Interval();
			interval.setValue(values);
			Model.intervalArray.add(interval);
		}
	}
	
	/**
	 * initialize communication task bound
	 * @param root
	 */
	public void initCommuTaskBound(Element root) {
		Element e = root.element("commuTaskBound");
		String bound = e.elementText("value");
		long longBound;
		if (bound.equals(""))
			longBound = -1;
		else longBound = Long.parseLong(bound);
		Model.commuTaskBound = longBound;
	}


	public String getSRList(Element root) {
		String result = "";

		List names = root.elements("name");	// no read/write SRs
		if (names == null || names.size() == 0)
			return result;
		else { // has read/write SRs
			for (Iterator it = names.iterator(); it.hasNext();) {
				Element name = (Element) it.next();
				result += name.getText() + ",";
			}
			//去掉最后一个','
			result = result.substring(0, result.length() - 1);
			return result;
		}	
	}


	/**
	 * backup current model information
	 */
	public void backup() {
		this.taskArray = new ArrayList<Task>(Model.taskArray);
		this.controlVariableArray = new ArrayList<ControlVariable>(Model.controlVariableArray);
		this.interArray = new ArrayList<Interruption>(Model.interArray);
		this.shareResourceArray = new ArrayList<ShareResource>(Model.shareResourceArray);
		this.taskSequences = new ArrayList<TaskSequence>(Model.taskSequences);
		this.intervalArray = new ArrayList<Interval>(Model.intervalArray);
		this.commuTaskBound = Model.commuTaskBound;

		// erase information
		Model.taskArray.clear();
		Model.controlVariableArray.clear();
		Model.interArray.clear();
		Model.shareResourceArray.clear();
		Model.taskSequences.clear();
		Model.intervalArray.clear();
		Model.commuTaskBound = -1;
	}


	/**
	 * recover previous model information if fail to read file
	 */
	public void recover() {
		Model.taskArray = this.taskArray;
		Model.controlVariableArray = this.controlVariableArray;
		Model.interArray = this.interArray;
		Model.shareResourceArray = this.shareResourceArray;
		Model.taskSequences = this.taskSequences;
		Model.intervalArray = this.intervalArray;
		Model.commuTaskBound = this.commuTaskBound;
	}

}
