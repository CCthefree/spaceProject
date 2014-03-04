package algorithm;

import initializer.Model;
import ita.ITA;
import ita.ITAEdge;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import programStructure.Constrain;
import programStructure.Interruption;
import programStructure.Procedure;
import programStructure.ProgramPoint;
import programStructure.SRArray;
import programStructure.SubExpr;
import programStructure.Task;
import z3.Z3;

import com.microsoft.z3.Z3Exception;

import fileOperator.Logger;
import fileOperator.ResultWriter;

/**
 * class to record the restore information of an event
 * 
 * @author zengke.cai
 * 
 */
class RestoreInfo {

	int type; // type of event

	// 0 for ITA transition,
	// 1 for push in new procedure,
	// 2 for procedure goes on,
	// 3 for pop out procedure

	// info for event 0
	int ITAIndex; // index of ITA

	int preLoc; // present location

	// info for event 1
	int procIndex; // index of push in procedure

	// info for event 2 and 3
	int prePnt; // present program point

	// info for event 3
	int preProc;

	// shared info for event 2 and 3
	String cvName; // control variable name, use when statement at prePnt is
					// assign statement

	int preValue; // present value of control variable


	public RestoreInfo(int type) {
		this.type = type;
		this.cvName = "";
	}

}


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


	public SRRestoreInfo() {

	}
}


/**
 * class of the main verification algorithm of BMC
 * 
 * @author zengke.cai
 * 
 */
public class Verification {

	private GlobalState globalstate;

	private ArrayList<Event> eventPath;

	private int bound;

	private int pathCount;

	private Z3 z3;

	private int[] timeStamp;

	private boolean otFlag; // flag of procedure over time

	private boolean lostFlag; // flag of interruption lost

	private boolean conflictFlag; // 共享资源冲突标志


	/**
	 * constructor, initialize global state and event path, then go searching
	 * 
	 * @throws Z3Exception
	 */
	public Verification(int bound) throws Z3Exception {
		this.globalstate = new GlobalState();
		this.eventPath = new ArrayList<Event>();
		this.bound = bound;
		this.pathCount = 0;
		this.otFlag = false;
		this.lostFlag = false;
		this.conflictFlag = false;
		z3 = new Z3(bound);

		// ///////////////////////////output test
		printGS();
		System.out.println(Calendar.getInstance().getTime());
		// ///////////////////////////////////

		search(this.bound);

		System.out.println(Calendar.getInstance().getTime());
	}


