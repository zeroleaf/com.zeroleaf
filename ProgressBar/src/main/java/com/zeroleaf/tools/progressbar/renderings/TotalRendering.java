package com.zeroleaf.tools.progressbar.renderings;

import com.zeroleaf.tools.progressbar.Config;
import com.zeroleaf.tools.progressbar.Progress;

/**
 *
 *
 * @author zeroleaf
 */
public class TotalRendering implements Rendering {

    public static final String TOTAL_TOKEN = ":total";

    @Override
    public String getToken() {
        return TOTAL_TOKEN;
    }

    @Override
    public String render(Config config, Progress progress) {
        return String.format("%d", progress.getTotalStep());
    }
}
