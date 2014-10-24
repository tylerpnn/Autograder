package edu.gatech.cs1331.json;

import java.io.File;
import java.io.FileReader;

import com.google.gson.Gson;

public class AutoGraderJson {

	private ClassJson[] classes;
	private TestJson[] tests;
	
	public ClassJson[] getClasses() {
		return this.classes;
	}
	
	public TestJson[] getTests() {
		return this.tests;
	}
	
	public static AutoGraderJson buildClassList(String filename) {
		AutoGraderJson agj = null;
		try {
			File json = new File(filename);
			FileReader fr = new FileReader(json);
		agj = (AutoGraderJson) new Gson().fromJson(fr, AutoGraderJson.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return agj;
	}
}
