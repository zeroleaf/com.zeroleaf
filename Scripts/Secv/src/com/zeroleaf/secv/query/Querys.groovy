package com.zeroleaf.secv.query

import com.zeroleaf.secv.domain.Word
import com.zeroleaf.secv.persistence.Sqlite
import com.zeroleaf.secv.view.DictView
import com.zeroleaf.secv.view.View

/**
 * Created by zeroleaf on 14-6-23.
 */
class Querys {

    private static final Query youdao = new YouDao()
    private static final Sqlite sqlite = new Sqlite()

    private static final View view = new DictView()

    static void query(String keyword) {
        keyword = keyword.trim()
        if (!keyword) {
            return
        }

        def result = sqlite.getWord(keyword)
        if (!result) {
            result = queryAndSaveWord(keyword)
        }
        view.render(result)
    }

    static Word queryAndSaveWord(String identify) {
        try {
            Word word = youdao.query(identify) as Word
            sqlite.saveWord(word)
            return word
        } catch (Exception e) {
            throw new RuntimeException("请在连接网络之后重试")
        }
    }
}
