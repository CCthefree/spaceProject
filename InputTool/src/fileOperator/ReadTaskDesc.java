package fileOperator;

import inputAnalysis.ModelInfoCheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.table.DefaultTableModel;

import userInterface.MainFrame;
import util.Lexer;
import util.Notifier;
import util.define;
import variableDefinition.ControlVariable;
import variableDefinition.Model;
import variableDefinition.Procedure;
import variableDefinition.ShareResource;
import variableDefinition.Task;


public class ReadTaskDesc {

	public String fileName;
	private Map<String, String> taskDesc;


	public ReadTaskDesc(String fileName) {
		this.fileName = fileName;
		taskDesc = new HashMap<String, String>();
		taskDesc.clear();
	}


	/**
	 * read task file
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	public void readFile() {

		try {
			File file = new File(this.fileName);
			Scanner scanner = new Scanner(file);

			int flag = 0;
			String lines = "";
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				// 空白行，直接跳过
				if (line.equals("") || line.matches("[\\s]+"))
					continue;

				// 找到大括号匹配的内容
				lines += line + "\n";
				for (int i = 0; i < line.length(); i++) {
					if (line.charAt(i) == '{')
						flag++;
					if (line.charAt(i) == '}')
						flag--;
				}

				if (flag == 0) {
					//对一段完整的内容作基本语法检查，通过后存入哈希表
					if (synaxCheck(lines) == define.syntaxError) {
						Notifier.ErrorPrompt("附加文件存在语法错误！");
						return;
					}
					lines = "";
				}

			}
			scanner.close();

			//文件读取完毕，从哈希表中取出内容逐一分析
			for (String key : this.taskDesc.keySet()) {
				analysis(key, this.taskDesc.get(key));
			}

			ModelInfoCheck.totalAnalysis();
		}
		catch (FileNotFoundException e) {
			Notifier.ErrorPrompt("附加文件不存在！");
			e.printStackTrace();
		}
	}


	/**
	 * 语法分析
	 */
	private int synaxCheck(String content) {
		ArrayList<String> token = Lexer.lexer(content);
		if (token.size() < 4)
			return define.syntaxError;
		if (!Lexer.isVariableName(token.get(0)) || !token.get(1).equals("(")
				|| !token.get(2).equals(")"))
			return define.syntaxError;

		String desc = content.substring(content.indexOf("{") + 1, content.lastIndexOf("}"));
		this.taskDesc.put(token.get(0), desc);
		return define.noError;
	}


	/**
	 * 分析某个添加的子过程定义，并作相应的处理
	 */
	public void analysis(String taskName, String desc) {

		Task task = Model.getTask(taskName);
		if (task == null) {
			Notifier.ErrorPrompt("模型中不存在子过程: " + taskName);
		}
		else if (task.longUBD != -1) {
			Notifier.ErrorPrompt("子过程" + taskName + " 已有定义！");
		}
		else if (task.proc != null && !task.proc.description.equals(desc)) {
			Notifier.ErrorPrompt("子过程" + taskName + " 读入的处理程序与原有程序冲突！");
		}
		else {
			Procedure preProc = task.proc;
			Procedure proc = new Procedure();
			task.setProc(proc);
			proc.setValue(taskName, desc);
			
			// 添加的子过程定义有语法错误，恢复子过程原有定义
			if (proc.statement.errorIndex == define.syntaxError) {
				task.setProc(preProc);
				Notifier.ErrorPrompt(proc.statement.errorInfo);
			}
			// 添加的子过程定义有语义错误 （未定义的子过程、控制变量或共享资源）
			else if (proc.statement.errorIndex == define.semanticError) {
				//添加未定义的子过程
				if (proc.undefineTasks != null) {
					for (String undefineTask : proc.undefineTasks) {
						Task newTask = Model.addNewTask(undefineTask);
						proc.addTask(newTask);
					}
				}

				//添加未定义的读/写资源
				if (proc.undefineRSRs != null) {
					for (String undefineSR : proc.undefineRSRs) {
						ShareResource sr = Model.addNewSR(undefineSR);
						((DefaultTableModel) MainFrame.srTable.getModel()).addRow(ShareResource
								.getContent(sr));
						proc.addReadSR(undefineSR);
					}
				}
				if (proc.undefineWSRs != null) {
					for (String undefineSR : proc.undefineWSRs) {
						ShareResource sr = Model.addNewSR(undefineSR);
						((DefaultTableModel) MainFrame.srTable.getModel()).addRow(ShareResource
								.getContent(sr));
						proc.addWriteSR(undefineSR);
					}
				}

				//添加未定义的控制变量
				if (proc.undefineCVs != null) {
					for (String undefineCV : proc.undefineCVs) {
						ControlVariable cv = Model.addNewCV(undefineCV);
						((DefaultTableModel) MainFrame.cvTable.getModel()).addRow(ControlVariable
								.getContent(cv));
						proc.addCV(cv);
					}
				}
			}
		}
	}

}
