package edu.gatech.cs1331;

import edu.gatech.cs1331.json.AutoGraderJson;
import edu.gatech.cs1331.json.ClassJson;
import edu.gatech.cs1331.json.ClassJson.MethodJson;

public class Application {
	
	public static void main(String ... args) {
		if(args.length == 0) {
			return;
		}
		
		testParse(args[0]);
	}
	
	public static void testParse(String filename) {
		AutoGraderJson ag = AutoGraderJson.buildClassList(filename);
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
