#!/usr/bin/env groovy

// @version 0.1.0

package com.zeroleaf.secv

@GrabConfig(systemClassLoader = true)
@Grab(group = 'org.xerial', module = 'sqlite-jdbc', version = '3.7.2')
@Grab(group = 'org.jsoup', module = 'jsoup', version = '1.7.3')

import com.zeroleaf.secv.query.SearchEngine

/**
 * Created by zeroleaf on 14-6-23.
 */
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
