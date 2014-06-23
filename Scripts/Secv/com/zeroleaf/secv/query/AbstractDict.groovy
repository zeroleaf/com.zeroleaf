package com.zeroleaf.secv.query

import com.zeroleaf.secv.domain.Word

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by zeroleaf on 14-6-23.
 */
abstract class AbstractDict implements Query {

    protected Document page

    @Override
    def query(String keyword) {
        final String queryUrl = getRealQueryUrl(keyword)
        page = Jsoup.connect(queryUrl).get()

        def args = ["identify": keyword]
        args.put("pronounces", extractPronounces())
        args.put("meanings", extractMeanings())
        args.put("webTrans", extractWebTrans())
        args.put("wordGroups", extractWordGroups())

        return args as Word
    }

    protected abstract String getRealQueryUrl(String keyword)

    protected abstract String extractPronounces()

    protected abstract String extractMeanings()

    protected abstract String extractWebTrans()

    protected abstract String extractWordGroups()
}
