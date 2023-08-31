import cn.taskeren.explode3.parser.CMapParser
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class TestCMapParser {

	private fun read(path: String): String {
		val inputStream = TestCMapParser::class.java.classLoader.getResourceAsStream(path)
			?: error("Resource not found: $path")
		return inputStream.bufferedReader().readText()
	}

	@Test
	fun testParse() {
		val c = assertDoesNotThrow { CMapParser.parse(read("test1.xml")) }

		assertEquals(1312, c.noteCount)
		assertContentEquals(emptyArray(), c.validate().toTypedArray())
	}

}