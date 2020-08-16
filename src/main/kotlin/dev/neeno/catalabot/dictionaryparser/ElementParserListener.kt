package dev.neeno.catalabot.dictionaryparser

import java.io.BufferedWriter
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement

interface ElementParserListener {
    fun startElement(element: StartElement)
    fun characters(elementName:String, text: String)
    fun endElement(element: EndElement, log: BufferedWriter)

    companion object {
        fun noop(): ElementParserListener = NoopElementListener()
    }
}

private class NoopElementListener : ElementParserListener {
    override fun startElement(element: StartElement) {
    }

    override fun characters(elementName: String, text: String) {
    }

    override fun endElement(element: EndElement, log: BufferedWriter) {
    }
}