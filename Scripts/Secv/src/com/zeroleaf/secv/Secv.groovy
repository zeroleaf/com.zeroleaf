#!/usr/bin/env groovy

// @version 0.0.3

package com.zeroleaf.secv

@GrabConfig(systemClassLoader = true)
@Grab(group = 'org.xerial', module = 'sqlite-jdbc', version = '3.7.2')
@Grab(group = 'org.jsoup', module = 'jsoup', version = '1.7.3')

import com.zeroleaf.secv.query.Querys

import com.zeroleaf.secv.util.ANSIStyle

/**
 * Created by zeroleaf on 14-6-23.
 */
class Secv {

    def inReader = System.console()

    static main(args) {
        new Secv().run()
    }

    def run() {
        while (true) {
            def keyWord = inReader.readLine("> ")
            if (!keyWord) {
                break
            }

            try {
                Querys.query(keyWord)
            } catch (Exception e) {
                println ANSIStyle.error("\n  ${e.getMessage()}")
                System.exit(1)
            }
        }
        println "Good bye"
    }
}
