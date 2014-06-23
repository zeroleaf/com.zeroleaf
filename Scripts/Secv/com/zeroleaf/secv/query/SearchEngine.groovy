package com.zeroleaf.secv.query

import com.zeroleaf.secv.domain.Word
import com.zeroleaf.secv.persistence.Sqlite
import com.zeroleaf.secv.view.DictView
import com.zeroleaf.secv.view.View

/**
 * Created by zeroleaf on 14-6-23.
 */
class SearchEngine implements Query {
    Query youdao = new YouDao()
    Sqlite sqlite = new Sqlite()

    View view = new DictView()

    public static SearchEngine getInstance() {
        return new SearchEngine()
    }

    @Override
    def query(String keyword) {
        keyword = keyword.trim()
        if (!keyword) {
            return
        }
        def result = sqlite.getWord(keyword)
        if (result) {
            view.render(result)
        } else {
            result = youdao.query(keyword)
            sqlite.addWord(result as Word)
            view.render(result)
        }
    }
}
