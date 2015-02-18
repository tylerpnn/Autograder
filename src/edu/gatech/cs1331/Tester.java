package edu.gatech.cs1331;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.TimeoutException;

import edu.gatech.cs1331.annotations.After;
import edu.gatech.cs1331.annotations.AfterClass;
import edu.gatech.cs1331.annotations.Before;
import edu.gatech.cs1331.annotations.BeforeClass;
import edu.gatech.cs1331.annotations.Test;
import edu.gatech.cs1331.json.AutoGraderJson;
import edu.gatech.cs1331.json.TestJson;


public class Tester {

    private Class<?> clazz;
    private HashMap<String, TestWrapper> tests;
    private List<Method> before, after, beforeClass, afterClass;
    private HashMap<TestWrapper, LinkedList<TestWrapper>> depGraph;
    private Results results;
    private Queue<TestWrapper> pending;

    public Tester(String className, AutoGraderJson agj) throws Exception {
        File f = new File(className);
        ClassLoader cl = getClass().getClassLoader();
        if(f.getAbsoluteFile().getParent() != null) {
            URL[] uu = { f.getAbsoluteFile().getParentFile().toURI().toURL() };
            cl = new URLClassLoader(uu);
        }
        clazz = cl.loadClass(f.getName().replace(".class", ""));

        tests = new HashMap<>();
        before = new ArrayList<>();
        after = new ArrayList<>();
        beforeClass = new ArrayList<>();
        afterClass = new ArrayList<>();
        List<Method> methods = Arrays.asList(clazz.getDeclaredMethods());
        for(Method m : methods) {
            for(Annotation a : m.getAnnotations()) {
                if(a instanceof BeforeClass) {
                    beforeClass.add(m);
                } else if(a instanceof AfterClass) {
                    afterClass.add(m);
                } else if(a instanceof After) {
                    after.add(m);
                } else if(a instanceof Before) {
                    before.add(m);
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
        depGraph = new HashMap<>();
        for(TestWrapper t : tws) {
            depGraph.put(t, new LinkedList<>());
            for(String s : t.getDependencies()) {
                if(tests.get(s) != null) {
                    depGraph.get(t).add(tests.get(s));
                } else if(t.getDependencies().length > 1 && !s.equals("")){
                    throw new Exception(String.format("Test %s does not exist", s));
                }
            }
        }
        HashMap<TestWrapper, Boolean> visited = new HashMap<TestWrapper, Boolean>() {
            public Boolean get(Object k) { return containsKey(k) ? super.get(k) : false; }
        };
        Stack<TestWrapper> stack = new Stack<>();
        for(TestWrapper t : tws) {
            if(isCyclic(depGraph, t, visited, stack)) {
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
                if(!visited.get(v) && isCyclic(graph, v, visited, stack) ||
                        stack.contains(v)) {
                    return true;
                }
            }
            stack.pop();
        }
        return false;
    }

    public void startTests() {
        if(clazz == null) {
            System.err.println("Test class is null");
            System.exit(0);
        }

        try {
            Object testObject = clazz.newInstance();
            for(Method m : beforeClass) m.invoke(testObject);
            while(!pending.isEmpty()) {
                TestWrapper next = pending.poll();
                if(!next.getDependencies()[0].equals("")) {
                    List<TestWrapper> dependencies = depGraph.get(next);
                    ArrayList<TestJson> failedDependencies = new ArrayList<>();
                    boolean anyFailed = false, allComplete = true;
                    for(TestWrapper d : dependencies) {
                        if(d.failed) {
                            anyFailed = true;
                            failedDependencies.add(d.tj);
                        }
                        if(!d.complete) {
                        	allComplete = false;
                        }
                    }
                    if(anyFailed && allComplete) {
                        results.addFailedDependency(next.tj,
                                failedDependencies.toArray(new TestJson[0]));
                        next.failed = true;
                        next.complete = true;
                        continue;
                    } else if(!allComplete) {
                        pending.add(next);
                        continue;
                    }
                }
                if(!next.getIfFailed()[0].equals("")) {
                	String[] mustFail = next.getIfFailed();
                	boolean allFailed = true;
                	boolean allComplete = false;
                	for(String f : mustFail) {
                		TestWrapper t = tests.get(f);
                		allFailed &= t.failed;
                		allComplete |= t.complete;
                	}
                	if(!allFailed && !allComplete) {
                		pending.add(next);
                		continue;
                	} else if(!allFailed && allComplete) {
                		results.addDependencyNotFailed(next.tj);
                		continue;
                	}
                }
                for(Method m : before) m.invoke(testObject);
                next.start(testObject);
                for(Method m : after) m.invoke(testObject);
            }
            for(Method m : afterClass) m.invoke(testObject);
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
        	if(target instanceof AssertionErrorWithPoints) {
        		results.addFailedTest(tw.tj, target,
        				((AssertionErrorWithPoints) target).getPoints());
        	} else if(target instanceof AssertionError
                    || !(tw.annotation.expectedException().isInstance(target))) {
                results.addFailedTest(tw.tj, target);
            }
        } else if(t instanceof TimeoutException
                || t instanceof ExceptionExpectedException) {
            results.addFailedTest(tw.tj, t);
        }
    }

    private class TestWrapper {

        private volatile Throwable throwableThrown;
        private volatile boolean complete, failed, silent;
        private TestJson tj;
        private Method test;
        private Test annotation;
        private String[] dependencies, ifFailed;

        public TestWrapper(Method test, Test an, TestJson tj) {
            this.test = test;
            this.tj = tj;
            this.annotation = an;
            this.silent = tj.isSilent();
            this.dependencies = tj.getDependencies().split(",");
            this.ifFailed = tj.getIfFailed().split(",");
        }
        
        public boolean isSilent() {
        	return this.silent;
        }
        
        public String getName() {
        	return tj.getName();
        }
        
        public String[] getDependencies() {
        	return dependencies;
        }
        
        public String[] getIfFailed() {
        	return ifFailed;
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
            if(throwableThrown == null && !annotation.expectedException().equals(None.class)) {
            	this.failed = this.complete = true;
                Tester.this.handleThrowable(
                        new ExceptionExpectedException(annotation.expectedException()), this);
            } else if(throwableThrown != null) {
            	this.failed = this.complete = true;
                Tester.this.handleThrowable(throwableThrown, this);
            }
        }

        private void runTest(Object testObject) {
            try {
                test.invoke(testObject);
                results.addPassedTest(tj);
                complete = true;
            } catch(InvocationTargetException e) {
                throwableThrown = e;
            } catch(IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}
