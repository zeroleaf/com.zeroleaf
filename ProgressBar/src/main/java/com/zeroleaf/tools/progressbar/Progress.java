package com.zeroleaf.tools.progressbar;

/**
 *
 *
 * @author zeroleaf
 */
public class Progress {

    private final long totalStep;
    private final long updateInterval;

    private long currentStep;
    private long lastUpdateTime;

    private boolean isStartTimeSet = false;
    private long startTime;

    private double lastStepSpeed;

    public Progress(final long totalStep, final long updateInterval) {
        this.totalStep      = totalStep;
        this.updateInterval = updateInterval;
    }

    public long getTotalStep() {
        return totalStep;
    }

    public void setStartTime(long startTime) {
        if (isStartTimeSet) {
            throw new IllegalStateException("startTime can be set only once.");
        }
        this.startTime = startTime;
        isStartTimeSet = true;
        lastUpdateTime = startTime;
    }

    public long getCurrentStep() {
        return currentStep;
    }

    public void addStep(long step) {
        currentStep += step;
        if (currentStep > totalStep) {
            currentStep = totalStep;
        }

        long now = System.currentTimeMillis();
        lastStepSpeed  = (double) step / (now - lastUpdateTime);
    }

    public long elapsed() {
        return lastUpdateTime - startTime;
    }

    public boolean isComplete() {
        return currentStep == totalStep;
    }

    public double completeRatio() {
        return (double) currentStep / totalStep;
    }

    public long eta() {
        return (long) ((totalStep - currentStep) / lastStepSpeed);
    }

    /**
     * Last step speed, unit is step per millisecond.
     *
     * @return last step speed.
     */
    public double getLastStepSpeed() {
        return lastStepSpeed;
    }

    /**
     * Last step speed, unit is step per second.
     *
     * @return last step speed.
     */
    public double getLastStepSpeedInSecond() {
        return lastStepSpeed * 1000;
    }

    /**
     * Judge now need to update progress bar or not.
     *
     * @return If now need to update progress bar, return true; else false.
     */
    public boolean updateNeeded() {
        boolean updateNeeded =
            (System.currentTimeMillis() - lastUpdateTime) >= updateInterval;
        if (updateNeeded) {
            lastUpdateTime = System.currentTimeMillis();
        }
        return updateNeeded;
    }
}
