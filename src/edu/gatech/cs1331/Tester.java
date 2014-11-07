package edu.gatech.cs1331;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

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
	private Queue<TestWrapper> pending;
	
	public Tester(String className, AutoGraderJson agj) throws Exception {
		clazz = Class.forName(className);
		tests = new HashMap<>();
		List<Method> methods = Arrays.asList(clazz.getMethods());
		for(Method m : methods) {
			for(Annotation a : m.getAnnotations()) {
				if(before == null && a instanceof BeforeClass) {
					before = m;
				} else if(after == null && a instanceof AfterClass) {
					after = m;
				}
			}
		}

		pending = new ArrayDeque<>();
		List<TestJson> json = Arrays.asList(agj.getTests());
		for(TestJson tj : json) {
			for(Method m : methods) {
				if(m.getName().equals(tj.getName())) {
					for(Annotation a : m.getAnnotations()) {
						if(a instanceof Test) {
							TestWrapper tw = new TestWrapper(m, (Test) a, tj);
							tests.put(tj.getName(), tw);
							pending.add(tw);
						}
					}
				}
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
		HashMap<TestWrapper, Boolean> visited = new HashMap<TestWrapper, Boolean>() {
			public Boolean get(Object k) { return containsKey(k) ? super.get(k) : false; }
		};
		Stack<TestWrapper> stack = new Stack<>();
		for(TestWrapper t : tws) {
			stack.push(t);
			if(isCyclic(graph, t, visited, stack)) {
				throw new Exception("Please resolve cyclic dependencies");
			}
		}
	}
	
	private boolean isCyclic(HashMap<TestWrapper, LinkedList<TestWrapper>> graph, TestWrapper u,
							 HashMap<TestWrapper, Boolean> visited, Stack<TestWrapper> stack) {
		if(u != null && !visited.get(u)) {
			visited.put(u, true);
			stack.push(u);
			for(TestWrapper v : graph.get(u)) {
				if(!visited.get(v) && isCyclic(graph, v, visited, stack))
					return true;
				else if(stack.contains(v))
					return true;
			}
		}
		stack.pop();
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
					List<TestWrapper> dependencies = 
							Arrays.asList(next.annotation.depends()).stream()
								.map(s -> tests.get(s))
								.collect(Collectors.toList());
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
						pending.add(next);
					} else if(failed) {
						results.addFailedDependency(next.tj,
								failedDependencies.toArray(new TestJson[0]));
					} else {
						next.start(testObject);
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
	
	private class TestWrapper {
		
		private volatile Throwable throwableThrown;
		private volatile boolean complete, failed;
		private TestJson tj;
		private Method test;
		private Test annotation;
		
		public TestWrapper(Method test, Test an, TestJson tj) {
			this.test = test;
			this.tj = tj;
			this.annotation = an;
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
	}
}