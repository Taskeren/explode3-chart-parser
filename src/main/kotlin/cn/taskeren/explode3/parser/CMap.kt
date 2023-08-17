package cn.taskeren.explode3.parser

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("CMap")
data class CMap(
	@JsonProperty("m_path")
	val path: String,
	@JsonProperty("m_barPerMin")
	val barPerMin: Double,
	@JsonProperty("m_timeOffset")
	val timeOffset: Double,
	@JsonProperty("m_leftRegion")
	val leftRegion: SideType,
	@JsonProperty("m_rightRegion")
	val rightRegion: SideType,
	@JsonProperty("m_mapID")
	val mapId: String,
	@JsonProperty("m_notes")
	val mainNotes: CMapNoteAssetContainer,
	@JsonProperty("m_notesLeft")
	val leftNotes: CMapNoteAssetContainer,
	@JsonProperty("m_notesRight")
	val rightNotes: CMapNoteAssetContainer,
) : Transformable<CMap, CMapNoteAsset> {

	val noteCount: Int get() = mainNotes.notes.size + leftNotes.notes.size + rightNotes.notes.size

	fun validate(): List<String> {
		val errors = mutableListOf<String>()

		listOf(mainNotes, leftNotes, rightNotes).forEach { noteLists ->
			noteLists.notes.forEach { note ->
				if(note.width <= 0) {
					errors += "Width cannot be zero or negative (id=${note.id}, type=${note.type}, time=${note.time})"
				}
				if(note.type == NoteType.HOLD) {
					runCatching { noteLists.getSubFor(note) }.onFailure {
						errors += "Missing end of HOLD (id=${note.id}, time=${note.time})"
					}
				}
			}
		}

		fun validateSide(notes: CMapNoteAssetContainer, sideType: SideType, side: String) {
			notes.notes.forEach { note ->
				if(sideType == SideType.PAD && note.type == NoteType.CHAIN) {
					errors += "Invalid CHAIN in $side side (id=${note.id}, time=${note.time})"
				} else if(sideType == SideType.MIXER && note.type != NoteType.CHAIN) {
					errors += "Invalid ${note.type} in $side side (id=${note.id}, time=${note.time})"
				}
			}
		}

		validateSide(leftNotes, leftRegion, "left")
		validateSide(rightNotes, rightRegion, "right")

		return errors
	}

	override fun transform(block: (CMapNoteAsset) -> CMapNoteAsset): CMap {
		return copy(
			mainNotes = mainNotes.transform(block),
			leftNotes = leftNotes.transform(block),
			rightNotes = rightNotes.transform(block),
		)
	}
}
