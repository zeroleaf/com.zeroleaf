package com.zeroleaf.secv.persistence

import com.zeroleaf.secv.PathConfig
import com.zeroleaf.secv.domain.Word
import groovy.sql.GroovyRowResult
import groovy.sql.Sql

import java.sql.Timestamp

/**
 * Created by zeroleaf on 14-6-23.
 */
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
