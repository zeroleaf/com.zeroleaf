package com.zeroleaf.secv.view

import static com.zeroleaf.secv.util.ANSIStyle.*

import com.zeroleaf.secv.domain.Word

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by zeroleaf on 14-6-23.
 */
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

    def renderPronounces(Word word) {
        if (word.getPronounces()) {
            String str = word.getPronounces().replace(Word.SEPARATOR, "  ")
            Matcher m = pPronounce.matcher(str)
            StringBuffer sb = new StringBuffer("  ")
            m.each {
                String replacement = String.
                        format("[%s]", yellow(m.group(2)))
                if (it[1]) {
                    replacement = bright(it[1]) + " " + replacement
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
            println cyan("  单词释意")
            word.getMeanings().split(Word.SEPARATOR).each { meaning ->
                String str = "    * $meaning"
                pSpeech.matcher(meaning).each {
                    str = str.replaceFirst(it[1], lyellow(it[1]))
                }
                println str
            }
            return true
        }
    }

    def renderWebTrans(Word word) {
        if (word.getWebTrans()) {
            println cyan("  网络释意")
            print "    * "
            println word.getWebTrans().replaceAll(Word.SEPARATOR, " | ")
            return true
        }
    }

    static final Pattern pWordGroup = ~/([\w\s]+?)\s*[^\w\s]/

    def renderWordGroups(Word word) {
        if (word.getWordGroups()) {
            def wordGroups = word.getWordGroups().split(Word.SEPARATOR)
            println cyan("  词组")
            int max = Math.min(wordGroups.size(), 5)
            for (i in 0..<max) {
                String str = "    * ${wordGroups[i]}"
                pWordGroup.matcher(wordGroups[i]).each {
                    str = str.replaceFirst(it[1], lcyan(it[1]))
                }
                println str
            }
            return true
        }
    }
}
