package hauveli.fishcasting.features.paraphernalia

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.item.VariantItem
import at.petrak.hexcasting.common.items.storage.ItemFocus
import at.petrak.hexcasting.common.lib.HexDataComponents
import com.li64.tide.registries.items.FishingBobberItem
import hauveli.fishcasting.Fishcasting
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Unit
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level


// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/common/items/storage/ItemFocus.java
class TideyFocusItem(pProperties: Properties) : ItemFocus(pProperties.stacksTo(1)), IotaHolderItem, VariantItem {
    companion object {
        const val LUCK_TWEAKING_BOBBER_PROBABILITY = 1.0 / 1000.0
        val OVERLAY_PREDICATE: ResourceLocation = Fishcasting.id("overlay_layer")
        val VARIANT_PRED: ResourceLocation = Fishcasting.id("variant")
        const val NUM_VARIANTS: Int = 2 // todo: add a fish variant that is literally just the crystalline carp? I would need to load Tide's ResourceLocation to make that happen, I think
    }

    override fun numVariants(): Int {
        return NUM_VARIANTS
    }


    /*
    override fun inventoryTick(p0: ItemStack, p1: Level, p2: Entity, p3: Int, p4: Boolean) {
        super.inventoryTick(p0, p1, p2, p3, p4)
        Fishcasting.LOGGER.info("what the fuck: {}", p0.item.javaClass)
    }
     */
}
