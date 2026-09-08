package hauveli.fishcasting.features.paraphernalia

import at.petrak.hexcasting.api.HexAPI.modLoc
import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.common.lib.HexDataComponents
import com.li64.tide.registries.items.FishingBobberItem
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level


// Using code from:
// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/common/items/storage/ItemFocus.java
class TideyFocusItem(val pProperties: Properties) : FishingBobberItem(
    pProperties
        .stacksTo(1)), IotaHolderItem {

    override fun inventoryTick(p0: ItemStack, p1: Level, p2: Entity, p3: Int, p4: Boolean) {
        super.inventoryTick(p0, p1, p2, p3, p4)
        // pProperties.component(HexDataComponents.IOTA_HOLDER_IOTA.get(), NullIota())
        if (p1.gameTime % 200L != 0L)
            return
        p2.sendSystemMessage(Component.nullToEmpty("___________"))
        if (p0.has(HexDataComponents.IOTA_HOLDER_IOTA.get())) {
            p2.sendSystemMessage(Component.nullToEmpty("Already have capability for IOTA_HOLDER_IOTA???"))
        } else {
            p2.sendSystemMessage(Component.nullToEmpty("Writing NullIota to it..."))
            p0.set(HexDataComponents.IOTA_HOLDER_IOTA.get(), NullIota())
        }
        p2.sendSystemMessage(Component.nullToEmpty("Moving forward..."))
        val iotaHolderLookup: ItemApiLookup<ADIotaHolder, Void> =
            ItemApiLookup.get(modLoc("iota_holder_item"), ADIotaHolder::class.java, Void::class.java)
        val whatDidWeFind = iotaHolderLookup.find(p0, null)
        p2.sendSystemMessage(Component.nullToEmpty("What did we find?"))
        p2.sendSystemMessage(Component.nullToEmpty(whatDidWeFind.toString()))
        if (whatDidWeFind != null) {
            p2.sendSystemMessage(Component.nullToEmpty(whatDidWeFind.writeable().toString()))
        } else {
            p2.sendSystemMessage(Component.nullToEmpty("We found NOTHING."))
        }
        p2.sendSystemMessage(Component.nullToEmpty("___________"))

    }

    override fun getDescriptionId(stack: ItemStack): String {
        return super.getDescriptionId(stack) + (if (stack.has(HexDataComponents.SEALED_IOTA_HOLDER.get())) ".sealed" else "")
    }

    // c
    fun isSealed(stack: ItemStack): Boolean {
        return false // stack.has(HexDataComponents.SEALED_IOTA_HOLDER.get())
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