	/**
	 * state iterator function
	 * 
	 * @param K
	 * @throws Z3Exception
	 */
	public void search(int K) throws Z3Exception {
		if (K == 0) {
			writeLog(0, "");
		}
		else {
			ArrayList<Event> eventList = Event.potentialEvents(globalstate);
			for (Event e : eventList) {

				z3.push(); // push event constrain

				/** 加入所有当前状态约束和事件约束 **/
				pushConstrain(e);
				
				if (z3.check() == false) { // 表示该event在时间约束上不满足要求，直接pass掉
					z3.pop(); // pop event constrain
					continue;
				}

				/** 记录GS恢复信息并计算后继状态 **/
				RestoreInfo rInfo = setRestoreInfo(e);

				this.eventPath.add(e); // 放在前面为了输出造成中断丢失的那个事件
				int lostIndex = this.globalstate.successor(e);

				/** 判断是否出现中断丢失 **/
				if (lostIndex != -1) {
					writeLog(1, Model.getInterAt(lostIndex).getName());
					setResultInfo(1, lostIndex);
					
					this.lostFlag = true;
					return;
				}

				/** 判断是否有共享资源上的冲突 **/
				SRRestoreInfo srRInfo = operateSR(rInfo.prePnt); 
				int conflictIndex = conflictCheck();
				if(conflictIndex != -1){
					setResultInfo(4, conflictIndex);
					
					this.conflictFlag = true;
					return;
				}
		

				/** 判断是否出现子过程超时 **/				
				this.otFlag = taskOTCheck(rInfo.prePnt);	
				if (this.otFlag == true) 	//子过程超时处理
					return;

				
				/** 判断是否出现处理程序超时 **/				
				this.otFlag = procOTCheck(rInfo.prePnt);	
				if (this.otFlag == true) 	//处理程序超时处理
					return;

				/** keep search recursively **/
				search(K - 1);

				// 如果有检测到错误，直接返回，不再继续遍历
				if (this.otFlag == true || this.lostFlag == true || this.conflictFlag == true) {
					return;
				}

				else { // 否则，继续遍历
					z3.pop(); // pop event constrain

					/** restore global state and eventPath **/
					restoreSR(srRInfo);
					restore(rInfo);// restore global state
					this.eventPath.remove(this.eventPath.size() - 1);// restore event path
				}
			}
		}
	}

	
	/**
	 * push in constrain of current global state and event
	 * @param e
	 */
	public void pushConstrain(Event e) throws Z3Exception{

		/** add the constrains of the new event **/
		ArrayList<Constrain> eCons = genEventCons(e);
		for (Constrain con : eCons){
			z3.addCons(con);
		}

		/** add the constrains of current global state **/
		ArrayList<Constrain> gsCons = genGSCons();
		for (Constrain con : gsCons) {
			z3.addCons(con);
		}
	}
	
	
	/**
	 * check whether there exist SR conflict at current global state
	 * @return the index of conflict share resource
	 */
	public int conflictCheck(){
		int result = -1;
		
		Event e = this.eventPath.get(this.eventPath.size() - 1);
		if(e.getType() == 0 || e.getType() == 3)	//自动机事件或出栈事件，不会发生资源竞争
			return result;
		else{
			int pnt = (e.getType() == 1) ? 0 : e.getPnt();
			ProgramPoint pp = Model.getProcAt(e.getProcIndex()).getPP(pnt);
			if(pp.getType() == 'I' || pp.getType() == 'A')	//执行IF语句或赋值语句，不会发生资源竞争
				return result;
		}
		
		/**其它情况下，遍历共享资源列表，检查资源竞争**/
		SRArray srArray = this.globalstate.getSRArray();
		int[] nRead = srArray.getnRead();
		int[] nWrite = srArray.getnWrite();
		int length = nRead.length;
		for (int i = 0; i < length; i++) {
			if (nWrite[i] > 1) {
				result = i;
				writeLog(4, srArray.getNameAt(i) + "write-write");
				
			}
			else if (nWrite[i] > 0 && nRead[i] > 0) {
				result = i;
				writeLog(4, srArray.getNameAt(i) + "read-write");
			}
		}
		
		return result;
	}
	
	
	/**
	 * check whether there exist task overtime at current global state
	 * @return
	 */
	public boolean taskOTCheck(int prePnt) throws Z3Exception{
		boolean result = false;
		
		Event e = this.eventPath.get(this.eventPath.size() - 1);
		if(e.getType() == 1)	//进栈事件，时钟不前进，不会发生超时
			return result;
		else if(e.getType() != 0 ){
			ProgramPoint pp = Model.getProcAt(e.getProcIndex()).getPP(prePnt);
			if(pp.getType() == 'I' || pp.getType() == 'A')	//执行IF语句或赋值语句，时钟不前进，不会发生超时
				return result;
		}
		
		/**其它情况下，生成超时约束，调用Z3求解**/
		ArrayList<Constrain> taskOTCons = genTaskOTCons();
		
		for (Constrain con : taskOTCons) {
			z3.push(); // push task overTime constrain
			z3.addNegCons(con);

			if (z3.check() == true) { // task over time
				int eventIndex = con.getSecondVar();
				
				writeLog(3, String.valueOf(con.getSecondVar()));	//write log
				// set error info
				setResultInfo(3, eventIndex);
				result = true;

				z3.pop();
				break;

			}

			z3.pop(); // pop task over time constrain
		}
		return result;
	}
	
	
	/**
	 * check whether there exist procedure overtime at current global state
	 * @return
	 */
	public boolean procOTCheck(int prePnt) throws Z3Exception{
		boolean result = false;
		
		Event e = this.eventPath.get(this.eventPath.size() - 1);
		if(e.getType() == 1)	//进栈事件，时钟不前进，不会发生超时
			return result;
		else if(e.getType() != 0 ){
			ProgramPoint pp = Model.getProcAt(e.getProcIndex()).getPP(prePnt);
			if(pp.getType() == 'I' || pp.getType() == 'A')	//执行IF语句或赋值语句，时钟不前进，不会发生超时
				return result;
		}
		
		/**其它情况下，生成超时约束，调用Z3求解**/
		ArrayList<Constrain> procOTCons = genProcOTCons();
		
		for (int i = 0; i < procOTCons.size(); i++) {
			Constrain con = procOTCons.get(i);
			z3.push(); // push over time constrain
			z3.addNegCons(con);

			if (z3.check() == true) { // over time error handling
				int procIndex = this.eventPath.get(con.getSecondVar()).getProcIndex();
		
				writeLog(2, Model.getInterAt(procIndex).getName());	 //write log
				//set error info
				setResultInfo(2, procIndex);
				result = true;

				z3.pop();
				break;
			}

			z3.pop(); // pop over time constrain
		}
		
		return result;
	}


//////////////////////////////////////////////////////event constrains///////////////////////////////////////////////
	
