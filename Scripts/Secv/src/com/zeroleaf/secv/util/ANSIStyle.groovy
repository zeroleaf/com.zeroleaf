package com.zeroleaf.secv.util

/**
 * Created by zeroleaf on 14-6-23.
 */
class ANSIStyle {

    private ANSIStyle() {}

    private static final boolean canBeStyled
    static {
        canBeStyled = System.console() != null
    }

    static final String ANSI_RESET = "\u001B[0m"
    static final String ANSI_BRIGHT = "\u001B[1m"

    static final String ANSI_BLACK = "\u001B[30m"
    static final String ANSI_RED = "\u001B[31m"
    static final String ANSI_GREEN = "\u001B[32m"
    static final String ANSI_YELLOW = "\u001B[33m"
    static final String ANSI_BLUE = "\u001B[34m"
    static final String ANSI_MAGENTA = "\u001B[35m"
    static final String ANSI_CYAN = "\u001B[36m"
    static final String ANSI_WHITE = "\u001B[37m"

    static final String ANSI_LBLACK = "\u001B[90m"
    static final String ANSI_LRED = "\u001B[91m"
    static final String ANSI_LGREEN = "\u001B[92m"
    static final String ANSI_LYELLOW = "\u001B[93m"
    static final String ANSI_LBLUE = "\u001B[94m"
    static final String ANSI_LMAGENTA = "\u001B[95m"
    static final String ANSI_LCYAN = "\u001B[96m"
    static final String ANSI_LWHITE = "\u001B[97m"

    static String bright(Object obj) { styleStr(ANSI_BRIGHT, obj) }

    static String red(Object obj) { styleStr(ANSI_RED, obj) }

    static String error(Object obj) { red(obj) }

    static String green(Object obj) { styleStr(ANSI_GREEN, obj) }

    static String yellow(Object obj) { styleStr(ANSI_YELLOW, obj) }

    static String blue(Object obj) { styleStr(ANSI_BLUE, obj) }

    static String magenta(Object obj) { styleStr(ANSI_MAGENTA, obj) }

    static String cyan(Object obj) { styleStr(ANSI_CYAN, obj) }

    static String lblack(Object obj) { styleStr(ANSI_LBLACK, obj) }

    static String lcyan(Object obj) { styleStr(ANSI_LCYAN, obj) }

    static String lyellow(Object obj) { styleStr(ANSI_LYELLOW, obj) }

    static String styleStr(String style, Object obj) {
        if (!canBeStyled)
            return obj.toString()
        return style + obj.toString() + ANSI_RESET
    }

    static String style(Object obj, String first, String ... others) {
        return styleStr(resolveStyle(first, others), obj)
    }

    private static String resolveStyle(String first, String ... others) {
        if (others.length == 0)
            return first;
        for (String style : others)
            first += style;
        return first;
    }
}
