package hauveli.fishcasting.features.paraphernalia

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.common.lib.HexDataComponents
import com.li64.tide.registries.items.FishingBobberItem
import hauveli.fishcasting.Fishcasting
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag


// Using code from:
// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/common/items/storage/ItemFocus.java
class TideyFocusItem(pProperties: Properties) : FishingBobberItem(pProperties.stacksTo(1)), IotaHolderItem {
    companion object {
        const val LUCK_TWEAKING_BOBBER_PROBABILITY = 1.0 / 1000.0
        val OVERLAY_PREDICATE: ResourceLocation = Fishcasting.id("overlay_layer")
        val VARIANT_PRED: ResourceLocation = Fishcasting.id("variant")

        const val NUM_VARIANTS: Int = 2 // todo: add a fish variant that is literally just the crystalline carp? I would need to load Tide's ResourceLocation to make that happen, I think
    }


    override fun getDescriptionId(stack: ItemStack): String {
        return super.getDescriptionId(stack) + (if (stack.has(HexDataComponents.SEALED_IOTA_HOLDER.get())) ".sealed" else "")
    }

    // c
    fun isSealed(stack: ItemStack): Boolean {
        return false // stack.has(HexDataComponents.SEALED_IOTA_HOLDER.get())
    }

    // todo: make it a VariantItem
    fun numVariants(): Int {
        return NUM_VARIANTS
    }

    override fun readIota(p0: ItemStack?): Iota? {
        return super.readIota(p0)
    }

    override fun writeDatum(itemStack: ItemStack, iota: Iota?) {
        if (iota == null) {
            itemStack.remove(HexDataComponents.IOTA_HOLDER_IOTA.get());
            itemStack.remove(HexDataComponents.SEALED_IOTA_HOLDER.get());
        } else if (!isSealed(itemStack)) {
            itemStack.set(HexDataComponents.IOTA_HOLDER_IOTA.get(), iota);
        }
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        IotaHolderItem.appendHoverText(this, stack, tooltipComponents, tooltipFlag)
    }

    override fun writeable(itemStack: ItemStack): Boolean {
        return !isSealed(itemStack)
    }

    override fun canWrite(itemStack: ItemStack, iota: Iota?): Boolean {
        return iota == null || !isSealed(itemStack)
    }
}