	/**
	 * 生成当前event所对应的constraint, 当前event还未加入eventPath
	 */
	public ArrayList<Constrain> genEventCons(Event e) {
		ArrayList<Constrain> cons = new ArrayList<Constrain>();

		int curPoint = this.eventPath.size(); // 因为当前event未加入eventPath,所以当前event实际是
												// 第size个

		/** ITA transition event **/
		if (e.getType() == 0) {
			int ITAIndex = e.getITAIndex();
			int curLoc = this.globalstate.getLocVec()[ITAIndex];
			ITA ita = Model.getITA(ITAIndex);
			ITAEdge edge = ita.getLocation(curLoc).getEdge();
			String op = edge.getOp();

			if (!op.equals("")) { // 自动机当前转换上的约束不为空
				int value = edge.getValue();

				// 寻找路径中最近的一个将此中断自动机时钟重置的event下标
				int j;
				for (j = this.eventPath.size() - 1; j >= 0; j--){
					Event event = this.eventPath.get(j);
					if (event.getType() == 0 && event.getITAIndex() == ITAIndex
							&& event.getReset() == true) 
						break;
				}
				// 自动机在初始位置时，j = -1,表示事件到时间点0的约束
				cons.add(new Constrain(curPoint, j, op, value));
			}
		}

		/** push in new procedure event **/
		else if (e.getType() == 1) {
			// type 1 event cann't be the first event in path
			cons.add(new Constrain(curPoint, curPoint - 1, "==", 0));
		}

		/** procedure goes on event, include event type 2 and type 3 **/
		else {

			int procIndex = e.getProcIndex();
			Interruption inter = Model.getInterAt(procIndex);
			ProgramPoint pp = inter.getIP().getPP(this.globalstate.getStack().getTopPnt());
			int entryPoint = getEventIndex(procIndex); // get the most recently
														// event on this
														// procedure

			// current program point is assign statement or if statement
			if (pp.getType() == 'A' || pp.getType() == 'I') {
				cons.add(new Constrain(curPoint, entryPoint, "==", 0));
			}
			// task call statement
			else if (pp.getType() == 'C') {

				Task task = Model.getTask(pp.getTaskName());
				int lowerBound = task.getLowerBound();
				int upperBound = task.getUpperBound();

				ArrayList<SubExpr> subExprs = new ArrayList<SubExpr>();
				int higherProcNum = 0;// 表示打断当前proc的proc数目
				subExprs.add(new SubExpr(curPoint, entryPoint));
				for (int i = entryPoint + 1; i < curPoint; i++) {
					Event event = this.eventPath.get(i);
					if (event.getType() == 1) {// 更高级中断开始
						higherProcNum++;
						if (higherProcNum == 1)
							subExprs.get(subExprs.size() - 1).setFirstPara(i);
					}
					else if (event.getType() == 3) {// 更高级中断结束
						higherProcNum--;
						if (higherProcNum == 0)
							subExprs.add(new SubExpr(curPoint, i));
					}

				}
				cons.add(new Constrain(subExprs, "<=", upperBound));
				cons.add(new Constrain(subExprs, ">=", lowerBound));

			}
		}

		return cons;
	}


