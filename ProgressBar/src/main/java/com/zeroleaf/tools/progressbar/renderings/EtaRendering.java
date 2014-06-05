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
public class EtaRendering implements Rendering {

    public static final String ETA_TOKEN = ":eta";

    @Override
    public String getToken() {
        return ETA_TOKEN;
    }

    @Override
    public String render(Config config, Progress progress) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        return sdf.format(new Date(progress.eta()));
    }
}
