package hauveli.fishcasting.features.fish

import com.li64.tide.Tide
import com.li64.tide.data.FishLengthHolder
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.fishing.mediums.FishingMedium
import com.li64.tide.data.item.TideItemData
import com.li64.tide.registries.entities.fish.AbstractTideFish
import com.li64.tide.registries.entities.fish.TideVoidFish
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.registry.FishcastingAdvancements.tryGrantingAdvancement
import hauveli.fishcasting.registry.FishcastingAdvancements
import hauveli.fishcasting.registry.FishcastingItems
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.PanicGoal
import net.minecraft.world.entity.animal.Bucketable
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.function.Consumer
import java.util.function.Function


// melted axolotl fish
class CursedEntity(
    entityType: EntityType<out TideVoidFish>,
    level: Level
) : TideVoidFish(entityType, level), Bucketable, FishLengthHolder {
    private val bucketItem: Item
    private var length: Double

    override fun getHeadRotSpeed(): Int {
        return 1
    }

    override fun getBucketItemStack(): ItemStack {
        return bucketItem.defaultInstance
    }

    override fun getPickupSound(): SoundEvent {
        return SoundEvents.BUCKET_FILL_AXOLOTL
    }

    override fun isInvulnerableTo(source: DamageSource): Boolean {
        return false
    }

    // these next 4 are so its goes ouch all the time
    override fun getMaxAirSupply(): Int {
        return 0
    }

    override fun isInWater(): Boolean {
        return super.isInWater() // false
    }

    override fun isInWaterOrRain(): Boolean {
        return false
    }

    override fun isInWaterRainOrBubble(): Boolean {
        return false
    }

    // todo: CHECK THIS in create and vanilla
    override fun getPassengerRidingPosition(p0: Entity): Vec3 {
        return super.getPassengerRidingPosition(p0).add(Vec3(0.0,1.0,0.0))
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (relevantDamageSource(source)) {
            doAllaySpawnOnLightningHitMob(this, source)
            return super.hurt(source, amount)
        }
        return super.hurt(source, 0f)
    }

    // TODO:
    // Some ideas that I may or may not bother to implemeent if ever
    // Make this thing accomplish the following:
    // Have a 3D model and be a useless fatty that just lays around and does nothing but eat (including poisonous potatoes)
    // possibly move on timescales of one block per one real life day
    // should rotate very slowly, too.
    // saturation level such that it becomes fatter if fed food
    // at maximum saturation, become able to be mind flayed at which point it bursts into an explosion
    // and DIES
    // a cursed existence.
    // also should be able to be turned into a yummy treat
    // should be bucketable if it is not too big
    // uses saturation to heal
    // becomes smaller when saturation decreases
    // drowns in water
    // sinks if saturation level above 0 (maybe this is how I can make it not just despawn if spawned naturally?)
    // should have a chance of being fished up from the void (any)
    // more stuff maybe.
    // should be one of only two mobs at most that this addon adds.
    init {
        this.isNoGravity = false
        this.setCanPickUpLoot(false)
        //this.setInvulnerable(true);
        this.speed = 0.01f
        this.attributes.getInstance(Attributes.STEP_HEIGHT)?.baseValue = 0.1

        // need to give the fish the properties of a tide fish, but I still want the entity to be an axolotl so I can keep all its goals and animations.
        // this was the least effort for me, but I would gladly accept PRs to change it haha...
        // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/entities/fish/AbstractTideFish.java#L46
        val key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType)
        val fishItem = BuiltInRegistries.ITEM.getOptional(key).orElseThrow()
        this.bucketItem = BuiltInRegistries.ITEM.getOptional(key.withSuffix("_bucket")).orElseThrow()
        // sure whatever
        this.length = FishData.get(fishItem).map(Function {
                data: FishData ->
            data.getRandomLength(getRandom())
        }).orElse(0.0) ?: 0.0
    }

    override fun `tide$getLength`(): Double {
        return this.length
    }

    override fun `tide$setLength`(length: Double) {
        this.length = length
    }

    override fun isBaby(): Boolean {
        return false
    }

    companion object {
        fun relevantOptionalDamageSource(source: DamageSource): Boolean {
            return source.`is`(DamageTypes.LIGHTNING_BOLT)
        }

        // terrible name
        fun relevantVoidDamageSource(source: DamageSource): Boolean {
            return source.`is`(DamageTypes.FELL_OUT_OF_WORLD)
        }

        fun relevantDebugDamageSource(source: DamageSource): Boolean {
            return source.`is`(DamageTypes.GENERIC_KILL)
        }

        fun relevantDamageSource(source: DamageSource): Boolean {
            return relevantOptionalDamageSource(source)
                    || relevantDebugDamageSource(source)
                    || relevantVoidDamageSource(source)
        }

        fun spawnAllayAtEntity(entity: Entity) {
            val allay = EntityType.ALLAY.create(entity.level())
            allay!!.setPos(entity.position())
            // allay will catch fire otherwise
            allay.addEffect(
                MobEffectInstance(
                    MobEffects.FIRE_RESISTANCE,
                    20,  // this is in ticks I think so 1 second
                    1
                )
            )
            // with fish length disabled it has to be possible to obtain the disc somehow
            val fishLength = getFishLength(entity)
            if (fishLength >= 66.6
                || (Tide.CONFIG.server().items.fishItemSizes.key != "ALWAYS" && entity.random.nextFloat() < 0.025f)) {
                allay.setItemInHand(
                    InteractionHand.MAIN_HAND,
                    FishcastingItems.DISC.value.defaultInstance
                )
            }
            entity.level().addFreshEntity(allay)
        }

        // hmm... spell?
        // could be good for fishing up something -> immediately smiting it if under a size
        // downside is that it is hyper-specific, and other addons can already read item data (so it doesnt add much then)
        private fun getFishLength(entity: Entity?): Double {
            if (entity is FishLengthHolder) {
                return entity.`tide$getLength`()
            } else if (entity is ItemEntity) {
                if (TideItemData.FISH_LENGTH.get(entity.item) is Double) {
                    return TideItemData.FISH_LENGTH.get(entity.item)
                }
            }
            return 0.0
        }

        fun doAllaySpawnOnLightningHitMob(entity: Entity, damageSource: DamageSource) {
            if (!damageSource.`is`(DamageTypes.LIGHTNING_BOLT)) {
                return
            }
            spawnAllayAtEntity(entity)
            entity.kill()
            entity.discard()
        }

        // I should clean this up and move this or do something smarter when I port it to kotlin I think
        fun doAllaySpawnOnLightningHitItem(itemEntity: ItemEntity, damageSource: DamageSource) {
            // unreachable code but whatever man
            if (!damageSource.`is`(DamageTypes.LIGHTNING_BOLT)) {
                return
            }
            // I do it like this so that the itemEntity does not hit 0 before an advancement can deal with this!!!
            while (itemEntity.item.count > 1) {
                spawnAllayAtEntity(itemEntity)
                itemEntity.item.shrink(1)
            }
            spawnAllayAtEntity(itemEntity)
            // for advancement criteria to work this is necessary: itemEntity.shrink() is NOT called in such a way that the count reaches 0
            // fuck that was a stupid bug to have, but understandably not obvious...
            itemEntity.kill()
            itemEntity.discard()
        }

        // lightning strike damageSource is null
        // it's joever
        // we're sop bnack
        fun tryGrantingAdvancement(damageSource: DamageSource) {
            val damager = damageSource.entity
            if (damager is ServerPlayer) {
                tryGrantingAdvancement(damager, FishcastingAdvancements.CURSED_ZAPPED)
            }
        }

        // all of the below from tide
        // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/entities/fish/AbstractTideFish.java#L46
        // whyat the fuck it returns an integer?
    }
}