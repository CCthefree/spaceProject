package initializer;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import paint.Painter;

/**
 * main class
 *  Read check result from file, and generate the event positions, pass them to Painter to display
 * 
 * @author zengke.cai
 * 
 */
public class Main {

	private static String fileLoc;
	private static String fileName;
	
	private static int[] x; 	//X coordinates of CPU line
	private static int[] y; 	//Y coordinates of CPU line


	public static void main(String[] args) {
		fileLoc = System.getProperty("user.dir");
		fileLoc = "d:\\bmc";
		fileName = fileLoc + "\\result.xml";
		
		Result rs = new Result();
		
		String resultFile = (args.length == 0) ? fileName : args[0];
		FileReader reader = new FileReader(resultFile);
		
		//读取文件失败，退出程序
		if(reader.initResult(rs) == false)
			System.exit(1);
		
		rs.sortInter(); // 将中断按优先级从高到低排序，方便图示

		String result = rs.getResult();
		if (result.equals("YES")) {
			JOptionPane.showMessageDialog(null, "检验未发现错误！");
		}
		else {
			
			calCPULine();
			
			//将X和Y传递给作图对象
			new Painter(x, y);
		}
	}
	
	
	/**
	 * 计算CPU占用曲线的座标序列
	 */
	public static void calCPULine(){
		ArrayList<Event> path = Result.getPath();
		int pathLength = path.size();

		// 图形上的转折点，最多2*pathLength个点
		x = new int[2 * pathLength];
		y = new int[2 * pathLength];
		for (int i = 0; i < 2 * pathLength; i++) {
			x[i] = Integer.MIN_VALUE;
			y[i] = Integer.MIN_VALUE;
		}

		ArrayList<Integer> processProc = new ArrayList<Integer>(); // 记录CPU栈中的处理程序

		// 遍历事件路径，生成图形坐标点数组
		for (int i = 0; i < pathLength; i++) {
			Event e = path.get(i);
			if (e.getType() == 1) { // 处理程序进栈事件
				if (processProc.isEmpty()) { // 进栈前CPU栈为空
					y[2 * i - 1] = -1;
				}
				else {
					y[2 * i - 1] = processProc.get(processProc.size() - 1);

				}
				x[2 * i - 1] = e.getTimeStamp();

				x[2 * i] = e.getTimeStamp();
				y[2 * i] = e.getProcIndex();

				processProc.add(e.getProcIndex());
			}
			else if (e.getType() == 3) { // 处理程序出栈事件
				x[2 * i - 1] = e.getTimeStamp();
				y[2 * i - 1] = e.getProcIndex();
				processProc.remove(processProc.size() - 1);

				if (processProc.isEmpty()) {
					y[2 * i] = -1;
				}
				else {
					y[2 * i] = processProc.get(processProc.size() - 1);
				}
				x[2 * i] = e.getTimeStamp();
			}

			else if (i == pathLength - 1) { // 设置路径坐标序列的终点
				if (processProc.isEmpty())
					y[2 * i - 1] = -1;
				else
					y[2 * i - 1] = processProc.get(processProc.size() - 1);
				x[2 * i - 1] = Result.getMaxTime();
			}
		}
	}
}
