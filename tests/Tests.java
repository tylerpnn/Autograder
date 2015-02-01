import edu.gatech.cs1331.annotations.Test;
import edu.gatech.cs1331.annotations.BeforeClass;
import edu.gatech.cs1331.annotations.AfterClass;
import edu.gatech.cs1331.annotations.Before;
import edu.gatech.cs1331.annotations.After;
import static edu.gatech.cs1331.Assert.*;

public class Tests {

	int i = 0;

	@BeforeClass
	public void doBefore() {
		System.out.println("Start");
	}
	
	@Before
	public void before() {
		System.out.println(i);
	}

	@After
	public void after() {
		System.out.println("after" + i++);
	}

	@Test
	public void foo() {
		System.out.println("foo");
		boolean b = false;
		assertTrue(b);
	}

	@Test(depends="foo")
	public void bar() {
		System.out.println("bar");
		int i = 1;
	}

	@Test(depends="bar")
	public void checkDepends() {
		System.out.println("checkDepends");
		assertFalse(true);
	}

	@Test(depends="checkDepends")
	public void doubleCheckDepends() {
		System.out.println("doubleCheckDepends");
		assertTrue(true);
	}

	@Test(points=5)
	public void doSomething() {
		System.out.println("doSomething");
		assertEquals("hello", "world");
	}
	
	@Test(timeout=2000l)
	public void loopTest() {
		System.out.println("loopTest");
		int i = 1;
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void exceptionTest() {
		System.out.println("exceptionTest");
		int[] arr = { 1, 2, 3 };
		for(int i=0; i < arr.length; i++) {
			int a = arr[i+1];
		}
	}

	@AfterClass
	public void blah() {
		System.out.println("Done");
	}
}
