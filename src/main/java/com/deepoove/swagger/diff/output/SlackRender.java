package com.deepoove.swagger.diff.output;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.deepoove.swagger.diff.SwaggerDiff;
import com.deepoove.swagger.diff.model.*;

import io.swagger.models.HttpMethod;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import org.apache.commons.lang3.StringUtils;

public class SlackRender implements Render {

    final String NO_CHANGES_RENDER = "";
    final String BOLD_START = "*";
    final String BOLD_END = "*";
    final String ITALIC_START = "_";
    final String ITALIC_END = "_";
    final String CODE_START = "`";
    final String CODE_END = "`";
    final String TAB = "    ";
    final String DOUBLE_TAB = "        ";
    final String LI = "* ";
    final String HR = "\n";
    final String PING = "<!here>";

    public SlackRender() {}

    @Override
    public String render(SwaggerDiff diff, RenderOptions options) {
        if (diff.hasSameEndpoints()) {
            return NO_CHANGES_RENDER;
        }

        List<Endpoint> newEndpoints = diff.getNewEndpoints();
        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        List<ChangedEndpoint> changedEndpoints = diff.getChangedEndpoints();
        BreakingDiff breakingDiff = diff.getBreakingChanges();

        String ol_newEndpoint = ol_newEndpoint(newEndpoints);
        String ol_missingEndpoint = ol_missingEndpoint(missingEndpoints);
        String ol_changed = ol_changed(changedEndpoints);

        String ol_breakingNewEndpoint = "";
        String ol_breakingMissingEndpoint = breakingDiff.isBreaking() ? ol_missingEndpoint(breakingDiff.getMissingEndpoints()) : "";
        String ol_breakingChangedEndpoint = breakingDiff.isBreaking() ? ol_changed(breakingDiff.getChangedEndpoints()) : "";
        boolean hasBreakingChanges = breakingDiff.isBreaking();

        String slackMarkdown = renderSlackMarkdown(options.isPingHere(), options.getBranchName(), options.isBreakingSummary(), hasBreakingChanges, diff.getOldVersion(), diff.getNewVersion(),
                ol_newEndpoint, ol_missingEndpoint, ol_changed, ol_breakingNewEndpoint, ol_breakingMissingEndpoint, ol_breakingChangedEndpoint);
        return slackMarkdown;
    }

    public String renderSlackMarkdown(boolean isPingHere, String branchName, boolean isBreakingSummary, boolean hasBreakingChanges, String oldVersion, String newVersion,
                String ol_new, String ol_miss, String ol_changed, String ol_breakingNewEndpoint, String ol_breakingMissingEndpoint, String ol_breakingChangedEndpoint) {
        StringBuffer sb = new StringBuffer();
        appendMainHeader(sb, isPingHere, branchName, oldVersion, newVersion);
        if (isBreakingSummary && hasBreakingChanges) {
            appendBreakingChangesHeader(sb);
            appendBreakingChangesBody(sb, ol_breakingNewEndpoint, ol_breakingMissingEndpoint, ol_breakingChangedEndpoint);
            appendBreakingChangesFooter(sb);
            appendAllChangesHeader(sb);
        }
        appendAllChangesSummary(sb, ol_new, ol_miss, ol_changed);
        return sb.toString();
    }

    private void appendMainHeader(StringBuffer sb, boolean isPingHere, String branchName, String oldVersion, String newVersion) {
        if (isPingHere) {
            appendPing(sb);
        }
        if (StringUtils.isNotEmpty(branchName)) {
            appendBranchTitle(sb, branchName);
        }
        appendVersionHeader(sb, oldVersion, newVersion);
    }

    private void appendPing(StringBuffer sb) {
        sb.append(PING).append(HR);
    }

    private void appendBranchTitle(StringBuffer sb, String branchName) {
        sb.append(BOLD_START).append("Branch being used: " + branchName).append(BOLD_END).append(HR);
    }

    private void appendVersionHeader(StringBuffer sb, String oldVersion, String newVersion) {
        sb.append(BOLD_START).append("Version " + oldVersion + " to " + newVersion).append(BOLD_END).append("\n").append(HR);
    }

    private void appendBreakingChangesHeader(StringBuffer sb) {
        sb.append(BOLD_START).append("Summary of Breaking Changes").append(BOLD_END).append(HR).append(HR);
    }

    private void appendBreakingChangesBody(StringBuffer sb, String ol_breakingNewEndpoint, String ol_breakingMissingEndpoint, String ol_breakingChangedEndpoint) {
        appendAllChangesSummary(sb, ol_breakingNewEndpoint, ol_breakingMissingEndpoint, ol_breakingChangedEndpoint);
    }

    private void appendBreakingChangesFooter(StringBuffer sb) {
        sb.append(HR).append(HR);
    }

    private void appendAllChangesHeader(StringBuffer sb) {
        sb.append(BOLD_START).append("All Changes").append(BOLD_END).append(HR).append(HR);
    }

