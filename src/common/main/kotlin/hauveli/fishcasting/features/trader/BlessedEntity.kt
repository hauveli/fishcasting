package hauveli.fishcasting.features.trader

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.common.lib.HexSounds
import com.google.common.collect.ImmutableList
import com.li64.tide.Tide
import com.li64.tide.compat.seasons.SeasonsCompat
import com.li64.tide.data.commands.TestType
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.fishing.FishingContext
import com.li64.tide.data.fishing.mediums.FishingMedium
import com.li64.tide.data.fishing.selector.FishingEntry
import com.li64.tide.registries.TideEntityTypes
import com.li64.tide.registries.TideItems
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import com.li64.tide.util.TideUtils
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.features.fish.CursedEntity
import hauveli.fishcasting.registry.FishcastingAdvancements
import hauveli.fishcasting.registry.FishcastingEntities
import hauveli.fishcasting.registry.FishcastingSounds
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.util.Mth
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.Brain
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation
import net.minecraft.world.entity.ai.sensing.Sensor
import net.minecraft.world.entity.ai.sensing.SensorType
import net.minecraft.world.entity.ai.util.DefaultRandomPos
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.npc.VillagerTrades
import net.minecraft.world.entity.npc.WanderingTrader
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.trading.MerchantOffers
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.pathfinder.Path
import net.minecraft.world.phys.Vec3
import java.util.*
import java.util.function.Predicate
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class BlessedEntity(entityType: EntityType<out WanderingTrader?>, level: Level) : WanderingTrader(entityType, level) {
    private val wanderTarget: BlockPos? = null
    private var ticksSincePain = 0
    private var isFishing = false
    private var isHappy = false
    var fakeBobberPos: Vec3

    enum class Mood(val value: Int) {
        VERY_SAD(-2),
        SAD(-1),
        NEUTRAL(0),
        HAPPY(1),
        VERY_HAPPY(2);

        fun add(x: Int): Mood {
            val newValue =
                max(-2, min(2, value + x)) // todo: get max and minimum from the enum itself but in a neat way, how?
            return fromValue(newValue)
        }

        fun increment(): Mood {
            return add(1)
        }

        fun decrement(): Mood {
            return add(-1)
        }

        companion object {
            fun fromValue(value: Int): Mood {
                for (mood in entries) {
                    if (mood.value == value) {
                        return mood // todo: is there a smarter way to get this?
                    }
                }
                return Mood.NEUTRAL // whatever, failsafe
            }
        }
    }

    var mood: Mood

    private fun decrementMood() {
        this.mood = this.mood.decrement()
    }

    private fun incrementMood() {
        this.mood = this.mood.increment()
    }

    val splayLimbs: AnimationState = AnimationState()
    val castingAnimationState: AnimationState = AnimationState()
    val idleAnimationState: AnimationState = AnimationState()
    val leftEarWiggleState: AnimationState = AnimationState()
    val rightEarWiggleState: AnimationState = AnimationState()
    val tailWiggleState: AnimationState = AnimationState()
    val sittingAnimationState: AnimationState = AnimationState()
    private var idleAnimationTimeout: Long = 0

    /*
    public BlessedEntity(EntityType<Entity> entityEntityType, Level level) {
        super(entityEntityType, level);
    }

     */
    internal enum class CustomBrain {
        DEFAULT,
        INJURED,
        LEAVING
    }

    // todo: java made this ok, in kotlin this hides brain
    private var brain = CustomBrain.DEFAULT

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, AvoidEntityGoal(this, Player::class.java, 3.0f, 0.5, 0.5))
        this.goalSelector.addGoal(2, BlessedFishGoal(this))
        this.goalSelector.addGoal(3, BlessedFindFluidGoal(this))

        // remove milk and potion goals.
        this.goalSelector.availableGoals.stream()
            .filter { wrappedGoal: WrappedGoal? -> wrappedGoal!!.goal is UseItemGoal<*> }
            .forEach { wrappedGoal: WrappedGoal? -> this.goalSelector.removeGoal(wrappedGoal!!.goal) }
    }


    private fun atMaximumHealth(): Boolean {
        return this.maxHealth == this.health
    }

    private fun summonCursedAtPosition(entity: Entity) {
        val cursed = CursedEntity(FishcastingEntities.CURSED, entity.level())
        cursed.setPos(entity.position())

        cursed.moveTo(
            entity.x,
            entity.y,
            entity.z,
            entity.yRot,
            entity.xRot
        )

        cursed.deltaMovement = entity.deltaMovement

        if (entity.hasCustomName()) {
            cursed.customName = entity.customName
            cursed.isCustomNameVisible = entity.isCustomNameVisible
        }

        cursed.remainingFireTicks = entity.remainingFireTicks
        entity.level().addFreshEntity(cursed)
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        this.decrementMood()
        return super.hurt(source, amount)
    }

    override fun actuallyHurt(p0: DamageSource, p1: Float) {
        super.actuallyHurt(p0, p1)
    }

    private fun tryClosingInventory() {
        val player = this.tradingPlayer
        if (player != null) {
            player.inventory.stopOpen(player)
            player.inventoryMenu.broadcastChanges()
        }
        this.stopTrading()
    }

    private fun vanish() {
        this.stopTrading()
        this.setPos(OUTTA_HERE)
        this.discard()
    }

    override fun aiStep() {
        super.aiStep()
        if (!this.level().isClientSide) {
            if (HexAPI.instance().isBrainswept(this)) {
                this.mood = Mood.VERY_HAPPY
                summonCursedAtPosition(this)
                this.kill()
                this.vanish()
                return
            }
            when (brain) {
                CustomBrain.DEFAULT -> {
                    if (!this.atMaximumHealth()) {
                        brain = CustomBrain.INJURED
                    } else {
                        //this.setItemInHand(InteractionHand.MAIN_HAND, Items.AIR.getDefaultInstance());
                    }
                }

                CustomBrain.INJURED -> {
                    if (this.isDeadOrDying) return
                    if (this.atMaximumHealth()) {
                        brain = CustomBrain.DEFAULT
                        ticksSincePain = 0
                        this.setItemInHand(InteractionHand.MAIN_HAND, Items.AIR.defaultInstance)
                    } else {
                        if (ticksSincePain > 20) {
                            brain = CustomBrain.LEAVING
                            this.doTheatrics()
                        } else if (ticksSincePain >= 2) { // pulled out much later than I'd like but it works, I guess....
                            // note: this technically provides a way to obtain this from the trader, but the timing is so tight I think it's ok.
                            // significantly easier to just make a cypher than it is it both get the good tick rng and to make it die at the same time
                            this.setItemInHand(InteractionHand.MAIN_HAND, HexItems.ANCIENT_CYPHER.defaultInstance)
                        }
                        ticksSincePain += ticksSincePain + Fishcasting.random.nextInt(0, 2)
                    }
                }

                CustomBrain.LEAVING -> {
                    this.stopTrading()
                    this.discard()
                }
            }
        }
    }

    protected fun addRandomListing(merchantoffers: MerchantOffers, itemListing: Array<VillagerTrades.ItemListing>) {
        val i = this.random.nextInt(itemListing.size)
        val chosenRandomListing = itemListing[i]
        val merchantoffer = chosenRandomListing.getOffer(this, this.random)
        if (merchantoffer != null) {
            merchantoffers.add(merchantoffer)
        }
    }

    override fun updateTrades() {
        val commonListing = BlessedTrades.BLESSED_TRADER_TRADES.get(1) as Array<VillagerTrades.ItemListing>
        val rareListing = BlessedTrades.BLESSED_TRADER_TRADES.get(2) as Array<VillagerTrades.ItemListing>
        val nonOverworldListing = BlessedTrades.BLESSED_TRADER_TRADES.get(3) as Array<VillagerTrades.ItemListing>
        val dyeListing = BlessedTrades.BLESSED_TRADER_TRADES.get(4) as Array<VillagerTrades.ItemListing>
        val bedrockEaterListing = BlessedTrades.BLESSED_TRADER_TRADES.get(5) as Array<VillagerTrades.ItemListing>

        val merchantoffers = this.getOffers()
        this.addOffersFromItemListings(merchantoffers, commonListing, 3)
        addRandomListing(merchantoffers, rareListing)
        addRandomListing(merchantoffers, nonOverworldListing)
        addRandomListing(merchantoffers, dyeListing)
        addRandomListing(merchantoffers, bedrockEaterListing)
    }


    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        // ItemStack itemstack = player.getItemInHand(hand);
        if (this.isAlive && !this.isTrading) {
            if (hand == InteractionHand.MAIN_HAND) {
                player.awardStat(Stats.TALKED_TO_VILLAGER)
            }

            if (!this.level().isClientSide) {
                if (this.getOffers().isEmpty()) {
                    return InteractionResult.CONSUME
                }

                this.tradingPlayer = player
                this.openTradingScreen(player, this.displayName!!, 1)
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide)
        } else {
            return super.mobInteract(player, hand)
        }
    }

    // I couldn't figure out how to directly write the pigment color values
    // BlessedEntity.class.getPackage().getName().split("\\.")[0] // if I wanted to use my github username
    // public static final Supplier<FrozenPigment> BLESSED = () -> new FrozenPigment(new ItemStack(HexItems.UUID_PIGMENT), UUID.fromString(""));
    @JvmOverloads
    fun doTheatrics(position: Vec3 = this.eyePosition) {
        doTheatricsAtVec(position, 30, 0.4f)
        this.level().playSound(this, this.blockPosition(), HexSounds.CAST_SPELL, SoundSource.NEUTRAL, 0.1f, 1.0f)
        // AHHH ITS SO LOUD
    }

    private fun doTheatricsAtVec(pos: Vec3, count: Int, fuzziness: Float) {
        val level = this.level()
        // for some reason this happens at its feet...
        doTheatricsAtVec(pos, count, fuzziness, Math.PI / 3)
    }

    fun doTheatricsAtVec(pos: Vec3, count: Int, fuzziness: Float, spread: Double) {
        val level = this.level()
        // for some reason this happens at its feet...
        ParticleSpray(pos, Vec3(0.0, 1.5, 0.0), fuzziness.toDouble(), spread, count)
            .sprayParticles(level.server!!.getLevel(level.dimension())!!, FrozenPigment.ANCIENT.get())
    }

    override fun brainProvider(): Brain.Provider<BlessedEntity?> {
        return Brain.provider<BlessedEntity?>(MEMORY_TYPES, SENSOR_TYPES)
    }

    class BlessedFindFluidGoal : RandomStrollGoal {
        private var pathToFluid: Path? = null

        constructor(mob: PathfinderMob) : super(mob, 0.5)

        constructor(mob: PathfinderMob, speedModifier: Double) : super(mob, speedModifier, 100)

        constructor(mob: PathfinderMob, speedModifier: Double, interval: Int) : super(
            mob,
            speedModifier,
            interval,
            false
        )

        constructor(mob: PathfinderMob, speedModifier: Double, interval: Int, checkNoActionTime: Boolean) : super(
            mob,
            speedModifier,
            interval,
            checkNoActionTime
        )

        var maxPathfindingDistance: Int = 20
        override fun getPosition(): Vec3? {
            // at least make an *attempt* to find water, then go to it.
            // if we are already on water, I think whatever
            // need to remember to eject from vehicle if no valid water nearby for fishing
            val serverLevel = this.mob.level() as ServerLevel
            val mobBlockPos = this.mob.blockPosition()
            val maybeFluid = BlockPos.findClosestMatch(
                mobBlockPos,
                maxPathfindingDistance, maxPathfindingDistance,
                Predicate { testBlockPos: BlockPos? ->
                    val state = serverLevel.getBlockState(testBlockPos!!) // well, if I've chosen a blockPos it MUST exist, even if it's not valid.
                    if (state.`is`(Blocks.WATER) || state.`is`(Blocks.LAVA)) {
                        return@Predicate state.fluidState.isSource
                    }
                    false
                })
            if (maybeFluid.isPresent) {
                val fluidTarget = maybeFluid.get()
                this.mob.getBrain().setMemory<GlobalPos?>(
                    MemoryModuleType.JOB_SITE,
                    GlobalPos.of(mob.level().dimension(), fluidTarget)
                ) // tiny little ram, just for me!
                this.pathToFluid = this.mob.getNavigation().createPath(fluidTarget, 4)
                return fluidTarget.center
            } else {
                this.pathToFluid = null
                this.mob.getBrain()
                    .eraseMemory<GlobalPos?>(MemoryModuleType.JOB_SITE) // and if the villager tries to find more and can't, become unemployed from being a freelance fisher
            }
            return DefaultRandomPos.getPos(this.mob, 6, 6)
        }

        // should I check if I should interrupt the path?
        override fun start() {
            if (this.pathToFluid != null) {
                this.mob.getNavigation().moveTo(this.pathToFluid!!, this.speedModifier)
            }
        }
    }

    init {
        this.fakeBobberPos = this.eyePosition.add(0.0, 1.0, 0.0)

        // from Villager
        (this.getNavigation() as GroundPathNavigation).setCanOpenDoors(true)
        this.getNavigation().setCanFloat(true)
        this.setCanPickUpLoot(true)

        // to make it despawn at some point I gave it a mood meter
        this.mood = Mood.NEUTRAL

        // animation stuff
        if (this.level().isClientSide) {
            splayLimbs.start(0) // we only call this once.
        }
    }

    // TryFindWaterGoal already exists huh... but I do need lava as well, and also void fluid so I will need to keep this in mind.
    class BlessedFishGoal(private val mob: Mob) : Goal() {
        private val yd = 0.5f
        private var bobberActive = false
        private var bobberBiteTimer: Long = 0
        private var bobberSummonedAtTime: Long = 0
        private var fishCaught = 0
        private val maxFishCaught: Int = Fishcasting.random.nextInt(3, 8)

        private fun remembersFluid(): Boolean {
            return mob.getBrain().hasMemoryValue(MemoryModuleType.JOB_SITE)
        }

        private val fluid: BlockPos
            get() {
                val memory = mob.getBrain().getMemory(MemoryModuleType.JOB_SITE)
                return memory.get().pos()
            }

        // at least make an attempt to check if casting a line could work
        private fun hasLineOfSightToFluid(): Boolean {
            if (!this.remembersFluid()) {
                return false
            }
            val pos = this.fluid
            val start = mob.eyePosition
            // should I check the top/sides/ figure out which side? I think this is usually ok...
            // I decided that checking the surface is most reasonable...
            val end = Vec3.atCenterOf(pos).add(raycastOffset)

            val result = mob.level().clip(
                ClipContext(
                    start,
                    end,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    mob
                )
            )

            return result.blockPos == pos
        }

        private fun updateBobberPos() {
            if (this.remembersFluid()) {
                val be = this.mob as BlessedEntity
                val diffToWater = this.fluid.center.subtract(be.fakeBobberPos).scale(0.1)
                be.fakeBobberPos = be.fakeBobberPos.add(diffToWater)
            }
        }

        private fun ejectFreshBobber() {
            // I think this is good enough?
        }

        override fun canUse(): Boolean {
            if (hasLineOfSightToFluid() && fishCaught <= maxFishCaught) {
                return true // need more conditions
            }
            (this.mob as BlessedEntity).pose = Pose.STANDING // I dont know all the places where I need to and dont need to have this but I know I need it here.
            // I definitely need to rewrite this class at some point...
            this.bobberSummonedAtTime++ // if blocked for too long, make it so that we can't keep it active too long
            return false
        }

        override fun canContinueToUse(): Boolean {
            return (this.bobberSummonedAtTime + minimumTimeSinceLastSpawn >= this.mob.tickCount)
        }

        /*
        @Override
        public void stop() {
            this.bobberActive = false;
            this.bobberBiteTimer = 0;
        }

         */
        // entity.setDeltaMovement(dx * 0.1, dy * 0.11 + Math.sqrt(Math.sqrt(dx * dx + dy * dy + dz * dz)) * 0.08, dz * 0.1);
        private fun summonFishMakeItFlyTowrdsFisher(medium: FishingMedium?, fisher: Entity, fishStack: ItemStack) {
            val entity: Entity?
            val spawnPos = this.fluid.center
            if (medium === FishingMedium.LAVA) {
                entity = object : ItemEntity(fisher.level(), spawnPos.x, spawnPos.y + 0.5, spawnPos.z, fishStack) {
                    override fun displayFireAnimation(): Boolean {
                        return false
                    }

                    override fun lavaHurt() {}
                }
            } else entity = ItemEntity(fisher.level(), spawnPos.x, spawnPos.y + 0.5, spawnPos.z, fishStack)

            val dx = fisher.x - entity.x
            val dy = fisher.y - entity.y
            val dz = fisher.z - entity.z
            entity.setDeltaMovement(dx * 0.1, dy * 0.11 + sqrt(sqrt(dx * dx + dy * dy + dz * dz)) * 0.08, dz * 0.1)
            entity.addTag(BLESSED_PICKUP_TAG)
            fisher.level().addFreshEntity(entity)
            fishCaught++
        }

        private fun fishUpFromPos() {
            // get conditions
            val level = this.mob.level()
            // I need to generate a random fish for a specific blockPos...
            // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/entities/fish/TideFishEntity.java#L6
            val biome = level.getBiome(this.fluid)
            // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/entities/misc/fishing/TideFishingHook.java#L376
            val fishingMedium = FishingMedium.MEDIUMS.stream()
                .filter { medium: FishingMedium? -> medium!!.isAt(this.fluid, level as ServerLevel) }.findFirst()

            if (fishingMedium.isEmpty) return
            val fakeHook = TideFishingHook(TideEntityTypes.FISHING_BOBBER, level)

            val bait = TideItems.BAIT.defaultInstance

            val context = FishingContext(
                level as ServerLevel, fakeHook, TideItems.CRYSTAL_FISHING_ROD.defaultInstance,
                this.mob.getRandom(), this.fluid.center, this.fluid, 0,
                fishingMedium.get().id().path, biome, biome, level.dimension(),
                TideUtils.getTemperatureAt(this.fluid, level),
                level.moonPhase, SeasonsCompat.getSeason(level)
            )
            val results: MutableMap<FishingEntry?, Double?> = HashMap<FishingEntry?, Double?>()
            val result = Tide.FISHING_MANAGER.selectCatch(context)
            val be = this.mob as BlessedEntity
            if (result.entry().isPresent && result.entry().get().matchesTestType(TestType.FISH)) {
                val entry = result.entry().get()
                summonFishMakeItFlyTowrdsFisher(fishingMedium.get(), this.mob, result.items().first())
                be.doTheatrics(this.fluid.center)
            } else {
                be.decrementMood() // unhappy because no fish, i don't remember if this can be reached but I think crates and treasure are included so maybe?
            }
        }

        override fun start() {
            val be = this.mob as BlessedEntity
            be.doTheatricsAtVec(be.fakeBobberPos, 1, 0.1f, 0.1)
            be.isFishing = true
            if (!this.bobberActive) {
                be.fakeBobberPos = this.mob.position().add(raycastOffset)
                this.bobberActive = true
                this.bobberBiteTimer = 0
                this.bobberSummonedAtTime = this.mob.tickCount.toLong()
                be.pose = Pose.STANDING
            } else {
                // stuff I do when bobber is actively out
                updateBobberPos()
                this.bobberBiteTimer++
                be.pose = Pose.SHOOTING
            }

            if (this.bobberBiteTimer > minimumTicksUntilBite) {
                // only after 3 ish seconds, consider removing bobber
                if (Fishcasting.random.nextFloat() > 0.9) {
                    // todo: play fancy animation and stuff

                    fishUpFromPos()
                    this.bobberActive = false
                    this.bobberBiteTimer = 0
                    be.setPose(Pose.STANDING)
                }
            }
        }

        companion object {
            private const val minimumTimeSinceLastSpawn = (20 * 12).toLong()
            private const val minimumTicksUntilBite = (20 * 6).toLong()
            private val raycastOffset = Vec3(0.0, 0.49, 0.0)
        }
    }


    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)
        // todo: figure out a better way to choose what animation to play
        if (DATA_POSE == key) {
            when (this.pose) {
                Pose.STANDING -> {
                    this.isFishing = false
                    this.isHappy = false
                }

                Pose.SHOOTING -> {
                    this.isFishing = true
                }

                Pose.SPIN_ATTACK -> {
                    this.isHappy = true
                }

                else -> {
                    // do nothing?
                }
            }
        }
    }

    // thank you kaupenjoe
    private fun setupAnimationStates() {
        // Breathing in/out
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout =
                (BlessedAnimations.standIdle.lengthInSeconds() * 20).toLong() // I hope the compiler can see this is a constant
            this.idleAnimationState.startIfStopped(this.tickCount) // do I need to do this if it's looping? I don't know......... kaupenjoe seemed to do this but I'm unsure if it's actually needed...
        } else {
            --this.idleAnimationTimeout
        }
        // Waving hand for fishing
        if (this.isFishing) {
            this.castingAnimationState.startIfStopped(this.tickCount)
        } else {
            stopIfPlaying(castingAnimationState)
        }
        // Ear twitching randomly
        // deterministic based on UUID and tick count, actually...
        // this is to sync between server players as a fun little test for me
        if (this.random.nextFloat() > 0.8) {
            if (this.random.nextFloat() > 0.99) {
                this.leftEarWiggleState.start(this.tickCount)
            }
            if (this.random.nextFloat() > 0.99) {
                this.rightEarWiggleState.start(this.tickCount)
            }
        }
        // tail if nearby a player with reputation?
        if (this.jumping || this.isHappy) {
            this.tailWiggleState.startIfStopped(this.tickCount)
        } else {
            stopIfPlaying(tailWiggleState)
        }
        // if forced into a boat or chair
        if (this.isPassenger) {
            sittingAnimationState.startIfStopped(this.tickCount)
        } else {
            stopIfPlaying(sittingAnimationState)
        }
    }

    private fun stopIfPlaying(animationState: AnimationState) {
        if (animationState.isStarted()) {
            animationState.stop()
        }
    }

    override fun canBeLeashed(): Boolean {
        return true // makes it easier to set up the brainsweep, if the player does not want to time it with a fishing rod. also makes it meaner.
    }

    override fun tick() {
        super.tick()
        if (this.level().isClientSide) {
            this.setupAnimationStates()
        } else {
            if (this.isLeashed) {
                if (Mth.positiveModulo(this.tickCount, TICKS_BETWEEN_MOOD_DROPS / 100) == 0) {
                    this.decrementMood()
                }
            } else {
                if (Mth.positiveModulo(this.tickCount, TICKS_BETWEEN_MOOD_DROPS) == 0) {
                    this.decrementMood()
                }
            }
            when (this.mood) {
                Mood.VERY_HAPPY -> {
                    this.pose = Pose.SPIN_ATTACK
                }

                Mood.VERY_SAD -> {
                    this.brain = CustomBrain.LEAVING
                    doTheatrics()
                }

                else -> {
                    // do nothing?
                    if (this.pose == Pose.SPIN_ATTACK) {
                        this.pose = Pose.STANDING
                    }
                }
            }
        }
    }

    // todo: can I just use itemEntity.getOwnmer() for my bobber?
    public override fun pickUpItem(itemEntity: ItemEntity) {
        if (this.level().gameRules.getBoolean(GameRules.RULE_MOBGRIEFING)
            && this.isAlive
            && itemIsFish(itemEntity.item)
        ) {
            if (itemEntity.removeTag(BLESSED_PICKUP_TAG)) {
                this.incrementMood()
            } else {
                if (FishData.get(itemEntity).isPresent
                    && FishData.get(itemEntity).get().quality() > 3
                ) {
                    this.incrementMood()
                }
            }

            val itemstack = itemEntity.item
            val itemstack1 = itemstack.copy()
            this.onItemPickup(itemEntity)
            this.take(itemEntity, itemstack1.count)
            itemstack.shrink(itemstack1.count)
            if (itemstack.isEmpty) {
                itemEntity.discard()
            }
        }
    }

    override fun canHoldItem(stack: ItemStack): Boolean {
        return itemIsFish(stack)
    }

    override fun canPickUpLoot(): Boolean {
        return true
    }

    // thank you kaupenjoe
    /* VARIANT */
    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(VARIANT, 0)
    }

    private val typeVariant: Int
        get() = this.entityData.get(VARIANT)

    var variant: BlessedVariant?
        get() = BlessedVariant.byId(this.typeVariant and 255) // why does mojang check if we have more than 255 variants? kaupenjoe does too, I assume it's something obscure or a silly choice and in either case it matters little
        private set(variant) {
            this.entityData.set(VARIANT, variant!!.id and 255)
        }

    override fun addAdditionalSaveData(compoundTag: CompoundTag) {
        super.addAdditionalSaveData(compoundTag)
        compoundTag.putInt("Variant", this.typeVariant)
    }

    override fun readAdditionalSaveData(compoundTag: CompoundTag) {
        super.readAdditionalSaveData(compoundTag)
        this.entityData.set<Int>(VARIANT, compoundTag.getInt("Variant"))
    }

    override fun finalizeSpawn(
        serverLevelAccessor: ServerLevelAccessor, difficultyInstance: DifficultyInstance,
        mobSpawnType: MobSpawnType, spawnGroupData: SpawnGroupData?
    ): SpawnGroupData {
        val variant = Util.getRandom(BlessedVariant.entries.toTypedArray(), this.random)
        this.variant = variant
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData)!!
    }

    // thank you kaupenjoe, again
    /* SOUNDS */ // todo: custom sounds
    override fun getAmbientSound(): SoundEvent? {
        return if (this.isSleeping) {
            null
        } else {
            if (this.isTrading) FishcastingSounds.BLESSED_TRADE else FishcastingSounds.BLESSED_AMBIENT
        }
    }

    override fun getHurtSound(damageSource: DamageSource): SoundEvent {
        return FishcastingSounds.BLESSED_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return FishcastingSounds.BLESSED_DEATH
    }

    // this is no longer needed, I think?
    override fun getDrinkingSound(stack: ItemStack): SoundEvent {
        return if (stack.`is`(Items.MILK_BUCKET)) FishcastingSounds.BLESSED_DRINK_MILK else FishcastingSounds.BLESSED_DRINK_POTION
    }

    // TODO: include a lore message detailing the steps to make a "CURSED"
    override fun getTradeUpdatedSound(getYesSound: Boolean): SoundEvent {
        return if (getYesSound) FishcastingSounds.BLESSED_YES else FishcastingSounds.BLESSED_NO
    }

    override fun getNotifyTradeSound(): SoundEvent {
        return FishcastingSounds.BLESSED_YES
    }

    companion object {
        private const val SECONDS_BETWEEN_MOOD_DROPS = 300
        private const val TICKS_BETWEEN_MOOD_DROPS: Int = SECONDS_BETWEEN_MOOD_DROPS * 20

        // thank you kaupenjoe
        private val VARIANT: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(BlessedEntity::class.java, EntityDataSerializers.INT)

        fun poofIntoExistence(spawnPosition: Vec3, level: Level) {
            if (!level.isClientSide) {
                val blessedEntity = BlessedEntity(FishcastingEntities.BLESSED, level)
                blessedEntity.variant = Util.getRandom<BlessedVariant?>(
                    BlessedVariant.entries.toTypedArray(),
                    blessedEntity.random
                )
                blessedEntity.setPos(spawnPosition)
                level.addFreshEntity(blessedEntity)
                blessedEntity.doTheatrics()
            }
        }

        private val OUTTA_HERE = Vec3(0.0, 1000000.0, 0.0)

        val MEMORY_TYPES: ImmutableList<MemoryModuleType<*>?> = ImmutableList.of<MemoryModuleType<*>?>(
            MemoryModuleType.JOB_SITE,
            MemoryModuleType.HIDING_PLACE
        )

        val SENSOR_TYPES: ImmutableList<SensorType<out Sensor<in BlessedEntity?>?>?> =
            ImmutableList.of<SensorType<out Sensor<in BlessedEntity?>?>?>()

        private const val BLESSED_PICKUP_TAG = "fishcasting_blessed_pickup"

        // only grab known fish
        private fun itemIsFish(maybeFish: ItemStack?): Boolean {
            return TideUtils.isJournalFish(maybeFish)
        }
    }
}