package dev.neeno.catalabot.dictionaryparser

import java.io.BufferedWriter
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement

class TranslationElementListener(private val context: ParsingContext) : ElementParserListener {
    override fun startElement(element: StartElement) {
        context.addTag(element.attribute("catagory"))
    }

    override fun characters(elementName:String, text: String) {
        context.charsForTranslation(text)
    }

    override fun endElement(element: EndElement, log: BufferedWriter) {
        context.translationParseCompleted()
    }
}
