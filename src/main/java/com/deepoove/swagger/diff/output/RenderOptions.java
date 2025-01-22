package com.deepoove.swagger.diff.output;

public class RenderOptions {

    private boolean pingHere = false;

    private String branchName = null;

    public RenderOptions() {}

    public RenderOptions(boolean pingHere, String branchName) {
        this.pingHere = pingHere;
        this.branchName = branchName;
    }

    public boolean isPingHere() {
        return pingHere;
    }

    public String getBranchName() {
        return branchName;
    }
}
