package edu.gatech.cs1331;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.gatech.cs1331.json.AutoGraderJson;
import edu.gatech.cs1331.json.ClassJson;
import edu.gatech.cs1331.json.ClassJson.MethodJson;

public class Application {
	
	private static String jsonFileName;
	private static String testClassName;
	private static String outputFileName;
	private static boolean concise;
	
	public static void main(String... args) {
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("-h")) {
				printHelp();
				System.exit(0);
			} else if(args.length - i > 1) {
				if(args[i].equals("-j")) {
					jsonFileName = args[i+1];
				} else if(args[i].equals("-t")) {
					testClassName = args[i+1];
				} else if(args[i].equals("-o")) {
					outputFileName = args[i+1];
				}
			}
			concise |= args[i].equals("-c");
		}
		
		if(jsonFileName == null || testClassName == null) {
			printHelp();
			System.exit(0);
		}
		
		Tester t = null;
		try {
			AutoGraderJson agj = AutoGraderJson.buildClassList(jsonFileName);
			t = new Tester(testClassName, agj);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		t.startTests();
		if(outputFileName != null) {
			try(FileOutputStream fos = new FileOutputStream(new File(outputFileName))) {
				t.getResults().printResult(fos, concise);
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else {
			t.getResults().printResult(System.err, concise);
		}
	}
	
	public static void printHelp() {
		System.out.printf(new String(new char[6]).replace("\0", "%s\n"),
				"Autograder help:",
				"\t-j\tJson filename",
				"\t-t\tTest Class Name",
				"\t-o\tOutput filename",
				"\t-c\tExclude test stack traces",
				"\t-h\tPrint this help");
	}
	
	public static void testParse(String filename) {
		AutoGraderJson ag = null;
		try {
			ag = AutoGraderJson.buildClassList(jsonFileName);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		for(ClassJson c : ag.getClasses()) {
			System.out.printf("%s:\n\t", c.getClassName());
			if(c.getMethods() == null) {
				System.out.println();
				continue;
			}
			for(MethodJson m : c.getMethods()) {
				System.out.printf("%s %s%s %s(",
						m.getVisibility(), (m.getIsStatic()) ? "static " : "",
						m.getReturnType(), m.getName());
				
				String[] params = m.getParams();
				for(int i=0; i < params.length-1; i++) {
					System.out.printf("%s, ", params[i]);
				}
				System.out.printf("%s)\n\t", params[params.length-1]);
			}
			System.out.println();
		}
	}
}