    private void appendAllChangesSummary(StringBuffer sb, String ol_new, String ol_miss, String ol_changed) {
        if (!ol_new.isEmpty()) {
            appendNewHeader(sb);
            appendNewBody(sb, ol_new);
        }
        if (!ol_miss.isEmpty()) {
            appendDeprecatedHeader(sb);
            appendDeprecatedBody(sb, ol_miss);
        }
        if (!ol_changed.isEmpty()) {
            appendChangedHeader(sb);
            appendChangedBody(sb, ol_changed);
        }
    }

    private void appendNewHeader(StringBuffer sb) {
        sb.append(BOLD_START).append("New Methods").append(BOLD_END).append("\n").append(HR);
    }

    private void appendNewBody(StringBuffer sb, String ol_new) {
        sb.append(ol_new).append("\n");
    }

    private void appendDeprecatedHeader(StringBuffer sb) {
        sb.append(BOLD_START).append("Deprecated Methods").append(BOLD_END).append("\n").append(HR);
    }

    private void appendDeprecatedBody(StringBuffer sb, String ol_miss) {
        sb.append(ol_miss).append("\n");
    }

    private void appendChangedHeader(StringBuffer sb) {
        sb.append(BOLD_START).append("Changed Methods").append(BOLD_END).append("\n").append(HR);
    }

    private void appendChangedBody(StringBuffer sb, String ol_changed) {
        sb.append(ol_changed);
    }

    private String ol_newEndpoint(List<Endpoint> endpoints) {
        if (null == endpoints) return "";
        StringBuffer sb = new StringBuffer();
        for (Endpoint endpoint : endpoints) {
            sb.append(li_newEndpoint(endpoint.getMethod().toString(),
                    endpoint.getPathUrl(), endpoint.getSummary()));
        }
        return sb.toString();
    }

    private String li_newEndpoint(String method, String path, String desc) {
        StringBuffer sb = new StringBuffer();
        sb.append(LI).append(CODE_START).append(method).append(CODE_END)
                .append(" " + path).append(" " + desc + "\n");
        return sb.toString();
    }

    private String ol_missingEndpoint(List<Endpoint> endpoints) {
        if (null == endpoints) return "";
        StringBuffer sb = new StringBuffer();
        for (Endpoint endpoint : endpoints) {
            sb.append(li_newEndpoint(endpoint.getMethod().toString(),
                    endpoint.getPathUrl(), endpoint.getSummary()));
        }
        return sb.toString();
    }

    private String ol_changed(List<ChangedEndpoint> changedEndpoints) {
        if (null == changedEndpoints) return "";
        StringBuffer sb = new StringBuffer();
        for (ChangedEndpoint changedEndpoint : changedEndpoints) {
            String pathUrl = changedEndpoint.getPathUrl();
            Map<HttpMethod, ChangedOperation> changedOperations = changedEndpoint
                    .getChangedOperations();
            for (Entry<HttpMethod, ChangedOperation> entry : changedOperations
                    .entrySet()) {
                String method = entry.getKey().toString();
                ChangedOperation changedOperation = entry.getValue();
                String desc = changedOperation.getSummary();

                StringBuffer ul_detail = new StringBuffer();
                if (changedOperation.isDiffParam()) {
                    ul_detail.append(TAB).append("Parameters")
                            .append(ul_param(changedOperation));
                }
                if (changedOperation.isDiffProp()) {
                    ul_detail.append(TAB).append("Return Type")
                            .append(ul_response(changedOperation));
                }
                if (changedOperation.isDiffProduces()) {
                    ul_detail.append(TAB).append("Produces")
                            .append(ul_produce(changedOperation));
                }
                if (changedOperation.isDiffConsumes()) {
                    ul_detail.append(TAB).append("Consumes")
                            .append(ul_consume(changedOperation));
                }
                sb.append(LI).append(CODE_START).append(method).append(CODE_END)
                        .append(" " + pathUrl).append(" " + desc + "  \n")
                        .append(ul_detail);
            }
        }
        return sb.toString();
    }

    private String ul_response(ChangedOperation changedOperation) {
        List<ElProperty> addProps = changedOperation.getAddProps();
        List<ElProperty> delProps = changedOperation.getMissingProps();
        List<ElProperty> changedProps = changedOperation.getChangedProps();
        StringBuffer sb = new StringBuffer("\n\n");

        for (ElProperty prop : addProps) {
            sb.append(DOUBLE_TAB).append(li_addProp(prop) + "\n");
        }
        for (ElProperty prop : delProps) {
            sb.append(DOUBLE_TAB).append(li_missingProp(prop) + "\n");
        }
        for (ElProperty prop : changedProps) {
            sb.append(DOUBLE_TAB).append(li_changedProp(prop) + "\n");
        }
        return sb.toString();
    }

    private String li_missingProp(ElProperty prop) {
        Property property = prop.getProperty();
        StringBuffer sb = new StringBuffer("");
        sb.append("Delete ").append(prop.getEl())
                .append(null == property.getDescription() ? ""
                        : (" //" + property.getDescription()));
        return sb.toString();
    }

