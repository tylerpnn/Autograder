package edu.gatech.cs1331;

public class AssertionErrorWithPoints extends AssertionError {

	private int points;
	
	public AssertionErrorWithPoints(int points) {
		this.points = points;
	}
	
	public AssertionErrorWithPoints(String m, int points) {
		super(m);
		this.points = points;
	}
	
	public int getPoints() {
		return points;
	}
}
