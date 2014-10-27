package edu.gatech.cs1331;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

import edu.gatech.cs1331.annotations.AfterClass;
import edu.gatech.cs1331.annotations.BeforeClass;
import edu.gatech.cs1331.annotations.Test;
import edu.gatech.cs1331.json.AutoGraderJson;
import edu.gatech.cs1331.json.TestJson;

public class Tester {

	private Class<?> clazz;
	private HashMap<String, TestWrapper> tests;
	private Method before, after;
	private Results results;
	Queue<TestWrapper> pending;
	
	public Tester(String className, AutoGraderJson agj) throws Exception {
		clazz = Class.forName(className);
		tests = new HashMap<>();		
		for(Method m : clazz.getMethods()) {
			for(Annotation a : m.getAnnotations()) {
				if(a instanceof Test) {
					TestWrapper tw = new TestWrapper(m, (Test) a);
					tests.put(m.getName(), tw);
				} else if(before == null && a instanceof BeforeClass) {
					before = m;
				} else if(after == null && a instanceof AfterClass) {
					after = m;
				}
			}
		}
		
		pending = new PriorityQueue<>();
		for(TestJson tj : agj.getTests()) {
			TestWrapper tw = tests.get(tj.getName());
			if(tw != null) {
				tw.tj = tj;
				pending.add(tw);
			}
		}
		
		checkDependencies(tests.values().toArray(new TestWrapper[0]));		
		results = new Results(tests.keySet().size());
	}
	
	private void checkDependencies(TestWrapper[] tws) throws Exception {
		HashMap<TestWrapper, LinkedList<TestWrapper>> graph = new HashMap<>();
		for(TestWrapper t : tws) {
			graph.put(t, new LinkedList<>());
			for(String s : t.annotation.depends()) {
				graph.get(t).add(tests.get(s));
			}
		}
		class MyHashMap extends HashMap<TestWrapper, Boolean> {
			public Boolean get(Object k) { return containsKey(k) ? super.get(k) : false; }
		}
		HashMap<TestWrapper, Boolean> visited = new MyHashMap();
		HashMap<TestWrapper, Boolean> stack = new MyHashMap();
		for(TestWrapper t : tws) {
			if(isCyclic(graph, t, visited, stack))
				throw new Exception("Please resolve cyclic dependencies");
		}
	}
	
	private boolean isCyclic(HashMap<TestWrapper, LinkedList<TestWrapper>> graph, TestWrapper t,
					HashMap<TestWrapper, Boolean> visited, HashMap<TestWrapper, Boolean> stack) {
		if(!visited.get(t)) {
			visited.put(t, true);
			stack.put(t, true);
			for(TestWrapper tw : graph.get(t)) {
				if(!visited.get(tw) && isCyclic(graph, t, visited, stack))
					return true;
				else if(stack.get(t))
					return true;
			}
		}
		stack.put(t, false);
		return false;
	}
	
	public void startTests() {
		if(clazz == null) {
			System.err.println("Test class is null");
			System.exit(0);
		}
		
		try {
			Object testObject = clazz.newInstance();
			if(before != null) before.invoke(testObject);
			while(!pending.isEmpty()) {
				TestWrapper next = pending.poll();
				if(!next.annotation.depends()[0].equals("")) {
					TestWrapper[] dependencies = new TestWrapper[next.annotation.depends().length];
					for(int i=0; i < dependencies.length; i++) {
						dependencies[i] = tests.get(next.annotation.depends()[i]);
					}
					ArrayList<TestJson> failedDependencies = new ArrayList<>();
					boolean failed = false, complete = true;
					for(TestWrapper d : dependencies) {
						if(d.failed) {
							failed = true;
							failedDependencies.add(d.tj);
						}
						complete = d.complete || complete;
					}
					if(!complete) {
						next.time = System.currentTimeMillis();
						pending.add(next);
					} else if(failed) {
						results.addFailedDependency(next.tj, failedDependencies.toArray(new TestJson[0]));
					}
				} else {
					next.start(testObject);
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
				tw.failed = true;
			}
		} else if(t instanceof TimeoutException
				|| t instanceof ExceptionExpectedException) {
			results.addFailedTest(tw.tj, t);
			tw.failed = true;
		}
	}
	
	private class TestWrapper implements Comparable<TestWrapper> {
		
		private volatile Throwable throwableThrown;
		private volatile boolean complete, failed;
		private TestJson tj;
		private Method test;
		private Test annotation;
		private long time;
		
		public TestWrapper(TestJson tj, Method test, Test an) {
			this.test = test;
			this.tj = tj;
			this.annotation = an;
		}
		
		public TestWrapper(Method test, Test an) {
			this(null, test, an);
		}
		
		public void start(Object testObject) {
			if(annotation.timeout() > 0l) {
				Thread t = new Thread(() -> runTest(testObject));
				t.setDaemon(true);
				t.start();
				long time = System.currentTimeMillis();
				while(System.currentTimeMillis() - time < annotation.timeout() && !complete);
				if(throwableThrown == null && !complete) {
					throwableThrown = new TimeoutException(
							"Operation timed out; took more than "
							+ annotation.timeout() + " millis");
					t.interrupt();
				}
			} else {
				runTest(testObject);
			}
			if(throwableThrown == null && !annotation.expected().equals(None.class)) {
				Tester.this.handleThrowable(
						new ExceptionExpectedException(annotation.expected()), this);
			} else if(throwableThrown != null) {
				Tester.this.handleThrowable(throwableThrown, this);
			}
		}
		
		private void runTest(Object testObject) {
			try {
				test.invoke(testObject);
				complete = true;
			} catch(InvocationTargetException e) {
				throwableThrown = e;
			} catch(IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		@Override
		public int compareTo(TestWrapper o) {
			return (int) (time - o.time);
		}
	}
}