	/**
	 * find the index of the most recently event about the procedure of given
	 * procIndex
	 * 
	 * @param procIndex
	 * @return -1 if find nothing
	 */
	public int getEventIndex(int procIndex) {
		int result = -1;
		for (int i = this.eventPath.size() - 1; i >= 0; i--){
			Event e = this.eventPath.get(i);
			if (e.getType() != 0 && e.getProcIndex() == procIndex) {
				result = i;
				break;
			}
		}
		return result;
	}


	/**
	 * generate constrains to the new event by current global state
	 * 
	 * @return
	 */
	public ArrayList<Constrain> genGSCons() {
		ArrayList<Constrain> gsCons = new ArrayList<Constrain>();

		// 当前状态CPU栈非空，生成栈的约束
		if (!this.globalstate.getStack().isEmpty()) {
			ArrayList<Constrain> stackCons = genStackCons();
			gsCons.addAll(stackCons);
		}

		// 生成中断自动机的约束
		ArrayList<Constrain> ITACons = genITACons();
		gsCons.addAll(ITACons);

		return gsCons;
	}


	/**
	 * generate constrains to the new event by procedures in CPU stack
	 * 
	 * @return
	 */
	public ArrayList<Constrain> genStackCons() {
		ArrayList<Constrain> cons = new ArrayList<Constrain>();

		int cur = this.eventPath.size(); // 新添加事件的下标

		// 记录各个procedure的运行区间
		Map<Integer, ArrayList<SubExpr>> processMap = new HashMap<Integer, ArrayList<SubExpr>>();
		// 记录各个procedure的程序点
		Map<Integer, Integer> processPoint = new HashMap<Integer, Integer>();
		// 记录CPU中procedure的进栈序列
		ArrayList<Integer> processList = new ArrayList<Integer>();

		for (int i = 0; i < this.eventPath.size(); i++) {
			Event event = this.eventPath.get(i);
			int procIndex = event.getProcIndex();

			/** 新procedure加入CPU栈 **/
			if (event.getType() == 1) {

				if (!processList.isEmpty()) { // 说明 当前procedure打断了低级procedure
					int preTop = processList.get(processList.size() - 1);
					// 设置低级procedure占用CPU的截止时间
					ArrayList<SubExpr> exprs = processMap.get(preTop);
					exprs.get(exprs.size() - 1).setFirstPara(i);
				}

				// 设置当前procedure的占用CPU时间
				ArrayList<SubExpr> exprList = new ArrayList<SubExpr>();
				SubExpr sub = new SubExpr(cur, i); // 默认执行到当前事件发生的时刻
				exprList.add(sub);

				processMap.put(procIndex, exprList);
				processPoint.put(procIndex, 0);
				processList.add(procIndex);
			}

			/** procedure程序点后移 **/
			else if (event.getType() == 2) {
				// 更新该procedure的占用CPU区间
				SubExpr sub = new SubExpr(cur, i);
				processMap.get(procIndex).clear();
				processMap.get(procIndex).add(sub);

				// 更新该procedure的程序点
				processPoint.remove(procIndex);
				processPoint.put(procIndex, event.getPnt());
			}

			/** procedure出栈 **/
			else if (event.getType() == 3) {
				// 从processList和processMap, processPoint中移除当前procedure
				processList.remove(processList.size() - 1);
				processMap.remove(procIndex);
				processPoint.remove(procIndex);

				if (!processList.isEmpty()) { // 说明当前procedure打断了低级procedure
					int preTop = processList.get(processList.size() - 1);
					SubExpr sub = new SubExpr(cur, i);
					processMap.get(preTop).add(sub);
				}
			}
		}

		// 根据各procedure的占用CPU区间生成时间约束
		/** TODO 其实约束只与CPU栈最顶上的procedure有关 **/
		for (int key : processMap.keySet()) {
			ArrayList<SubExpr> exprs = processMap.get(key);
			if (!exprs.isEmpty()) {
				// 获取procedure当前程序点
				int pnt = processPoint.get(key);
				ProgramPoint pp = Model.getInterAt(key).getIP().getPP(pnt);
				int statTime = 0;

				if (pp == null) // 说明当前程序点为procedure结束点
					statTime = 0;
				// 如果是赋值语句或IF语句，允许执行时间为0
				else if (pp.getType() == 'A' || pp.getType() == 'I')
					statTime = 0;
				else
					// 如果是子过程调用语句，允许执行时间为该子过程时间上界
					statTime = Model.getTask(pp.getTaskName()).getUpperBound();

				cons.add(new Constrain(exprs, "<=", statTime));
			}
		}

		return cons;
	}


