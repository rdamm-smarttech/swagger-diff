package com.deepoove.swagger.diff.output;

import com.alibaba.fastjson.JSON;
import com.deepoove.swagger.diff.SwaggerDiff;

public class JsonRender implements Render {

    @Override
    public String render(SwaggerDiff diff, RenderOptions options) {
        return JSON.toJSONString(diff);
    }
}
