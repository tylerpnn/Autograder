package edu.gatech.cs1331;

import edu.gatech.cs1331.json.AutoGraderJson;
import edu.gatech.cs1331.json.ClassJson;
import edu.gatech.cs1331.json.ClassJson.MethodJson;

public class Application {
	
	private static String jsonFileName;
	private static String testClassName;
	
	public static void main(String... args) {
		
		for(int i = 0; i < args.length; i++) {
			if(args.length - i - 1 > 0) {
				if(args[i].equals("-j")) {
					jsonFileName = args[i+1];
				} else if(args[i].equals("-t")) {
					testClassName = args[i+1];
				} else if(args[i].equals("-h")) {
					printHelp();
					System.exit(0);
				}
			}
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
		t.getResults().printResult(System.out);
	}
	
	public static void printHelp() {
		System.out.printf("%s\n%s\n%s\n%s\n",
				"Autograder help:",
				"\t-j\tJson filename",
				"\t-t\tTest Class Name",
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