	/**
	 * generate constrains to the new event by ITA location invariant
	 * 
	 * @return
	 */
	public ArrayList<Constrain> genITACons() {
		ArrayList<Constrain> cons = new ArrayList<Constrain>();

		int curPoint = this.eventPath.size();
		if (curPoint <= 0) // 已限定第一个事件时间点为0，所以这里不用生成
			return cons;

		int[] locVec = this.globalstate.getLocVec();
		for (int itaIndex = 0; itaIndex < locVec.length; itaIndex++) {// i表示ITAIndex
			ITA ita = Model.getITA(itaIndex);
			String op = ita.getLocation(locVec[itaIndex]).getOp();
			if (!op.equals("")) {// location上存在时间的约束
				int value = ita.getLocation(locVec[itaIndex]).getValue();

				if (ita.getInitLoc() == locVec[itaIndex]) // 如果自动机i在初始位置，设置约束为到时间点0的表达式
					cons.add(new Constrain(curPoint, -1, op, value));
				else {// 否则，找到事件路径中最近的该自动机上的转换
					int j = curPoint - 1;
					for (; j >= 0; j--){
						Event e = this.eventPath.get(j);
						if (e.getType() == 0 && e.getITAIndex() == itaIndex
								&& e.getReset() == true) {	
							break;
						}
					}
					cons.add(new Constrain(curPoint, j, op, value));
				}
			}
		}
		return cons;
	}

	
///////////////////////////////////////////////////specification///////////////////////////////////////////////////

	/**
	 * generate overtime constraints of task
	 */
	public ArrayList<Constrain> genTaskOTCons() {
		ArrayList<Constrain> cons = new ArrayList<Constrain>();
		int cur = this.eventPath.size() - 1;
		if (cur <= 0)
			return cons;
		else {
			int stackSize = this.globalstate.getStack().getSize();
			for (int i = 0; i < stackSize; i++) {
				int procIndex = this.globalstate.getStack().getProcIndexAt(i);
				int point = this.globalstate.getStack().getPntAt(i);
				ProgramPoint pp = Model.getInterAt(procIndex).getIP().getPP(point);
				if (pp.getType() == 'C') { // 该子过程是call语句
					Task task = Model.getTask(pp.getTaskName());
					int startPoint = getEventIndexFromPP(procIndex, point);
					if (startPoint < cur && startPoint != -1)
						if (task.getFinishTime() != -1)
							/** 目前输入模型中允许finishiTime为-1，以后再约束 **/
							cons.add(new Constrain(cur, startPoint, "<=", task.getFinishTime()));
				}
			}

		}

		return cons;
	}


	/**
	 * 根据procIndex和pp获取最近的事件序号
	 */
	public int getEventIndexFromPP(int procIndex, int point) {
		for (int i = this.eventPath.size() - 1; i >= 0; i--) {
			Event e = this.eventPath.get(i);
			if (point == 0 && e.getType() == 1 && e.getProcIndex() == procIndex)
				return i;
			else if (e.getType() == 2 && e.getProcIndex() == procIndex && e.getPnt() == point)
				return i;
		}

		return -1;
	}


