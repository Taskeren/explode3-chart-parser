package cn.taskeren.explode3.parser

import com.fasterxml.jackson.annotation.JsonProperty

data class CMapNoteAssetContainer(
	@JsonProperty("m_notes")
	val notes: List<CMapNoteAsset>
) : Transformable<CMapNoteAssetContainer, CMapNoteAsset> {
	fun getSubFor(holdNote: CMapNoteAsset): CMapNoteAsset? {
		if(holdNote.type == NoteType.HOLD && holdNote.subId != -1) {
			return notes.single { it.id == holdNote.subId }
		}
		return null
	}

	// analyze

	fun count(): Int = notes.count()

	fun countTap(): Int = notes.count { it.type == NoteType.NORMAL }

	fun countHold(strict: Boolean = true): Int =
		if(strict) notes.count { it.type == NoteType.HOLD } else notes.count { it.type == NoteType.HOLD || it.type == NoteType.SUB }

	fun countChain(): Int = notes.count { it.type == NoteType.CHAIN }

	// transform

	override fun transform(block: (CMapNoteAsset) -> CMapNoteAsset): CMapNoteAssetContainer {
		return copy(notes = notes.map(block))
	}

}