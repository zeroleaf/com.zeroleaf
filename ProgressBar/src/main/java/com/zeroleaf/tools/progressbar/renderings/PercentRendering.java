package com.zeroleaf.tools.progressbar.renderings;

import com.zeroleaf.tools.progressbar.Config;
import com.zeroleaf.tools.progressbar.Progress;

/**
 *
 *
 * @author zeroleaf
 */
public class PercentRendering implements Rendering {

    public static final String PERCENT_TOKEN = ":percent";

    @Override
    public String getToken() {
        return PERCENT_TOKEN;
    }

    @Override
    public String render(Config config, Progress progress) {
        return String.format("%6.2f", progress.completeRatio() * 100);
    }
}
