package com.deepoove.swagger.diff.model;

import java.util.ArrayList;
import java.util.List;

public class BreakingDiff {

    private List<ChangedEndpoint> changedEndpoints;
    private List<Endpoint> missingEndpoints;

    public BreakingDiff() {
        this.changedEndpoints = new ArrayList<>();
        this.missingEndpoints = new ArrayList<>();
    }

    public List<Endpoint> getMissingEndpoints() {
        return missingEndpoints;
    }

    public void setMissingEndpoints(List<Endpoint> missingEndpoints) {
        this.missingEndpoints = missingEndpoints;
    }

    public List<ChangedEndpoint> getChangedEndpoints() {
        return changedEndpoints;
    }

    public void setChangedEndpoints(List<ChangedEndpoint> changedEndpoints) {
        this.changedEndpoints = changedEndpoints;
    }

    public boolean isBreaking() {
        return !this.missingEndpoints.isEmpty() || !this.changedEndpoints.isEmpty();
    }
    
}
