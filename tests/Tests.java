import edu.gatech.cs1331.annotations.Test;
import edu.gatech.cs1331.annotations.BeforeClass;
import edu.gatech.cs1331.annotations.AfterClass;
import static edu.gatech.cs1331.Assert.*;

public class Tests {

	public boolean b;
	
	@BeforeClass
	public void doBefore() {
		System.out.println("Start");
	}

	@Test
	public void foo() {
		assertTrue(b);
	}

	@Test(depends="foo")
	public void bar() {
		b = true;
	}

	@Test
	public void checkDepends() {
		assertFalse(b);
	}

	@Test(points=5)
	public void doSomething() {
		assertEquals("hello", "world");
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
