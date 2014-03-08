package paint;

import initializer.Event;
import initializer.Result;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;

/**
 * UI class to paint the check result
 * 
 * @author zengke.cai
 * 
 */
public class Painter extends JFrame {

	private int width;

	private int height;

	private int interCount; // 中断数量

	private int zeroX; // X轴起点坐标

	private int zeroY; // Y轴起点坐标

	private int xLength; // X轴长度

	private int yLength; // y轴长度

	private int tickStep; // Y轴刻度步长

	private int[] x; // 事件点的X坐标序列（此处值由事件路径算出，画图之前需转换）

	private int[] y; // 事件点的Y坐标序列（此处值由事件路径算出，画图之前需转换）

	private int positionCount; // 最终事件点坐标的数量（因为有些事件不需要画线）

	private Font titleFont;

	private Font labelFont;

	private Stroke axisStroke;

	private Stroke interLineStroke;

	private static final long serialVersionUID = 1L;


	/**
	 * 构造函数，传入由事件路径得到的事件点坐标序列
	 */
	public Painter(int[] x, int[] y) {
		super("Check Result");

		this.interCount = Result.getInterCount();
		this.x = x;
		this.y = y;

		this.axisStroke = new BasicStroke(3.0f);
		this.interLineStroke = new BasicStroke(1.0f);
		this.titleFont = new Font("宋体", Font.PLAIN, 24);
		this.labelFont = new Font("宋体", Font.PLAIN, 16);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = screenSize.width;
		this.height = screenSize.height;
		setSize(width, height);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}


	/**
	 * 画图函数
	 */
	public void paint(Graphics g) {

		/** 根据界面大小设置坐标轴信息（起点、长度、步长等） **/
		this.zeroX = this.width / 6;
		this.zeroY = this.height * 3 / 5;
		this.xLength = this.width * 3 / 4;
		this.yLength = this.height * 2 / 5;
		this.tickStep = yLength / (this.interCount + 1);

		Graphics2D g2 = (Graphics2D) g;
		g2.setClip(0, 0, width, height);
		g2.clearRect(0, 0, width, height);

		/** 设置标题 **/
		g2.setFont(this.titleFont);
		g2.setColor(Color.BLUE);
		g2.drawString(Result.genFaultInfo(), this.width / 3, 80);

		/** 画坐标轴 **/
		g2.setStroke(this.axisStroke);
		g2.setColor(Color.BLACK);
		g2.drawLine(zeroX, zeroY, zeroX + xLength, zeroY);
		g2.drawLine(zeroX, zeroY, zeroX, zeroY - yLength);

		/** 画各个中断的水平线 **/
		g2.setColor(Color.GRAY);
		g2.setStroke(interLineStroke);
		for (int i = 1; i <= this.interCount; i++) {
			g2.drawLine(zeroX, zeroY - i * tickStep, zeroX + xLength, zeroY - i * tickStep);
		}

		/** 纵轴上标中断名 **/
		g2.setFont(labelFont);
		g2.setColor(Color.BLACK);
		for (int i = 0; i < this.interCount; i++) {
			g2.drawString(Result.getInterArray().get(i).getName(), zeroX - 100, zeroY
					- (this.interCount - i) * tickStep);
		}

		/** 画事件点垂直线 **/
		g2.setColor(Color.GRAY);
		ArrayList<Event> path = Result.getPath();
		for (int i = 0; i < path.size(); i++) {
			int x = convertTS(path.get(i).getTimeStamp());
			int yStart = this.height;
			int yEnd = zeroY;
			if (path.get(i).getType() != 0) {
				int index = Result.getNewIndex(path.get(i).getProcIndex());
				yEnd = zeroY - tickStep * (interCount - index);
			}

			g2.drawLine(x, yStart, x, yEnd);
			g2.drawString(path.get(i).getTimeStamp() + "", x - 10, zeroY + 20);
		}

		/** 标注事件 **/
		g2.setFont(labelFont);
		g2.setColor(Color.BLACK);

		int round = 10;
		int eventY = zeroY + 40;
		for (int i = 0; i < path.size(); i++) {
			int x = convertTS(path.get(i).getTimeStamp());

			String label = (i + 1) + "." + path.get(i).genLabel();

			g2.drawString(label, x, eventY + (i % round) * 20);
		}

		/** 画CPU占用曲线 **/
		g2.setColor(Color.RED);
		g2.setStroke(axisStroke);
		int[] AX = convertX(this.x);
		int[] AY = convertY(this.y);
		g2.drawPolyline(AX, AY, this.positionCount);

		/** 如果错误类型是资源竞争，画资源访问未意图 **/
		int faultType = Result.getType();
		if (faultType == 4) {
			paintSR(g2);
		}

	}