	/**
	 * generate overtime constrains of procedure
	 * 
	 * @return
	 */
	public ArrayList<Constrain> genProcOTCons() {
		ArrayList<Constrain> cons = new ArrayList<Constrain>();

		int cur = this.eventPath.size() - 1; // index of current event

		if (cur <= 0) // current event is the 0th, no OT constrain
			return cons;
		else {
			for (int i = 0; i < cur; i++) {
				Event e = this.eventPath.get(i);
				if (e.getType() == 1) { // find procedure pushed in event
					// add to constrain list
					cons.add(new Constrain(cur, i, "<=", Model.getInterAt(e.getProcIndex())
							.getUBD()));
				}
				else if (e.getType() == 3) { // procedure pop out event
					// remove the last constrain from 'cons'
					cons.remove(cons.size() - 1);
				}
			}
		}

		return cons;
	}

	
//////////////////////////////////////////////////traverse info/////////////////////////////////////////////////////

	/**
	 * set restore information according to present handling event
	 * 
	 * @param e
	 * @return
	 */
	public RestoreInfo setRestoreInfo(Event e) {
		int type = e.getType();
		RestoreInfo rInfo = new RestoreInfo(type);

		/** ITA transition event **/
		if (type == 0) {
			// record present ITA index and location
			rInfo.ITAIndex = e.getITAIndex();
			rInfo.preLoc = this.globalstate.getLocVec()[rInfo.ITAIndex];
		}

		/** push in new procedure event **/
		else if (type == 1) {
			rInfo.procIndex = e.getProcIndex();
		}

		/** procedure goes on event, include type 2 or 3 **/
		else {
			rInfo.prePnt = this.globalstate.getStack().getTopPnt();
			Procedure proc = Model.getInterAt(this.globalstate.getStack().getTopProc()).getIP();
			ProgramPoint pp = proc.getPP(rInfo.prePnt);
			if (pp.getType() == 'A') { // if present statement if assign
										// statement
				// record the control variable name and value
				rInfo.cvName = pp.getCvName();
				rInfo.preValue = this.globalstate.getCvMap().getValue(rInfo.cvName);
			}

			if (type == 3) // procedure pop out event, record previous procedure
							// index
				rInfo.preProc = this.globalstate.getStack().getTopProc();
		}

		return rInfo;
	}


	/**
	 * restore global state according to the restore information
	 * 
	 * @param rInfo
	 */
	public void restore(RestoreInfo rInfo) {
		int type = rInfo.type;
		/** restore ITA location **/
		if (type == 0) {

			this.globalstate.getLocVec()[rInfo.ITAIndex] = rInfo.preLoc;
			// if the edge has interruption, reset 'interVec'
			ITAEdge edge = Model.getITA(rInfo.ITAIndex).getLocation(rInfo.preLoc).getEdge();
			if (edge.getInterIndex() != -1)
				this.globalstate.getInterVec().setFalse(edge.getInterIndex());
		}

		/** restore stack and vector **/
		else if (type == 1) {
			// pop out new procedure and reset interruption vector
			this.globalstate.getStack().pop();
			this.globalstate.getInterVec().setTrue(rInfo.procIndex);
		}

		/** restore CPU stack and control variable **/
		else {
			if (type == 3) // event 3 need push back member
				this.globalstate.getStack().push(rInfo.preProc, rInfo.prePnt);
			else
				this.globalstate.getStack().setPnt(rInfo.prePnt);

			if (!rInfo.cvName.equals("")) {
				this.globalstate.getCvMap().setValue(rInfo.cvName, rInfo.preValue);
			}
		}
	}


