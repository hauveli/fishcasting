package hauveli.fishcasting.features.trader.goals

import com.li64.tide.Tide
import com.li64.tide.compat.seasons.SeasonsCompat
import com.li64.tide.data.TideFishingManager
import com.li64.tide.data.fishing.FishingContext
import com.li64.tide.data.fishing.mediums.FishingMedium
import com.li64.tide.data.fishing.selector.FishingEntry
import com.li64.tide.util.TideUtils
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.features.trader.BlessedEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.Vec3


class BlessedFishGoal(private val mob: Mob) : Goal() {
    private val yd = 0.5f
    private var bobberActive = false
    private var bobberBiteTimer: Long = 0
    private var bobberSummonedAtTime: Long = 0
    private fun remembersFluid(): Boolean {
        return mob.getBrain().hasMemoryValue(MemoryModuleType.JOB_SITE)
    }

    private val fluid: BlockPos
        get() {
            val memory = mob.getBrain().getMemory<GlobalPos?>(MemoryModuleType.JOB_SITE)
            return memory.get().pos()
        }

    // at least make an attempt to check if casting a line could work
    private fun hasLineOfSightToFluid(): Boolean {
        if (!this.remembersFluid()) {
            return false
        }
        val pos: BlockPos = this.fluid
        val start = mob.getEyePosition()
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

        return result.getBlockPos() == pos
    }

    private fun updateBobberPos() {
        if (this.remembersFluid()) {
            val be = this.mob as BlessedEntity
            val diffToWater: Vec3 = this.fluid.getCenter().subtract(be.fakeBobberPos).scale(0.1)
            be.fakeBobberPos = be.fakeBobberPos.add(diffToWater)
        }
    }

    private fun ejectFreshBobber() {
        // I think this is good enough?
    }

    override fun canUse(): Boolean {
        if (hasLineOfSightToFluid()) {
            return true // need more conditions
        }
        this.bobberSummonedAtTime++ // if blocked for too long, make it so that we can't keep it active too long
        return false
    }

    override fun canContinueToUse(): Boolean {
        return (this.bobberSummonedAtTime + minimumTimeSinceLastSpawn >= this.mob.tickCount)
    }


    private fun fishUpFromPos() {
        // get conditions
        val level = this.mob.level()
        // I need to generate a random fish for a specific blockPos...
        // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/entities/fish/TideFishEntity.java#L6
        val biome = level.getBiome(this.fluid)
        // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/entities/misc/fishing/TideFishingHook.java#L376
        val fishingMedium = FishingMedium.MEDIUMS.stream()
            .filter { medium: FishingMedium? -> Fishcasting.random.nextBoolean() }
            .findFirst() // this is way funnier than implementing it properly

        if (fishingMedium.isEmpty()) return

        val context = FishingContext(
            level as ServerLevel, null, null, this.fluid.getCenter(), this.fluid, 0,
            fishingMedium.get().id().getPath(), null, biome, biome, level.dimension(),
            TideUtils.getTemperatureAt(this.fluid, level),
            level.getMoonPhase(), SeasonsCompat.getSeason(level)
        )
        val results: MutableMap<FishingEntry?, Double?> = HashMap<FishingEntry?, Double?>()
        val result = Tide.FISHING_MANAGER.selectCatch(context)

        //CatchResult result = fishMan.getFishSelector().getResult(context, results, entry -> entry.matchesTestType(TestType.FISH));
        if (result.entry().isPresent()) {
            val entry = result.entry().get()
            this.mob.level().getServer()!!.sendSystemMessage(Component.nullToEmpty(entry.toString()))
        } else {
            this.mob.level().getServer()!!.sendSystemMessage(Component.nullToEmpty("no fishbert"))
        }
        // choose one fish
        /*
            double totalWeight = results.values().stream().mapToDouble(Double::doubleValue).sum();
            double roll = level.random.nextDouble() * totalWeight;

            FishingEntry selected = null;
            for (Map.Entry<FishingEntry, Double> entry : results.entrySet()) {
                roll -= entry.getValue();
                if (roll <= 0) {
                    selected = entry.getKey();
                    break;
                }
            }


            EntityType<?> type = selected.getResult(context).;
            Entity entity = type.create((ServerLevel) level);

            entity.moveTo(this.getFluid().getCenter());

            level.addFreshEntity(entity);

             */
    }

    override fun start() {
        val be = this.mob as BlessedEntity
        be.doTheatricsAtVec(be.fakeBobberPos, 1, 0.1f, 0.1)
        if (!this.bobberActive) {
            be.fakeBobberPos = this.mob.position().add(raycastOffset)
            this.bobberActive = true
            this.bobberBiteTimer = 0
            this.bobberSummonedAtTime = this.mob.tickCount.toLong()
        } else {
            // stuff I do when bobber is actively out
            updateBobberPos()
            this.bobberBiteTimer++
        }

        if (this.bobberBiteTimer > minimumTicksUntilBite) {
            // only after 3 ish seconds, consider removing bobber
            if (Fishcasting.random.nextFloat() > 0.9) {
                // todo: play fancy animation and stuff

                fishUpFromPos()
                this.bobberActive = false
                this.bobberBiteTimer = 0
            }
        }
    }

    companion object {
        private val minimumTimeSinceLastSpawn = (20 * 12).toLong()
        private val minimumTicksUntilBite = (20 * 6).toLong()

        private val raycastOffset = Vec3(0.0, 0.49, 0.0)

        /*
        @Override
        public void stop() {
            this.bobberActive = false;
            this.bobberBiteTimer = 0;
        }

         */
        private val fishMan = TideFishingManager()
    }
}
