package com.deepoove.swagger.diff.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.deepoove.swagger.diff.SwaggerDiff;
import com.deepoove.swagger.diff.output.HtmlDivRender;
import com.deepoove.swagger.diff.output.HtmlRender;
import com.deepoove.swagger.diff.output.JsonRender;
import com.deepoove.swagger.diff.output.MarkdownRender;
import com.deepoove.swagger.diff.output.Render;
import com.deepoove.swagger.diff.output.RenderOptions;
import com.deepoove.swagger.diff.output.SlackRender;
import com.deepoove.swagger.diff.output.SlackWebhookRender;

/**
 * $java -jar swagger-diff.jar -old http://www.petstore.com/swagger.json \n
 *  -new http://www.petstore.com/swagger_new.json \n
 *  -v 2.0 \n
 *  -output-mode markdown \n
 *
 * @author Sayi
 * @version
 */
public class CLI {

    private static final String OUTPUT_MODE_HTML_DIV = "html_div";
    private static final String OUTPUT_MODE_SLACK = "slack";
    private static final String OUTPUT_MODE_SLACK_WEBHOOK = "slack_webhook";
    private static final String OUTPUT_MODE_MARKDOWN = "markdown";
    private static final String OUTPUT_MODE_JSON = "json";

    @Parameter(names = "-old", description = "old api-doc location:Json file path or Http url", required = true, order = 0)
    private String oldSpec;

    @Parameter(names = "-new", description = "new api-doc location:Json file path or Http url", required = true, order = 1)
    private String newSpec;

    @Parameter(names = "-v", description = "swagger version:1.0 or 2.0", validateWith=  RegexValidator.class, order = 2)
    @Regex("(2\\.0|1\\.0)")
    private String version = SwaggerDiff.SWAGGER_VERSION_V2;

    @Parameter(names = "-output-mode", description = "render mode: markdown, html, html_div, json, slack or slack_webhook", validateWith = RegexValidator.class, order = 3)
    @Regex("(markdown|html|html_div|json|slack|slack_webhook)")
    private String outputMode = OUTPUT_MODE_MARKDOWN;

    @Parameter(names = "--help", help = true, order = 5)
    private boolean help;

    @Parameter(names = "--version", description = "swagger-diff tool version", help = true, order = 6)
    private boolean v;

    @Parameter(names = "-here", description = "prepend a @here (slack encoded) ping to beginning of output")
    private boolean pingHere;

    @Parameter(names = "-branch_title", description = "Add branch title to the output")
    private String branchTitle;

    @Parameter(names = "-breaking_summary", description = "Add a summary of the breaking changes")
    private boolean breakingSummary;

    public static void main(String[] args) {
        CLI cli = new CLI();
        JCommander jCommander = JCommander.newBuilder()
            .addObject(cli)
            .build();
        jCommander.parse(args);
        cli.run(jCommander);
    }

    public void run(JCommander jCommander) {
        if (help){
            jCommander.setProgramName("java -jar swagger-diff.jar");
            jCommander.usage();
            return;
        }
        if (v){
            JCommander.getConsole().println("1.2.2");
            return;
        }

        SwaggerDiff diff = SwaggerDiff.SWAGGER_VERSION_V2.equals(version)
                ? SwaggerDiff.compareV2(oldSpec, newSpec) : SwaggerDiff.compareV1(oldSpec, newSpec);

        String render = getRender(outputMode).render(diff, new RenderOptions(pingHere, branchTitle, breakingSummary));
        JCommander.getConsole().println(render);
    }

    private Render getRender(String outputMode) {
        if (OUTPUT_MODE_MARKDOWN.equals(outputMode)) {
            return new MarkdownRender();
        } else if (OUTPUT_MODE_JSON.equals(outputMode)) {
            return new JsonRender();
        } else if (OUTPUT_MODE_SLACK.equals(outputMode)) {
            return new SlackRender();
        } else if (OUTPUT_MODE_SLACK_WEBHOOK.equals(outputMode)) {
            return new SlackWebhookRender();
        } else if (OUTPUT_MODE_HTML_DIV.equals(outputMode)) {
            return new HtmlDivRender();
        }
        return new HtmlRender("Changelog", "http://deepoove.com/swagger-diff/stylesheets/demo.css");
    }

    public String getOldSpec() {
        return oldSpec;
    }

    public String getNewSpec() {
        return newSpec;
    }

    public String getVersion() {
        return version;
    }

    public String getOutputMode() {
        return outputMode;
    }


}
