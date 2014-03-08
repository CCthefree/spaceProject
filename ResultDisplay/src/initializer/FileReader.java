package initializer;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * read XML file to initialize checking result information
 * 
 * @author zengke.cai
 * 
 */
public class FileReader {

	private String fileName;

	private int faultType;


	public FileReader(String name) {
		this.fileName = name;
	}


	/**
	 * initialize mode information
	 */
	public boolean initResult(Result rs) {
		SAXReader reader = new SAXReader();

		try {
			File file = new File(fileName);
			Document doc = reader.read(file);
			Element root = doc.getRootElement();

			String result = root.elementText("result");
			rs.setResult(result);
			if (result.equals("NO")) {
				getFault(rs, root);
				getInterruption(rs, root);
				getEvent(rs, root);

				// 共享资源访问记录只在错误类型需要时才读入（其它情况下文件中不会有该数据，会导致读入出错）
				if (this.faultType == 4)
					getSRRecord(rs, root);
			}

			return true;
		}
		catch (DocumentException e) {
			JOptionPane.showMessageDialog(null, "找不到文件" + fileName);
			e.printStackTrace();
			return false;
		}
		catch (NullPointerException e) {
			JOptionPane.showMessageDialog(null, "读取文件：" + fileName + "时发生空指针错");
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * initialize fault information
	 * 
	 * @param rs
	 * @param root
	 */
	public void getFault(Result rs, Element root) {
		Element fault = root.element("fault");
		if (fault == null)
			return;

		String type = fault.elementText("type");
		String name = fault.elementText("name");
		String upbnd = fault.elementText("upperBound");

		this.faultType = Integer.parseInt(type);
		rs.setFault(Integer.parseInt(type), name, Integer.parseInt(upbnd));
	}


	/**
	 * initialize interruption information
	 * 
	 * @param rs
	 * @param root
	 */
	public void getInterruption(Result rs, Element root) {
		List inters = root.element("interruption").elements("value");
		if (inters == null)
			return;

		for (Iterator it = inters.iterator(); it.hasNext();) {
			Element inter = (Element) it.next();
			String name = inter.elementText("name");
			String prio = inter.elementText("prio");
			String index = inter.elementText("index");

			rs.addInter(name, prio, index);
		}
	}


	/**
	 * initialize counter path information
	 */
	public void getEvent(Result rs, Element root) {
		List events = root.element("event").elements("value");
		if (events == null)
			return;

		for (Iterator it = events.iterator(); it.hasNext();) {
			Element event = (Element) it.next();
			String type = event.elementText("type");
			String index = event.elementText("index");
			String time = event.elementText("timeStamp");
			String procIndex = event.elementText("procIndex");
			String statement = event.elementText("statement");
			rs.addEvent(type, index, time, procIndex, statement);
		}
	}


	/**
	 * initialize SR read/write record
	 */
	public void getSRRecord(Result rs, Element root) {
		List recs = root.element("SRRecord").elements("value");
		if (recs == null)
			return;

		for (Iterator it = recs.iterator(); it.hasNext();) {
			Element record = (Element) it.next();
			String readCount = record.elementText("readCount");
			String writeCount = record.elementText("writeCount");
			rs.addReadRec(Integer.parseInt(readCount));
			rs.addWriteRec(Integer.parseInt(writeCount));
		}

	}

}
