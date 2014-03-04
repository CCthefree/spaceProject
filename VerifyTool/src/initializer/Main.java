package initializer;

import algorithm.Verification;
import algorithm.VerificationAll;

import com.microsoft.z3.Z3Exception;

import fileOperator.ITATest;
import fileOperator.Logger;
import fileOperator.ResultWriter;
import fileOperator.XMLFileReader;

public class Main {

	private static int bound;

	private static String fileLoc;

	private static String xmlFileName;

	private static String itaFileName;

	private static String ppFileName;

	private static String logFileName;

	private static String resultFileName;


	public static void main(String[] args) throws Z3Exception {
		bound = 20;
//		 fileLoc = System.getProperty("user.dir");
		fileLoc = "d:\\bmc";

		xmlFileName = fileLoc + "\\model.xml";
		// xmlFileName = ".\\input\\case1.xml";
		resultFileName = fileLoc + "\\result.xml";
		itaFileName = fileLoc + "\\itatest.txt";
		ppFileName = fileLoc + "\\pptest.txt";

		String inputFile = (args.length == 0) ? xmlFileName : args[0];
		String resultFile = (args.length == 0) ? resultFileName : args[1];
		int K = (args.length == 0) ? bound : Integer.parseInt(args[2]);
		logFileName = fileLoc + "\\log(K=" + K + ").txt";

		Model model = new Model();

		XMLFileReader xmlReader = new XMLFileReader(inputFile);
		// 读取文件失败，退出检验
		if (xmlReader.initModel(model) == false)
			System.exit(1);

		Model.getSRArray().initialize(); // initialize nRead and nWrite of SR
											// array

		// output log file
		Logger logger = new Logger(logFileName);
		logger.clearContent();

		new Verification(K);
		logger.output();

		// output checking result
		ResultWriter result = new ResultWriter(resultFile);
		result.output();
	}

}
