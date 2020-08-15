package dev.neeno.catalabot

import org.apache.commons.lang3.StringUtils.*
import java.io.File
import java.util.*
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants
import kotlin.collections.HashSet

fun main() {
    val factory = XMLInputFactory.newInstance()
    val elementStack: Deque<String> = LinkedList()
    var char = 'a'
    var frequency: Long = -1
    var originalWord = ""
    val tags = HashSet<String>()
    val translation = StringBuilder()
    val translations = LinkedList<String>()
    val example = StringBuilder()
    val examples = LinkedList<String>()
    val synonym = StringBuilder()
    val synonyms = LinkedList<String>()
    var valid = true

    while (char <= 'z') {
        val file = File("src/main/resources/dictionaries/$char.dic")
        val reader = factory.createXMLEventReader(file.inputStream())
        while (reader.hasNext()) {
            val event = reader.nextEvent()
            if (event.eventType == XMLStreamConstants.START_ELEMENT) {
                val element = event.asStartElement()
                val elementName = element.name.localPart
                elementStack.push(elementName)
                if ("Entry" == elementName) {
                    val str = element.getAttributeByName(QName.valueOf("frequency")).value
                    frequency = if (isBlank(str)) 0 else str.toLong()
                } else if ("translation" == elementName) {
                    val attr = element.getAttributeByName(QName.valueOf("catagory"))
                    if (attr != null && isNotBlank(attr.value)) tags.add(attr.value)
                } else if (listOf("catacro","abbreviations","acronyms").contains(elementName)) {
                    valid = false
                } else if (listOf("prepositions", "adjectives", "verbs", "pronouns", "nouns", "adverbs", "conjunctions", "exclamations").contains(elementName)) {
                    tags.add(elementName)
                }

            } else if (event.eventType == XMLStreamConstants.END_ELEMENT) {
                when {
                    "Entry" == elementStack.peek() -> {
                        if (valid) {
                            val word = Word(
                                originalWord,
                                frequency,
                                tags,
                                synonyms,
                                examples,
                                translations
                            )
                            println(word)
                        }

                        frequency = -1
                        originalWord = ""
                        tags.clear()
                        translations.clear()
                        examples.clear()
                        synonyms.clear()
                        valid = true
                    }
                    "translation" == elementStack.peek() -> {
                        translations.add(sanified(translation.toString()))
                        translation.clear()
                    }
                    "synonyms" == elementStack.peek() -> {
                        synonyms.add(sanified(synonym.toString()))
                        synonym.clear()
                    }
                    listOf("example", "catexamp", "engexamp").contains(elementStack.peek()) -> {
                        examples.add(sanified(example.toString()))
                        example.clear()
                    }
                }

                elementStack.pop()

            } else if (event.eventType == XMLStreamConstants.CHARACTERS) {
                val text = sanified(event.asCharacters().data)
                if (isNotBlank(text)) {
                    when {
                        "Entry" == elementStack.peek() -> originalWord = text
                        "translation" == elementStack.peek() -> translation.append(text)
                        "synonyms" == elementStack.peek() -> synonym.append(text)
                        "example" == elementStack.peek() -> example.append(text)
                        "catexamp" == elementStack.peek() -> example.append("(ca.) $text")
                        "engexamp" == elementStack.peek() -> example.append("(en.) $text")
                        "expressions" == elementStack.peek() -> translations.add("(exp.) $text")
                    }

                }
            }
        }

        char++
    }

}

private fun sanified(raw: String?) =
    trim(defaultIfEmpty(raw, ""))
        .replace("<b>", " ")
        .replace("</b>", " ")
        .replace("<i>, ", " ")
        .replace("</i> ", " ")