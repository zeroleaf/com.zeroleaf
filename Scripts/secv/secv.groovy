#!/usr/bin/env groovy

// @version 0.0.1

@GrabConfig(systemClassLoader = true)
@Grab(group = 'org.xerial', module = 'sqlite-jdbc', version = '3.7.2')
@Grab(group = 'org.jsoup', module = 'jsoup', version = '1.7.3')

import groovy.sql.Sql
import groovy.sql.GroovyRowResult

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.sql.Timestamp
import java.util.regex.Matcher
import java.util.regex.Pattern

class Secv {

    def inReader = System.console()
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

/* ------------------------ Query ---------------------- */

interface Query {

    def query(String keyword)
}

// 规则
// 1. 词组不显示发音信息
class YouDao implements Query {

    final String QUERY_URL_TEMPLATE = "http://dict.youdao.com/search?q=:keyWord"

    Document page

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

        def args = ["identify": keyword]
        args.put("pronounces", extractPronounces())
        args.put("meanings", extractMeanings())
        args.put("webTrans", extractWebTrans())
        args.put("wordGroups", extractWordGroups())

        return args as Word
    }

    String extractPronounces() {
        return page.select("#phrsListTab span.pronounce").collect { it.text() }.
                findAll {
                    String trimStr = it.trim()
                    return trimStr && trimStr != "英" && trimStr != "美"
                }.join(Word.SEPARATOR)
    }

    private String getRealQueryUrl(String keyWord) {
        return QUERY_URL_TEMPLATE.replace(":keyWord", keyWord)
    }

    static final String CN_SELECTOR = "#phrsListTab li"
    static final String ENG_SELECTOR = "#phrsListTab ul > p"

    String extractMeanings() {
        def meanings = extractWordMeanings(CN_SELECTOR)
        return meanings ? meanings : extractWordMeanings(ENG_SELECTOR)
    }

    String extractWordMeanings(String selector) {
        return page.select(selector).collect { it.text() }.join(Word.SEPARATOR)
    }

    String extractWebTrans() {
        def webTrans = page.select("#tWebTrans > div").collect {
            it.select("div span").text().trim()
        }
        if (webTrans) {
            webTrans.remove(webTrans.size() - 1)
        }
        return webTrans.join(Word.SEPARATOR)
    }

    String extractWordGroups() {
        return page.select("#wordGroup p.wordGroup").collect { it.text() }.
                join(Word.SEPARATOR)
    }
}

/* ------------------------ View ---------------------- */

interface View {

    def render(obj)
}

class ANSIStyle {

    private ANSIStyle() {}

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

    static String green(Object obj) { styleStr(ANSI_GREEN, obj) }

    static String yellow(Object obj) { styleStr(ANSI_YELLOW, obj) }

    static String blue(Object obj) { styleStr(ANSI_BLUE, obj) }

    static String magenta(Object obj) { styleStr(ANSI_MAGENTA, obj) }

    static String cyan(Object obj) { styleStr(ANSI_CYAN, obj) }

    static String lblack(Object obj) { styleStr(ANSI_LBLACK, obj) }

    static String lcyan(Object obj) { styleStr(ANSI_LCYAN, obj) }

    static String lyellow(Object obj) { styleStr(ANSI_LYELLOW, obj) }

    static String styleStr(String style, Object obj) {
        return style + obj.toString() + ANSI_RESET
    }
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
        int renderCount = RENDER_PROPERTIES.size()
        RENDER_PROPERTIES.each {
            String methodName = "render" << it.capitalize()
            "$methodName"(word) ? println() : renderCount--
        }
        renderCount == 0 ? println() : ""
    }

    static final Pattern pPronounce = ~/(?:(.)\s+)?\[(.*?)\]/

    // @TODO 如果只有一个发音 没有 英, 美 时, 模式无法匹配从而无法上色
    // 举例: [dʒun]
    def renderPronounces(Word word) {
        if (word.getPronounces()) {
            String str = word.getPronounces().replace(Word.SEPARATOR, "  ")
            Matcher m = pPronounce.matcher(str)
            StringBuffer sb = new StringBuffer("  ")
            m.each {
                String replacement = String.
                        format("[%s]", ANSIStyle.yellow(m.group(2)))
                if (it[1]) {
                    replacement = ANSIStyle.bright(it[1]) + " " + replacement
                }
                m.appendReplacement(sb, replacement)
            }
            m.appendTail(sb)
            print sb.toString()
            return true
        }
    }

    static final Pattern pSpeech = ~/(.*)\./

    def renderMeanings(Word word) {
        if (word.getMeanings()) {
            println ANSIStyle.cyan("  单词释意")
            word.getMeanings().split(Word.SEPARATOR).each { meaning ->
                String str = "    * $meaning"
                pSpeech.matcher(meaning).each {
                    str = str.replaceFirst(it[1], ANSIStyle.lyellow(it[1]))
                }
                println str
            }
            return true
        }
    }

    def renderWebTrans(Word word) {
        if (word.getWebTrans()) {
            println ANSIStyle.cyan("  网络释意")
            print "    * "
            println word.getWebTrans().replaceAll(Word.SEPARATOR, " | ")
            return true
        }
    }

    static final Pattern pWordGroup = ~/([\w\s]+?)\s*[^\w\s]/

    def renderWordGroups(Word word) {
        if (word.getWordGroups()) {
            def wordGroups = word.getWordGroups().split(Word.SEPARATOR)
            println ANSIStyle.cyan("  词组")
            int max = Math.min(wordGroups.size(), 5)
            for (i in 0..<max) {
                String str = "    * ${wordGroups[i]}"
                pWordGroup.matcher(wordGroups[i]).each {
                    str = str.replaceFirst(it[1], ANSIStyle.lcyan(it[1]))
                }
                println str
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

    private static final String WORD_INDEX_IDENTIFY = '''
CREATE UNIQUE INDEX IF NOT EXISTS identify_idx ON word (identify)
'''

    private static final dbSettings = [
            url     : "jdbc:sqlite:${PathConfig.DB_FILE}",
            driver  : "org.sqlite.JDBC",
            user    : "sa",
            password: ""
    ]
    Sql sql

    public Sqlite() {
        sql = Sql.newInstance(dbSettings)
        sql.execute(WORD_CREATE_SQL)
        sql.execute(WORD_INDEX_IDENTIFY)
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
            if (it.next()) {
                GroovyRowResult rs = it.toRowResult()
                def args = [:]
                rs.each { key, value ->
                    args.put(key, value)
                }
                args.put("addTime",
                         Timestamp.valueOf(args.get("addTime").toString()))
                word = args as Word
            }
        }
        return word
    }
}

/* -------------------------- File --------------------------- */

class PathConfig {

    static final String USER_HOME = System.getProperty("user.home")
    static final String DB_PATH = USER_HOME + "/.secv/"
    static {
        new File(DB_PATH).mkdirs()
    }
    static final String DB_FILE = DB_PATH + "se.db"
}
