package edu.gatech.cs1331.json;

public class TestJson {
    
    private String name, comment;
    private int points;
    private boolean extraCredit, silent;
    private String dependencies, ifFailed;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String s) {
        this.name = s;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(String s) {
        this.comment = s;
    }
    
    public int getPoints() {
        return this.points;
    }
    
    public void setPoints(int x) {
        this.points = x;
    }

    public boolean isExtraCredit() {
        return this.extraCredit;
    }

    public void setExtraCredit(boolean b) {
        this.extraCredit = b;
    }
    
    public void setSilent(boolean b) {
        this.silent = b;
    }
    
    public boolean isSilent() {
        return silent;
    }
    
    public void setDependencies(String d) {
        this.dependencies = d;
    }
    
    public String getDependencies() {
        return this.dependencies;
    }
    
    public String getIfFailed() {
    	return ifFailed;
    }
    
    public void setIfFailed(String i) {
    	this.ifFailed = i;
    }
    
    public String toString() {
        return this.name;
    }
}
