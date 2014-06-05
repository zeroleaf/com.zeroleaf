package com.zeroleaf.tools.progressbar.renderings;

import com.zeroleaf.tools.progressbar.Config;
import com.zeroleaf.tools.progressbar.Progress;

/**
 *
 *
 * @author zeroleaf
 */
public class DlSpeedRendering implements Rendering {

    private static final String DL_SPEED_TOKEN = ":dlspeed";

    @Override
    public String getToken() {
        return DL_SPEED_TOKEN;
    }

    private static final int RATE = 1024;

    private static final int B  = 1;
    private static final int KB = B  * RATE;
    private static final int MB = KB * RATE;
    private static final int GB = MB * RATE;

    private static final int[] VOLUMES  = {B, KB, MB, GB};

    private static final String[] UNITS = {"B/s", "KB/s", "MB/s", "GB/s"};

    @Override
    public String render(Config config, Progress progress) {
        final int dlSpeed = (int) progress.getLastStepSpeedInSecond();
        for (int i = VOLUMES.length - 1; i >= 0; i--) {
            if (dlSpeed > VOLUMES[i]) {
                return toHumanReadableSpeed(dlSpeed, i);
            }
        }
        return toHumanReadableSpeed(dlSpeed, 0);
    }

    private String toHumanReadableSpeed(int speed, int unitIndex) {
        final int readableSpeed = speed / VOLUMES[unitIndex];
        return String.format("%4d %s", readableSpeed, UNITS[unitIndex]);
    }
}