    private String li_addProp(ElProperty prop) {
        Property property = prop.getProperty();
        StringBuffer sb = new StringBuffer("");
        sb.append("Insert ").append(prop.getEl())
                .append(null == property.getDescription() ? ""
                        : (" //" + property.getDescription()));
        return sb.toString();
    }

    private String li_changedProp(ElProperty prop) {
        Property property = prop.getProperty();
        String prefix = "Modify ";
        String desc = " //" + property.getDescription();
        String postfix = (null == property.getDescription() ? "" : desc);

        StringBuffer sb = new StringBuffer("");
        sb.append(prefix).append(prop.getEl())
                .append(postfix);
        return sb.toString();
    }

    private String ul_param(ChangedOperation changedOperation) {
        List<Parameter> addParameters = changedOperation.getAddParameters();
        List<Parameter> delParameters = changedOperation.getMissingParameters();
        List<ChangedParameter> changedParameters = changedOperation
                .getChangedParameter();
        StringBuffer sb = new StringBuffer("\n\n");
        for (Parameter param : addParameters) {
            sb.append(DOUBLE_TAB)
                    .append(li_addParam(param) + "\n");
        }
        for (ChangedParameter param : changedParameters) {
            List<ElProperty> increased = param.getIncreased();
            for (ElProperty prop : increased) {
                sb.append(DOUBLE_TAB)
                        .append(li_addProp(prop) + "\n");
            }
        }
        for (ChangedParameter param : changedParameters) {
            boolean changeRequired = param.isChangeRequired();
            boolean changeDescription = param.isChangeDescription();
            if (changeRequired || changeDescription) {
                sb.append(DOUBLE_TAB)
                        .append(li_changedParam(param) + "\n");
            }
        }
        for (ChangedParameter param : changedParameters) {
            List<ElProperty> missing = param.getMissing();
            List<ElProperty> changed = param.getChanged();
            for (ElProperty prop : missing) {
                sb.append(DOUBLE_TAB)
                        .append(li_missingProp(prop) + "\n");
            }
            for (ElProperty prop : changed) {
                sb.append(DOUBLE_TAB)
                        .append(li_changedProp(prop) + "\n");
            }
        }
        for (Parameter param : delParameters) {
            sb.append(DOUBLE_TAB)
                    .append(li_missingParam(param) + "\n");
        }
        return sb.toString();
    }

    private String li_addParam(Parameter param) {
        StringBuffer sb = new StringBuffer("");
        sb.append("Add ").append(param.getName())
                .append(null == param.getDescription() ? ""
                        : (" //" + param.getDescription()));
        return sb.toString();
    }

    private String li_missingParam(Parameter param) {
        StringBuffer sb = new StringBuffer("");
        sb.append("Delete ").append(param.getName())
                .append(null == param.getDescription() ? ""
                        : (" //" + param.getDescription()));
        return sb.toString();
    }

    private String li_changedParam(ChangedParameter changeParam) {
        boolean changeRequired = changeParam.isChangeRequired();
        boolean changeDescription = changeParam.isChangeDescription();
        Parameter rightParam = changeParam.getRightParameter();
        Parameter leftParam = changeParam.getLeftParameter();
        StringBuffer sb = new StringBuffer("");
        sb.append(rightParam.getName());
        if (changeRequired) {
            sb.append(" change into " + (rightParam.getRequired() ? "required" : "not required"));
        }
        if (changeDescription) {
            sb.append(" Notes ").append(leftParam.getDescription()).append(" change into ")
                    .append(rightParam.getDescription());
        }
        return sb.toString();
    }

    private String ul_produce(ChangedOperation changedOperation) {
        List<String> addProduce = changedOperation.getAddProduces();
        List<String> delProduce = changedOperation.getMissingProduces();
        StringBuffer sb = new StringBuffer("\n\n");

        for (String mt : addProduce) {
            sb.append(DOUBLE_TAB).append(li_addMediaType(mt) + "\n");
        }
        for (String mt : delProduce) {
            sb.append(DOUBLE_TAB).append(li_missingMediaType(mt) + "\n");
        }
        return sb.toString();
    }

    private String ul_consume(ChangedOperation changedOperation) {
        List<String> addConsume = changedOperation.getAddConsumes();
        List<String> delConsume = changedOperation.getMissingConsumes();
        StringBuffer sb = new StringBuffer("\n\n");

        for (String mt : addConsume) {
            sb.append(DOUBLE_TAB).append(li_addMediaType(mt) + "\n");
        }
        for (String mt : delConsume) {
            sb.append(DOUBLE_TAB).append(li_missingMediaType(mt) + "\n");
        }
        return sb.toString();
    }

    private String li_missingMediaType(String type) {
        StringBuffer sb = new StringBuffer("");
        sb.append("Delete ").append(type);
        return sb.toString();
    }

    private String li_addMediaType(String type) {
        StringBuffer sb = new StringBuffer("");
        sb.append("Insert ").append(type);
        return sb.toString();
    }
    
}
