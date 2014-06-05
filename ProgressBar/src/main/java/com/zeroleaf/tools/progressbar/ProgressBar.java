package com.zeroleaf.tools.progressbar;

/**
 *
 *
 * @author zeroleaf
 */
public interface ProgressBar {

    /**
     * Tick that the progress is start.
     */
    void start();

    /**
     * Tick one step.
     */
    void tick();

    /**
     * Tick the specified number of step.
     *
     * @param step number of step.
     */
    void tick(long step);

    /**
     * This method is usually called when the progress has been finished
     * and you want to display the progress result.
     *
     * The format argument is the format that you want the result to
     * displayed, if it is {@code null} or empty, the default format which
     * is applied when you create this instance will be used.
     *
     * @param rf result format. Null or empty to use default format.
     */
    void keepResult(String rf);
}
