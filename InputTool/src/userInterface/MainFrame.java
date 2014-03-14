
package userInterface;

import fileOperator.ReadTaskDesc;
import fileOperator.XMLFileReader;
import fileOperator.XMLFileWriter;
import inputAnalysis.ExecuteTime;
import inputAnalysis.ModelInfoCheck;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import util.ErrorInfo;
import util.Lexer;
import util.Notifier;
import variableDefinition.ControlVariable;
import variableDefinition.Interruption;
import variableDefinition.Interval;
import variableDefinition.Model;
import variableDefinition.Procedure;
import variableDefinition.ShareResource;
import variableDefinition.Task;
import variableDefinition.TaskSequence;


/**
 * main user interface class, read and modify system information
 */

public class MainFrame extends JFrame implements WindowListener{

	private static final long serialVersionUID = 1L;
	
	//jButton
	private JButton jbtInterAdd;
	private JButton jbtInterDel;
	private JButton jbtTaskAdd;
	private JButton jbtTaskDel;
	private JButton jbtCvAdd;
	private JButton jbtCvDel;
	private JButton jbtSrAdd;
	private JButton jbtSrDel;
	private JButton jbtProcDesc;
	private JButton jbtTaskSeq;
	private JButton jbtRead;
	private JButton jbtSave;
	private JButton jbtSaveas;
	private JButton jbtCheck;
	private JButton jbtIntervalAdd;
	private JButton jbtIntervalDel;
	private JButton jbtCommuTask;
	private JButton jbtTaskRead;
	
	//jLabel
	private JLabel jlb_cv;
	private JLabel jlb_sr;
	private JLabel jlb_seq;
	private JLabel jlb_task;
	private JLabel jlb_fileName;
	private JLabel jlb_errorInfo;
	private JLabel jlb_interval;
	private JLabel jlb_commuTask;
	private JLabel jlb_taskDesc;
	
	//jPanel
	private JPanel jp_task;
	private JPanel jp_inter;
	private JPanel jp_otherInfo;
	
	//jScrollPane
	private JScrollPane jsp_sr;
	private JScrollPane jsp_cv;
	private JScrollPane jsp_task;
	private JScrollPane jsp_inter;
	private JScrollPane jsp_seqLogic;
	private JScrollPane jsp_interProcDesc;
	private JScrollPane jsp_interTaskTable;
	private JScrollPane jsp_interCompute;
	private JScrollPane jsp_errorInfo;
	private JScrollPane jsp_interval;
	private JScrollPane jsp_taskDesc;
	
	public static JTabbedPane jTabbedPane1;
	
	//jTable
	public static JTable interTable;
	public static JTable taskTable;
	public static JTable cvTable;
	public static JTable srTable;
	public static JTable inter_taskTable;
	public static JTable intervalTable;
	
	//jTextField
	private JTextField jtf_commuTask;
	
	//jTextArea
	private JTextArea jta_seqLogic;
	private JTextArea jta_interProcDesc;
	private JTextArea jta_interCompute;
	private JTextArea jta_taskDesc;
	private static JTextArea jta_errorInfo;
	//////////////////////////////////////////////// End of variables declaration

	private String fileName;


	/**
	 * constructor new a user interface and set content and user listen actions
	 */

	public MainFrame(String fileName) {
		super("InputTool");
		this.fileName = fileName;
		initComponents();
		//get screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize);

		setLocationRelativeTo(null);
		setVisible(true);
		setContent();
		this.addWindowListener(this);
		action();
		
		//设置主窗口关闭按钮默认响应
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}


