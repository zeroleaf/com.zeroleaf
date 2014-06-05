package com.zeroleaf.tools.progressbar;

import java.io.PrintStream;

/**
 *
 *
 * @author zeroleaf
 */
public class Config {

    private final PrintStream stream;
    private final long totalStep;
    private final int  barWidth;
    private final char completeChar;
    private final char incompleteChar;
    private final long updateInterval;

    private Config(Builder builder) {
        this.stream         = builder.stream;
        this.totalStep      = builder.totalStep;
        this.barWidth       = builder.barWidth;
        this.completeChar   = builder.completeChar;
        this.incompleteChar = builder.incompleteChar;
        this.updateInterval = builder.updateInterval;
    }

    public static class Builder {
        private PrintStream stream  = System.out;
        private long totalStep      = 100;
        private int barWidth        = 70;
        private char completeChar   = '=';
        private char incompleteChar = '-';
        private long updateInterval = 1000;

        public Builder stream(PrintStream display) {
            this.stream = display;
            return this;
        }

        public Builder totalStep(long totalStep) {
            this.totalStep = totalStep;
            return this;
        }

        public Builder barWidth(int barWidth) {
            this.barWidth = barWidth;
            return this;
        }

        public Builder completeChar(char completeChar) {
            this.completeChar = completeChar;
            return this;
        }

        public Builder incompleteChar(char incompleteChar) {
            this.incompleteChar = incompleteChar;
            return this;
        }

        /**
         * Set updateInterval, unit is millisecond.
         *
         * @param updateInterval Min interval to update progress bar.
         * @return This builder.
         */
        public Builder updateInterval(long updateInterval) {
            this.updateInterval = updateInterval;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }

    public PrintStream getStream() {
        return stream;
    }

    public long getTotalStep() {
        return totalStep;
    }

    public int getBarWidth() {
        return barWidth;
    }

    public char getCompleteChar() {
        return completeChar;
    }

    public char getIncompleteChar() {
        return incompleteChar;
    }

    /**
     * The interval to update progress bar, unit is millisecond.
     *
     * @return The interval to update progress bar.
     */
    public long getUpdateInterval() {
        return updateInterval;
    }
}