	/**
	 * 画资源竞争访问示意图
	 */
	public void paintSR(Graphics2D g2) {
		ArrayList<Event> path = Result.getPath();

		g2.setStroke(this.interLineStroke);
		/** 标注发生冲突的资源名 **/
		g2.setColor(Color.BLACK);
		g2.drawString(Result.getName(), zeroX - 50, zeroY - yLength);

		int start = 0;// 开始事件位置
		int eventSize = Result.getPath().size();

		/** 对于每个占用CPU时间的事件，画对应的资源访问矩形块 **/
		while (start < eventSize) {
			Event curE = path.get(start);
			// 找到占用CPU时间的事件
			if (start < eventSize - 1 && curE.getTimeStamp() == path.get(start + 1).getTimeStamp()) {
				start++;
				continue;
			}

			int end = start + 1;
			// 排除自动机转换事件对图形的干扰
			while (end < eventSize && path.get(end).getType() == 0)
				end++;

			// 坐标转换，画资源访问矩形块
			int startX = convertTS(curE.getTimeStamp());
			int startY = zeroY - yLength - 30;
			int endX = end < eventSize ? convertTS(path.get(end).getTimeStamp()) : zeroX + xLength;
			Rectangle rec = new Rectangle(startX, startY, endX - startX, 30);

			// 根据是否冲突对矩形块填 充颜色
			int read = Result.getReadRecAt(start);
			int write = Result.getWriteRecAt(start);
			if (write > 1 || (read > 0 && write > 0))
				g2.setColor(Color.RED);
			else
				g2.setColor(Color.GREEN);
			g2.fill(rec);

			g2.setColor(Color.BLACK);
			g2.draw(rec);

			// 在矩形块内标注 读/写子过程数
			String label = read + "R/" + write + "W";
			g2.drawString(label, (int) rec.getCenterX(), (int) rec.getCenterY());

			/** 准备下一次迭代 **/
			start = end;
		}
	}


	/**
	 * 将传入的X坐标序列转化为图形上的坐标点
	 */
	public int[] convertX(int[] x) {
		int[] result = new int[x.length];
		result[0] = this.zeroX;
		int count = 1;

		for (int i = 0; i < x.length; i++) {
			if (x[i] != Integer.MIN_VALUE) {
				result[count] = convertTS(x[i]);
				count++;
			}
		}

		this.positionCount = count;
		return result;
	}


	/**
	 * 将传入的y坐标序列转化为图形上的坐标点
	 */
	public int[] convertY(int[] y) {
		int[] result = new int[y.length];
		int count = 1;
		result[0] = this.zeroY;

		for (int i = 0; i < y.length; i++) {
			if (y[i] == -1) {
				result[count] = zeroY;
				count++;
			}
			else if (y[i] != Integer.MIN_VALUE) {
				int index = Result.getNewIndex(y[i]);
				result[count] = zeroY - tickStep * (interCount - index);
				count++;
			}
		}

		return result;
	}


	/**
	 * convert timeStamp to the location of X axis
	 * 
	 * @param timeStamp
	 * @return
	 */
	public int convertTS(int timeStamp) {
		int maxTime = Result.getMaxTime();
		return zeroX + (xLength * timeStamp / maxTime) * 5 / 6;
	}

}