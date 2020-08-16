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
    private var filtered: List<Word>? = null

    companion object {
        const val NEW_LINE = "<pre>\n\n</pre>"

        fun initializeFromFiles(): Dictionary {
            val parser = DictionaryParser()
            val words = parser.parseAllFiles(BufferedWriter(StringWriter()))
            return Dictionary(words)
        }
    }

    fun randomWord(): String {
        if (filtered == null) {
            filtered = words
                .filter { it.examples.isNotEmpty() }
                .filter { it.synonyms.isNotEmpty() }
                .filter { it.tags.isNotEmpty() }
                .filter { it.translations.isNotEmpty() }
        }
        val aWord = filtered!!.random(random)

        val builder = StringBuilder("<b>${aWord.original}</b>$NEW_LINE")
        builder.addList("translations", aWord.translations)
        builder.addList("synonyms", aWord.synonyms)
        builder.addList("examples", aWord.examples)
        builder.addList("categories", aWord.tags, elementPrefix = "* ", elementSuffix = " ")

        return builder.toString()
    }

    private fun StringBuilder.addList(
        listName: String,
        list: Collection<String>,
        elementPrefix: String = "- ",
        elementSuffix: String = NEW_LINE
    ) {
        if (list.isNotEmpty()) {
            this.append("$NEW_LINE<i>$listName</i>$NEW_LINE")
            list.forEach { this.append("$elementPrefix$it$elementSuffix") }
        }
    }
}
