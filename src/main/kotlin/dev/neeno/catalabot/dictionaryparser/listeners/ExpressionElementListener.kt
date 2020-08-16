package dev.neeno.catalabot.dictionaryparser.listeners

import dev.neeno.catalabot.dictionaryparser.ParsingContext
import java.io.BufferedWriter
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement

class ExpressionElementListener(private val context: ParsingContext) : ElementParserListener {
    override fun startElement(element: StartElement) {
    }

    override fun characters(elementName:String, text: String) {
        context.addExpression(text)
    }

    override fun endElement(element: EndElement, log: BufferedWriter) {
    }
}
