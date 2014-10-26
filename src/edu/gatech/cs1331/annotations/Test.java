package edu.gatech.cs1331.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.gatech.cs1331.None;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {

	long timeout() default 0L;
	
	Class<? extends Throwable> expected() default None.class;
	
}