	/**
	 * 判断event的类型并对共享资源的nRead和nWrite做相应操作
	 */
	public SRRestoreInfo operateSR(int prePnt) {
		SRRestoreInfo srRInfo = new SRRestoreInfo();

		int cur = this.eventPath.size() - 1;
		Event e = this.eventPath.get(cur);
		if (e.getType() == 2 || e.getType() == 3) { // nRead和nWrite减一操作
			Procedure proc = Model.getProcAt(e.getProcIndex());

			ProgramPoint pp = proc.getPP(prePnt);// 上一个程序点
			if (pp.getType() == 'C') {
				String name = pp.getTaskName();
				Task task = Model.getTask(name);
				ArrayList<Integer> readSR = task.getReadSR();
				ArrayList<Integer> writeSR = task.getWriteSR();
				for (int i : readSR) {
					Model.getSRArray().decReadAt(i);
				}
				for (int i : writeSR) {
					Model.getSRArray().decWriteAt(i);
				}

				// 设置共享资源读写数组恢复信息
				srRInfo.incRead = readSR;
				srRInfo.incWrite = writeSR;
			}

		}

		if (e.getType() == 1 || e.getType() == 2) { // nRead和nWrite加一操作
			Procedure proc = Model.getProcAt(e.getProcIndex());
			ProgramPoint pp;
			if (e.getType() == 1)
				pp = proc.getPP(0);// 获取初始程序点
			else
				pp = proc.getPP(e.getPnt());
			if (pp.getType() == 'C') {
				String name = pp.getTaskName();
				Task task = Model.getTask(name);
				ArrayList<Integer> readSR = task.getReadSR();
				ArrayList<Integer> writeSR = task.getWriteSR();

				for (int i : readSR) {
					this.globalstate.getSRArray().incReadAt(i);
				}
				for (int i : writeSR) {
					this.globalstate.getSRArray().incWriteAt(i);
				}

				// 设置共享资源读写数组恢复信息
				srRInfo.decRead = readSR;
				srRInfo.decWrite = writeSR;
			}
		}

		return srRInfo;
	}


	/**
	 * restore share resource array of global state
	 * 
	 * @param info
	 */
	public void restoreSR(SRRestoreInfo info) {
		/** decease must done before increase **/
		if (info.decRead != null)
			for (int i : info.decRead) {
				this.globalstate.getSRArray().decReadAt(i);
			}
		if (info.decWrite != null)
			for (int i : info.decWrite) {
				this.globalstate.getSRArray().decWriteAt(i);
			}

		if (info.incRead != null)
			for (int i : info.incRead) {
				this.globalstate.getSRArray().incReadAt(i);
			}
		if (info.incWrite != null)
			for (int i : info.incWrite) {
				this.globalstate.getSRArray().incWriteAt(i);
			}
	}

////////////////////////////////////////////////////result info////////////////////////////////////////////////

