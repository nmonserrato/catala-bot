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
    private val context = ParsingContext()
    private val listeners = initializeListeners()

    //TODO remove log after this is tested properly
    fun parse(log: BufferedWriter): List<Word> {
        val output = ArrayList<Word>()
        val factory = XMLInputFactory.newInstance()
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)

        for (letter in 'a'..'z') {
            val resource = this.javaClass.getResourceAsStream("/dictionaries/$letter.dic")
            val reader = factory.createXMLEventReader(resource)
            output.addAll(parseDictionaryFile(reader, log))
        }

        return output
    }

    private fun parseDictionaryFile(reader: XMLEventReader, log: BufferedWriter): List<Word> {
        val stack: Deque<String> = LinkedList()

        while (reader.hasNext()) {
            val event = reader.nextEvent()
            if (event.eventType == XMLStreamConstants.START_DOCUMENT) {
                context.documentStarted()
            } else if (event.eventType == XMLStreamConstants.START_ELEMENT) {
                val element = event.asStartElement()
                stack.push(element.name.localPart)
                elementListener(stack).startElement(element)
            } else if (event.eventType == XMLStreamConstants.END_ELEMENT) {
                elementListener(stack).endElement(event.asEndElement(), log)
                stack.pop()
            } else if (event.eventType == XMLStreamConstants.CHARACTERS) {
                val text = event.asCharacters().data
                if (isNotBlank(text)) {
                    elementListener(stack).characters(stack.peek(), text)
                }
            }
        }

        return context.collectedWords()
    }

    private fun elementListener(stack: Deque<String>): ElementParserListener =
        defaultIfNull(listeners[stack.peek()], ElementParserListener.noop())!!

    private fun initializeListeners(): Map<String, ElementParserListener> {
        return mapOf(
            "expressions" to ExpressionElementListener(context),
            "Entry" to EntryElementListener(context),
            "translation" to TranslationElementListener(context),
            "synonyms" to SynonymsElementListener(context),
            "example" to ExampleElementListener(context),
            "catexamp" to ExampleElementListener(context),
            "engexamp" to ExampleElementListener(context),
            "catacro" to AbbreviationElementListener(context),
            "abbreviations" to AbbreviationElementListener(context),
            "acronyms" to AbbreviationElementListener(context),
            "prepositions" to GrammarParticlesElementListener(context),
            "adjectives" to GrammarParticlesElementListener(context),
            "verbs" to GrammarParticlesElementListener(context),
            "pronouns" to GrammarParticlesElementListener(context),
            "nouns" to GrammarParticlesElementListener(context),
            "adverbs" to GrammarParticlesElementListener(context),
            "conjunctions" to GrammarParticlesElementListener(context),
            "exclamations" to GrammarParticlesElementListener(context)
        )
    }
}

fun StartElement.attribute(name: String): String {
    val attribute = this.getAttributeByName(QName.valueOf(name))
    return if (attribute == null) "" else defaultIfNull(attribute.value, "")
}
