package com.deepoove.swagger.diff.output;

import com.deepoove.swagger.diff.SwaggerDiff;

public interface Render {

    default String render(SwaggerDiff diff) {
        return render(diff, new RenderOptions());
    }

    String render(SwaggerDiff diff, RenderOptions options);

}
