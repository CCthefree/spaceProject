
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import util.Notifier;
import variableDefinition.Model;
import variableDefinition.ShareResource;


/**
 * UI of share resource chooser, appear when user editing share resources on
 * task table
 * 
 * @author ting.rong
 * 
 */
public class ChooseResource extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int numOfRes;
	private int row;
	private int col;
	private ArrayList<String> selectedRes = new ArrayList<String>();
	private JFrame frame;
	private JCheckBox[] checkBoxes;
	private JButton jbtAdd;
	private JButton jbtConfirm;
	private JPanel jpBoxes;
	private JTextField jtfAdd;
	private JLabel add_label;

	private static ChooseResource uniqueInstance;		//keep only one instance 
	
	
	/**
	 * constructor
	 * 
	 * @param resources: is the list of original share resources, set these item
	 *            as checked
	 * @param row: the editing row in task table
	 * @param col: the editing column in task table
	 */
	private ChooseResource(JFrame frame, ArrayList<String> resources, int row, int col) {
		super(frame, "共享资源选择");
		this.numOfRes = Model.shareResourceArray.size(); //共享资源数目
		this.frame = frame;
		this.selectedRes = resources;
		this.row = row;
		this.col = col;
		initComponent();
		setLocationRelativeTo(null);
		this.setModal(true);		//锁定当前窗口
		action();		//action监听必须在setVisible之前
		setVisible(true);
	}

	
	/**
	 * static method to new a instance, dispose the old one
	 */
	public static ChooseResource newInstance(JFrame frame, ArrayList<String> res, int row, int col){
		if(uniqueInstance != null)
			uniqueInstance.dispose();
	
		uniqueInstance = new ChooseResource(frame, res, row, col);
		return uniqueInstance;
	}

	/**
	 * 
	 */
	private void initComponent(){
		jbtAdd = new JButton();
		jbtConfirm = new JButton();
		jpBoxes = new JPanel();
		jbtAdd.setText("添 加");
		jbtAdd.setFont(new Font("宋体", 1, 14));
		jbtConfirm.setText("确 认");
		jbtConfirm.setFont(new Font("宋体", 1, 14));
		jtfAdd = new JTextField();
	    add_label = new JLabel();
	    add_label.setText("请输入要添加的共享资源名：");

		jpBoxes.setLayout(new GridLayout(0, 1, 5, 5));
		checkBoxes = new JCheckBox[numOfRes];

		for (int i = 0; i < numOfRes; i++){
			String res = Model.shareResourceArray.get(i).name;
			if(selectedRes.contains(res)) //默认选中
				checkBoxes[i] = new JCheckBox(res, true);
			else
				checkBoxes[i] = new JCheckBox(res, false);
			jpBoxes.add(checkBoxes[i]);
		}

		for (int i = 0; i < numOfRes; i++)
			checkBoxes[i].setFont(new Font("宋体", 1, 16));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap(50, Short.MAX_VALUE)
								.addComponent(jpBoxes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(
						layout.createSequentialGroup()
							.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(
									layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(add_label, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
									    .addComponent(jtfAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
							.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(
						layout.createSequentialGroup()
						    .addContainerGap(80, Short.MAX_VALUE)
						    .addComponent(jbtAdd)
						    .addContainerGap(35, Short.MAX_VALUE)
						    .addComponent(jbtConfirm)
							.addContainerGap(90, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap(25, Short.MAX_VALUE)
						.addComponent(jpBoxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(25, Short.MAX_VALUE)
						.addComponent(add_label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(jtfAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
						.addContainerGap(20, Short.MAX_VALUE)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
									.addComponent(jbtAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addComponent(jbtConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(30, Short.MAX_VALUE)));
		pack();

	}


	/**
	 * action listener
	 */
	public void action(){

		/**
		 * confirm button listener
		 */
		this.jbtConfirm.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				selectedRes.clear();
				for (int i = 0; i < checkBoxes.length; i++){ //get selected items
					if(checkBoxes[i].isSelected()){
						selectedRes.add(checkBoxes[i].getText());
						//						System.out.println(checkBoxes[i].getText());
					}
				}

				writeBack(); //write back to table
			}
		});

		/**
		 * add button listener
		 */
		this.jbtAdd.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				String addName = jtfAdd.getText();
				if(addName.equals(""))
					Notifier.Reminder("请输入要添加的共享资源名");
				else{
					ShareResource sr = Model.addNewSR(addName);
					if(sr != null){
						addSuccess(sr);
					}
				}
			}
		});

	}


	/**
	 * write value back to table in main UI
	 */
	public void writeBack(){
		MainFrame.taskTable.setValueAt(convertToString(selectedRes), row, col);
		this.dispose();
	}


	/**
	 * add success, update share resource table and new 'ChooseResource'
	 */
	public void addSuccess(ShareResource sr){
		//add new share resource in table
		DefaultTableModel tablemodel = (DefaultTableModel) MainFrame.shareResourceTable.getModel();
		tablemodel.addRow(new String[] { sr.name, "", "" });

		//refresh resource chooser
		this.dispose();
		this.selectedRes.add(sr.name);
		writeBack();
		new ChooseResource(this.frame, this.selectedRes, this.row, this.col);
	}


	/**
	 * convert a list of string to string
	 * 
	 * @return string of elements, separated by delimiter
	 */
	public String convertToString(ArrayList<String> list){
		String result = "";
		String delimiter = ","; //set delimiter between to element

		if(list == null || list.isEmpty())
			;
		else{
			int i = 0;
			for (; i < list.size() - 1; i++){
				result += list.get(i) + delimiter;
			}
			result += list.get(i);
		}

		return result;
	}
}
