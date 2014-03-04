package fileOperator;

import initializer.Lexer;
import initializer.Model;
import ita.ITA;
import ita.ITAEdge;
import ita.ITALocation;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XMLFileReader {

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
	public boolean initModel(Model model) {
		SAXReader reader = new SAXReader();

		try {
			File file = new File(fileName);
			Document doc = reader.read(file);
			Element root = doc.getRootElement();

			// read interruption data
			initInterruption(model, root);

			// read control variable data
			initCV(model, root);

			// read share resource
			initSR(model, root);

			// read task
			initTask(model, root);
			
			//read ITA
			initITANet(model, root);

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
	 * initialize interruption information
	 * 
	 * @param model
	 *            : whole model
	 * @param root
	 *            : root of xml doc
	 */
	public void initInterruption(Model model, Element root) {
		List inters = root.element("interruption").elements("value");
		if (inters == null)
			return;

		for (Iterator it = inters.iterator(); it.hasNext();) {
			Element inter = (Element) it.next();
			String name = inter.elementText("name");
			String prio = inter.elementText("priority");
			String type = inter.elementText("type");
			String repeat = inter.elementText("repeat");
			String interval = inter.elementText("interval");
			String offset = inter.elementText("offset");
			String lbd = inter.elementText("lowerBound");
			String ubd = inter.elementText("upperBound");
			String proc = inter.elementText("procedure");

			String period = type.equals("random") ? interval : repeat;

			model.addInter(name, prio, type, period, offset, lbd, ubd, proc);
		}
	}


	/**
	 * initialize control variable information
	 * 
	 * @param model
	 * @param root
	 */
	public void initCV(Model model, Element root) {
		List cvs = root.element("controlVariable").elements("value");
		if (cvs == null)
			return;

		for (Iterator it = cvs.iterator(); it.hasNext();) {
			Element cv = (Element) it.next();
			String name = cv.elementText("name");
			String value = cv.elementText("initValue");

			model.addCV(name, value);
		}
	}


	/**
	 * initialize share resource information
	 * 
	 * @param model
	 * @param root
	 */
	public void initSR(Model model, Element root) {
		List srs = root.element("shareResource").elements("value");
		if (srs == null)
			return;

		for (Iterator it = srs.iterator(); it.hasNext();) {
			Element sr = (Element) it.next();
			String name = sr.elementText("name");

			model.addSR(name);
		}
	}


	/**
	 * initialize task
	 */
	public void initTask(Model model, Element root) {
		List tasks = root.element("task").elements("value");
		if (tasks == null)
			return;

		for (Iterator it = tasks.iterator(); it.hasNext();) {
			Element task = (Element) it.next();
			String name = task.elementText("name");
			String lb = task.elementText("lowerBound");
			String ub = task.elementText("upperBound");
			String finishTime = task.elementText("finishTime");
			
			ArrayList<String> read = getSRList(task.element("readSource"));
			ArrayList<String> write = getSRList(task.element("writeSource"));
			model.addTask(name, lb, ub, finishTime, read, write);
		}
	}
	
	

	/**
	 * 根据输入文件生成ITAs
	 */
	public void initITANet(Model model, Element root) {
		List itas = root.element("ITANet").elements("ITA");
		if (itas == null)
			return;
		
		for (Iterator it = itas.iterator(); it.hasNext();) {
			Element ita = (Element) it.next();
			List states = ita.elements("state");
			List edges = ita.elements("transition");
			constructITA(model, states, edges);
		}
	}
	
	/**
	 *根据状态集和边集构造ITA
	 */
	public void constructITA(Model model, List states, List edges) {
		ITA ita = new ITA();
		
		for (Iterator it = states.iterator(); it.hasNext();) {   //设置location
			Element state = (Element) it.next();
			String label = state.elementText("name");
			String invariant = state.elementText("invariant");
			String isInit = state.elementText("initial");
			int id = Integer.parseInt(state.attributeValue("ID"));
			
			String op = "";
			int value = -1;
			if(!invariant.equals("")){
				ArrayList<String> token = Lexer.lexerAnalyse(invariant);
				op = token.get(1);
				value = Integer.parseInt(token.get(2));
			}
			ITALocation itaLocation = new ITALocation(label, op, value);
			ita.addLocation(itaLocation);
			if (isInit.equals("true"))
				ita.setInitLoc(id);
		}
		
		for (Iterator it = edges.iterator(); it.hasNext();) {   //设置edge
			Element edge = (Element) it.next();
			String interName = edge.elementText("name");
			String sourceID = edge.elementText("sourceid");
			String targetID = edge.elementText("targetid");
			String guard = edge.elementText("guard");
			String reset = edge.elementText("reset");
			
			int inter = Model.getInterIndexByName(interName);
			int from = Integer.parseInt(sourceID);
			int to = Integer.parseInt(targetID);
			
			String op = "";
			int value = -1;
			if(!guard.equals("")){
				ArrayList<String> token = Lexer.lexerAnalyse(guard);
				op = token.get(1);
				value = Integer.parseInt(token.get(2));
			}
		
			boolean r = (reset.equals("")) ? false : true;
			ITAEdge itaEdge = new ITAEdge(from, to, inter, op, value, r);
			ita.getLocation(from).setEdge(itaEdge);
		}
		
		
		
		model.addITA(ita);
	}
	
	
	public ArrayList<String> getSRList(Element root){
		ArrayList<String> result = new ArrayList<String>();
		List names = root.elements("name");
		if(names == null)
			return result;
		
		for(Iterator it = names.iterator(); it.hasNext();){
			Element name = (Element) it.next();
			result.add(name.getText());
		}
		return result;
	}
	
	

}
