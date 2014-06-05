package com.zeroleaf.tools.progressbar.renderings;

import com.zeroleaf.tools.progressbar.Config;
import com.zeroleaf.tools.progressbar.Progress;

/**
 *
 *
 * @author zeroleaf
 */
public interface Rendering {

    /**
     * The token this rendering to render.
     *
     * @return Token this rendering to render.
     */
    String getToken();

    /**
     * Render progressbar using the specified config and progress.
     *
     * @param config render config.
     * @param progress progressbar progress.
     * @return Rendered result string.
     */
    String render(Config config, Progress progress);
}
