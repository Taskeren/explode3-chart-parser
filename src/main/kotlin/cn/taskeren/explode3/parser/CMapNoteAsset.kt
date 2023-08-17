package cn.taskeren.explode3.parser

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("CMapNoteAsset")
@JsonIgnoreProperties(ignoreUnknown = true)
data class CMapNoteAsset(
	@JsonProperty("m_id")
	val id: Int,
	@JsonProperty("m_type")
	val type: NoteType,
	@JsonProperty("m_time")
	val time: Double,
	@JsonProperty("m_position")
	val position: Double,
	@JsonProperty("m_width")
	val width: Double,
	@JsonProperty("m_subId")
	val subId: Int, // default -1
	@JsonProperty("status")
	val status: String,
)