	/////////////////////////////////////////////////user interface///////////////////////////////////////////////////////
	/**
	 * user interface initialization function
	 */
	private void initComponents(){

		//jLabel
		jlb_cv = new JLabel();
		jlb_sr = new JLabel();
		jlb_seq = new JLabel();
		jlb_errorInfo = new JLabel();
		jlb_task = new JLabel();
		jlb_fileName = new JLabel();
		jlb_interval = new JLabel();
		jlb_commuTask = new JLabel();
		jlb_taskDesc = new JLabel();
		
		jTabbedPane1 = new JTabbedPane();
		
		//jTextField
		jtf_commuTask = new JTextField();
		
		//jTextArea
		jta_seqLogic = new JTextArea();
		jta_interProcDesc = new JTextArea();
		jta_interCompute = new JTextArea();
		jta_errorInfo = new JTextArea();
		jta_taskDesc = new JTextArea();
		
		//jScrollPane
		jsp_task = new JScrollPane();
		jsp_cv = new JScrollPane();
		jsp_inter = new JScrollPane();
		jsp_sr = new JScrollPane();
		jsp_seqLogic = new JScrollPane();
		jsp_interTaskTable = new JScrollPane();
		jsp_interProcDesc = new JScrollPane();
		jsp_interCompute = new JScrollPane();
		jsp_errorInfo = new JScrollPane();
		jsp_interval = new JScrollPane();
		jsp_taskDesc = new JScrollPane();
		
		//jPanel
		jp_task = new JPanel();
		jp_inter = new JPanel();
		jp_otherInfo = new JPanel();
		
		//jTable
		taskTable = new JTable();
		interTable = new JTable();
		cvTable = new JTable();
		srTable = new JTable();
		inter_taskTable = new JTable();
		intervalTable = new JTable();
		
		//jButton
		jbtRead = new JButton();
		jbtSave = new JButton();
		jbtSaveas = new JButton();
		jbtInterAdd = new JButton();
		jbtInterDel = new JButton();
		jbtTaskAdd = new JButton();
		jbtTaskDel = new JButton();
		jbtCvAdd = new JButton();
		jbtCvDel = new JButton();
		jbtSrAdd = new JButton();
		jbtSrDel = new JButton();
		jbtProcDesc = new JButton();
		jbtTaskSeq = new JButton();
		jbtCheck = new JButton();
		jbtIntervalAdd = new JButton();
		jbtIntervalDel = new JButton();
		jbtCommuTask = new JButton();
		jbtTaskRead = new JButton();
		
		///////////////////设置组件属性///////////////////////
		Font textAreaFont = new Font("宋体", Font.PLAIN, 16);
		Font textFieldFont = new Font("宋体", Font.PLAIN, 16);
		Font buttonFont = new Font("宋体", 1, 14);
		Font labelFont = new Font("宋体", 1, 14);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		jlb_fileName.setFont(new Font("宋体", 1, 18));
		jlb_errorInfo.setFont(labelFont);
		jlb_cv.setFont(labelFont);
		jlb_sr.setFont(labelFont);
		jlb_task.setFont(labelFont);
		jlb_seq.setFont(labelFont);
		jlb_interval.setFont(labelFont);
		jlb_commuTask.setFont(labelFont);
		jlb_taskDesc.setFont(labelFont);
		
		jlb_cv.setText("控制变量");
		jlb_sr.setText("共享资源");
		jlb_seq.setText("时序逻辑");
		jlb_task.setText("子过程");
		jlb_errorInfo.setText("错误信息");
		jlb_fileName.setText("文件名：" + fileName.substring(fileName.lastIndexOf("\\") + 1));
		jlb_interval.setText("中断号的最小时间间隔");
		jlb_commuTask.setText("打断通信子过程的中断的时间上限");
		jlb_taskDesc.setText("低层子过程的描述信息");

		jTabbedPane1.setFont(buttonFont);

		jsp_cv.setViewportView(cvTable);
		jsp_task.setViewportView(taskTable);
		jsp_inter.setViewportView(interTable);
		jsp_sr.setViewportView(srTable);
		jsp_seqLogic.setViewportView(jta_seqLogic);
		jsp_interProcDesc.setViewportView(jta_interProcDesc);
		jsp_interTaskTable.setViewportView(inter_taskTable);
		jsp_interCompute.setViewportView(jta_interCompute);
		jsp_errorInfo.setViewportView(jta_errorInfo);
		jsp_interval.setViewportView(intervalTable);
		jsp_taskDesc.setViewportView(jta_taskDesc);
		
		jtf_commuTask.setFont(textFieldFont);
		
		jta_seqLogic.setFont(textAreaFont);
		jta_seqLogic.setLineWrap(true); //自动换行
		jta_interProcDesc.setFont(textAreaFont);
		jta_interProcDesc.setLineWrap(true);
		jta_interCompute.setFont(textAreaFont);
		jta_interCompute.setLineWrap(true);
		jta_interCompute.setEditable(false);
		jta_interCompute.setLineWrap(true);
		jta_errorInfo.setFont(textAreaFont);
		jta_errorInfo.setLineWrap(true);
		jta_errorInfo.setEditable(false);
		jta_taskDesc.setEditable(false);
		jta_taskDesc.setLineWrap(false);
		jta_taskDesc.setFont(textAreaFont);

		jbtRead.setText("读 取");
		jbtSave.setText("保 存");
		jbtSaveas.setText("另存为");
		jbtCheck.setText("检验");
		jbtRead.setFont(buttonFont);
		jbtSave.setFont(buttonFont);
		jbtSaveas.setFont(buttonFont);
		jbtCheck.setFont(buttonFont);
		jbtInterAdd.setText("添 加");
		jbtInterAdd.setFont(buttonFont);
		jbtInterDel.setText("删 除");
		jbtInterDel.setFont(buttonFont);
		jbtTaskAdd.setText("添 加");
		jbtTaskAdd.setFont(buttonFont);
		jbtTaskDel.setText("删 除");
		jbtTaskDel.setFont(buttonFont);
		jbtCvAdd.setText("添 加");
		jbtCvAdd.setFont(buttonFont);
		jbtCvDel.setText("删 除");
		jbtCvDel.setFont(buttonFont);
		jbtSrAdd.setText("添 加");
		jbtSrAdd.setFont(buttonFont);
		jbtSrDel.setText("删 除");
		jbtSrDel.setFont(buttonFont);
		jbtProcDesc.setText("编辑完毕");
		jbtProcDesc.setFont(buttonFont);
		jbtTaskSeq.setText("编辑完毕");
		jbtTaskSeq.setFont(buttonFont);
		jbtIntervalAdd.setText("添 加");
		jbtIntervalAdd.setFont(buttonFont);
		jbtIntervalDel.setText("删 除");
		jbtIntervalDel.setFont(buttonFont);
		jbtCommuTask.setText("确 认");
		jbtCommuTask.setFont(buttonFont);
		jbtTaskRead.setText("读取附加文件");
		jbtTaskRead.setFont(new Font("宋体", 1, 12));

		DefaultTableCellRenderer render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		interTable.setDefaultRenderer(Object.class, render);
		taskTable.setDefaultRenderer(Object.class, render);
		cvTable.setDefaultRenderer(Object.class, render);
		srTable.setDefaultRenderer(Object.class, render);
		inter_taskTable.setDefaultRenderer(Object.class, render);
		

		
		/////////////////////////////////////////////////////////// 中断及处理程序界面///////////////////////////////////////
		//水平
		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jp_inter);
		jPanel1Layout.setAutoCreateContainerGaps(true);
		jPanel1Layout.setAutoCreateGaps(true);
		jp_inter.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout.createSequentialGroup().addGroup(
						jPanel1Layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jsp_inter, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
								.addGroup(
										jPanel1Layout
												.createSequentialGroup()
												.addContainerGap(400, Short.MAX_VALUE)
												.addComponent(jbtInterAdd, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addContainerGap(400, Short.MAX_VALUE)
												.addComponent(jbtInterDel, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap(400, Short.MAX_VALUE))
								.addGroup(
										jPanel1Layout.createSequentialGroup()
											.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jsp_interProcDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
												.addGroup(jPanel1Layout.createSequentialGroup()
														.addContainerGap(200, Short.MAX_VALUE)
														.addComponent(jbtProcDesc, javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addContainerGap(200, Short.MAX_VALUE)))
											.addContainerGap(10, Short.MAX_VALUE)
											.addComponent(jsp_interTaskTable, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
											.addContainerGap(10, Short.MAX_VALUE)
											.addComponent(jsp_interCompute, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
											.addContainerGap()))));
		//垂直
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel1Layout
								.createSequentialGroup()
								.addContainerGap(10, Short.MAX_VALUE)
								.addComponent(jsp_inter, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
								.addContainerGap(10, Short.MAX_VALUE)
								.addGroup(
										jPanel1Layout
												.createParallelGroup()
												.addComponent(jbtInterAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jbtInterDel, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(10, Short.MAX_VALUE)
								.addGroup(
										jPanel1Layout.createParallelGroup()
											.addGroup(jPanel1Layout.createSequentialGroup()
												.addComponent(jsp_interProcDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
												.addContainerGap(10, Short.MAX_VALUE)
												.addComponent(jbtProcDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE))
											.addComponent(jsp_interTaskTable, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
											.addComponent(jsp_interCompute, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE))
								.addContainerGap()));
		jTabbedPane1.addTab("中断及处理程序", jp_inter);

		
		////////////////////////////////////////////////////// 子过程及时序逻辑界面///////////////////////////////////////////
		//水平
		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jp_task);
		jp_task.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel3Layout
						.createSequentialGroup()
						.addContainerGap(20, Short.MAX_VALUE)
						.addGroup(
								jPanel3Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jlb_task, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
										.addComponent(jsp_task, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
										.addGroup(
												jPanel3Layout
												.createSequentialGroup()
												.addContainerGap(160, Short.MAX_VALUE)
												.addComponent(jbtTaskAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 80,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addContainerGap(120, Short.MAX_VALUE)
												.addComponent(jbtTaskDel, javax.swing.GroupLayout.PREFERRED_SIZE, 80,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addContainerGap(120, Short.MAX_VALUE)
												.addComponent(jbtTaskRead, javax.swing.GroupLayout.PREFERRED_SIZE, 120,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addContainerGap(160, Short.MAX_VALUE)))
						.addContainerGap(50, Short.MAX_VALUE)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jlb_taskDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
								.addComponent(jsp_taskDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
								)
						.addContainerGap()));
		//垂直
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel3Layout
								.createSequentialGroup()
								.addContainerGap(100, Short.MAX_VALUE)
								.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(jlb_task)
										.addComponent(jlb_taskDesc))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										jPanel3Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(jsp_task, javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
												.addComponent(jsp_taskDesc, javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE))
								.addContainerGap(20, Short.MAX_VALUE)
								.addGroup(
										jPanel3Layout
										.createParallelGroup()
										.addComponent(jbtTaskAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 32,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jbtTaskDel, javax.swing.GroupLayout.PREFERRED_SIZE, 32,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jbtTaskRead, javax.swing.GroupLayout.PREFERRED_SIZE, 32,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(150, Short.MAX_VALUE)));
		jTabbedPane1.addTab("子过程", jp_task);

		
		///////////////////////////////////////////控制变量、共享资源界面、各中断号最小时间间隔、通信子过程的允许打断时间///////////////////////////////////////
		//水平
		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jp_otherInfo);
		jp_otherInfo.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel4Layout
						.createSequentialGroup()
						.addContainerGap(50, Short.MAX_VALUE)
						.addGroup(
								jPanel4Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(
												jPanel4Layout.createSequentialGroup()
														.addComponent(jlb_cv, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
														.addContainerGap(100, Short.MAX_VALUE))
										.addComponent(jsp_cv, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
										.addGroup(
												jPanel4Layout.createSequentialGroup()
														.addContainerGap(80, Short.MAX_VALUE)
														.addComponent(jbtCvAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
														.addContainerGap(80, Short.MAX_VALUE)
														.addComponent(jbtCvDel, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
														.addContainerGap(80, Short.MAX_VALUE)))
						.addContainerGap(50, Short.MAX_VALUE)
						.addGroup(
								jPanel4Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(
												jPanel4Layout.createSequentialGroup()
														.addComponent(jlb_sr, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
														.addContainerGap(150, Short.MAX_VALUE))
										.addComponent(jsp_sr, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
										.addGroup(
												jPanel4Layout.createSequentialGroup()
														.addContainerGap(60, Short.MAX_VALUE)
														.addComponent(jbtSrAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
														.addContainerGap(60, Short.MAX_VALUE)
														.addComponent(jbtSrDel, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
														.addContainerGap(60, Short.MAX_VALUE)))
						.addContainerGap(50, Short.MAX_VALUE)
						.addGroup(
								jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel4Layout.createSequentialGroup()
										.addComponent(jlb_interval, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
										.addContainerGap(100, Short.MAX_VALUE))
								.addComponent(jsp_interval, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
								.addGroup(
										jPanel4Layout.createSequentialGroup()
											.addContainerGap(60, Short.MAX_VALUE)
											.addComponent(jbtIntervalAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
											.addContainerGap(60, Short.MAX_VALUE)
											.addComponent(jbtIntervalDel, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
											.addContainerGap(60, Short.MAX_VALUE))
								.addGroup(jPanel4Layout.createSequentialGroup()
										.addComponent(jlb_commuTask, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
										.addContainerGap(100, Short.MAX_VALUE))
								.addComponent(jtf_commuTask, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
								.addGroup(
										jPanel4Layout.createSequentialGroup()
										    .addContainerGap(120, Short.MAX_VALUE)
										    .addComponent(jbtCommuTask, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
										    .addContainerGap(120, Short.MAX_VALUE))
								)
							.addContainerGap(50, Short.MAX_VALUE)
				
				));
		//垂直
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel4Layout.createSequentialGroup()
						.addContainerGap(100, Short.MAX_VALUE)
						.addGroup(
								jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jlb_cv)
										.addComponent(jlb_sr)
										.addComponent(jlb_interval))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(
												jPanel4Layout.createSequentialGroup()
														.addGroup(
																jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(jsp_cv, javax.swing.GroupLayout.DEFAULT_SIZE, 650,
																				Short.MAX_VALUE)
																		.addComponent(jsp_sr, javax.swing.GroupLayout.DEFAULT_SIZE, 650,
																				Short.MAX_VALUE))
														.addContainerGap(20, Short.MAX_VALUE)
														.addGroup(
																jPanel4Layout
																		.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(jbtCvAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(jbtCvDel, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(jbtSrAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(jbtSrDel, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addContainerGap(100, Short.MAX_VALUE))
										.addGroup(jPanel4Layout.createSequentialGroup()
												.addComponent(jsp_interval, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
												.addContainerGap(20, Short.MAX_VALUE)
												.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jbtIntervalAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jbtIntervalDel, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
												.addContainerGap(100, Short.MAX_VALUE)
												.addComponent(jlb_commuTask)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(jtf_commuTask, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
												.addContainerGap(20, Short.MAX_VALUE)
												.addComponent(jbtCommuTask, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addContainerGap(150, Short.MAX_VALUE)))
						.addContainerGap(100, Short.MAX_VALUE))
				);
		jTabbedPane1.addTab("其他信息", jp_otherInfo);

		
		///////////////////////////////////////////////////// 主界面//////////////////////////////////////////////////
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap(300, Short.MAX_VALUE)
								.addComponent(jlb_fileName, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
								.addContainerGap(200, Short.MAX_VALUE)
								.addComponent(jbtRead, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
								.addContainerGap(200, Short.MAX_VALUE)
								.addComponent(jbtSave, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
								.addContainerGap(200, Short.MAX_VALUE)
								.addComponent(jbtSaveas, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
								.addContainerGap(200, Short.MAX_VALUE)
								.addComponent(jbtCheck, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE) 
								.addContainerGap(500, Short.MAX_VALUE))
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup().addContainerGap(10, Short.MAX_VALUE)
								.addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1450, Short.MAX_VALUE)
								.addContainerGap(10, Short.MAX_VALUE)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jlb_errorInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
										.addComponent(jsp_errorInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
								.addContainerGap()));
		////////////////////////////////////////////////垂直方向
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap(10, Short.MAX_VALUE)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jlb_fileName, javax.swing.GroupLayout.PREFERRED_SIZE, 32,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jbtRead, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jbtSave, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jbtSaveas, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jbtCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(20, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 950, Short.MAX_VALUE)
								.addGroup(layout.createSequentialGroup()
										.addComponent(jlb_errorInfo)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jsp_errorInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
										.addContainerGap(100, Short.MAX_VALUE)))
						.addContainerGap()));
		pack();

	}

	
	/////////////////////////////////////////////////////information setting///////////////////////////////////////////////////////
	/**
	 * use the syntax analysis result to set the user interface content by call
	 * four set functions
	 */
	public void setContent(){
		setTaskContent();
		setInterContent();
		setCVContent();
		setSRContent();
//		setSeqLogicContent();
		setIntervalContent();
		setCommuTaskBound();
	
		//内容设置完成后做一次totalAnalysis
		ModelInfoCheck.totalAnalysis();			
	}

	
	/**
	 * set interruption table content
	 */
	public void setInterContent(){
		String[][] s = null;
		DefaultTableModel tableModel = new DefaultTableModel(s, Interruption.labels){
			//设置表格可编辑列
			public boolean isCellEditable(int row, int column){
				if(column >= Interruption.paraSize )		//WCET/UPBND is not editable
					return false;
				else
					return true;
			}
		};
		
		//设置表格内容
		for (int i = 0; i < Model.interArray.size(); i++){
			tableModel.addRow(Interruption.getContent(Model.interArray.get(i)));
		}
		
		//设置表格数据改变监听
		tableModel.addTableModelListener(new TableModelListener(){

			public void tableChanged(TableModelEvent e){
				int col = e.getColumn();
				int row = e.getFirstRow();
				
				if(col != -1 && row != -1 && col < interTable.getColumnCount() - 2){
					updateInterInfo(row);
					ModelInfoCheck.totalAnalysis();
					updatePanels(row);
				}
			}
		});
		
		interTable.setModel(tableModel);

		// 设置‘中断类型’的输入方式
		TableColumn col = interTable.getColumnModel().getColumn(3);
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("random");
		comboBox.addItem("periodical");
		col.setCellEditor(new DefaultCellEditor(comboBox));
		
	}
	
	
	/**
	 * 设置“中断及处理程序”中的“WCET/UPBOUND”衍生数据
	 */
	public static void updateWCET(){
		DefaultTableModel tableModel = (DefaultTableModel) interTable.getModel();
		int rows = tableModel.getRowCount();
		int columns = tableModel.getColumnCount();
		ArrayList<Integer> errorRows = new ArrayList<Integer>();
		
		if (ModelInfoCheck.computeInfoCorrect == false){	//compute info incorrect, set WCET as "N.A."
			for(int k = 0; k < rows; k++){
				tableModel.setValueAt("N.A.", k, columns-1);
				setWCETRenderer(errorRows);
			}
		}	
		else{	//otherwise	
			ExecuteTime.updateWCET();		//recompute WCET first
			for (Interruption inter : Model.interArray){
				long wcet = inter.worstTime;
				
				//设置中断WCET
				int i;
				for (i = 0; i < rows; i++)
					if(tableModel.getValueAt(i, 0).equals(inter.name)){
						if(wcet != -1 && !inter.upperBound.equals("-1"))
							tableModel.setValueAt(String.valueOf(wcet) + " / " + inter.upperBound, i, columns-1);
						else
							tableModel.setValueAt("N.A.", i, columns-1);
						break;
					}
				//mark the interruptions that wcet exceeds upperBound
				if(wcet != -1 && inter.longUBD != -1 && wcet > inter.longUBD) 
					errorRows.add(i);
				setWCETRenderer(errorRows);
			}
		}
	}

	
	/**
	 * 设置“中断及处理程序”中的“静态执行时间区间”衍生数据
	 */
	public static void updateSET(){
		DefaultTableModel tableModel = (DefaultTableModel) interTable.getModel();
		int rows = tableModel.getRowCount();
		int columns = tableModel.getColumnCount();
		
		if(ModelInfoCheck.computeInfoCorrect == false){
			for(int k = 0; k < rows; k++)	{//模型计算数据存在错误，将区间设置为"N.A."
				tableModel.setValueAt("N.A.", k, columns-2);
			}
		}
		else{
			ExecuteTime.updateWCET();
			for (Interruption inter : Model.interArray) {
				long bset = inter.proc.bestTime;
				long wset = inter.proc.worstTime;
				
				for (int i = 0; i < rows; i++) {
					if (tableModel.getValueAt(i, 0).equals(inter.name)){
						tableModel.setValueAt(bset + "-" + wset, i, columns-2);
						break;
					}
				}
			}
		}	
	}

	
	/**
	 * set task table content
	 */
	public static void setTaskContent(){
		String[][] s = null;
		DefaultTableModel tableModel = new DefaultTableModel(s, Task.labels){
			public boolean isCellEditable(int row, int column){
				//column4，5分别为读资源和写资源列，设为不可编辑，响应双击事件
				if (column >= Task.paraSize || column == 4 || column == 5)
					return false;
				else
					return true;
			}
		};
		
		//设置表格内容
		for (int i = 0; i < Model.taskArray.size(); i++){
			tableModel.addRow(Task.getContent(Model.taskArray.get(i)));
		}	
		
		//设置表格改变监听
		tableModel.addTableModelListener(new TableModelListener(){

			public void tableChanged(TableModelEvent e){
				int col = e.getColumn();
				int row = e.getFirstRow();
				if(col != -1 && row != -1 && col < taskTable.getColumnCount() - 1){
					updateTaskInfo(row);
					ModelInfoCheck.totalAnalysis();
				}
			}
		});

		taskTable.setModel(tableModel);
		
		// 设置‘通信子过程’标签的输入方式
		TableColumn col = taskTable.getColumnModel().getColumn(6);
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("yes");
		comboBox.addItem("no");
		col.setCellEditor(new DefaultCellEditor(comboBox));
		
	}


	/**
	 * set control variable table content
	 */
	public void setCVContent(){
		String[][] s = null;
    	DefaultTableModel tableModel = new DefaultTableModel(s, ControlVariable.labels) {
    		//设置表格可编辑列
			public boolean isCellEditable(int row, int column){
				if(column >= ControlVariable.paraSize)		
					return false;
				else
					return true;
			}
		};
		
		//设置表格内容
		for (int i = 0; i < Model.controlVariableArray.size(); i++) {
			tableModel.addRow(ControlVariable.getContent(Model.controlVariableArray.get(i)));
		}
		
		//设置表格改变监听
		tableModel.addTableModelListener(new TableModelListener(){
			public void tableChanged(TableModelEvent e){
				int col = e.getColumn();
				int row = e.getFirstRow();
				if(col != -1 && row != -1 && col < cvTable.getColumnCount() - 1){
					updateCVInfo(row);
					ModelInfoCheck.totalAnalysis();
				}
			}
		});

		cvTable.setModel(tableModel);
		

		// 设置‘顺序取值’标签的输入方式
		TableColumn col = cvTable.getColumnModel().getColumn(4);
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("yes");
		comboBox.addItem("no");
		col.setCellEditor(new DefaultCellEditor(comboBox));
	}


	/**
	 * set share resource table content
	 */
	public void setSRContent(){
		String[][] s = null;
		DefaultTableModel tableModel = new DefaultTableModel(s, ShareResource.labels) {
			//设置表格可编辑列
			public boolean isCellEditable(int row, int column){
				if(column >= ShareResource.paraSize)		
					return false;
				else
					return true;
			}
		};
		
		//设置表格内容
		for (int i = 0; i < Model.shareResourceArray.size(); i++)
			tableModel.addRow(ShareResource.getContent(Model.shareResourceArray.get(i)));
		
		//设置表格改变监听
		tableModel.addTableModelListener(new TableModelListener(){
			public void tableChanged(TableModelEvent e){
				int col = e.getColumn();
				int row = e.getFirstRow();
				if(col != -1 && row != -1 && col < srTable.getColumnCount() - 2){
					updateSRInfo(row);
					ModelInfoCheck.totalAnalysis();
				}
			}
		});
		
		srTable.setModel(tableModel);
	}
	
	
	/**
	 * 设置各中断号的最小时间间隔信息
	 */
	public void setIntervalContent() {
		String[][] s = null;
		DefaultTableModel tableModel = new DefaultTableModel(s, Interval.labels);
		
		for (int i = 0; i < Model.intervalArray.size(); i++) {
			tableModel.addRow(Interval.getContent(Model.intervalArray.get(i)));
		}
		
		//设置表格改变监听
		tableModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e){
				int col = e.getColumn();
				int row = e.getFirstRow();
				if(col != -1 && row != -1 && col < intervalTable.getColumnCount()){
					updateIntervalInfo(row);
					ModelInfoCheck.totalAnalysis();
				}
			}
		});
		
		intervalTable.setModel(tableModel);
	}
	
	

	/**
	 * set content of sequential logic text area
	 */
	public void setSeqLogicContent(){
		String content = "";
		for (TaskSequence seqLogic : Model.taskSequences){
			content += seqLogic.content + '\n';
		}
		
		this.jta_seqLogic.setText(content);
	}
	
	
	/**
	 * 设置通信子过程的上界值
	 */
	public void setCommuTaskBound() {
		jtf_commuTask.setText(String.valueOf(Model.commuTaskBound));
	}

	
	// ///////////////////////////////////////////////颜色渲染////////////////////////////////////////////////////////
	
	/**
	 * function to renderer the column of "WCET/UPBND" in interruption table
	 * @param errorRows
	 */
	public static void setWCETRenderer(final ArrayList<Integer> errorRows){ //设置WCET/UPBOUND这一列的颜色显示
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer(){
			private static final long serialVersionUID = 1L;

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
				Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if(errorRows.contains(row) && column == interTable.getColumnCount() - 1 && cell.isBackgroundSet())
					cell.setBackground(Color.RED);
				else
					cell.setBackground(Color.WHITE);
				setHorizontalAlignment(SwingConstants.CENTER);
				return cell;
			}
		};

		String str = interTable.getColumnName(interTable.getColumnCount() - 1);
		interTable.getColumn(str).setCellRenderer(tcr);
	}
	
	
	/**
	 * table renderer function, renderer error(only syntax error) cells into red
	 * @param table
	 * @param map : error locations mappings, form column to rows
	 */
	public static void renderer(JTable table, final Map<Integer,ArrayList<Integer>> map){
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer (){
			private static final long serialVersionUID = 1L;

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
				Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if(map == null)
					cell.setBackground(Color.WHITE);
				else if(map.containsKey(column) && map.get(column).contains(row) && cell.isBackgroundSet())
					cell.setBackground(Color.RED);
				else if(row != table.getSelectedRow())
					cell.setBackground(Color.WHITE);
				else
					;
				
				setHorizontalAlignment(SwingConstants.CENTER);
				return cell;
			}
		};
		
		for(int i = 0; i < table.getColumnCount(); i++){
			table.getColumnModel().getColumn(i).setCellRenderer(tcr);

		}
		
	}
	
	
/////////////////////////////////////////////////////////////////事件触发///////////////////////////////////////////
	/**
	 * action function, the response to user actions
	 */
	public void action(){

		buttonAction(); //按钮事件触发

		 
		/**
		 * 监听interruption表格上鼠标事件
		 * 鼠标驻留时显示备注信息，其它列不显示
		 */
		interTable.addMouseMotionListener(new MouseAdapter() {

			public void mouseMoved(MouseEvent e) {
				int row = interTable.rowAtPoint(e.getPoint());
				int col = interTable.columnAtPoint(e.getPoint());
				if (row > -1 && col == 8) {
					Object value = interTable.getValueAt(row, col);
					if (value != null && !value.equals(""))
						interTable.setToolTipText(value.toString());// 悬浮显示单元格内容
					else
						interTable.setToolTipText(null); // 关闭提示
				}
				else
					interTable.setToolTipText(null); // 关闭提示
			}
		});

		
		/**
		 * 对于interrupt表格的鼠标选择事件监听
		 * 在面板中下面三个表格显示对应中断的处理程序、处理程序包含的子过程、WCET计算细节
		 */
		interTable.addMouseListener(new MouseAdapter(){

			public void mouseClicked(MouseEvent e){
				int row = interTable.getSelectedRow();
				updatePanels(row);
			}
		});

	
		
		/**
		 * 监听task表格上鼠标事件
		 * 鼠标驻留时显示备注、调用的程序信息，其它列不显示
		 */
		taskTable.addMouseMotionListener(new MouseAdapter() {

			public void mouseMoved(MouseEvent e) {
				int row = taskTable.rowAtPoint(e.getPoint());
				int col = taskTable.columnAtPoint(e.getPoint());
				if (row > -1 && (col == 7 || col == 8)) {
					Object value = taskTable.getValueAt(row, col);
					if (value != null && !value.equals(""))
						taskTable.setToolTipText(value.toString());// 悬浮显示单元格内容
					else
						taskTable.setToolTipText(null); // 关闭提示
				}
				else
					taskTable.setToolTipText(null); // 关闭提示
			}
		});
		
		
		/**
		 * 监听task表格的鼠标事件
		 * 在读资源或写资源列上双击时，弹出共享资源选择框
		 */
		taskTable.addMouseListener(new MouseAdapter(){

			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() == 1) { //鼠标单击
					int row = taskTable.getSelectedRow();
					Task task = Model.taskArray.get(row);
					
					if (task.proc != null)
						jta_taskDesc.setText(task.proc.description);
					else
						jta_taskDesc.setText("");
					
				}
				
				else if(e.getClickCount() == 2){   //鼠标双击
					//JOptionPane.showMessageDialog(null, "double click");
					int row = taskTable.rowAtPoint(e.getPoint());
					int col = taskTable.columnAtPoint(e.getPoint());
					if(row != -1 && col == 4){ //读资源
						ArrayList<String> res = Model.taskArray.get(row).readVariable;
						ChooseResource.newInstance(getMainFrame(), res, row, col);
					}
					else if(row != -1 && col == 5){ //写资源
						ArrayList<String> res = Model.taskArray.get(row).writeVariable;
						ChooseResource.newInstance(getMainFrame(), res, row, col);
					}
				}
			}
		});
		
		
		/**
		 * 监听controlVariable表格上鼠标事件
		 * 鼠标驻留时显示备注、调用的程序信息，其它列不显示
		 */
		cvTable.addMouseMotionListener(new MouseAdapter() {

			public void mouseMoved(MouseEvent e) {
				int row = cvTable.rowAtPoint(e.getPoint());
				int col = cvTable.columnAtPoint(e.getPoint());
				if (row > -1 && (col == 5 || col == 6)) {
					Object value = cvTable.getValueAt(row, col);
					if (value != null && !value.equals(""))
						cvTable.setToolTipText(value.toString());// 悬浮显示单元格内容
					else
						cvTable.setToolTipText(null); // 关闭提示
				}
				else
					cvTable.setToolTipText(null); // 关闭提示
			}
		});
		
	}
	
	
/////////////////////////////////////////////////按钮事件触发/////////////////////////////////////////////////
	/**
	 * trigger button event
	 */
	public void buttonAction(){
		
		/**
		 * 总界面读取按钮
		 */
		jbtRead.addActionListener(new ActionListener(){ 

			public void actionPerformed(ActionEvent e){
				try{
					jbtReadActionPerformed(e);
				} catch (Exception e1){
					e1.printStackTrace();
				}
			}
		});

		
		/**
		 * 总界面保存按钮
		 */
		jbtSave.addActionListener(new ActionListener() { 

			public void actionPerformed(ActionEvent e) {
				if (ModelInfoCheck.totalAnalysis() == true) {
					try {
						if (!fileName.equals("")) {
							XMLFileWriter xmlWriter = new XMLFileWriter(fileName);
							xmlWriter.save();
							
							Notifier.Notification("信息已成功保存！");
						}
						else
							jbtSaveActionPerformed(e);
					}
					catch (Exception e1) {
						System.out.println("error at jbtSave :" + e1.toString());
						e1.printStackTrace();
					}
				}
				else
					// don't allow saving if error exists in model
					Notifier.Notification("模型参数存在错误，请修改后再保存!");

			}
		});

		
		/**
		 * 总界面另存为按钮
		 */
		jbtSaveas.addActionListener(new ActionListener() { 

			public void actionPerformed(ActionEvent e) {
				if (ModelInfoCheck.totalAnalysis() == true) {
					try {
						jbtSaveActionPerformed(e);
					}
					catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				else
					Notifier.Notification("模型参数存在错误，请修改后再保存!");

			}
		});
		
		
		
		/**
		 * 总界面检查按钮
		 */
		jbtCheck.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				//模型文件为空
//				if (fileName == ""){	
//					Notifier.Notification("请读入模型文件！");
//				}
				//模型参数存在错误
				if (ModelInfoCheck.totalAnalysis() == false) {
					Notifier.Notification("模型参数存在错误，请修改正确后再进行检验！");
				}
				//模型正确，执行检验
				else {
					Integration integra = new Integration();
					boolean bmc = integra.execBMC();
//					boolean bmc = true;
					if (bmc == true) {
						System.out.println("BMC execute success!");
//						if (integra.execDisplay() == true)
//							System.out.println("display execute success!");
//						else
//							Notifier.ErrorPrompt("执行display命令时出错！");
					}
					else {
						Notifier.ErrorPrompt("执行BMC命令时出错！");
					}
				}

			}
		});
		
		
		/**
		 * 中断界面添加按钮
		 */
		jbtInterAdd.addActionListener(new ActionListener(){ 

				public void actionPerformed(ActionEvent e){
						DefaultTableModel tableModel = (DefaultTableModel) interTable.getModel();
						Interruption newInter = Model.addNewInter("");
						tableModel.addRow(Interruption.getContent(newInter));
					}
				});

		
		/**
		 * 中断界面删除按钮
		 */
		jbtInterDel.addActionListener(new ActionListener(){ 

					public void actionPerformed(ActionEvent e){
						DefaultTableModel tableModel = (DefaultTableModel) interTable.getModel();
						int row = interTable.getSelectedRow();
						if(row != -1){
							boolean choice = Notifier.DelConfirm();
							if(choice == true){
								tableModel.removeRow(row);
								Model.interArray.remove(row);
								updatePanels(-1);  //清空panels组的信息
								
								//trigger total analysis
								ModelInfoCheck.totalAnalysis();
							}
						}
					}
				});

		

		/**
		 * 程序描述界面完成编辑按钮
		 */
		jbtProcDesc.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int row = interTable.getSelectedRow();
				String desc = jta_interProcDesc.getText();
				if (row != -1) { // no selected interruption
					Procedure proc = Model.interArray.get(row).proc;
					
					proc.setValue(proc.name, desc);

					if (proc.undefineCVs != null && !proc.undefineCVs.isEmpty())
						AddCVPanel.newInstance(getMainFrame(), proc.undefineCVs);
					if (proc.undefineTasks != null && !proc.undefineTasks.isEmpty())
						AddTaskPanel.newInstance(getMainFrame(), proc.undefineTasks);

					ModelInfoCheck.totalAnalysis();
					updatePanels(row);
				}
				else
					Notifier.ErrorPrompt("请先在中断表格中选择对应的中断！");
			}
		});


		/**
		 * 子程序界面添加按钮
		 */
		jbtTaskAdd.addActionListener(new ActionListener(){ 

					public void actionPerformed(ActionEvent e){
						DefaultTableModel tableModel = (DefaultTableModel) taskTable.getModel();
						Task task = Model.addNewTask("");
						
						tableModel.addRow(Task.getContent(task));
					}
				});
		

		/**
		 * 子程序界面删除按钮
		 */
		jbtTaskDel.addActionListener(new ActionListener(){  

					public void actionPerformed(ActionEvent e){
						DefaultTableModel tableModel = (DefaultTableModel) taskTable.getModel();
						int row = taskTable.getSelectedRow();
						if(row != -1){
							boolean choice = Notifier.DelConfirm();
							if(choice == true){
								tableModel.removeRow(row);
								Model.taskArray.remove(row);
								
								//trigger total analysis 
								ModelInfoCheck.totalAnalysis();
							}
						}
					}
				});
		
		
		/**
		 * 子过程读取按钮
		 */
		jbtTaskRead.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String taskFileName;
				JFrame frame = new JFrame();
				FileDialog readFileDialog = new FileDialog(frame, "打开文件", FileDialog.LOAD);
				readFileDialog.setFile("*.txt");
				readFileDialog.setVisible(true);
				String loadFileName = readFileDialog.getFile();
				String loadFileDirectory = readFileDialog.getDirectory();
				String loadFilePath = loadFileDirectory + loadFileName;
				if(loadFileName != null){
					taskFileName = loadFilePath;
					ReadTaskDesc reader = new ReadTaskDesc(taskFileName);
					reader.readFile();
				}
			}
		});
		
		
		/**
		 * 控制变量界面添加按钮
		 */
		jbtCvAdd.addActionListener(new ActionListener(){ 

			public void actionPerformed(ActionEvent e){
				DefaultTableModel tableModel = (DefaultTableModel) cvTable.getModel();
				ControlVariable cv = Model.addNewCV("");
				
				tableModel.addRow(ControlVariable.getContent(cv));			
			}
		});
		

		/**
		 * 控制变量界面删除按钮
		 */
		jbtCvDel.addActionListener(new ActionListener(){ 

			public void actionPerformed(ActionEvent e){
				DefaultTableModel tableModel = (DefaultTableModel) cvTable.getModel();
				int row = cvTable.getSelectedRow();
				if(row != -1){
					boolean choice = Notifier.DelConfirm();
					if(choice == true){
						tableModel.removeRow(row);
						Model.controlVariableArray.remove(row);
						
						//trigger total analysis
						ModelInfoCheck.totalAnalysis();
					}
				}
			}
		});
		

		/**
		 * 共享资源界面添加按钮
		 */
		jbtSrAdd.addActionListener(new ActionListener(){ 

			public void actionPerformed(ActionEvent e){
				DefaultTableModel tableModel = (DefaultTableModel) srTable.getModel();
				ShareResource sr = new ShareResource();//TODO
				Model.shareResourceArray.add(sr);
				
				tableModel.addRow(ShareResource.getContent(sr));
				
			}
		});
		

		/**
		 * 共享资源界面删除按钮
		 */
		jbtSrDel.addActionListener(new ActionListener(){ 

			public void actionPerformed(ActionEvent e){
				DefaultTableModel tableModel = (DefaultTableModel) srTable.getModel();
				int row = srTable.getSelectedRow();
				if(row != -1){
					boolean choice = Notifier.DelConfirm();
					if(choice == true){
						tableModel.removeRow(row);
						Model.shareResourceArray.remove(row);
						
						//trigger total analysis
						ModelInfoCheck.totalAnalysis();
					}				
				}
			}
		});
		
		
		/**
		 * 时序逻辑界面编辑按钮
		 */
		jbtTaskSeq.addActionListener(new ActionListener(){  
			
			public void actionPerformed(ActionEvent e){
				String text = jta_seqLogic.getText();
				TaskSequence.updateTaskSeqs(text);
				
				ModelInfoCheck.totalAnalysis();
			}
		});
		

		/**
		 * 中断间隔表格添加按钮
		 */
		jbtIntervalAdd.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel tableModel = (DefaultTableModel) intervalTable.getModel();
				Interval interval = Model.addNewInterval("");
				
				tableModel.addRow(Interval.getContent(interval));
			}
		});
		
		
		/**
		 * 中断间隔表格删除按钮
		 */
		jbtIntervalDel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel tableModel = (DefaultTableModel) intervalTable.getModel();
				int row = intervalTable.getSelectedRow();
				if(row != -1){
					boolean choice = Notifier.DelConfirm();
					if(choice == true){
						tableModel.removeRow(row);
						Model.intervalArray.remove(row);
						
						//trigger total analysis
						ModelInfoCheck.totalAnalysis();
					}
				}
			}
		});
		
		
		/**
		 * 通信子过程文本框确定按钮
		 */
		jbtCommuTask.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String bound = jtf_commuTask.getText();
				Model.commuTaskBound = Lexer.toLong(bound);
				
				ModelInfoCheck.totalAnalysis();
			}
		});
		
	}
	
	////////////////////////////////////////////////response functions/////////////////////////////////////////////////////
	
	/**
	 * update the three panels in interruption page
	 * @param row : the row selected in interruption table
	 */
	private void updatePanels(int row){
		Procedure proc = (row == -1) ? null : Model.interArray.get(row).proc;
		
		//显示子过程的时间信息
		String[] names = { "子过程名", "时间下界", "时间上界" };
		String[][] s = null;
		DefaultTableModel tableModel = new DefaultTableModel(s, names){

			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		
		if (row == -1) {
			jta_interProcDesc.setText("");
			jta_interCompute.setText("");
			inter_taskTable.setModel(tableModel);
			return;
		}
		
		//显示选择中断的处理程序描述	
		if (proc != null)
			jta_interProcDesc.setText(proc.description);
		else 
			jta_interProcDesc.setText("Not Available");
		
		//显示中断处理程序所包含的task
		if (proc != null && proc.containedTasks != null)
			for (Task task : proc.containedTasks) {
				tableModel.addRow(new String[] { task.name, task.lowerBound, task.upperBound });
			}
		inter_taskTable.setModel(tableModel);
		
		
		//显示所选择中断WCET的计算信息
		if(proc == null || ModelInfoCheck.computeInfoCorrect == false)
			jta_interCompute.setText("Not Available");
		else {
			String interName = (String) interTable.getValueAt(row, 0);
			jta_interCompute.setText(ExecuteTime.getComputeDetail(interName));
		}
	}
	
	
	/**
	 * perform the read file action. use fileDialog to choose the input file
	 * and new a MainFrame
	 */
	private void jbtReadActionPerformed(ActionEvent e) throws Exception{ //打开文件 
		JFrame frame = new JFrame();
		FileDialog readFileDialog = new FileDialog(frame, "打开文件", FileDialog.LOAD);
		readFileDialog.setFile("*.xml");
		readFileDialog.setVisible(true);
		String loadFileName = readFileDialog.getFile();
		String loadFileDirectory = readFileDialog.getDirectory();
		String loadFilePath = loadFileDirectory + loadFileName;
		if(loadFileName != null){
			this.fileName = loadFilePath;
			XMLFileReader reader = new XMLFileReader(this.fileName);
			if(reader.initModel()){
				this.dispose();
				new MainFrame(fileName);
			}
			else 
				Notifier.ErrorPrompt("读取文件失败!");
		}
	}
	
	
	/**
	 * perform the save file action. use fileDialog to set the file path
	 * @param e
	 * @throws Exception
	 */
	private void jbtSaveActionPerformed(ActionEvent e) throws Exception{
		JFrame frame = new JFrame();
		FileDialog saveAsFileDialog = new FileDialog(frame, "另存为", FileDialog.SAVE);
		saveAsFileDialog.setVisible(true);
		// 得到文件名
		String saveAsFileName = saveAsFileDialog.getFile();
		String saveAsFileDirectory = saveAsFileDialog.getDirectory();
		String saveAsFilePath = saveAsFileDirectory + saveAsFileName;
		if(saveAsFileName == null){
			//cann't save while user hasn't input a file name
		}
		else {
			try {
				//文件始终保存为XML格式
				if (! saveAsFileName.endsWith(".xml")) {
					saveAsFilePath += ".xml";
				}
				
				XMLFileWriter xmlWriter = new XMLFileWriter(saveAsFilePath);
				xmlWriter.save();
				Notifier.Notification("信息已成功保存！");
			}
			catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}


	/**
	 * update the content of interruption on user interface to program variables, trigger when
	 * change happens in one row
	 */
	private void updateInterInfo(int row){ // 更新中断信息
		DefaultTableModel tableModel = (DefaultTableModel) interTable.getModel();
		int col = tableModel.getColumnCount() - 2;	//注：最后两栏不可编辑
		Interruption inter = Model.interArray.get(row);
		String[] interToken = new String[col];
		for (int j = 0; j < col; j++){	
			String str = (String) tableModel.getValueAt(row, j);
			interToken[j] = str;
		}
		
		inter.setValue(interToken);
	}



	/**
	 * update the content of task on user interface to program variables, trigger when
	 * change happens in one row
	 */
	private static void updateTaskInfo(int row){
		DefaultTableModel tableModel = (DefaultTableModel) taskTable.getModel();
		int col = tableModel.getColumnCount() - 1;   //注：最后一栏不可编辑
		Task task = Model.taskArray.get(row);
		String[] taskToken = new String[col];
		for (int j = 0; j < col; j++){
			taskToken[j] = (String) tableModel.getValueAt(row, j);
		}
		
		task.setValue(taskToken);
	}


	/**
	 * update the content of control variable on user interface to program variables, trigger when
	 * change happens in one row
	 */
	private static void updateCVInfo(int row){
		DefaultTableModel tableModel = (DefaultTableModel) cvTable.getModel();
		int col = tableModel.getColumnCount() - 1;       //注：最后一栏不可编辑
		ControlVariable cvariable = Model.controlVariableArray.get(row);
		String[] cvToken = new String[col];
		for (int j = 0; j < col; j++){ 
			String str = (String) tableModel.getValueAt(row, j);
			cvToken[j] = str;
		}
		
		cvariable.setValue(cvToken);
	}


	/**
	 * update the content of share resource on user interface to program variables, trigger when
	 * change happens in one row
	 */
	private static void updateSRInfo(int row){
		DefaultTableModel tableModel = (DefaultTableModel) srTable.getModel();
		ShareResource sr = Model.shareResourceArray.get(row);
		String str = (String) tableModel.getValueAt(row, 0);
		//update share resource info
		sr.setValue(str);
	}
	
	
	/**
	 * 中断间隔表格内容修改后做相应的更新
	 * @param row
	 */
	private void updateIntervalInfo(int row) {
		DefaultTableModel tableModel = (DefaultTableModel) intervalTable.getModel();
		int col = tableModel.getColumnCount();
		Interval interval = Model.intervalArray.get(row);
		String[] token = new String[col];
		for (int j = 0; j < col; j++){ 
			String str = (String) tableModel.getValueAt(row, j);
			token[j] = str;
		}
		
		interval.setValue(token);	
	}
	
	/**
	 * 
	 */
	public static void showErrorInfo() {
		jta_errorInfo.setText(ErrorInfo.getInfo());
	}

	
	/**
	 * 
	 */
	public String getFileName(){
		return this.fileName;
	}
	
	
	/**
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	
	
	/**
	 * 
	 */
	public JFrame getMainFrame(){
		return this;
	}
	
	
	/////////////////////////////////////////window action listener///////////////////////////////////////////////
	@Override
	public void windowClosing(WindowEvent e){
		boolean option = Notifier.ExitConfirm();
		if(option == true){
			System.exit(1);
		}
	}

	
	@Override
	public void windowDeactivated(WindowEvent e){

	}


	@Override
	public void windowOpened(WindowEvent e){

	}


	@Override
	public void windowClosed(WindowEvent e){

	}


	@Override
	public void windowIconified(WindowEvent e){

	}


	@Override
	public void windowDeiconified(WindowEvent e){

	}


	@Override
	public void windowActivated(WindowEvent e){

	}

}