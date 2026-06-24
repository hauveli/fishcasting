package hauveli.fishcasting.features.paraphernalia

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.common.items.storage.ItemFocus
import net.minecraft.resources.ResourceLocation


class TideyFocusItem(pProperties: Properties) : ItemFocus(pProperties.stacksTo(1)) {

    companion object {
        @JvmField
        final val LUCK_TWEAKING_BOBBER_PROBABILITY = 1.0 / 1000.0
        // https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/common/items/magic/ItemPackagedHex.java
        const val TAG_PROGRAM: String = "patterns"
        const val TAG_PIGMENT: String = "pigment"
        val HAS_PATTERNS_PRED: ResourceLocation? = HexAPI.modLoc("has_patterns")
    }
}
