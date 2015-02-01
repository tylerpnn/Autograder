package edu.gatech.cs1331;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.gatech.cs1331.json.TestJson;

public class Results {

	private List<CompletedTestWrapper> tests;
	private HashMap<TestJson, TestJson[]> failedDependencies;
	private int totalTests;
	private int lostPoints;
	private int extraPoints;
	private int totalPoints;
	
	public Results(int totalTests) {
		tests = new ArrayList<>();
		failedDependencies = new HashMap<>();
		this.totalTests = totalTests;
	}
	
	public void addFailedTest(TestJson test, Throwable throwable) {
		tests.add(new FailedTestWrapper(test, throwable));
	}

	public void addPassedTest(TestJson test) {
		tests.add(new CompletedTestWrapper(test));
	}
	
	public void addFailedDependency(TestJson test, TestJson[] dependencies) {
		failedDependencies.put(test, dependencies);
	}
	
	public void printResult(OutputStream out, boolean concise) {
		PrintWriter p = new PrintWriter(out);
		StringBuilder comments = new StringBuilder();
		for(int i = 0; i < tests.size(); i++) {
			CompletedTestWrapper test = tests.get(i);
			if(test.failed() || test.isExtraCredit()) {
				if(!concise) p.printf("%d) %s", i, test);
				comments.append(String.format("(%s%d) %s\n",
					test.isExtraCredit() ? "+" : "-", test.getPoints(), test.getComment()));
			}
			totalPoints += test.getPoints();
		} 
		if(failedDependencies.keySet().size() > 0) {
			p.println("\nThe following tests did not run due to failed dependencies:");
			for(TestJson t : failedDependencies.keySet()) {
				p.printf("\t%s, failed dependencies: %s\n",
						t.getName(), Arrays.toString(failedDependencies.get(t)));
				
			}
		}
		
		lostPoints += tests.stream().filter(CompletedTestWrapper::failed)
			.mapToInt(CompletedTestWrapper::getPoints).sum();

		extraPoints += tests.stream().filter(CompletedTestWrapper::isExtraCredit)
			.mapToInt(CompletedTestWrapper::getPoints).sum();

		totalPoints -= extraPoints;


		p.printf("\nTOTAL: %d, RAN: %d, FAILURES: %d\n",
				totalTests, totalTests - failedDependencies.size(), tests.stream()
				.filter(CompletedTestWrapper::failed).count());
		p.printf("\n%s\n", comments.toString());

		p.printf("Points Lost: %d\n", lostPoints);
		p.printf("Extra Credit Points: %d\n", extraPoints); 
		p.printf("Score: %d/%d\n", totalPoints - lostPoints + extraPoints, totalPoints);
		p.close();
	}
	
	public void printResult() {
		printResult(System.err, false);
	}
	
	private class CompletedTestWrapper {
		private TestJson test;
		private boolean failed;
		
		public CompletedTestWrapper(TestJson t) {
			this.test = t;
		}

		protected CompletedTestWrapper(TestJson t, boolean f) {
			this(t);
			this.failed = f;
		}
		
		public int getPoints() {
			return test.getPoints(); 
		}
		
		public String getComment() {
			return test.getComment();
		}
		
		public String toString() {
			return test.getName();
		}

		public boolean failed() {
			return failed;
		}
		
		public boolean isExtraCredit() {
			return test.isExtraCredit();
		}
	}

	private class FailedTestWrapper extends CompletedTestWrapper {
		private Throwable throwable;
		
		public FailedTestWrapper(TestJson t, Throwable th) {
			super(t, true);
			throwable = th;
		}

		public String getComment() {
			return super.getComment().equals("") ?
					throwable.getMessage() : super.getComment();
		}

		public String toString() {
			String res = String.format("%s:\n%s\n",
					super.toString(), throwable.toString());
			for(StackTraceElement ste : throwable.getStackTrace()) {
				res += "\tat " + ste + "\n";
			}
			return res;
		}
	}
}
