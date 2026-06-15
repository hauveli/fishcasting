package hauveli.fishcasting.common.cursed;

import com.li64.tide.data.FishLengthHolder;
import com.li64.tide.data.fishing.FishData;
import com.li64.tide.registries.entities.fish.AbstractTideFish;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static com.li64.tide.data.item.TideItemData.FISH_LENGTH;
import static hauveli.fishcasting.common.registries.FishcastingItems.DISC;

// melted axolotl fish
public class CursedEntity extends Axolotl implements Bucketable, FishLengthHolder {
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
    public CursedEntity(EntityType<? extends Axolotl> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(false);
        this.setCanPickUpLoot(false);
        //this.setInvulnerable(true);
        this.setSpeed(0.01f);

        // need to give the fish the properties of a tide fish, but I still want the entity to be an axolotl so I can keep all its goals and animations.
        // this was the least effort for me, but I would gladly accept PRs to change it haha...
        // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/entities/fish/AbstractTideFish.java#L46
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        Item fishItem = BuiltInRegistries.ITEM.getOptional(key).orElseThrow();
        this.bucketItem = BuiltInRegistries.ITEM.getOptional(key.withSuffix("_bucket")).orElseThrow();
        this.length = FishData.get(fishItem).map(data -> data.getRandomLength(getRandom())).orElse(0.0);
    }

    private final Item bucketItem;
    private double length;

    @Override
    public int getHeadRotSpeed() {
        return 1;
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

    @Override
    public @NotNull ItemStack getBucketItemStack() {
        return bucketItem.getDefaultInstance();
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_AXOLOTL;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return false;
    }

    @Override
    public int getMaxAirSupply() {
        return 0; // constant pain
    }

    @Override
    public boolean isInWater() {
        return false; // constant pain
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (relevantDamageSource(source)) {
            doAllaySpawnOnLightningHitMob(this, source);
            return super.hurt(source, amount);
        }
        return super.hurt(source, 0f);
    }

    public static boolean relevantOptionalDamageSource(DamageSource source) {
        return source.is(DamageTypes.LIGHTNING_BOLT);
    }

    // terrible name
    public static boolean relevantVoidDamageSource(DamageSource source) {
        return source.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    public static boolean relevantDebugDamageSource(DamageSource source) {
        return source.is(DamageTypes.GENERIC_KILL);
    }

    public static boolean relevantDamageSource(DamageSource source) {
        return relevantOptionalDamageSource(source)
                || relevantDebugDamageSource(source)
                || relevantVoidDamageSource(source);
    }

    public static void spawnAllayAtEntity(Entity entity) {
        Allay allay = EntityType.ALLAY.create(entity.level());
        allay.setPos(entity.position());
        // allay will catch fire otherwise
        allay.addEffect(
                new MobEffectInstance(
                        MobEffects.FIRE_RESISTANCE,
                        20, // this is in ticks I think so 1 second
                        1
                )
        );
        // if it is spawned in via creative, odds are the player wants to test this
        if (getFishLength(entity) >= 66.6d) {
            allay.setItemInHand(
                    InteractionHand.MAIN_HAND,
                    DISC.getDefaultInstance()
            );
        }
        entity.level().addFreshEntity(allay);
    }

    // hmm... spell?
    // could be good for fishing up something -> immediately smiting it if under a size
    // downside is that it is hyper-specific, and other addons can already read item data (so it doesnt add much then)
    private static double getFishLength(Entity entity) {
        if (entity instanceof FishLengthHolder fishLengthHolder) {
            return fishLengthHolder.tide$getLength();
        } else if (entity instanceof ItemEntity itemEntity) {
            if (FISH_LENGTH.get(itemEntity.getItem()) instanceof Double) {
                return FISH_LENGTH.get(itemEntity.getItem());
            }
        }
        return 0;
    }

    public static void doAllaySpawnOnLightningHitMob(Entity entity, DamageSource damageSource) {
        if (!damageSource.is(DamageTypes.LIGHTNING_BOLT)) {
            return;
        }
        spawnAllayAtEntity(entity);
        entity.kill();
        entity.discard();
    }

    // I should clean this up and move this or do something smarter when I port it to kotlin I think
    public static void doAllaySpawnOnLightningHitItem(ItemEntity itemEntity, DamageSource damageSource) {
        // unreachable code but whatever man
        if (!damageSource.is(DamageTypes.LIGHTNING_BOLT)) {
            return;
        }
        while (itemEntity.getItem().getCount() > 0) {
            spawnAllayAtEntity(itemEntity);
            itemEntity.getItem().shrink(1);
        }
    }

    // all of the below from tide
    // https://github.com/Lightning-64/Tide-2/blob/f9fc2d04ae4d544ad134025cebd83c7438f67098/src/main/java/com/li64/tide/registries/entities/fish/AbstractTideFish.java#L46
    // whyat the fuck it returns an integer?
    private static final EntityDataAccessor<Integer> FROM_BUCKET = SynchedEntityData.defineId(AbstractTideFish.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_SHINY = SynchedEntityData.defineId(AbstractTideFish.class, EntityDataSerializers.BOOLEAN);

    @Override
    public double tide$getLength() {
        return this.length;
    }

    @Override
    public void tide$setLength(double length) {
        this.length = length;
    }


    @Override
    public boolean fromBucket() {
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
        return fuckThisThingMan(this.entityData.get(FROM_BUCKET));
    }
    public boolean fuckThisThingMan(int what) {
        return what > 0;
    }
    public boolean fuckThisThingMan(boolean what) {
        return what;
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        if (fromBucket) {
            this.entityData.set(FROM_BUCKET, 1); // this.entityData.set(FROM_BUCKET, fromBucket) // for some reason this changes the size. I don't like that.
        } else {
            this.entityData.set(FROM_BUCKET, 0); // this.entityData.set(FROM_BUCKET, fromBucket) // for some reason this changes the size. I don't like that.
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.fromBucket()); // I just dont get it
        compound.putDouble(tide$LENGTH_KEY, this.length);
        //compound.putBoolean(tide$SHINY_KEY, this.isShiny);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setFromBucket(tag.contains("FromBucket") && tag.getBoolean("FromBucket"));
        this.length = tag.contains(tide$LENGTH_KEY) ? tag.getDouble(tide$LENGTH_KEY) : this.length;
        //this.tide$setIsShiny(tag.contains(tide$SHINY_KEY) && tag.getBoolean(tide$SHINY_KEY));
    }

    @Override @SuppressWarnings("deprecation")
    public void saveToBucketTag(@NotNull ItemStack stack) {
        Bucketable.saveDefaultDataToBucketTag(this, stack);
        //? if >=1.21 {
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag -> {
            tag.putDouble(tide$LENGTH_KEY, this.tide$getLength());
            //tag.putBoolean(tide$SHINY_KEY, this.tide$isShiny());
        });
        //?} else {
        /*CompoundTag tag = stack.getOrCreateTag();
        tag.putDouble(tide$LENGTH_KEY, this.tide$getLength());
        *///?}
    }

    @Override @SuppressWarnings("deprecation")
    public void loadFromBucketTag(@NotNull CompoundTag tag) {
        Bucketable.loadDefaultDataFromBucketTag(this, tag);
        this.length = tag.contains(tide$LENGTH_KEY) ? tag.getDouble(tide$LENGTH_KEY) : this.tide$getLength();
        //this.tide$setIsShiny(tag.contains(tide$SHINY_KEY) && tag.getBoolean(tide$SHINY_KEY));
    }

}