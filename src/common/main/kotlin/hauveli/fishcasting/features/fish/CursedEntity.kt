package hauveli.fishcasting.features.fish

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
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.PanicGoal
import net.minecraft.world.entity.animal.Bucketable
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level
import java.util.function.Consumer
import java.util.function.Function


// melted axolotl fish
class CursedEntity(entityType: EntityType<out Axolotl?>, level: Level) : Axolotl(entityType, level), Bucketable,
    FishLengthHolder {
    private val bucketItem: Item
    private var length: Double

    override fun getHeadRotSpeed(): Int {
        return 1
    }

    /*
    @Override
    public void saveToBucketTag(ItemStack stack) {
        Bucketable.saveDefaultDataToBucketTag(this, stack);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, (p_330644_) -> {
            p_330644_.putInt("Variant", this.getVariant().getId());
            p_330644_.putInt("Age", this.getAge());
            Brain<?> brain = this.getBrain();
            if (brain.hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN)) {
                p_330644_.putLong("HuntingCooldown", brain.getTimeUntilExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN));
            }

        });
    }

     */
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
        return false
    }

    override fun isInWaterOrRain(): Boolean {
        return false
    }

    override fun isInWaterRainOrBubble(): Boolean {
        return false
    }

    override fun tick() {
        super.tick()
        if (FishingMedium.VOID.isInVoid(position(), level())) {
            this.deltaMovement = this.deltaMovement.add(0.0, 0.005, 0.0);
        }
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
        this.length =
            FishData.get(fishItem).map(Function { data: FishData? -> data!!.getRandomLength(getRandom()) })
                .orElse(0.0)!!
    }

    override fun `tide$getLength`(): Double {
        return this.length
    }

    override fun `tide$setLength`(length: Double) {
        this.length = length
    }


    override fun fromBucket(): Boolean {
        // I don't fucking understand what this is sometimes it is bool sometimes it is int... I must b e doing something wrong TODO:<<<<
        /*
            Caused by: java.lang.ClassCastException: class java.lang.Integer cannot be cast to class java.lang.Boolean (java.lang.Integer and java.lang.Boolean are in module java.base of loader 'bootstrap'
            on line:
            return (Boolean) this.entityData.get(FROM_BUCKET);

            But compiler complains! I can not cast boolean to integer! Runtime is int -> bool, compiletime is bool -> int...
            So which is it? is it an integer or is it a bool? What the fuck is this thing?

            can not instanceof on Java 21 either.
        if (this.entityData.get(FROM_BUCKET) instanceof Boolean bool) {
            return bool;
        }
        var monster = this.entityData.get(FROM_BUCKET);
        return (Boolean) (Object) monster; // I really really don't understand
         */
        return this.entityData.get(FROM_BUCKET) == true
    }

    fun fuckThisThingMan(what: Int): Boolean {
        return what > 0
    }

    fun fuckThisThingMan(what: Boolean): Boolean {
        return what
    }

    override fun isBaby(): Boolean {
        return false
    }

    // update: it broke agin and I don't know why. awesome.
    // this looks fucked because I ran into so many issues with entityData.set and I have no fucking clue why or what is causing it exactly.
    // so I'm not touching it
    override fun setFromBucket(fromBucket: Boolean) {
        this.entityData.set<Boolean>(
            FROM_BUCKET,
            fromBucket == true // I don't fucking get it, type isn't `Boolean?` so why does it work now? error has to do with int but I can only see this failing if fromBucket is null without this check, or does java allow wrong type arguments? what the fuck even is the problem I had
        )
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putBoolean("FromBucket", this.fromBucket()) // I just dont get it
        compound.putDouble(FishLengthHolder.`tide$LENGTH_KEY`, this.length)
        //compound.putBoolean(tide$SHINY_KEY, this.isShiny);
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        this.setFromBucket(tag.contains("FromBucket") && tag.getBoolean("FromBucket"))
        this.length = if (tag.contains(FishLengthHolder.`tide$LENGTH_KEY`)) tag.getDouble(FishLengthHolder.`tide$LENGTH_KEY`) else this.length
        //this.tide$setIsShiny(tag.contains(tide$SHINY_KEY) && tag.getBoolean(tide$SHINY_KEY));
    }

    // fabric won't work with it, but!!!:
    // neoforge requires the bucketable deprecated method
    // what the fuck even is going on?
    // this sucks so much..................
    override fun saveToBucketTag(stack: ItemStack) {
        // Bucketable.saveDefaultDataToBucketTag(this, stack)
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, Consumer { tag: CompoundTag? ->
            tag!!.putDouble(
                FishLengthHolder.`tide$LENGTH_KEY`, this.`tide$getLength`()
            )
        })
    }

    override fun loadFromBucketTag(tag: CompoundTag) {
        // Bucketable.loadDefaultDataFromBucketTag(this, tag)
        setFromBucket(false)
        this.length =
            if (tag.contains(FishLengthHolder.`tide$LENGTH_KEY`)) tag.getDouble(FishLengthHolder.`tide$LENGTH_KEY`) else this.`tide$getLength`()
        //this.tide$setIsShiny(tag.contains(tide$SHINY_KEY) && tag.getBoolean(tide$SHINY_KEY));
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
                || (fishLength == 0.0 && entity.random.nextFloat() < 0.025f)) {
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
        private val FROM_BUCKET: EntityDataAccessor<Boolean?> =
            SynchedEntityData.defineId(AbstractTideFish::class.java, EntityDataSerializers.BOOLEAN)
        private val IS_SHINY: EntityDataAccessor<Boolean?> =
            SynchedEntityData.defineId(AbstractTideFish::class.java, EntityDataSerializers.BOOLEAN)
    }
}