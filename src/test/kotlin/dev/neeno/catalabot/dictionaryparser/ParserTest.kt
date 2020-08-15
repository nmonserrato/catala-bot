package dev.neeno.catalabot.dictionaryparser

import dev.neeno.catalabot.Word
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class ParserTest {
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

    private fun runTest(tempFile: File): ArrayList<Word> {
        val writer = tempFile.bufferedWriter()
        val output = Parser().parse(writer)
        writer.flush()
        writer.close()
        return output
    }
}