package edu.gatech.cs1331;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import edu.gatech.cs1331.annotations.AfterClass;
import edu.gatech.cs1331.annotations.BeforeClass;
import edu.gatech.cs1331.annotations.Test;
import edu.gatech.cs1331.json.AutoGraderJson;
import edu.gatech.cs1331.json.TestJson;

public class Tester {

	private Class<?> clazz;
	private Map<String, Method> methods;
	private Map<Method, Annotation> annotations;
	private Method before, after;
	
	private StringBuilder comments;
	private int lostPoints;
	
	public Tester(String className) {
		
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		methods = new HashMap<>();
		annotations = new HashMap<>();
		
		for(Method m : clazz.getMethods()) {
			for(Annotation a : m.getAnnotations()) {
				if(a instanceof Test) {
					methods.put(m.getName(), m);
					annotations.put(m, a);
				} else if(before == null && a instanceof BeforeClass) {
					before = m;
				} else if(after == null && a instanceof AfterClass) {
					after = m;
				}
			}
		}
		
		comments = new StringBuilder();
	}
	
	private void failTest(TestJson test) {
		comments.append(test.getComment() + " (-" + test.getPoints() + ")\n");
		lostPoints += test.getPoints();
	}
	
	public String toString() {
		return String.format("%s\nPoints Lost: %d",
				comments.toString(), lostPoints);
	}
	
	public void runTests(AutoGraderJson agj) throws InstantiationException,
													IllegalAccessException {
		Object testObject = clazz.newInstance();
		
		if(before != null) {
			try {
				before.invoke(testObject);
			} catch (IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		int testnum = 0;
		for(TestJson tj : agj.getTests()) {
			System.out.printf("%d) %s:\t", testnum++, tj.getName());
			Method test = methods.get(tj.getName());
			if(test != null && annotations.containsKey(test)) {
				try {
					test.invoke(testObject);
					System.out.printf("%s\n", "Pass");
				} catch(InvocationTargetException e) {
					Throwable target = e.getCause();
					Test a = (Test) annotations.get(test);
					if(target != null) {
						if(target instanceof AssertionError) {
							failTest(tj);
						} else if(!(a.expected().isInstance(target))) {
							failTest(tj);
						}
						target.printStackTrace();
					}
				}
			}
		}
		
		if(after != null) {
			try {
				after.invoke(testObject);
			} catch (IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}