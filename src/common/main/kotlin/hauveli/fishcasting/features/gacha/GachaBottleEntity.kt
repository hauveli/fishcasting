package hauveli.fishcasting.features.gacha

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.iota.*
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.common.items.storage.ItemScroll
import at.petrak.hexcasting.common.lib.hex.HexActions
import com.li64.tide.data.loot.LootTableRef
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.common.registries.FishcastingItems
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.StructureTags
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.projectile.ThrownPotion
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import java.math.BigInteger
import java.util.function.BiConsumer


class GachaBottleEntity : ThrownPotion {
    constructor(entityType: EntityType<out ThrownPotion?>, level: Level) : super(entityType, level)

    constructor(level: Level, shooter: LivingEntity) : super(level, shooter)

    constructor(level: Level, x: Double, y: Double, z: Double) : super(level, x, y, z)

    override fun onHitBlock(result: BlockHitResult) {
        super.onHitBlock(result)
    }

    override fun onHit(result: HitResult) {
        super.onHit(result)
        if (!this.level().isClientSide) {
            generateRandomLoot()
            this.discard()
        }
    }

    private fun applyWater() {
        return
    }

    private fun applySplash(effects: Iterable<MobEffectInstance>, p_entity: Entity) {
        return
    }

    private fun makeAreaOfEffectCloud(potionContents: PotionContents?) {
        return
    }

    private val isLingering: Boolean
        get() = false

    private fun dowseFire(pos: BlockPos?) {
        return
    }

    fun generateRandomLoot() {
        val server = this.server
        val owner = this.owner
        val lootTable = server!!
            .reloadableRegistries()
            .getLootTable(
                LOOT_TABLES.get(Fishcasting.random.nextInt(0, LOOT_TABLES.size - 1))
            )

        val serverLevel = this.level() as ServerLevel
        val params = LootParams.Builder(serverLevel)
            .withParameter<Vec3?>(LootContextParams.ORIGIN, owner!!.position())
            .withParameter<Entity?>(LootContextParams.THIS_ENTITY, owner)
            .create(LootContextParamSets.GIFT)

        val generatedLoot = lootTable.getRandomItems(params)

        for (loot in generatedLoot) {
            var maybeIotaHolder = loot.item
            if (maybeIotaHolder is IotaHolderItem) {
                // Dispensing it with a machine will yield non-written-to thoughtknots/scrolls every time
                if (owner is ServerPlayer) {
                    if (Fishcasting.random.nextFloat() > 0.075) { // about 92.5% of the time, apply datum
                        val maybeIotaExists: Iota? = maybeIotaHolder.readIota(loot)
                        if (maybeIotaExists == null || maybeIotaExists.size() == 0) {
                            if (maybeIotaHolder is ItemScroll) {
                                maybeIotaHolder.writeDatum(
                                    loot, PatternIota(this.randomPattern)
                                )
                            } else {
                                maybeIotaHolder.writeDatum(
                                    loot, generateRandomIota()
                                )
                            }
                        }
                    }
                }
            }

            this.spawnAtLocation(loot)
        }

        /*
            I do it this way because it's low effort while accomplishing a shatter-like effect
            10% 1 shard
            90% 2 shards or more
                -> 50% chance 2 shards
                -> 50% chance 3 shards or more
                    -> 90% chance 3 shards
                    -> 10% chance 4 shards
         */
        this.spawnAtLocation(ItemStack(FishcastingItems.GLASS_SHARD))
        if (Fishcasting.random.nextFloat() > 0.10) {
            this.spawnAtLocation(ItemStack(FishcastingItems.GLASS_SHARD))
            if (Fishcasting.random.nextFloat() > 0.5) {
                this.spawnAtLocation(ItemStack(FishcastingItems.GLASS_SHARD))
                if (Fishcasting.random.nextFloat() > 0.9) {
                    this.spawnAtLocation(ItemStack(FishcastingItems.GLASS_SHARD))
                }
            }
        }
    }

