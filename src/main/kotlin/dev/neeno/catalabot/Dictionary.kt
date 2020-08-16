package dev.neeno.catalabot

import dev.neeno.catalabot.dictionaryparser.DictionaryParser
import java.io.BufferedWriter
import java.io.StringWriter
import java.lang.System.currentTimeMillis
import kotlin.random.Random

class Dictionary private constructor(
    private val words: List<Word>
) {
    private val random: Random = Random(currentTimeMillis())

    companion object {
        fun initializeFromFiles(): Dictionary {
            val parser = DictionaryParser()
            val words = parser.parseAllFiles(BufferedWriter(StringWriter()))
            return Dictionary(words)
        }
    }

    fun randomWord(): String {
        val aWord = words.random(random)
        return "<b>${aWord.original}</b><pre>\n</pre>${aWord.translations[0]}"
    }
}
