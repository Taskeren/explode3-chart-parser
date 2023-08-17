package cn.taskeren.explode3.parser

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

object CMapParser {

	private val serializer = XmlMapper().registerModule(kotlinModule()).enable(SerializationFeature.INDENT_OUTPUT)

	fun parse(content: String): CMap {
		return serializer.readValue<CMap>(content)
	}

	fun serialize(map: CMap): String {
		return serializer.writeValueAsString(map)
	}
}