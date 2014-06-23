package com.zeroleaf.secv.domain

import java.sql.Timestamp

/**
 * Created by zeroleaf on 14-6-23.
 */
class Word {

    static final String SEPARATOR = "~"

    String identify
    String pronounces
    String meanings
    String webTrans
    String wordGroups
    Timestamp addTime
}
