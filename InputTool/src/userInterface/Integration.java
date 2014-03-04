package userInterface;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import fileOperator.XMLFileWriter;

/**
 * 
 * @author zengke.cai
 *
 */
public class Integration {
	private final String BMCPath;
	private final String displayPath;
	private final String modelFile;
	private final String resultFile;
	private final int K;
	
	private String cmdBMC;
	private String cmdDisplay;
	
	
	public Integration(){
		String fileLoc = System.getProperty("user.dir");
		System.out.println(fileLoc);
		
		BMCPath = fileLoc + "\\spaceBMC.jar";
		displayPath = fileLoc + "\\display.jar";
		modelFile = fileLoc + "\\model.xml";
		resultFile = fileLoc + "\\result.xml";
		K = 20;
		
		cmdBMC = "java -jar " + BMCPath + " " + modelFile + " " + resultFile + " " + K;
		cmdDisplay = "java -jar " + displayPath + " " + resultFile;
		
		System.out.println(cmdBMC);
//		System.out.println(cmdDisplay);

	}
	
	/**
	 * call runtime to execute BMC command
	 * @return
	 */
	public boolean execBMC(){
		boolean result = true;
		XMLFileWriter writer = new XMLFileWriter(modelFile);
		writer.save();
		
		Runtime run = Runtime.getRuntime();
		try {
			Process p = run.exec(cmdBMC);
			
			/**必须要处理外部命令的标准输入输出**/
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());     
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));     
            String lineStr;     
            while ((lineStr = inBr.readLine()) != null)     
                System.out.println(lineStr);
            
            //当前程序等待外部命令执行完后再继续执行
			if(p.waitFor() != 0){
				result = false;		//非正常退出
			}
		}
		catch (IOException e) {
			System.out.println("IOException when executing BMC cmd");
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			System.out.println("InterruptedException when executing BMC cmd");
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/**
	 * call runtime to execute display command
	 * @return
	 */
	public boolean execDisplay(){
		boolean result = true;
		
		Runtime run = Runtime.getRuntime();
		try {
			Process p = run.exec(cmdDisplay);
			
			/**必须要处理外部命令的标准输入输出**/
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());     
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));     
            String lineStr;     
            while ((lineStr = inBr.readLine()) != null)     
                System.out.println(lineStr);
			
			if(p.waitFor() != 0){
				result = false;
			}
		}
		catch (IOException e) {
			System.out.println("IOException when executing display cmd");
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			System.out.println("InterruptedException when executing display cmd");
			e.printStackTrace();
		}
		
		return result;
	}
}
