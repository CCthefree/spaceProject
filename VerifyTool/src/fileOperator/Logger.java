
package fileOperator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


public class Logger{

	private String fileName;
	private static String result = "";


	public Logger(String name) {
		this.fileName = name;
	}


	public void output(){

		try{
			File file = new File(this.fileName);
			file.createNewFile();
			PrintWriter output = new PrintWriter(file);
			output.print(Logger.result);
			output.close();
//			System.out.println(FileWriter.content);
		} catch (IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e){

			e.printStackTrace();
		}
	}


	public static void append(String con){
		Logger.result += con;
	}


	public void clearContent(){
		Logger.result = "";
	}
}
