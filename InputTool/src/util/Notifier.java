
package util;

import javax.swing.JOptionPane;


/**
 * class to handle all the notifications
 * 
 * @author zengke.cai
 * 
 */
public class Notifier{

	public static void ErrorPrompt(String info){
		JOptionPane.showMessageDialog(null, info, "Error", JOptionPane.ERROR_MESSAGE);
	}


	public static void Reminder(String info){
		JOptionPane.showMessageDialog(null, info, "Reminder", JOptionPane.WARNING_MESSAGE);
	}


	public static void Notification(String info){
		JOptionPane.showMessageDialog(null, info, "Notification", JOptionPane.INFORMATION_MESSAGE);
	}


	/**
	 * a delete confirm box, appear when user is deleting information from UI
	 * 
	 * @return user's choice, true for delete, false for cancel
	 */
	public static boolean DelConfirm(){
		int choice = JOptionPane.showConfirmDialog(null, "确认删除该行？", "Delete Comfirm", JOptionPane.YES_NO_OPTION);
		if(choice == JOptionPane.YES_OPTION){
			return true;
		}
		else{
			return false;
		}
	}


	/**
	 * program exit confirm box, appear when user closing the main window
	 * 
	 * @return user's choice, true for exit, false for cancel
	 */
	public static boolean ExitConfirm(){
		int choice = JOptionPane.showConfirmDialog(null, "退出前请确定模型信息已保存！\n确定退出程序？", "Exit Comfirm", JOptionPane.YES_NO_OPTION);
		if(choice == JOptionPane.YES_OPTION){
			return true;
		}
		else{
			return false;
		}
	}
}
