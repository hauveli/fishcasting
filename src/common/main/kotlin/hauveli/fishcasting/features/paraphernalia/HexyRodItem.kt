package hauveli.fishcasting.features.paraphernalia

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.HexAttributes
import at.petrak.hexcasting.common.lib.HexDataComponents
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C
import at.petrak.hexcasting.common.msgs.MsgNewSpiralPatternsS2C
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.li64.tide.registries.entities.misc.fishing.HookAccessor
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import com.li64.tide.registries.items.TideFishingRodItem
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.config.FishcastingConfigs.COMMON_CONFIG
import hauveli.fishcasting.config.FishcastingConfigs
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stat
import net.minecraft.stats.Stats
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.Vec3
import java.util.function.Consumer

class HexyRodItem // why does TideFishingRodItem take no baitslots here when it does in the source?
    (private val baitSlots: Int, baseDurability: Double, properties: Properties?) :
    TideFishingRodItem(baitSlots,baseDurability, properties) {
    // retrieveHook from Tide: https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/items/TideFishingRodItem.java#L57
    // Modification: rod.hurtAndBreak is not called
    override fun retrieveHook(rod: ItemStack, player: Player, level: Level) {
        // todo: figure out how to attach non-existent fish to this hook
        // this is because retrieveHook is called before the fish is spawned?
        // or because the hook is technically never attached to the fish when it does spawn...
        // (for later when I feel like it)
        // Maybe I should check if minigame was just completed?
        // I think it makes sense for the player to assume that it is hooked for at least the same tick that this is called...

        val activeHook = HookAccessor.getHook(player)
        if (activeHook != null) {
            val bobberItemStack = activeHook.bobber

            if (!level.isClientSide) {
                val bobberPos = activeHook.getPosition(0f)
                if (bobberItemStack != null && bobberItemStack.item is TideyFocusItem) {
                    // NOOOOOO BOOOOOBBEEEERTTTTT
                    //executeBobber(level, player, player.getUsedItemHand(), bobberItemStack, bobberPos);
                    // This was moved to a different method
                    // chance to summon thingy when fishing with a hexy rod and tidey focus
                    if (Fishcasting.random.nextFloat() < COMMON_CONFIG.spawnFishyTraderChance.get()
                        && activeHook.getCatchType() == TideFishingHook.CatchType.CRATE
                    ) {
                        player.sendSystemMessage(Component.nullToEmpty("This is where I would spawn my entity, if one was registered..."))
                        // poofIntoExistence(bobberPos, level)
                    }
                }

                val durabilityLoss = activeHook.retrieve(rod, level as ServerLevel, player)
                // if durability is 0, respect vanilla behavior
                if (rod.getMaxDamage() > 0) {
                    rod.hurtAndBreak(durabilityLoss, player, LivingEntity.getSlotForHand(player.getUsedItemHand()))
                }
            }

            level.playSound(
                null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE,
                SoundSource.NEUTRAL, 1.2f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f)
            )
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH)
        }
    }

    fun executeBobber(
        world: Level,
        player: Player?,
        usedHand: InteractionHand?,
        stack: ItemStack,
        bobberPos: Vec3
    ): InteractionResultHolder<ItemStack?> {
        return InteractionResultHolder.success<ItemStack?>(stack)
    }

    private fun playNoise(player: Player) {
        //Minecraft.getInstance().getSoundManager().play(new ElytraOnPlayerSoundInstance((LocalPlayer) player));
        player.playSound(HexSounds.CASTING_AMBIANCE.value(), 1.0f, 1.0f)
    }

    private fun stopNoise(level: Level) {
        if (level.isClientSide) {
            Minecraft.getInstance().soundManager.stop(HexSounds.CASTING_AMBIANCE.value().location, SoundSource.PLAYERS)
        }
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        // if bobber is already cast, we have to be able to pull it back in!
        // at least, I prefer it to behave this way.
        // debugging AAAAAAAA
        val offhand = player.getItemInHand(InteractionHand.OFF_HAND)
        player.sendSystemMessage(Component.nullToEmpty(offhand.descriptionId))
        if (offhand.item is TideyFocusItem) {
            player.sendSystemMessage(Component.nullToEmpty("Item is a TideyFocusItem"))
            val focusItem = offhand.item as TideyFocusItem
            /*
            if (focusItem.writeable(offhand))
                player.sendSystemMessage(Component.nullToEmpty("Item is writable"))
            if (focusItem.canWrite(offhand, null))
                player.sendSystemMessage(Component.nullToEmpty("Can write null to"))
             */
            val datumHolder = IXplatAbstractions.INSTANCE.findDataHolder(offhand)
            if (datumHolder != null && datumHolder.writeable()) {
                player.sendSystemMessage(Component.nullToEmpty("Can write null to"))
            } else {
                player.sendSystemMessage(Component.nullToEmpty("Not valid datum"))
            }
        }
        if (HookAccessor.getHook(player) != null) {
            return super.use(level, player, hand)
        }
        if (COMMON_CONFIG.castingIsMomentary()) {
            player.startUsingItem(hand)
            return InteractionResultHolder.pass<ItemStack?>(player.getItemInHand(hand))
        } else if (COMMON_CONFIG.shouldHexOffhand(hand)) { // a little bit silly, but whatever
            return useStaff(level, player, hand)
        }
        return super.use(level, player, hand)
    }

    // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/items/TideFishingRodItem.java#L368
    // for some reason, this is called TWICE each tick.
    // For this reason, ticksHeld needs to be divided by two...
    override fun onUseTick(level: Level, user: LivingEntity, rod: ItemStack, charge: Int) {
        super.onUseTick(level, user, rod, charge + COMMON_CONFIG.getCastingDelay())
        if (level.isClientSide) {
            if (COMMON_CONFIG.shouldHexMomentary(charge, getUseDuration(rod, user))) {
                playNoise(user as Player)
                hexyDischargePercent = (getUseDuration(rod, user) - charge).toFloat() / COMMON_CONFIG.getCastingDelay()
                // visual bug fix idiot solution approach
                // if recently swapped, this is set to 0f and not drawn if on non-casting rod, however
                // if we let go while charging hex casting rod after 0 ticks, this is 0f.
                // the code to set this back to 0 is never run in WindUpCastBarOverlayMixin and so we correctly
                // draw the bar. If we do not set this to greater than 0f then it disappear instantly.
                // alternative solution: check if we are in the casting GUI
                if (hexyDischargePercent == 0f) {
                    hexyDischargePercent = 0.000001f
                }
            } else {
                stopNoise(level)
            }
        }
    }

    // todo: if player just caught a fish, cooldown for 5 ticks. (configurable as well...)
    override fun releaseUsing(rod: ItemStack, level: Level, user: LivingEntity, charge: Int) {
        if (COMMON_CONFIG.shouldHexMomentary(charge, getUseDuration(rod, user))
            && user is Player
        ) {
            useStaff(level, user, user.getUsedItemHand())
        } else {
            super.releaseUsing(rod, level, user, charge)
        }
        stopNoise(level)
    }

    // From hexcasting github repository
    fun useStaff(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        stopNoise(level) // just in case...
        player.swing(hand, true)
        if (player.getAttributeValue(HexAttributes.FEEBLE_MIND) > 0) {
            return InteractionResultHolder.fail<ItemStack?>(player.getItemInHand(hand))
        }
        if (player.isShiftKeyDown) {
            if (level.isClientSide()) {
                player.playSound(HexSounds.STAFF_RESET.value(), 1f, 1f)
            } else if (player is ServerPlayer) {
                IXplatAbstractions.INSTANCE.clearCastingData(player)
                val packet = MsgClearSpiralPatternsS2C(player.getUUID())
                IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, packet)
                IXplatAbstractions.INSTANCE.sendPacketTracking(player, packet)
            }
        }

        if (!level.isClientSide() && player is ServerPlayer) {
            val vm = IXplatAbstractions.INSTANCE.getStaffcastVM(player, hand)
            val patterns = IXplatAbstractions.INSTANCE.getPatternsSavedInUi(player)

            val ravenmind = vm.image.ravenmind().orElse(null)


            IXplatAbstractions.INSTANCE.sendPacketToPlayer(
                player,
                MsgOpenSpellGuiS2C(
                    hand, patterns, vm.image.stack, ravenmind,
                    0
                )
            ) // TODO: Fix!
        }

        player.awardStat(Stats.ITEM_USED.get(this))

        //        player.gameEvent(GameEvent.ITEM_INTERACT_START);
        return InteractionResultHolder.success<ItemStack?>(player.getItemInHand(hand))
    }

    companion object {
        // I don't know what this is for but just in case, I'm including it.
        // 0 = normal. 1 = old. 2 = cherry preview
        val FUNNY_LEVEL_PREDICATE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(HexAPI.MOD_ID, "funny_level")
        var hexyDischargePercent: Float = 0.0f
            set(percent) {
                field = Mth.clamp(percent, 0.0f, 1.0f)
            }
    }
}