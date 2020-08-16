package dev.neeno.catalabot.dictionaryparser

import java.io.BufferedWriter
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement

class SynonymsElementListener(private val context: ParsingContext) : ElementParserListener {
    override fun startElement(element: StartElement) {
    }

    override fun characters(elementName:String, text: String) {
        context.charsForSynonym(text)
    }

    override fun endElement(element: EndElement, log: BufferedWriter) {
        context.synonymParseCompleted()
    }
}