	/**
	 * set information of checking result for writing into file
	 * 
	 * @param type
	 *  type=1: 中断丢失
	 *  type=2： 中断处理超时
	 *  type=3： 子过程超时
	 *  type=4： 资源冲突
	 */
	public void setResultInfo(int type, int i) {
		// set counter path
		ArrayList<Event> wrongPath = new ArrayList<Event>(this.eventPath);
		ResultWriter.setEventPath(wrongPath);

		// set counter time stamp
		int[] example = this.timeStamp.clone();
		ResultWriter.setCounterExample(example);

		// set fault interruption info
		if (type == 1 || type == 2) { // 中断丢失或程序超时
			int interIndex = i;
			String name = Model.getInterAt(interIndex).getName();
			int upbnd = Model.getInterAt(interIndex).getUBD();
			ResultWriter.setFaultInfo(type, name, upbnd);
		}
		else if (type == 3){ // 子过程超时
			Event e = this.eventPath.get(i);
			int procIndex = e.getProcIndex();
			int pnt = (e.getType() == 2) ? e.getPnt() : 0;
			String taskName = Model.getInterAt(procIndex).getIP().getPP(pnt).getTaskName();
			int upbnd = Model.getTask(taskName).getFinishTime();
			ResultWriter.setFaultInfo(type, taskName, upbnd);
		}
		else { //资源冲突
			String resName = Model.getSRArray().getNameAt(i);
			int[] nWriteIndices = getnRead_WriteIndices('w', resName);
			int[] nReadIndices = getnRead_WriteIndices('r', resName);
			ResultWriter.setSRConflictInfo(type, resName, nWriteIndices, nReadIndices);
		}
	}
	
	
	/**
	 * 获取发生冲突的那个资源在事件序列中的nRead和nWrite变化值
	 * @author rt
	 * @param type： 读或写
	 * @param resName:冲突的资源名
	 */
	public int[] getnRead_WriteIndices(char type, String resName) {
		
		int length = this.eventPath.size();
		int[] indices = new int[length];
		int readTasks = 0; //记录当前正在读resName的task数
		int writeTasks = 0; //记录当前正在写resName的task数
		for (int i = 0; i < length; i++) {
			
			Event e = this.eventPath.get(i);
			if (e.getType() == 2 || e.getType() == 3) { //减一
				ProgramPoint pp = getLastPPFrom(i);// 上一个程序点
				if (pp.getType() == 'C') {
					String name = pp.getTaskName();
					Task task = Model.getTask(name);
					if (task.hasRead(resName))
						readTasks--;
					if (task.hasWritten(resName))
						writeTasks--;
				}
			}
			
			if (e.getType() == 1 || e.getType() == 2) { //加一
				Procedure proc = Model.getProcAt(e.getProcIndex());
				ProgramPoint pp;
				if (e.getType() == 1)
					pp = proc.getPP(0);// 获取初始程序点
				else
					pp = proc.getPP(e.getPnt());
				if (pp.getType() == 'C') {
					String name = pp.getTaskName();
					Task task = Model.getTask(name);
					if (task.hasRead(resName))
						readTasks++;
					if (task.hasWritten(resName))
						writeTasks++;
				}
			}
			
			if (type == 'r')
				indices[i] = readTasks;
			else indices[i] = writeTasks;
			
		}
		
		return indices;
	}
	
	
	/**
	 * 获取当前程序点的上一个程序点
	 * @param index：eventPath中的第index个事件
	 */
	public ProgramPoint getLastPPFrom(int index) {
		Event curEvent = this.eventPath.get(index);
		int procIndex = curEvent.getProcIndex();
		Procedure proc = Model.getProcAt(procIndex);
		for (int i = index - 1; i >= 0; i--) {
			Event e = this.eventPath.get(i);
			if ((e.getType() == 2 || e.getType() == 1) && e.getProcIndex() == procIndex) {
				int point = (e.getType() == 2) ? e.getPnt() : 0;
				return proc.getPP(point);
			}
		}
		
		return null;
	}
	

	/**
	 * output to log at each end of path
	 * @param endType: 路径遍历结束类型
	 *  0： 正常结束
	 *  1： 中断丢失
	 *  2： 中断处理超时
	 *  3： 子过程超时
	 *  4： 资源冲突
	 * @param info： 说明遍历结束的附加信息
	 */
	public void writeLog(int endType, String info){
		
		this.pathCount++;	//increase path count
		
		/**output end reason**/
		if(endType == 0){	//end normally
			Logger.append("path " + this.pathCount
					+ "  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\r\n");
		}
		else if(endType == 1){	//interrupt lost
			Logger.append("path " + this.pathCount
					+ "  $$$$$$$$$$$$$$$$$$$$$$$$$$$$ interruption " + info
					+ " lost\r\n");
		}
		else if(endType == 4){	//SR conflict
			Logger.append("path" + this.pathCount 
					+ "++++++++++++++++++++++++++++++ shareResource " + info 
					+ " conflict!\r\n");
		}
		else if(endType == 3){	//task overTime
			Logger.append("path " + this.pathCount
					+ "  *****************************  event " + info
					+ " over time!\r\n");
		}
		else{	//procedure overTime
			Logger.append("path " + this.pathCount
					+ "  ############################# procedure " + info
					+ " over time\r\n");
		}
		
		this.timeStamp = z3.getSample();	//get time stamp of event in path
		printEventPath();	//write event path
		printGS();	//write global state
	}
	
	
	/**
	 * TEST print event path function
	 */
	public void printEventPath() {
		for (int i = 0; i < this.eventPath.size(); i++) {
			Logger.append("event " + i + " at time stamp " + this.timeStamp[i] + " : ");
			this.eventPath.get(i).printEvent();
			Logger.append("\r\n");
		}
		Logger.append("\r\n");
	}


	/**
	 * TEST print global state function
	 */
	public void printGS() {
		this.globalstate.printGS();
		Logger.append("\r\n");
	}

}
