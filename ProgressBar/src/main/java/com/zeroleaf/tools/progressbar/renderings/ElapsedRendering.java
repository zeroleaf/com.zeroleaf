package com.zeroleaf.tools.progressbar.renderings;

import com.zeroleaf.tools.progressbar.Config;
import com.zeroleaf.tools.progressbar.Progress;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 *
 * @author zeroleaf
 */
public class ElapsedRendering implements Rendering {

    public static final String ELAPSED_TOKEN = ":elapsed";

    @Override
    public String getToken() {
        return ELAPSED_TOKEN;
    }

    private static final String TIME_FORMAT = "mm:ss";

    @Override
    public String render(Config config, Progress progress) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf.format(new Date(progress.elapsed()));
    }
}
