package edu.gatech.cs1331;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.cs1331.json.TestJson;

public class Results {

	private List<FailedTestWrapper> failedTests;
	private int totalTests;
	private int lostPoints;
	
	public Results(int totalTests) {
		failedTests = new ArrayList<>();
		this.totalTests = totalTests;
	}
	
	public void addFailedTest(TestJson test, Throwable throwable) {
		failedTests.add(new FailedTestWrapper(test, throwable));
	}
	
	public void printResult(PrintStream s) {
		StringBuilder comments = new StringBuilder();
		for(int i = 0; i < failedTests.size(); i++) {
			FailedTestWrapper fail = failedTests.get(i);
			s.printf("%d) %s", i, fail);
			comments.append(String.format("%s (-%d)\n",
					fail.getComment(), fail.getPoints()));
			lostPoints += fail.getPoints();
		}
		s.printf("\nTOTAL: %d, FAILURES: %d\n", totalTests, failedTests.size());
		s.printf("\n%s\n", comments.toString());
		s.printf("Points Lost: %d", lostPoints);
	}
	
	public void printResult() {
		printResult(System.err);
	}
	
	private class FailedTestWrapper {
		private TestJson test;
		private Throwable throwable;
		
		public FailedTestWrapper(TestJson t, Throwable th) {
			test = t;
			throwable = th;
		}
		
		public int getPoints() {
			return test.getPoints(); 
		}
		
		public String getComment() {
			return test.getComment();
		}
		
		public String toString() {
			String res = String.format("%s:\n%s\n",
					test.getName(), throwable.toString());
			for(StackTraceElement ste : throwable.getStackTrace()) {
				res += "\tat " + ste + "\n";
			}
			return res;
		}
		
	}
}
