
package userInterface;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import util.Notifier;
import variableDefinition.Model;
import variableDefinition.Task;


/**
 * UI of add undefined tasks, appear when there are undefined tasks in procedure
 * description just edited
 * 
 * @author zengke.cai
 * 
 */
public class AddTaskPanel extends JDialog{

	private static final long serialVersionUID = 1L;

	private ArrayList<String> newTasks;
	private int numOfTask;
	private JCheckBox[] checkBoxes;
	private JButton jbtNotAdd;
	private JButton jbtAdd;
	private JButton jbtAddAndEdit;
	private JPanel jpBoxes;

	private static AddTaskPanel uniqueInstance;		//keep only one instance 
	
	/**
	 * constructor
	 * 
	 * @param tasks: list of optional tasks to be added
	 */
	private AddTaskPanel(JFrame frame, ArrayList<String> tasks) {
		super(frame, "添加子过程");
		this.newTasks = tasks;
		this.numOfTask = tasks.size();
		initComponent();
		setLocationRelativeTo(null);
		this.setModal(true);	//锁定当前窗口
		action();	//action监听必须在setVisible之前
		setVisible(true);
		
	}

	
	/**
	 * static method to new a instance, dispose the old one
	 */
	public static AddTaskPanel newInstance(JFrame frame, ArrayList<String> tasks){
		if(uniqueInstance != null)
			uniqueInstance.dispose();
	
		uniqueInstance = new AddTaskPanel(frame, tasks);
		return uniqueInstance;
	}

	/**
	 * 
	 */
	private void initComponent(){
		Font btnFont = new Font("宋体", 1, 14);
		Font optFont = new Font("宋体", 1, 20);
		jbtAdd = new JButton();
		jbtNotAdd = new JButton();
		jbtAddAndEdit = new JButton();
		jpBoxes = new JPanel();
		jbtNotAdd.setText("不 添 加");
		jbtNotAdd.setFont(btnFont);
		jbtAdd.setText("添加但不编辑");
		jbtAdd.setFont(btnFont);
		jbtAddAndEdit.setText("添加并编辑");
		jbtAddAndEdit.setFont(btnFont);

		jpBoxes.setLayout(new GridLayout(0, 1, 10, 10));
		checkBoxes = new JCheckBox[numOfTask];

		for (int i = 0; i < numOfTask; i++){
			String name = newTasks.get(i);
			checkBoxes[i] = new JCheckBox(name, false);
			jpBoxes.add(checkBoxes[i]);
		}

		for (int i = 0; i < numOfTask; i++)
			checkBoxes[i].setFont(optFont);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup().addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jpBoxes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(
						layout.createSequentialGroup().addContainerGap(80, Short.MAX_VALUE).addComponent(jbtNotAdd)
								.addContainerGap(30, Short.MAX_VALUE).addComponent(jbtAdd).addContainerGap(30, Short.MAX_VALUE)
								.addComponent(jbtAddAndEdit).addContainerGap(80, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap(25, Short.MAX_VALUE)
						.addComponent(jpBoxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(40, Short.MAX_VALUE)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jbtNotAdd).addComponent(jbtAdd)
										.addComponent(jbtAddAndEdit)).addContainerGap(50, Short.MAX_VALUE)));
		pack();

	}


	/**
	 * action listener
	 */
	public void action(){

		/**
		 * action of button "不添加"
		 */
		this.jbtNotAdd.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				notAdd();
			}
		});

		/**
		 * action of button "添加但不编辑"
		 */
		this.jbtAdd.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				ArrayList<String> selectedTasks = selectedTasks();
				if(selectedTasks.isEmpty())
					Notifier.Notification("请勾选需要添加的子过程名！");
				else
					add(selectedTasks);
			}
		});

		/**
		 * action of button "添加且编辑"
		 */
		this.jbtAddAndEdit.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				ArrayList<String> selectedTasks = selectedTasks();
				if(selectedTasks.isEmpty())
					Notifier.Notification("请勾选需要添加的子过程名！");
				else {
					add(selectedTasks);
					MainFrame.jTabbedPane1.setSelectedIndex(1);
				}
			}
		});
	}


	/**
	 * get selected tasks
	 */
	public ArrayList<String> selectedTasks(){
		ArrayList<String> tasks = new ArrayList<String>();

		for (int i = 0; i < this.checkBoxes.length; i++)
			if(this.checkBoxes[i].isSelected())
				tasks.add(this.checkBoxes[i].getText());

		return tasks;
	}


	/**
	 * 
	 */
	public void notAdd(){
		this.dispose();
	}


	/**
	 * add selected tasks into program structure, and update task table
	 * 
	 * @param tasks: name of tasks to be added, not null or empty
	 */
	public void add(ArrayList<String> tasks){
		DefaultTableModel tablemodel = (DefaultTableModel) MainFrame.taskTable.getModel();

		//add new tasks and refresh task table
		for (String temp : tasks){
			Task t = Model.addNewTask(temp);

			//add new task in table
			tablemodel.addRow(new String[] { t.name, t.lowerBound, t.upperBound, t.finishTime, t.getReadResource(), t.getWriteResource(), "" });
		}

		this.dispose();
	}


}
