package com.zeroleaf.secv.query

import com.zeroleaf.secv.domain.Word

/**
 * Created by zeroleaf on 14-6-23.
 */
class YouDao extends AbstractDict {

    private final String QUERY_URL_TEMPLATE =
            "http://dict.youdao.com/search?q=:keyWord"

    @Override
    String extractPronounces() {
        return page.select("#phrsListTab span.pronounce").collect { it.text() }.
                findAll {
                    String trimStr = it.trim()
                    return trimStr && trimStr != "英" && trimStr != "美"
                }.join(Word.SEPARATOR)
    }

    @Override
    protected String getRealQueryUrl(String keyWord) {
        return QUERY_URL_TEMPLATE.replace(":keyWord", keyWord)
    }

    static final String CN_SELECTOR = "#phrsListTab li"
    static final String ENG_SELECTOR = "#phrsListTab ul > p"

    @Override
    String extractMeanings() {
        def meanings = extractWordMeanings(CN_SELECTOR)
        return meanings ? meanings : extractWordMeanings(ENG_SELECTOR)
    }

    String extractWordMeanings(String selector) {
        return page.select(selector).collect { it.text() }.join(Word.SEPARATOR)
    }

    @Override
    String extractWebTrans() {
        def webTrans = page.select("#tWebTrans > div").collect {
            it.select("div span").text().trim()
        }
        if (webTrans) {
            webTrans.remove(webTrans.size() - 1)
        }
        return webTrans.join(Word.SEPARATOR)
    }

    @Override
    String extractWordGroups() {
        return page.select("#wordGroup p.wordGroup").collect { it.text() }.
                join(Word.SEPARATOR)
    }
}
