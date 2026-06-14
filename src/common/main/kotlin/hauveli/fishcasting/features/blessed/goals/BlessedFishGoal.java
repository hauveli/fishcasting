package hauveli.fishcasting.features.blessed.goals;

import com.li64.tide.Tide;
import com.li64.tide.compat.seasons.SeasonsCompat;
import com.li64.tide.data.TideFishingManager;
import com.li64.tide.data.fishing.CatchResult;
import com.li64.tide.data.fishing.FishingContext;
import com.li64.tide.data.fishing.mediums.FishingMedium;
import com.li64.tide.data.fishing.selector.FishingEntry;
import com.li64.tide.util.TideUtils;
import hauveli.fishcasting.Fishcasting;
import hauveli.fishcasting.features.blessed.BlessedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlessedFishGoal extends Goal {
    private final Mob mob;
    private final float yd = 0.5f;
    private boolean bobberActive = false;
    private long bobberBiteTimer = 0;
    private long bobberSummonedAtTime = 0;
    private static final long minimumTimeSinceLastSpawn = 20 * 12;
    private static final long minimumTicksUntilBite = 20 * 6;

    public BlessedFishGoal(Mob mob) {
        this.mob = mob;
    }

    private boolean remembersFluid() {
        return mob.getBrain().hasMemoryValue(MemoryModuleType.JOB_SITE);
    }

    private BlockPos getFluid() {
        Optional<GlobalPos> memory = mob.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        return memory.get().pos();
    }

    private static final Vec3 raycastOffset = new Vec3(0,0.49,0);
    // at least make an attempt to check if casting a line could work
    private boolean hasLineOfSightToFluid() {
        if (!this.remembersFluid()) {
            return false;
        }
        BlockPos pos = getFluid();
        Vec3 start = mob.getEyePosition();
        // should I check the top/sides/ figure out which side? I think this is usually ok...
        // I decided that checking the surface is most reasonable...
        Vec3 end = Vec3.atCenterOf(pos).add(raycastOffset);

        BlockHitResult result = mob.level().clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                mob
        ));

        return result.getBlockPos().equals(pos);
    }

    private void updateBobberPos() {
        if (this.remembersFluid()) {
            BlessedEntity be = (BlessedEntity) this.mob;
            Vec3 diffToWater = this.getFluid().getCenter().subtract(be.fakeBobberPos).scale(0.1);
            be.fakeBobberPos = be.fakeBobberPos.add(diffToWater);
        }
    }

    private void ejectFreshBobber() {
        // I think this is good enough?
    }

    @Override
    public boolean canUse() {
        if (hasLineOfSightToFluid()) {
            return true; // need more conditions
        }
        this.bobberSummonedAtTime++; // if blocked for too long, make it so that we can't keep it active too long
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return (this.bobberSummonedAtTime + minimumTimeSinceLastSpawn >= this.mob.tickCount);
    }

        /*
        @Override
        public void stop() {
            this.bobberActive = false;
            this.bobberBiteTimer = 0;
        }

         */


    private static TideFishingManager fishMan = new TideFishingManager();
    private void fishUpFromPos() {
        // get conditions
        Level level = this.mob.level();
        // I need to generate a random fish for a specific blockPos...
        // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/entities/fish/TideFishEntity.java#L6
        Holder<Biome> biome = level.getBiome(this.getFluid());
        // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/entities/misc/fishing/TideFishingHook.java#L376
        Optional<FishingMedium> fishingMedium = FishingMedium.MEDIUMS.stream()
                .filter(medium -> Fishcasting.random.nextBoolean()).findFirst(); // this is way funnier than implementing it properly

        if (fishingMedium.isEmpty()) return;

        FishingContext context = new FishingContext(
                (ServerLevel) level, null, null, this.getFluid().getCenter(), this.getFluid(), 0,
                fishingMedium.get().id().getPath(), null, biome, biome, level.dimension(),
                TideUtils.getTemperatureAt(this.getFluid(), (ServerLevel) level),
                level.getMoonPhase(), SeasonsCompat.getSeason(level)
        );
        Map<FishingEntry, Double> results = new HashMap<>();
        CatchResult result = Tide.FISHING_MANAGER.selectCatch(context);
        //CatchResult result = fishMan.getFishSelector().getResult(context, results, entry -> entry.matchesTestType(TestType.FISH));

        if (result.entry().isPresent()) {
            FishingEntry entry = result.entry().get();
            this.mob.level().getServer().sendSystemMessage(Component.nullToEmpty(entry.toString()));
        } else {

            this.mob.level().getServer().sendSystemMessage(Component.nullToEmpty("no fishbert"));
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

    @Override
    public void start() {
        BlessedEntity be = (BlessedEntity) this.mob;
        be.doTheatricsAtVec(be.fakeBobberPos,1, 0.1f, 0.1);
        if (!this.bobberActive) {
            be.fakeBobberPos = this.mob.position().add(raycastOffset);
            this.bobberActive = true;
            this.bobberBiteTimer = 0;
            this.bobberSummonedAtTime = this.mob.tickCount;
        } else {
            // stuff I do when bobber is actively out
            updateBobberPos();
            this.bobberBiteTimer++;
        }

        if (this.bobberBiteTimer > minimumTicksUntilBite) {
            // only after 3 ish seconds, consider removing bobber
            if (Fishcasting.random.nextFloat() > 0.9) {
                // todo: play fancy animation and stuff

                fishUpFromPos();
                this.bobberActive = false;
                this.bobberBiteTimer = 0;
            }
        }
    }
}
