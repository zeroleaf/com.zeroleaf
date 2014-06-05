package com.zeroleaf.test.tools.progressbar;

import com.zeroleaf.tools.progressbar.ProgressBar;
import com.zeroleaf.tools.progressbar.ProgressBarImpl;

/**
 *
 *
 * @author zeroleaf
 */
public class ProgressBarTest {

    public static void main(String[] args) throws InterruptedException {
        ProgressBar downloadBar = ProgressBarImpl.newDownloadProgressBar(1024000);
        downloadBar.start();
        for (int i = 10240; i < 10240000; i += 1024) {
            Thread.sleep(1000);
            downloadBar.tick(i);
        }
    }
}
