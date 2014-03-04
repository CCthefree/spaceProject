package fileOperator;

import initializer.Model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import programStructure.Interruption;
import algorithm.Event;

/**
 * class of check result writer write check result and counter example of event
 * path into a XML file
 * 
 * @author zengke.cai
 * 
 */
public class ResultWriter {

	private String fileName; // output file name

	private static ArrayList<Event> eventPath; // the counter example event path

	private static int[] counterExample; // time stamp of counter example

	private static int type; // fault type, (1 for interrupt lost, 2 for
								// interrupt over time, 3 for task over time
								// 4 for SR conflict)

	private static String name = ""; // the problem interruption/task/SR name

	private static int upbnd = -1; // upper bound of the interruption/task

	private static int[] writeRecord; // the record of number that tasks write
										// the SR

	private static int[] readRecord; // the record of number that tasks read the
										// SR


	/**
	 * constructor
	 * 
	 * @param name
	 */
	public ResultWriter(String name) {
		this.fileName = name;
	}


	/**
	 * output function. create XML file and write result
	 */
	public void output() {

		try {
			// set output format
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(new FileWriter(this.fileName), format);

			// create XML document
			Document doc = constructDoc();

			writer.write(doc);
			writer.close();

		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			JOptionPane.showMessageDialog(null, "写入文件：" + fileName + "时发生空指针错");
			e.printStackTrace();
		}
	}


	/**
	 * function to construct XML document
	 * 
	 * @return
	 */
	public Document constructDoc() {
		Document doc = DocumentHelper.createDocument();

		Element root = doc.addElement("root");

		Element result = root.addElement("result");
		if (ResultWriter.eventPath == null) {
			result.addText("YES");
		}
		else {
			result.addText("NO");
			Element fault = root.addElement("fault");
			construtFaultNode(fault);

			Element inter = root.addElement("interruption");
			constructInterNode(inter);

			Element event = root.addElement("event");
			constructEventNode(event);

			// 共享资源的访问记录只有在发生该类错误时才写入
			if (ResultWriter.type == 4) {
				Element SRRecord = root.addElement("SRRecord");
				constructRecNode(SRRecord);
			}

		}

		return doc;
	}


	/**
	 * construct element of interruption
	 * 
	 * @param root
	 */
	public void constructInterNode(Element root) {
		for (int i = 0; i < Model.getInterCount(); i++) {
			Interruption inter = Model.getInterAt(i);
			Element value = root.addElement("value");
			value.addElement("name").addText(inter.getName());
			value.addElement("prio").addText(String.valueOf(inter.getPriority()));
			value.addElement("index").addText(String.valueOf(i));
		}
	}


	/**
	 * construct element of event
	 * 
	 * @param root
	 */
	public void constructEventNode(Element root) {
		for (int i = 0; i < ResultWriter.eventPath.size(); i++) {
			Event e = ResultWriter.eventPath.get(i);
			Element value = root.addElement("value");

			value.addElement("index").addText(String.valueOf(i));
			value.addElement("timeStamp").addText(String.valueOf(ResultWriter.counterExample[i]));
			value.addElement("type").addText(String.valueOf(e.getType()));
			value.addElement("procIndex").addText(String.valueOf(e.getProcIndex()));

			int pnt = (e.getType() == 1) ? 0 : e.getPnt();
			String statement = Model.getStatement(e.getProcIndex(), pnt);
			value.addElement("statement").addText(statement);
		}
	}


	/**
	 * construct element of error information
	 * 
	 * @param root
	 */
	public void construtFaultNode(Element root) {
		if (ResultWriter.name == null)
			return;

		root.addElement("type").addText(String.valueOf(ResultWriter.type));
		root.addElement("name").addText(ResultWriter.name);
		root.addElement("upperBound").addText(String.valueOf(ResultWriter.upbnd));
	}


	/**
	 * construct element of SR read/write record
	 * 
	 * @param root
	 */
	public void constructRecNode(Element root) {
		for (int i = 0; i < ResultWriter.readRecord.length; i++) {
			Element value = root.addElement("value");

			value.addElement("readCount").addText(String.valueOf(ResultWriter.readRecord[i]));
			value.addElement("writeCount").addText(String.valueOf(ResultWriter.writeRecord[i]));
		}
	}


	/**
	 * 
	 * @param path
	 */
	public static void setEventPath(ArrayList<Event> path) {
		ResultWriter.eventPath = path;
	}


	/**
	 * 
	 * @param timeStamp
	 */
	public static void setCounterExample(int[] timeStamp) {
		ResultWriter.counterExample = timeStamp;
	}


	/**
	 * 设置子过程超时、中断超时和中断丢失的错误信息
	 * 
	 * @param type
	 * @param name
	 * @param upbnd
	 */
	public static void setFaultInfo(int type, String name, int upbnd) {
		ResultWriter.type = type;
		ResultWriter.name = name;
		ResultWriter.upbnd = upbnd;
	}


	/**
	 * 设置资源冲突时的错误信息
	 * 
	 * @param resName
	 */
	public static void setSRConflictInfo(int type, String resName, int[] nWriteIndices,
			int[] nReadIndices) {
		ResultWriter.type = type;
		ResultWriter.name = resName;
		ResultWriter.writeRecord = nWriteIndices;
		ResultWriter.readRecord = nReadIndices;
	}

}
