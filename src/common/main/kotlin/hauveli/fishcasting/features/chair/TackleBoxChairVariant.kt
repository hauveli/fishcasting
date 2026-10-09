package hauveli.fishcasting.features.chair

enum class TackleBoxChairVariant(val id: Int) {
    FACTORY(0),
    FLOATY(1),
    SECRET(2);

    companion object {
        private val BY_ID = entries
            .sortedBy { it.id }
            .toTypedArray()

        fun byId(id: Int): TackleBoxChairVariant {
            return BY_ID[id % BY_ID.size]
        }
    }
}