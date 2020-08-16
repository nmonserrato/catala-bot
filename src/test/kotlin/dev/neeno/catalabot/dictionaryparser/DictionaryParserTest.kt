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
        assertEquals(9446, output.size)
        assertEquals(golden.readText(), tempFile.readText())
        tempFile.delete()
    }

    private fun runTest(tempFile: File): List<Word> {
        val writer = tempFile.bufferedWriter()
        val output = DictionaryParser().parseAllFiles(writer)
        writer.flush()
        writer.close()
        return output
    }
}