package com.zeroleaf.secv

/**
 * Created by zeroleaf on 14-6-23.
 */
class PathConfig {
    static final String USER_HOME = System.getProperty("user.home")
    static final String DB_PATH = USER_HOME + "/.secv/"
    static {
        new File(DB_PATH).mkdirs()
    }
    static final String DB_FILE = DB_PATH + "se.db"
}
