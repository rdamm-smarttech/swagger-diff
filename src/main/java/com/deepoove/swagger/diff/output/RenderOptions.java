package com.deepoove.swagger.diff.output;

public class RenderOptions {

    private boolean pingHere = false;

    private String branchName = null;

    private boolean breakingSummary = false;

    public RenderOptions() {}

    public RenderOptions(boolean pingHere, String branchName, boolean breakingSummary) {
        this.pingHere = pingHere;
        this.branchName = branchName;
        this.breakingSummary = breakingSummary;
    }

    public boolean isPingHere() {
        return pingHere;
    }

    public String getBranchName() {
        return branchName;
    }

    public boolean isBreakingSummary() {
        return breakingSummary;
    }
}
