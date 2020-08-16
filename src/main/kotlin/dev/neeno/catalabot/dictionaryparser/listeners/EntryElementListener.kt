package dev.neeno.catalabot.dictionaryparser.listeners

import dev.neeno.catalabot.dictionaryparser.ParsingContext
import dev.neeno.catalabot.dictionaryparser.attribute
import java.io.BufferedWriter
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement

class EntryElementListener(private val context: ParsingContext) : ElementParserListener {
    override fun startElement(element: StartElement) {
        context.frequency(element.attribute("frequency"))
    }

    override fun characters(elementName:String, text: String) {
        context.newWord(text)
    }

    override fun endElement(element: EndElement, log: BufferedWriter) {
        context.collectWord(log)
    }
}
