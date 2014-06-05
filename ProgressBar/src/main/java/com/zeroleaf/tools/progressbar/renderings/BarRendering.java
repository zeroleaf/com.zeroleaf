package com.zeroleaf.tools.progressbar.renderings;

import com.zeroleaf.tools.progressbar.Config;
import com.zeroleaf.tools.progressbar.Progress;

import java.util.Arrays;

/**
 *
 *
 * @author zeroleaf
 */
public class BarRendering implements Rendering {

    public static final String BAR_TOKEN = ":bar";

    @Override
    public String getToken() {
        return BAR_TOKEN;
    }

    @Override
    public String render(Config config, Progress progress) {
        final double completeRatio = progress.completeRatio();
        final int completeCharCount = (int) (completeRatio * config.getBarWidth());

        return repeat(config.getCompleteChar(), completeCharCount)
               + repeat(config.getIncompleteChar(), config.getBarWidth() - completeCharCount);
    }

    private String repeat(char c, int count) {
        if (count == 0) {
            return "";
        }
        char[] chars = new char[count];
        Arrays.fill(chars, c);
        return new String(chars);
    }
}