    fun generateRandomIota(): Iota {
        //if (level.isClientSide) return new NullIota(); // I don't think this is possible?
        val serverLevel = this.level() as ServerLevel
        when (Fishcasting.random.nextInt(0, 8)) {
            0 -> {
                return Vec3Iota(findNearestTreasure(serverLevel, this.position()))
            }

            1 -> {
                // why did intelliJ split this into 2 runs? I dont know but whatever I'll leave it for now
                run {
                    val entityMaybe = getNearbyMob(serverLevel, this)
                    if (entityMaybe != null) {
                        return EntityIota(entityMaybe)
                    }
                }
                run {
                    // I think past 53 bits double likely is not prime, I'm setting it to 52 just in case....
                    return DoubleIota(BigInteger.probablePrime(52, Fishcasting.random).toDouble())
                }
            }

            2 -> {
                return DoubleIota(BigInteger.probablePrime(52, Fishcasting.random).toDouble())
            }

            3 -> {
                return BooleanIota(Fishcasting.random.nextBoolean())
            }

            4 -> {
                return GarbageIota()
            }

            5 -> {
                return PatternIota(this.randomPattern)
            }

            6 -> {
                // unlikely (impossible with Random?) to recurse forever, but its funny so it stays
                return ListIota(listOf<Iota?>(generateRandomIota()))
            }

            else -> {
                return NullIota()
            }
        }
    }

    /// uhhhh what did intellij do here
    val randomPattern: HexPattern
        get() {
            val patterns: MutableList<HexPattern> = ArrayList()
            HexActions.register({ entry: ActionRegistryEntry?, id: ResourceLocation? ->
                patterns.add(entry!!.prototype()) // heehee...
            })
            return patterns[Fishcasting.random.nextInt(0, patterns.size)]
        }

    fun getNearbyMob(
        level: ServerLevel,
        entity: Entity
    ): Entity? {
        val nearbyMobs = level.getEntitiesOfClass<Mob?>(
            Mob::class.java,
            entity.boundingBox.inflate(128.0)
        )
        // mob can not be player, so no true names will be written, I think
        if (nearbyMobs.isEmpty()) {
            return null
        }
        return nearbyMobs[Fishcasting.random.nextInt(0, nearbyMobs.size)]
    }

    // I didnt know how else to obtain a reference to this
    var RANDOM_SCROLL_TABLE: ResourceKey<LootTable> = ResourceKey.create(
        Registries.LOOT_TABLE,
        HexAPI.modLoc("random_scroll")
    )
    var RANDOM_CYPHER_TABLE: ResourceKey<LootTable> = ResourceKey.create(
        Registries.LOOT_TABLE,
        HexAPI.modLoc("random_cypher")
    )


    var MISC_JUNK_TABLE: LootTableRef = LootTableRef.createNew(
        id("gameplay/fishing/message_in_a_bottle_junk")
    )

    // I should move this out of here but I'm busy accruing tech debt
    var WARM_OCEAN_JUNK: LootTableRef = LootTableRef.createNew(
        id("gameplay/fishing/warm_ocean_junk")
    )

    var LOOT_TABLES: MutableList<ResourceKey<LootTable>> =
        listOf(
            RANDOM_CYPHER_TABLE,
            RANDOM_SCROLL_TABLE,
            RANDOM_SCROLL_TABLE,
            RANDOM_SCROLL_TABLE,
            MISC_JUNK_TABLE.key,
            MISC_JUNK_TABLE.key,
            MISC_JUNK_TABLE.key,
            MISC_JUNK_TABLE.key,
            MISC_JUNK_TABLE.key,
            MISC_JUNK_TABLE.key,
            MISC_JUNK_TABLE.key,
            MISC_JUNK_TABLE.key,
            MISC_JUNK_TABLE.key,
            MISC_JUNK_TABLE.key,
            MISC_JUNK_TABLE.key,
            MISC_JUNK_TABLE.key
        ) as MutableList<ResourceKey<LootTable>>

    companion object {
        fun findNearestTreasure(level: ServerLevel, origin: Vec3): Vec3 {
            val start = BlockPos.containing(origin)
            // 20 is quite low, but 100 is way too laggy. needs to have a mspt of at most 100 for a single tick.
            // 100 chunks caused this to take up to 2 seconds on my computer.
            val radius = 20
            val treasurePos = level.findNearestMapStructure(
                StructureTags.ON_TREASURE_MAPS,
                start,
                radius,
                false
            )

            if (treasurePos == null) {
                return Vec3.ZERO
            }

            return Vec3.atCenterOf(treasurePos).subtract(origin)
        }
    }
}
