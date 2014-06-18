#!/usr/bin/env groovy

@GrabConfig(systemClassLoader=true)
@Grab(group='org.xerial', module='sqlite-jdbc', version='3.7.2')
@Grab(group='org.jsoup', module='jsoup', version='1.7.3')

import groovy.sql.Sql
import groovy.sql.GroovyRowResult

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.sql.Timestamp

class Secv {

    def inReader = System.console()
//    def inReader = new Console()
    SearchEngine searchEngine = SearchEngine.getInstance()

    static main(args) {
        new Secv().run()
    }

    def run() {
        while (true) {
            def keyWord = inReader.readLine("> ")
            if (!keyWord) {
                break
            }
            searchEngine.query(keyWord)
        }
        println "Good bye"
    }
}

/* ------------------------ Helper --------------------- */

class SearchEngine implements Query {

    Query youdao = new YouDao()
    Sqlite sqlite = new Sqlite()

    View view = new DictView()


    public static SearchEngine getInstance() {
        return new SearchEngine()
    }

    @Override
    def query(String keyword) {
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

/* ------------------------ Query ---------------------- */

interface Query {

    def query(String keyword)
}

// 规则
// 1. 词组不显示发音信息
class YouDao implements Query {

    final String QUERY_URL_TEMPLATE = "http://dict.youdao.com/search?q=:keyWord"

    Document page

    private static final String[] EXTRACT_PROPERTIES =
            ["pronounces", "meanings", "webTrans", "wordGroups"]

    /**
     * 查询, 并返回查询到的单词实例.
     *
     * @param keyword 查询关键字
     * @return 关键字对应的 word 实例.
     */
    @Override
    def query(String keyword) {
        def queryUrl = getRealQueryUrl(keyword)
        page = Jsoup.connect(queryUrl).get()

        def args = [:]
        EXTRACT_PROPERTIES.each {
            String methodName = "extract" << it.capitalize()
            args.put(it, "$methodName"())
        }
        args.put("identify", keyword)

        return args as Word
    }

    def extractPronounces() {
        page.select("#phrsListTab span.pronounce").collect { it.text() }.findAll {
            String trimStr = it.trim()
            return trimStr && trimStr != "英" && trimStr != "美"
        }.join(Word.SEPARATOR)
    }

    private String getRealQueryUrl(String keyWord) {
        return QUERY_URL_TEMPLATE.replace(":keyWord", keyWord)
    }

    static final String CN_SELECTOR = "#phrsListTab li"
    static final String ENG_SELECTOR = "#phrsListTab ul > p"

    def extractMeanings() {
        def meanings = extractWordMeanings(CN_SELECTOR)
        return meanings ? meanings : extractWordMeanings(ENG_SELECTOR)
    }

    def extractWordMeanings(String selector) {
        page.select(selector).collect { it.text() }.join(Word.SEPARATOR)
    }

    def extractWebTrans() {
        def webTrans = page.select("#tWebTrans > div").collect {
            it.select("div span").text().trim()
        }
        if (webTrans) {
            webTrans.remove(webTrans.size() - 1)
        }
        return webTrans.join(Word.SEPARATOR)
    }

    def extractWordGroups() {
        page.select("#wordGroup p.wordGroup").collect { it.text() }.join(Word.SEPARATOR)
    }
}

/* ------------------------ View ---------------------- */

interface View {
    def render(obj)
}


class DictView implements View {

    @Override
    def render(word) {
        assert word instanceof Word
        renderWord(word)
    }

    private static final String[] RENDER_PROPERTIES =
            ["pronounces", "meanings", "webTrans", "wordGroups"]

    void renderWord(Word word) {
        int  renderCount = RENDER_PROPERTIES.size()
        RENDER_PROPERTIES.each {
            String methodName = "render" << it.capitalize()
            "$methodName"(word) ? println() : renderCount--
        }
        renderCount == 0 ? println() : ""
    }

    def renderPronounces(Word word) {
        if (word.getPronounces()) {
            print "  ${word.getPronounces().replaceAll(Word.SEPARATOR, "  ")}"
            return true
        }
    }

    def renderMeanings(Word word) {
        if (word.getMeanings()) {
            println "  单词释意"
            word.getMeanings().split(Word.SEPARATOR).each {
                println "    * $it"
            }
            return true
        }
    }

    def renderWebTrans(Word word) {
        if (word.getWebTrans()) {
            print "  网络释意\n    * "
            println word.getWebTrans().split(Word.SEPARATOR).join(" | ")
            return true
        }
    }

    def renderWordGroups(Word word) {
        if (word.getWordGroups()) {
            def wordGroups = word.getWordGroups().split(Word.SEPARATOR)
            println "  词组"
            int max = Math.min(wordGroups.size(), 5)
            for (i in 0..<max) {
                println "    * ${wordGroups[i]}"
            }
            return true
        }
    }
}



/* ----------------------- DB ----------------------- */

class Word {

    static final String SEPARATOR = "~"

    String identify
    String pronounces
    String meanings
    String webTrans
    String wordGroups
    Timestamp addTime
}

class Sqlite {

    private static final String WORD_CREATE_SQL = '''
CREATE TABLE IF NOT EXISTS word (
    identify TEXT PRIMARY KEY,
    pronounces TEXT,
    meanings TEXT,
    webTrans TEXT,
    wordGroups TEXT,
    addTime DATETIME
)
'''

    private static final dbSettings = [
            url: "jdbc:sqlite:se.db",
            driver: "org.sqlite.JDBC",
            user: "sa",
            password: ""
    ]
    Sql sql

    public Sqlite() {
        sql = Sql.newInstance(dbSettings)
        sql.execute(WORD_CREATE_SQL)
    }

    private static final String WORD_INSERT_SQL =
            "INSERT INTO word VALUES (?, ?, ?, ?, ?, datetime('now', 'localtime'))"


    void addWord(Word word) {
        sql.executeInsert(WORD_INSERT_SQL,
                          [word.identify, word.pronounces, word.meanings, word.webTrans, word.wordGroups])
    }

    private static final String WORD_SELECT_SQL =
            "SELECT * FROM word WHERE identify = ?"


    Word getWord(String identify) {
        Word word = null
        sql.query(WORD_SELECT_SQL, [identify]) {
            GroovyRowResult rs = it.toRowResult()
            if (rs.get("identify")) {
                def args = [:]
                rs.each { key, value ->
                    args.put(key, value)
                }
                args.put("addTime", Timestamp.valueOf(args.get("addTime")))
                word = args as Word
            }
        }
        return word
    }
}
