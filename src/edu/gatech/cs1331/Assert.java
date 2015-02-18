package edu.gatech.cs1331;

import java.util.Arrays;

public class Assert {

	protected Assert() {}
	
	public static void fail(String m) {
		if(m == null)
			throw new AssertionError();
		throw new AssertionError(m);
	}
	
	public static void fail(String m, int points) {
		if(m == null)
			throw new AssertionErrorWithPoints(points);
		throw new AssertionErrorWithPoints(m, points);
	}
	

	/********		TRUE/FALSE		********/
	public static void assertTrue(String m, boolean condition) {
		if(!condition)
			fail(m);
	}
	
	public static void assertTrue(String m, boolean condition, int points) {
		if(!condition)
			fail(m, points);
	}
	
	public static void assertTrue(boolean condition) {
		assertTrue("Expected: true, actual: false", condition);
	}
	
	public static void assertFalse(String m, boolean condition) {
		if(condition)
			fail(m);
	}
	
	public static void assertFalse(String m, boolean condition, int points) {
		if(condition)
			fail(m, points);
	}
	
	public static void assertFalse(boolean condition) {
		assertTrue("Expected: false, actual: true", !condition);
	}
	

	/********		EQUALS		********/
	public static void assertEquals(String m,
			Object expected, Object actual) {
		assertTrue(m, expected.equals(actual));
	}
	
	public static void assertEquals(Object expected, Object actual) {
		String m = String.format("Expected: %s, actual: %s",
				expected.toString(), actual.toString());
		assertTrue(m, expected.equals(actual));
	}
	
	public static void assertNotEquals(String m,
			Object unexpected, Object actual) {
		assertTrue(m, !unexpected.equals(actual));
	}
	
	public static void assertNotEquals(Object unexpected, Object actual) {
		String m = String.format("Did not expect: %s, got: ",
				unexpected.toString(), actual.toString());
		assertTrue(m, !unexpected.equals(actual));
	}

	/********		REFERENCES		********/
	public static void assertSame(String m,
			Object expected, Object actual) {
		assertTrue(m, expected == actual);
	}
	
	public static void assertSame(Object expected, Object actual) {
		assertTrue(expected == actual);
	}
	
	public static void assertNotSame(String m,
			Object expected, Object actual) {
		assertTrue(m, expected != actual);
	}
	
	public static void assertNotSame(Object expected, Object actual) {
		assertTrue(expected != actual);
	}
	
	public static void assertNull(String m, Object nullObject) {
		assertTrue(m, nullObject == null);
	}
	
	public static void assertNull(Object nullObject) {
		assertTrue(nullObject == null);
	}
	
	public static void assertNotNull(String m, Object nullObject) {
		assertTrue(m, nullObject != null);
	}
	
	public static void assertNotNull(Object nullObject) {
		assertTrue(nullObject != null);
	}
	
	/********		PRIMITIVES		********/
	public static void assertEquals(String m, double expected, double actual) {
		assertTrue(m, expected == actual);
	}
	
	public static void assertEquals(double expected, double actual) {
		assertTrue(expected == actual);
	}
	
	public static void assertNotEquals(String m, double expected, double actual) {
		assertTrue(m, expected != actual);
	}
	
	public static void assertNotEquals(double expected, double actual) {
		assertTrue(expected != actual);
	}
	
	public static void assertEquals(String m, float expected, float actual) {
		assertTrue(m, expected == actual);
	}
	
	public static void assertEquals(float expected, float actual) {
		assertTrue(expected == actual);
	}
	
	public static void assertNotEquals(String m, float expected, float actual) {
		assertTrue(m, expected != actual);
	}
	
	public static void assertNotEquals(float expected, float actual) {
		assertTrue(expected != actual);
	}
	
	public static void assertEquals(String m, long expected, long actual) {
		assertTrue(m, expected == actual);
	}
	
	public static void assertEquals(long expected, long actual) {
		assertTrue(expected == actual);
	}
	
	public static void assertNotEquals(String m, long expected, long actual) {
		assertTrue(m, expected != actual);
	}
	
	public static void assertNotEquals(long expected, long actual) {
		assertTrue(expected != actual);
	}
	
	public static void assertEquals(String m, double expected, double actual, double epsilon) {
		assertTrue(m, Math.abs(expected - actual) < epsilon);
	}
	
	public static void assertEquals(double expected, double actual, double epsilon) {
		assertTrue(Math.abs(expected - actual) < epsilon);
	}
	
	public static void assertEquals(String m, float expected, float actual, float epsilon) {
		assertTrue(m, Math.abs(expected - actual) < epsilon);
	}
	
	public static void assertEquals(float expected, float actual, float epsilon) {
		assertTrue(Math.abs(expected - actual) < epsilon);
	}
	
	
	/********		ARRAYS		********/
	public static void assertArrayEquals(String m,
			Object[] expected, Object[] actual) {
		assertTrue(m, Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(Object[] expected, Object[] actual) {
		assertTrue(Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(String m, int[] expected, int[] actual) {
		assertTrue(m, Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(int[] expected, int[] actual) {
		assertTrue(Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(String m, double[] expected, double[] actual) {
		assertTrue(m, Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(double[] expected, double[] actual) {
		assertTrue(Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(String m, float[] expected, float[] actual) {
		assertTrue(m, Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(float[] expected, float[] actual) {
		assertTrue(Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(String m, long[] expected, long[] actual) {
		assertTrue(m, Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(long[] expected, long[] actual) {
		assertTrue(Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(String m, short[] expected, short[] actual) {
		assertTrue(m, Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(short[] expected, short[] actual) {
		assertTrue(Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(String m, char[] expected, char[] actual) {
		assertTrue(m, Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(char[] expected, char[] actual) {
		assertTrue(Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(String m, byte[] expected, byte[] actual) {
		assertTrue(m, Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(byte[] expected, byte[] actual) {
		assertTrue(Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(String m, boolean[] expected, boolean[] actual) {
		assertTrue(m, Arrays.equals(expected, actual));
	}
	
	public static void assertArrayEquals(boolean[] expected, boolean[] actual) {
		assertTrue(Arrays.equals(expected, actual));
	}

}
