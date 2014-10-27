import edu.gatech.cs1331.annotations.Test;
import edu.gatech.cs1331.annotations.BeforeClass;
import edu.gatech.cs1331.annotations.AfterClass;
import static edu.gatech.cs1331.Assert.*;

public class Tests {
	
	@BeforeClass
	public void doBefore() {
		System.out.println("Start");
	}

	@Test
	public void foo() {
		String h = "Hello";
		String w = "World";
		assertEquals(h, w);
	}

	@Test(points=5)
	public void doSomething() {
		int[] a1 = {1, 2};
		int[] a2 = {1, 2};
		assertArrayEquals(a1, a2);
	}
	
	@Test(timeout=2000l)
	public void loopTest() {
		while(true);
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void exceptionTest() {
		int[] arr = { 1, 2, 3 };
		for(int i=0; i < arr.length; i++) {
			int a = arr[i];
		}
	}

	@AfterClass
	public void blah() {
		System.out.println("Done");
	}
}
