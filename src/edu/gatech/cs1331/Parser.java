package edu.gatech.cs1331;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.gatech.cs1331.annotations.Test;
import edu.gatech.cs1331.json.AutoGraderJson;
import edu.gatech.cs1331.json.TestJson;

public class Parser {
	
	private static Class<?> clazz;
	
	public static void main(String... args) {
		if(args.length < 2) {
			System.out.println("Please provide a java test class filename and output json filename");
			System.exit(0);
		}
		
		try {
			clazz = Class.forName(args[0]);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		AutoGraderJson agj = new AutoGraderJson();
		List<TestJson> tests = new ArrayList<>();
		for(Method m : clazz.getMethods()) {
			for(Annotation a : m.getAnnotations()) {
				if(a instanceof Test) {
					TestJson tj = new TestJson();
                    Test t = (Test)a;
					tj.setName(m.getName());
					tj.setComment(t.comment());
					tj.setPoints(t.points());
                    tj.setExtraCredit(t.extraCredit());
					tests.add(tj);
				}
			}
		}
		agj.setTests(tests.toArray(new TestJson[0]));
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(agj);
		try(PrintWriter p = new PrintWriter(new File(args[1]))) {
			p.print(json);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
