package hauveli.fishcasting.registry

import com.li64.tide.Tide
import com.li64.tide.data.TideData
import hauveli.fishcasting.Fishcasting
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementType
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.critereon.ImpossibleTrigger
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item


object FishcastingAdvancements {
    @JvmField
    val TACKLEBOX_CHAIR_FISHER = Fishcasting.id("instructional/fished_in_tacklebox_chair")
    @JvmField
    val BLESSED_BRAINSWEPT = Fishcasting.id("instructional/brainswept_trader_in_void")
    val CURSED_ZAPPED = Fishcasting.id("instructional/lightning_struck_cursed")
}