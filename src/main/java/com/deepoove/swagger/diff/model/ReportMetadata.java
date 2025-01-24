package com.deepoove.swagger.diff.model;

public class ReportMetadata {

    private Long reportTime;
    private String branchName;
    private boolean hasBreakingChanges;

    public Long getReportTime() {
        return reportTime;
    }

    public void setReportTime(Long reportTime) {
        this.reportTime = reportTime;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public boolean isHasBreakingChanges() {
        return hasBreakingChanges;
    }
    
    public void setHasBreakingChanges(boolean hasBreakingChanges) {
        this.hasBreakingChanges = hasBreakingChanges;
    }
    
}
