package dev.neeno.catalabot.dictionaryparser

import java.io.BufferedWriter
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement

class ExampleElementListener(private val context: ParsingContext) : ElementParserListener {
    override fun startElement(element: StartElement) {
    }

    override fun characters(elementName:String, text: String) {
        val prefix = when (elementName) {
            "catexamp" -> "(ca.) "
            "engexamp" -> "(en.) "
            else -> ""
        }
        context.charsForExample(text, prefix)
    }

    override fun endElement(element: EndElement, log: BufferedWriter) {
        context.exampleParseCompleted()
    }
}
