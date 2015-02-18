package edu.gatech.cs1331.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class AutoGraderJson {

    private ClassJson[] classes;
    private TestJson[] tests;
    
    public ClassJson[] getClasses() {
        return this.classes;
    }
    
    public void setClasses(ClassJson[] classes) {
        this.classes = classes;
    }
    
    public TestJson[] getTests() {
        return this.tests;
    }
    
    public void setTests(TestJson[] tj) {
        this.tests = tj;
    }
    
    public static AutoGraderJson buildClassList(String filename) throws FileNotFoundException,
            JsonSyntaxException, JsonIOException {
        AutoGraderJson agj = null;
        FileReader fr = new FileReader(new File(filename));
        agj = (AutoGraderJson) new Gson().fromJson(fr, AutoGraderJson.class);
        return agj;
    }
}
