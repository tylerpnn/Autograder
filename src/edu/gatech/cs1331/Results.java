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
	private List<TestJson> dependenciesNotFailed;
	private int totalTests;
	private int lostPoints;
	
	public Results(int totalTests) {
		tests = new ArrayList<>();
		failedDependencies = new HashMap<>();
		dependenciesNotFailed = new ArrayList<>();
		this.totalTests = totalTests;
	}
	
	public void addFailedTest(TestJson test, Throwable throwable) {
		tests.add(new FailedTestWrapper(test, throwable));
	}
	
	public void addFailedTest(TestJson test, Throwable throwable, int points) {
		tests.add(new FailedTestWrapper(test, throwable, points));
	}

	public void addPassedTest(TestJson test) {
		tests.add(new CompletedTestWrapper(test));
	}
	
	public void addFailedDependency(TestJson test, TestJson[] dependencies) {
		failedDependencies.put(test, dependencies);
	}
	
	public void addDependencyNotFailed(TestJson test) {
		dependenciesNotFailed.add(test);
	}
	
	public void printResult(OutputStream out, boolean concise) {
		PrintWriter p = new PrintWriter(out);
		StringBuilder comments = new StringBuilder();
		for(int i = 0; i < tests.size(); i++) {
			CompletedTestWrapper test = tests.get(i);
			if(test.failed() && !test.isSilent()) {
				if(!concise) p.printf("%d) %s", i, test);
				if(test.getComment() != null && test.getPoints() != 0) {
					comments.append(String.format("(-%d) %s\n", 
							test.getPoints(), test.getComment()));
					lostPoints += test.getPoints();
				}
			} else if(test.isExtraCredit()) {
				if(test.getComment() != null &&
						!test.getComment().equals("") && test.getPoints() != 0) {
					comments.append(String.format("(+%d) %s\n", 
							test.getPoints(), test.getComment()));
				}
				lostPoints -= test.getPoints();
			}
		}
		if(failedDependencies.keySet().size() > 0) {
			p.println("\nThe following tests did not run due to failed dependencies:");
			for(TestJson t : failedDependencies.keySet()) {
				p.printf("\t%s, failed dependencies: %s\n",
						t.getName(), Arrays.toString(failedDependencies.get(t)));
				
			}
		}
		if(dependenciesNotFailed.size() > 0) {
			p.print("\nThe following tests didn't run because they required another test to fail:\n\t");
			for(TestJson tj : dependenciesNotFailed) {
				p.print(tj.getName() + ", ");
			}
			p.println();
		}

		p.printf("\nTOTAL: %d, RAN: %d, FAILURES: %d\n",
				totalTests, totalTests - failedDependencies.size() - dependenciesNotFailed.size(),
				tests.stream().filter(CompletedTestWrapper::failed).count());
		p.printf("Points Lost: %d\n", lostPoints);
		p.printf("\n%s", comments.toString());

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
		
		public boolean isSilent() {
			return test.isSilent();
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
		
		public TestJson getJson() {
			return test;
		}
	}

	private class FailedTestWrapper extends CompletedTestWrapper {
		private Throwable throwable;
		private boolean pointsOverride;
		private int points;
		
		public FailedTestWrapper(TestJson t, Throwable th) {
			super(t, true);
			throwable = th;
		}
		
		public FailedTestWrapper(TestJson t, Throwable th, int points) {
			this(t, th);
			this.points = points;
			pointsOverride = true;
		}
		
		public int getPoints() {
			return (pointsOverride) ? points : super.getPoints();
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
