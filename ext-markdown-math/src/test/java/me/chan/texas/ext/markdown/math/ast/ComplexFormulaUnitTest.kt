package me.chan.texas.ext.markdown.math.ast

import me.chan.texas.utils.CharStream
import org.junit.Assert
import org.junit.Test
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class ComplexFormulaUnitTest {

    @Test
    fun test() {
        val inputStream = BufferedReader(FileReader(File("real_word_formula.txt")))
        val lines = inputStream.readLines()
        var i = 0;
        while (i < lines.size) {
            println(lines[i++])
            while (i < lines.size && lines[i].length > 0) {
                val input = lines[i++]
                val expectedOutput = lines[i++]
                assertParsesTo(input, expectedOutput)
            }
            ++i
        }
    }

    @Throws(MathParseException::class)
    fun parse(input: String): MathList {
        val stream = CharStream(input, 0, input.length)
        val parser = MathParser(stream)
        return parser.parse()
    }

    fun assertParsesTo(input: String, expectedOutput: String) {
        val ast: MathList = parse(input)
        val actual = ast.toString()
        println(">>> ${input}")
        println("<<< ${expectedOutput}")
        Assert.assertEquals(
            "输入: $input",
            expectedOutput,
            actual
        )
    }
}