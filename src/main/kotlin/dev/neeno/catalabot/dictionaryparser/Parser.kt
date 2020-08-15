package dev.neeno.catalabot.dictionaryparser

import dev.neeno.catalabot.Word
import org.apache.commons.lang3.StringUtils.isNotBlank
import java.io.BufferedWriter
import java.io.File
import java.util.*
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants
import kotlin.collections.ArrayList

class Parser {
    fun parse(log: BufferedWriter): ArrayList<Word> {
        val factory = XMLInputFactory.newInstance()
        val output = ArrayList<Word>()

        for (letter in 'a'..'z') {
            val file = File("src/main/resources/dictionaries/$letter.dic").inputStream()
            val reader = factory.createXMLEventReader(file)
            val fileOutput = parseFile(reader, log)
            output.addAll(fileOutput)
        }

        return output
    }

    private fun parseFile(reader: XMLEventReader, log: BufferedWriter): List<Word> {
        val elementStack: Deque<String> = LinkedList()
        val context = ParsingContext()

        while (reader.hasNext()) {
            val event = reader.nextEvent()
            if (event.eventType == XMLStreamConstants.START_ELEMENT) {
                val element = event.asStartElement()
                val elementName = element.name.localPart
                elementStack.push(elementName)
                if ("Entry" == elementName) {
                    val str = element.getAttributeByName(QName.valueOf("frequency")).value
                    context.frequency(str)
                } else if ("translation" == elementName) {
                    val attr = element.getAttributeByName(QName.valueOf("catagory"))
                    if (attr != null && isNotBlank(attr.value)) context.addTag(attr.value)
                } else if (listOf("catacro", "abbreviations", "acronyms").contains(elementName)) {
                    context.wordIsAnAbbreviation()
                } else if (listOf(
                        "prepositions",
                        "adjectives",
                        "verbs",
                        "pronouns",
                        "nouns",
                        "adverbs",
                        "conjunctions",
                        "exclamations"
                    ).contains(elementName)
                ) {
                   context.addTag(elementName)
                }

            } else if (event.eventType == XMLStreamConstants.END_ELEMENT) {
                when {
                    "Entry" == elementStack.peek() -> context.collectWord(log)
                    "translation" == elementStack.peek() -> context.translationParseCompleted()
                    "synonyms" == elementStack.peek() -> context.synonymParseCompleted()
                    listOf("example", "catexamp", "engexamp").contains(elementStack.peek()) -> context.exampleParseCompleted()
                }

                elementStack.pop()

            } else if (event.eventType == XMLStreamConstants.CHARACTERS) {
                val text = event.asCharacters().data
                if (isNotBlank(text)) {
                    when {
                        "Entry" == elementStack.peek() -> context.newWord(text)
                        "translation" == elementStack.peek() -> context.charsForTranslation(text)
                        "synonyms" == elementStack.peek() -> context.charsForSynonym(text)
                        "example" == elementStack.peek() -> context.charsForExample(text)
                        "catexamp" == elementStack.peek() -> context.charsForExample(value = text, prefix = "(ca.) ")
                        "engexamp" == elementStack.peek() -> context.charsForExample(value = text, prefix = "(en.) ")
                        "expressions" == elementStack.peek() -> context.addExpression(text)
                    }
                }
            }
        }

        return context.collectedWords()
    }
}