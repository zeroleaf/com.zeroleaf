package com.zeroleaf.tools.progressbar.renderings;

import com.zeroleaf.tools.progressbar.Config;
import com.zeroleaf.tools.progressbar.Progress;

/**
 *
 *
 * @author zeroleaf
 */
public class CurrentRendering implements Rendering {

    public static final String CURRENT_TOKEN = ":current";

    @Override
    public String getToken() {
        return CURRENT_TOKEN;
    }

    @Override
    public String render(Config config, Progress progress) {
        int width = Long.toString(progress.getTotalStep()).length();
        final String formatString = "%" + width + "d";
        return String.format(formatString, progress.getCurrentStep());
    }
}
