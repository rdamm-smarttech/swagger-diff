package com.deepoove.swagger.diff.compare;

import java.util.HashMap;

import com.deepoove.swagger.diff.SwaggerDiff;
import com.deepoove.swagger.diff.model.BreakingDiff;
import com.deepoove.swagger.diff.model.ChangedEndpoint;
import com.deepoove.swagger.diff.model.ChangedOperation;
import com.deepoove.swagger.diff.model.ChangedParameter;
import com.deepoove.swagger.diff.model.ElProperty;

import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.parameters.Parameter;

public class BreakingChangesCheckUtil {

    public static BreakingDiff findAllBreakingChanges(SwaggerDiff swaggerDiff) {
        BreakingDiff breakingDiff = new BreakingDiff();
        if (!swaggerDiff.getMissingEndpoints().isEmpty()) {
            breakingDiff.setMissingEndpoints(swaggerDiff.getMissingEndpoints());
        }
        for (ChangedEndpoint changedEndpoint : swaggerDiff.getChangedEndpoints()) {
            ChangedEndpoint breaking = filterBreakingChangesForChangedEndpoint(changedEndpoint);
            if (breaking != null) {
                breakingDiff.getChangedEndpoints().add(breaking);
            }
        }
        return breakingDiff;
    }

    private static ChangedEndpoint filterBreakingChangesForChangedEndpoint(ChangedEndpoint changedEndpoint) {
        ChangedEndpoint breakingChangedEndpoint = new ChangedEndpoint();
        breakingChangedEndpoint.setPathUrl(changedEndpoint.getPathUrl());
        breakingChangedEndpoint.setNewOperations(changedEndpoint.getNewOperations());
        breakingChangedEndpoint.setMissingOperations(new HashMap<HttpMethod, Operation>());
        breakingChangedEndpoint.setChangedOperations(new HashMap<HttpMethod, ChangedOperation>());

        if (!changedEndpoint.getMissingOperations().isEmpty()) {
            breakingChangedEndpoint.setMissingOperations(changedEndpoint.getMissingOperations());
        }
        for (HttpMethod httpMethod : changedEndpoint.getChangedOperations().keySet()) {
            ChangedOperation changedOperation = changedEndpoint.getChangedOperations().get(httpMethod);
            ChangedOperation breakingChangedOperation = filberBreakingChangesForChangedOperation(changedOperation);
            if (breakingChangedOperation != null) {
                breakingChangedEndpoint.getChangedOperations().put(httpMethod, breakingChangedOperation);
            }
        }
        if (breakingChangedEndpoint.getChangedOperations().isEmpty() &&
                breakingChangedEndpoint.getMissingOperations().isEmpty()) {
            return null;
        }
        return breakingChangedEndpoint;
    }

    private static ChangedOperation filberBreakingChangesForChangedOperation(ChangedOperation changedOperation) {
        ChangedOperation breakingChangedOperation = new ChangedOperation();
        breakingChangedOperation.setSummary(changedOperation.getSummary());

        // Props (Return Type)
        if (!changedOperation.getMissingProps().isEmpty()) {
            breakingChangedOperation.setMissingProps(changedOperation.getMissingProps());
        }
        for (ElProperty elProperty : changedOperation.getChangedProps()) {
            if (elProperty.isTypeChange() || elProperty.isRemovedEnums()) {
                breakingChangedOperation.getChangedProps().add(elProperty);
            }
        }

        // Params
        for (Parameter parameter : changedOperation.getAddParameters()) {
            if (parameter.getRequired() && 
                    (parameter.getAllowEmptyValue() != null && parameter.getAllowEmptyValue() == false)) {
                breakingChangedOperation.getAddParameters().add(parameter);
            }
        }
        for (ChangedParameter changedParameter : changedOperation.getChangedParameter()) {
            Parameter leftParameter = changedParameter.getLeftParameter();
            Parameter rightParameter = changedParameter.getRightParameter();
            if (!leftParameter.getRequired() && rightParameter.getRequired()) {
                breakingChangedOperation.getChangedParameter().add(changedParameter);
                continue;
            }
            if (leftParameter.getRequired() &&
                    rightParameter.getRequired() &&
                    (leftParameter.getAllowEmptyValue() != null && leftParameter.getAllowEmptyValue() == true) &&
                    (rightParameter.getAllowEmptyValue() == null || rightParameter.getAllowEmptyValue() == false)) {
                breakingChangedOperation.getChangedParameter().add(changedParameter);
                continue;
            }
        }

        // Consumes
        if (!changedOperation.getMissingConsumes().isEmpty()) {
            breakingChangedOperation.setMissingConsumes(changedOperation.getMissingConsumes());
        }

        // Produces
        if (!changedOperation.getMissingProduces().isEmpty()) {
            breakingChangedOperation.setMissingProduces(changedOperation.getMissingProduces());
        }

        if (breakingChangedOperation.getMissingProps().isEmpty() &&
                breakingChangedOperation.getChangedProps().isEmpty() &&
                breakingChangedOperation.getAddParameters().isEmpty() &&
                breakingChangedOperation.getChangedParameter().isEmpty() &&
                breakingChangedOperation.getMissingConsumes().isEmpty() &&
                breakingChangedOperation.getMissingProduces().isEmpty()) {
            return null;
        }
        return breakingChangedOperation;
    }
    
}
