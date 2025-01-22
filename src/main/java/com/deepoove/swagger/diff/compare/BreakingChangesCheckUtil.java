package com.deepoove.swagger.diff.compare;

import com.deepoove.swagger.diff.SwaggerDiff;
import com.deepoove.swagger.diff.model.ChangedEndpoint;
import com.deepoove.swagger.diff.model.ChangedOperation;
import com.deepoove.swagger.diff.model.ChangedParameter;
import com.deepoove.swagger.diff.model.ElProperty;

import io.swagger.models.HttpMethod;
import io.swagger.models.parameters.Parameter;

public class BreakingChangesCheckUtil {

    public static boolean hasBreakingChanges(SwaggerDiff swaggerDiff) {
        if (!swaggerDiff.getMissingEndpoints().isEmpty()) {
            return true;
        }
        for (ChangedEndpoint changedEndpoint : swaggerDiff.getChangedEndpoints()) {
            if (hasBreakingChanges(changedEndpoint)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasBreakingChanges(ChangedEndpoint changedEndpoint) {
        if (!changedEndpoint.getMissingOperations().isEmpty()) {
            return true;
        }
        for (HttpMethod httpMethod : changedEndpoint.getChangedOperations().keySet()) {
            ChangedOperation changedOperation = changedEndpoint.getChangedOperations().get(httpMethod);
            if (hasBreakingChanges(changedOperation)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasBreakingChanges(ChangedOperation changedOperation) {
        // Props (Return Type)
        if (!changedOperation.getMissingProps().isEmpty()) {
            return true;
        }
        for (ElProperty elProperty : changedOperation.getChangedProps()) {
            if (elProperty.isTypeChange() || elProperty.isRemovedEnums()) {
                return true;
            }
        }

        // Params
        for (Parameter parameter : changedOperation.getAddParameters()) {
            if (parameter.getRequired() && !parameter.getAllowEmptyValue()) {
                return true;
            }
        }
        for (ChangedParameter changedParameter : changedOperation.getChangedParameter()) {
            Parameter leftParameter = changedParameter.getLeftParameter();
            Parameter rightParameter = changedParameter.getRightParameter();
            if (!leftParameter.getRequired() && rightParameter.getRequired()) {
                return true;
            }
            if (leftParameter.getRequired() &&
                    rightParameter.getRequired() &&
                    leftParameter.getAllowEmptyValue() &&
                    !rightParameter.getAllowEmptyValue()) {
                return true;
            }
        }

        // Consumes
        if (!changedOperation.getMissingConsumes().isEmpty()) {
            return true;
        }

        // Produces
        if (!changedOperation.getMissingProduces().isEmpty()) {
            return true;
        }
        return false;
    }
    
}
