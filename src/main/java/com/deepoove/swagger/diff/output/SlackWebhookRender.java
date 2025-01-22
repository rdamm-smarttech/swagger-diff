package com.deepoove.swagger.diff.output;

import com.alibaba.fastjson.JSONObject;
import com.deepoove.swagger.diff.SwaggerDiff;

public class SlackWebhookRender extends SlackRender {

    final String MARKDOWN_TYPE = "mrkdwn";

    @Override
    public String render(SwaggerDiff diff) {
        if (hasNoChanges(diff)) {
            return NO_CHANGES_RENDER;
        }
        String slackMarkdown = super.render(diff);
        return formatSlackMarkdownForWebhook(slackMarkdown);
    }

    private String formatSlackMarkdownForWebhook(String slackMarkdown) {
        slackMarkdown = removeQuotes(slackMarkdown);
        return wrapMarkdownTextInSlackJsonPayload(slackMarkdown);
    }

    private String removeQuotes(String slackMarkdown) {
        return slackMarkdown.replace("'", "")
                .replace("\"", "")
                .replace("`", "");
    }

    private String wrapMarkdownTextInSlackJsonPayload(String slackMarkdown) {
        JSONObject json = new JSONObject();
        json.put("type", MARKDOWN_TYPE);
        json.put("text", slackMarkdown);
        return json.toString();
    }
    
}
