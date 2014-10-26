package edu.gatech.cs1331;

public class ExceptionExpectedException extends Exception {

	public ExceptionExpectedException(Class<? extends Throwable> expected) {
		super("Expected exception of " + expected);
	}
}
