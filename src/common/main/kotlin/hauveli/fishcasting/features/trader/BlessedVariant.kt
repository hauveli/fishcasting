package hauveli.fishcasting.features.trader

enum class BlessedVariant(val id: Int) {
    RED(0),
    GREEN(1),
    BLUE(2),
    PURPLE(3);

    companion object {
        private val BY_ID = entries
            .sortedBy { it.id }
            .toTypedArray()

        fun byId(id: Int): BlessedVariant {
            return BY_ID[id % BY_ID.size]
        }
    }
}