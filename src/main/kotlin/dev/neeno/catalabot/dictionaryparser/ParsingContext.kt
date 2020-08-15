package dev.neeno.catalabot.dictionaryparser

import dev.neeno.catalabot.Word
import org.apache.commons.lang3.StringUtils
import java.io.BufferedWriter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class ParsingContext {
    private var wordFrequency: Long = -1
    private var currentWord = ""
    private val tags = HashSet<String>()
    private val translation = StringBuilder()
    private val translations = LinkedList<String>()
    private val example = StringBuilder()
    private val examples = LinkedList<String>()
    private val synonym = StringBuilder()
    private val synonyms = LinkedList<String>()
    private var acceptedWord = true
    private val collectedWords = ArrayList<Word>()

    fun newWord(value: String) {
        this.currentWord = sanitize(value)
    }

    fun frequency(value: String) {
        this.wordFrequency = if (StringUtils.isBlank(value)) 0 else value.toLong()

    }

    fun addTag(value: String) {
        tags.add(sanitize(value))
    }

    fun charsForSynonym(value: String) {
        synonym.append(sanitize(value))
    }

    fun synonymParseCompleted() {
        synonyms.add(sanitize(synonym.toString()))
        synonym.clear()
    }

    fun charsForExample(value: String, prefix: String = "") {
        example.append(prefix + sanitize(value))
    }

    fun exampleParseCompleted() {
        examples.add(sanitize(example.toString()))
        example.clear()
    }

    fun charsForTranslation(value: String, prefix: String = "") {
        translation.append(prefix + sanitize(value))
    }

    fun addExpression(value: String) {
        translations.add("(exp.) ${sanitize(value)}")
    }

    fun translationParseCompleted() {
        translations.add(sanitize(translation.toString()))
        translation.clear()
    }

    fun collectWord(log: BufferedWriter) {
        if (acceptedWord) {
            val word = Word(currentWord, wordFrequency, tags, synonyms, examples, translations)
            collectedWords.add(word)
            log.appendln(word.toString())
        }

        this.resetForNewWord()
    }

    fun collectedWords(): List<Word> {
        return collectedWords
    }

    fun wordIsAnAbbreviation() {
        this.acceptedWord = false
    }

    private fun sanitize(raw: String?) =
        StringUtils.trim(StringUtils.defaultIfEmpty(raw, ""))
            .replace("<b>", " ")
            .replace("</b>", " ")
            .replace("<i>, ", " ")
            .replace("</i> ", " ")

    private fun resetForNewWord() {
        wordFrequency = -1
        currentWord = ""
        tags.clear()
        translation.clear()
        translations.clear()
        example.clear()
        examples.clear()
        synonym.clear()
        synonyms.clear()
        acceptedWord = true
    }
}