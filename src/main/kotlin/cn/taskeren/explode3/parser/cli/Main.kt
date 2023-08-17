package cn.taskeren.explode3.parser.cli

import cn.taskeren.explode3.parser.CMapParser
import kotlin.io.path.*

fun main(args: Array<String>) {
	val pa = parseArg(args)

	val path = Path(pa.input)
	if(!path.exists()) return println("Input file does not exist!")
	if(!path.isRegularFile()) return println("Input file is not a file!")

	var cm = runCatching { CMapParser.parse(path.readText()) }.getOrNull()
		?: return println("Input file cannot be parsed!")

	when(pa.operation) {
		"analyze" -> {
			val es = "=".repeat(6)
			println("$es[ Analyze ]$es")
			println("[ Total ]")
			val total = listOf(cm.mainNotes, cm.leftNotes, cm.rightNotes)
			println("Total Note Count: ${total.sumOf { it.count() }}")
			println("Tap Note Count:   ${total.sumOf { it.countTap() }}")
			println("Hold Note Count:  ${total.sumOf { it.countHold() }}")
			println("Chain Note Count: ${total.sumOf { it.countChain() }}")

			println("[ Main ]")
			println("Total Note Count: ${cm.mainNotes.count()}")
			println("Tap Note Count:   ${cm.mainNotes.countTap()}")
			println("Hold Note Count:  ${cm.mainNotes.countHold()}")
			println("Chain Note Count: ${cm.mainNotes.countChain()}")

			println("[ Left ]")
			println("Total Note Count: ${cm.leftNotes.count()}")
			println("Tap Note Count:   ${cm.leftNotes.countTap()}")
			println("Hold Note Count:  ${cm.leftNotes.countHold()}")
			println("Chain Note Count: ${cm.leftNotes.countChain()}")

			println("[ Right ]")
			println("Total Note Count: ${cm.rightNotes.count()}")
			println("Tap Note Count:   ${cm.rightNotes.countTap()}")
			println("Hold Note Count:  ${cm.rightNotes.countHold()}")
			println("Chain Note Count: ${cm.rightNotes.countChain()}")
		}
		"transform" -> {
			cm = when(pa.transformOp) {
				"width" -> {
					val ratio = (pa.remainingArgs.getOrNull(0) ?: return println("Missing argument ratio"))
						.toDoubleOrNull() ?: return println("Invalid argument ratio: failed to parse to Double")
					cm.transform { it.copy(width = it.width * ratio) }
				}

				"mirror" -> {
					// swap two sides
					cm.copy(leftRegion = cm.rightRegion, leftNotes = cm.rightNotes, rightRegion = cm.leftRegion, rightNotes = cm.leftNotes)
				}

				else -> return println("Invalid TransformOp: ${pa.transformOp}")
			}

			// save if output present
			pa.output?.let(::Path)?.let {
				val str = runCatching { CMapParser.serialize(cm) }.getOrNull()
					?: return println("Failed to serialize the map.")
				it.writeText(str)
			}
		}
		else -> return println("Invalid Op: ${pa.operation}")
	}
}

private data class ParsedArgument(
	val input: String,
	val output: String?,

	val operation: String,

	val transformOp: String?,
	val remainingArgs: List<String>,
)

private val validOperations = arrayOf("analyze", "transform")
private val validTransformOp = arrayOf("width", "mirror")

private fun parseArg(rawArgs: Array<String>): ParsedArgument {

	val args = rawArgs.toMutableList()

	// extract input and output
	fun findValueForArg(vararg name: String, nullable: Boolean): String? {
		if(name.isEmpty()) { // empty names
			error("Invalid names for argument!")
		} else if(name.size == 1) {
			val singleName = name[0]
			val index = args.indexOf(singleName)
			return if(index < 0) { // key not found
				if(!nullable) {
					error("Unable to find argument $singleName")
				}
				null
			} else { // found key
				val value = args.getOrNull(index + 1)
				if(value != null) { // value found, then remove them from args
					args.removeAt(index)
					args.removeAt(index) // because the key has been removed, so the value index should offset -1
					value
				} else { // value not found
					if(!nullable) {
						error("Unable to get value of argument $singleName")
					}
					null
				}
			}
		} else {
			// if matching key is more than one, just gather them first
			val result = name.mapNotNull { findValueForArg(it, nullable = true) }
			if(result.size > 1) {
				error(
					"Find more than one value (${result.joinToString(separator = ", ")}) for argument [${
						name.joinToString(
							separator = ", "
						)
					}]"
				)
			}
			if(!nullable && result.isEmpty()) {
				error("No specified value for argument [${name.joinToString(separator = ", ")}]")
			}
			return result.getOrNull(0)
		}
	}

	val input = findValueForArg("-i", "--input", "--input-file", nullable = false)!!
	val output = findValueForArg("-o", "--output", "--output-file", nullable = true)

	// get valid op
	val op = (args.removeFirstOrNull() ?: error("Missing argument: Op"))
		.takeIf { it in validOperations } ?: error("Invalid argument: Op")

	// get valid transform op
	val top = if(op == "transform") {
		(args.removeFirstOrNull() ?: error("Missing argument: TransformOp"))
			.takeIf { it in validTransformOp } ?: error("Invalid argument: TransformOp")
	} else null

	return ParsedArgument(
		input = input,
		output = output,
		operation = op,
		transformOp = top,
		remainingArgs = args
	)
}