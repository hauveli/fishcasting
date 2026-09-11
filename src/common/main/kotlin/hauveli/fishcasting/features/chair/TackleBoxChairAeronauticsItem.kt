package hauveli.fishcasting.features.chair

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.HitResult
import java.util.function.Predicate

class TackleBoxChairAeronauticsItem(properties: Properties) : TackleBoxChairItem(properties) {
    override val variant = TackleBoxChairVariant.AERONAUTICSY
}