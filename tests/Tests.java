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

	@Test
	public void doSomething() {
		int[] a1 = {1, 2};
		int[] a2 = {1, 2};
		assertArrayEquals(a1, a2);
	}

	@AfterClass
	public void blah() {
		System.out.println("Done");
	}
}
