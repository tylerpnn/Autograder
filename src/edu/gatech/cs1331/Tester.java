package edu.gatech.cs1331;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import edu.gatech.cs1331.Test.None;
import edu.gatech.cs1331.json.AutoGraderJson;
import edu.gatech.cs1331.json.TestJson;

public class Tester {

	private Class<?> clazz;
	private Map<String, Method> tests;
	private Map<Method, Annotation> annotations;
	private Method before, after;
	private Results results;
	
	public Tester(String className) {
		
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		tests = new HashMap<>();
		annotations = new HashMap<>();
		
		for(Method m : clazz.getMethods()) {
			for(Annotation a : m.getAnnotations()) {
				if(a instanceof Test) {
					tests.put(m.getName(), m);
					annotations.put(m, a);
				} else if(before == null && a instanceof BeforeClass) {
					before = m;
				} else if(after == null && a instanceof AfterClass) {
					after = m;
				}
			}
		}
		results = new Results(tests.keySet().size());
	}
	
	public void startTests(AutoGraderJson agj) {
		if(clazz == null) {
			System.err.println("Test class is null");
			System.exit(0);
		}
		
		try {
			Object testObject = clazz.newInstance();
			if(before != null) before.invoke(testObject);
			for(TestJson tj : agj.getTests()) {
				Method test = tests.get(tj.getName());
				Test an = (Test) annotations.get(test);
				if(test != null && annotations.containsKey(test)) {
					TestWrapper tw = new TestWrapper(testObject, tj, test, an);
					tw.start();
				}
			}
			if(after != null) after.invoke(testObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Results getResults() {
		return results;
	}
	
	private void handleThrowable(Throwable t, TestWrapper tw) {
		Throwable target = t.getCause();
		if(target != null) {
			if(target instanceof AssertionError
					|| !(tw.annotation.expected().isInstance(target))) {
				results.addFailedTest(tw.tj, target);
			}
		} else if(t instanceof TimeoutException
				|| t instanceof ExceptionExpectedException) {
			results.addFailedTest(tw.tj, t);
		}
	}
	
	private class TestWrapper {
		
		private volatile Throwable throwableThrown;
		private volatile boolean complete;
		private Object testObject;
		private TestJson tj;
		private Method test;
		private Test annotation;
		
		public TestWrapper(Object testObject, TestJson tj, Method test, Test an) {
			this.testObject = testObject;
			this.test = test;
			this.tj = tj;
			this.annotation = an;
		}
		
		public void start() {
			if(annotation.timeout() > 0l) {
				Thread t = new Thread(() -> runTest());
				t.setDaemon(true);
				t.start();
				long time = System.currentTimeMillis();
				while(System.currentTimeMillis() - time < annotation.timeout());
				if(throwableThrown == null && !complete) {
					throwableThrown = new TimeoutException(
							"Operation timed out; took more than " + annotation.timeout() + " millis");
					t.interrupt();
				}
			} else {
				runTest();
			}
			if(throwableThrown == null && !annotation.expected().equals(None.class)) {
				Tester.this.handleThrowable(new ExceptionExpectedException(annotation.expected()), this);
			}else if(throwableThrown != null) {
				Tester.this.handleThrowable(throwableThrown, this);
			}
		}
		
		private void runTest() {
			try {
				test.invoke(testObject);
				complete = true;
			} catch(InvocationTargetException e) {
				throwableThrown = e;
			} catch(IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
}