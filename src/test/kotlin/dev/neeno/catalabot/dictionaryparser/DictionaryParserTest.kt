package dev.neeno.catalabot.dictionaryparser

import dev.neeno.catalabot.Word
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class DictionaryParserTest {
    @Test
     fun golden_test() {
        val tempFile = File("temp.txt")
        tempFile.delete()
        val golden = File("golden-master.txt")

        val output = runTest(tempFile)
        val grouped = output.groupBy { it.original }
        assertEquals(9446, output.size)
        assertEquals(golden.readText(), tempFile.readText())
        tempFile.delete()
    }

    private fun runTest(tempFile: File): List<Word> {
        val writer = tempFile.bufferedWriter()
        val output = DictionaryParser().parse(writer)
        writer.flush()
        writer.close()
        return output
    }
}