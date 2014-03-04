package fileOperator;

import inputAnalysis.ModelInfoCheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import util.Lexer;
import util.Notifier;
import util.define;
import variableDefinition.ControlVariable;
import variableDefinition.Model;
import variableDefinition.Procedure;
import variableDefinition.Task;


public class ReadTaskDesc {
	public String fileName;
	private Map<String, String> taskDesc;
	
	public ReadTaskDesc(String fileName){
		this.fileName = fileName;
		taskDesc = new HashMap<String, String>();
		taskDesc.clear();
	}
	
	/**
	 * read task file
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
				//空白行，直接跳过
				if (line.equals("") || line.matches("[\\s]+"))
					continue;
				
				//找到大括号匹配的内容
				lines += line + "\n";
				for (int i = 0; i < line.length(); i++) {
					if (line.charAt(i) == '{')
						flag++;
					if (line.charAt(i) == '}')
						flag--;
				}
				
				if (flag == 0) {
					if (synaxCheck(lines) == define.syntaxError) {
						Notifier.ErrorPrompt("附加文件存在语法错误！");
						return;
					}
					lines = "";
				}
			
			}
			scanner.close();

			for (String key: this.taskDesc.keySet()) {
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
	 * @param content
	 * @return
	 */
	private int synaxCheck(String content) {
		ArrayList<String> token = Lexer.lexer(content);
		if (token.size() < 4)
			return define.syntaxError;
		if (!Lexer.isVariableName(token.get(0)) || !token.get(1).equals("(") || !token.get(2).equals(")"))
			return define.syntaxError;
		
		String desc = content.substring(content.indexOf("{")+1, content.lastIndexOf("}"));
		this.taskDesc.put(token.get(0), desc);
		return define.noError;
	}
	
	
	/**
	 * 分析某个添加的子过程定义，并作相应的处理
	 */
	public void analysis(String taskName, String desc){
		
		Task task = Model.getTask(taskName);
		if (task == null){	
			Notifier.ErrorPrompt("模型中不存在子过程: " + taskName);
		}
		else if(task.longUBD != -1){		
			Notifier.ErrorPrompt("子过程" + taskName + " 已有定义！");
		}
		else if(task.proc != null && !task.proc.description.equals(desc)){	
			Notifier.ErrorPrompt("子过程" + taskName + " 读入的处理程序与原有程序冲突！");
		}
		else{
			Procedure preProc = task.proc;
			Procedure proc = new Procedure();
			task.setProc(proc);
			proc.setValue(taskName, desc);
			
			if(proc.statement.errorIndex == define.syntaxError){
				task.setProc(preProc);
				Notifier.ErrorPrompt(proc.statement.errorInfo);
			}
			//添加的子过程定义有语义错误 （未定义的子过程、控制变量或共享资源）
			else if(proc.statement.errorIndex == define.semanticError){
				if(!proc.undefineTasks.isEmpty()){
					for(String undefineTask : proc.undefineTasks){
						Task newTask = Model.addNewTask(undefineTask);
						proc.containedTasks.add(newTask);
					}
				}
				
				if(!proc.undefineSRs.isEmpty()){
					for(String undefineSR : proc.undefineSRs)
						Model.addNewSR(undefineSR);
				}
				
				if(!proc.undefineCVs.isEmpty()){
					for(String undefineCV : proc.undefineCVs){
						ControlVariable cv = Model.addNewCV(undefineCV);
						proc.containedCVs.add(cv);
					}
				}
			}
		}
	}

}
