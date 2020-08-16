package dev.neeno.catalabot.dictionaryparser

import java.io.BufferedWriter
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement

class AbbreviationElementListener(private val context: ParsingContext) : ElementParserListener {
    override fun startElement(element: StartElement) {
        context.wordIsAnAbbreviation()
    }

    override fun characters(elementName:String, text: String) {
    }

    override fun endElement(element: EndElement, log: BufferedWriter) {
    }
}
