package dev.neeno.catalabot.dictionaryparser

import dev.neeno.catalabot.Word
import org.apache.commons.lang3.ObjectUtils.defaultIfNull
import org.apache.commons.lang3.StringUtils.isNotBlank
import java.io.BufferedWriter
import java.util.*
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants
import javax.xml.stream.events.StartElement
import kotlin.collections.ArrayList

class DictionaryParser {
    private val abbreviationTags = listOf("catacro", "abbreviations", "acronyms")
    private val exampleTags = listOf("example", "catexamp", "engexamp")
    private val grammarParticles =
        listOf("prepositions", "adjectives", "verbs", "pronouns", "nouns", "adverbs", "conjunctions", "exclamations")

    fun parse(log: BufferedWriter): ArrayList<Word> {
        val output = ArrayList<Word>()
        val factory = XMLInputFactory.newInstance()
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)

        for (letter in 'a'..'z') {
            val resource = this.javaClass.getResourceAsStream("/dictionaries/$letter.dic")
            val reader = factory.createXMLEventReader(resource)
            val fileOutput = parseFile(reader, log)
            output.addAll(fileOutput)
        }

        return output
    }

    private fun parseFile(reader: XMLEventReader, log: BufferedWriter): List<Word> {
        val stack: Deque<String> = LinkedList()
        val context = ParsingContext()

        while (reader.hasNext()) {
            val event = reader.nextEvent()
            if (event.eventType == XMLStreamConstants.START_ELEMENT) {
                val element = event.asStartElement()
                val elementName = element.name.localPart
                stack.push(elementName)
                when {
                    stack.elementIs("Entry") -> context.frequency(element.attribute("frequency"))
                    stack.elementIs("translation") -> context.addTag(element.attribute("catagory"))
                    stack.elementIsIn(abbreviationTags) -> context.wordIsAnAbbreviation()
                    stack.elementIsIn(grammarParticles) -> context.addTag(elementName)
                }

            } else if (event.eventType == XMLStreamConstants.END_ELEMENT) {
                when {
                    stack.elementIs("Entry") -> context.collectWord(log)
                    stack.elementIs("translation") -> context.translationParseCompleted()
                    stack.elementIs("synonyms") -> context.synonymParseCompleted()
                    stack.elementIsIn(exampleTags) -> context.exampleParseCompleted()
                }
                stack.pop()

            } else if (event.eventType == XMLStreamConstants.CHARACTERS) {
                val text = event.asCharacters().data
                if (isNotBlank(text)) {
                    when {
                        stack.elementIs("Entry") -> context.newWord(text)
                        stack.elementIs("translation") -> context.charsForTranslation(text)
                        stack.elementIs("synonyms") -> context.charsForSynonym(text)
                        stack.elementIs("example") -> context.charsForExample(text)
                        stack.elementIs("catexamp") -> context.charsForExample(value = text, prefix = "(ca.) ")
                        stack.elementIs("engexamp") -> context.charsForExample(value = text, prefix = "(en.) ")
                        stack.elementIs("expressions") -> context.addExpression(text)
                    }
                }
            }
        }

        return context.collectedWords()
    }

    private fun Deque<String>.elementIs(name: String) = name == this.peek()
    private fun Deque<String>.elementIsIn(collection: List<String>) = collection.contains(this.peek())

    private fun StartElement.attribute(name: String): String {
        val attribute = this.getAttributeByName(QName.valueOf(name))
        return if (attribute == null) "" else defaultIfNull(attribute.value, "")
    }
}