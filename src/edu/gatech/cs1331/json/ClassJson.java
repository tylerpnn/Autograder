package edu.gatech.cs1331.json;

import java.util.Arrays;

public class ClassJson {

	private String className, superClassName;
	private String[] interfaces;
	private ConstructorJson[] constructors;
	private MethodJson[] methods;
	
	public String getClassName() {
		return this.className;
	}
	
	public String getSuperClassName() {
		return this.superClassName;
	}
	
	public String[] getInterfaces() {
		return this.interfaces;
	}
	
	public ConstructorJson[] getConstructors() {
		return this.constructors;
	}
	
	public MethodJson[] getMethods() {
		return this.methods;
	}
	
	public static class ConstructorJson  {

		private String[] params;
		
		public String[] getParams() {
			return this.params;
		}
		
		public boolean equals(Object o) {
			if(!(o instanceof ConstructorJson)) return false;
			String[] oParams = ((ConstructorJson)o).getParams();
			String[] tParams = params;
			if(oParams.length == params.length) {
				Arrays.sort(tParams);
				Arrays.sort(oParams);
				return Arrays.equals(tParams, oParams);
			}
			return false;
		}
	}
	
	public static class MethodJson {
		
		private String visibility, name, returnType, annotations;
		private boolean isStatic;
		private String[] params;
		private String[] exceptions;
		
		public String getVisibility() {
			return this.visibility;
		}
		
		public String getReturnType() {
			return this.returnType;
		}
		
		public boolean getIsStatic() {
			return this.isStatic;
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getAnnotations() {
			return this.annotations;
		}
		
		public String[] getParams() {
			return this.params;
		}
		
		public String[] getExceptions() {
			return this.exceptions;
		}
	}
}
