package com.zeroleaf.tools.progressbar;

import com.zeroleaf.tools.progressbar.renderings.BarRendering;
import com.zeroleaf.tools.progressbar.renderings.CurrentRendering;
import com.zeroleaf.tools.progressbar.renderings.DlSpeedRendering;
import com.zeroleaf.tools.progressbar.renderings.ElapsedRendering;
import com.zeroleaf.tools.progressbar.renderings.EtaRendering;
import com.zeroleaf.tools.progressbar.renderings.PercentRendering;
import com.zeroleaf.tools.progressbar.renderings.Rendering;
import com.zeroleaf.tools.progressbar.renderings.TotalRendering;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @author zeroleaf
 */
public class ProgressBarImpl implements ProgressBar {

    private static final char CARRIAGE_RETURN = '\r';

    private final String format;

    private final Progress progress;
    private final Config   config;

    private final static Set<Rendering> renderings;

    static {
        renderings = new HashSet<>();
        renderings.add(new BarRendering());
        renderings.add(new CurrentRendering());
        renderings.add(new ElapsedRendering());
        renderings.add(new EtaRendering());
        renderings.add(new PercentRendering());
        renderings.add(new TotalRendering());
        renderings.add(new DlSpeedRendering());
    }

    private ProgressBarImpl(final String format, final Config config) {
        this.format   = format;
        this.config   = config;
        this.progress = new Progress(config.getTotalStep(),
                                     config.getUpdateInterval());
    }

    public static ProgressBarImpl newInstance(String format, Config config) {
        return new ProgressBarImpl(format, config);
    }

    public static ProgressBarImpl newInstance(String format, long totalStep) {
        Config config = new Config.Builder().totalStep(totalStep).build();
        return new ProgressBarImpl(format, config);
    }

    private static final String DEFAULT_TASK_FORMAT =
        ":current :elapsed [:bar] :percent%";

    public static ProgressBarImpl newTaskProgressBar(long totalStep) {
        return newInstance(DEFAULT_TASK_FORMAT, totalStep);
    }

    private static final String DEFAULT_DOWNLOAD_FORMAT =
        ":current :dlspeed :elapsed [:bar] :percent%";

    public static ProgressBarImpl newDownloadProgressBar(long totalStep) {
        return newInstance(DEFAULT_DOWNLOAD_FORMAT, totalStep);
    }


    @Override
    public void start() {
        progress.setStartTime(System.currentTimeMillis());
        showProgressBar();
    }

    @Override
    public void tick() {
        tick(1);
    }

    @Override
    public void tick(long step) {
        if (!progress.isComplete()) {
            progress.addStep(step);
            updateProgressBarIfNeeded();
        }
    }

    private void updateProgressBarIfNeeded() {
        if (progress.updateNeeded()) {
            showProgressBar();
        }
    }

    private void showProgressBar() {
        final String realContent = renderBar(format);
        config.getStream().print(realContent);
        config.getStream().print(CARRIAGE_RETURN);
    }

    private String renderBar(final String format) {
        String result = format;
        for (Rendering rendering : renderings) {
            if (format.contains(rendering.getToken())) {
                result = result.replace(
                    rendering.getToken(), rendering.render(config, progress));
            }
        }
        return result;
    }

    @Override
    public void keepResult(String rf) {
        if (rf == null || rf.isEmpty()) {
            rf = this.format;
        }
        config.getStream().println(renderBar(rf));
    }
}
