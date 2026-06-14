package hauveli.fishcasting.hexcasting.recipe.brainsweep;

import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredientType;
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.EntityTypeIngredient;
import com.li64.tide.Tide;
import com.li64.tide.TideConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;

public class FishingVoidEntityTypeIngredient extends EntityTypeIngredient {
    //@Override
    public Entity exampleEntity(Level level) {
        return this.entityType.create(level);
    }

    public FishingVoidEntityTypeIngredient(EntityType<?> entityType) {
        super(entityType);
    }

    public boolean isFishableDimension(Entity entity) {
        Level level = entity.level();

        return Tide.CONFIG.general.fishableVoidHeights.stream().anyMatch(entry ->
                Objects.equals(entry.dimension, level.dimension().location().toString()));
    }

    private int getHeightFromTideConfig(Entity entity) {
        // check dimension too
        Optional<TideConfig.General.VoidHeightEntry> entry = Tide.CONFIG.general.fishableVoidHeights.stream()
                .filter(e -> e.dimension.equals(entity.level().dimension().location().toString()))
                .findFirst();

        if (entry.isPresent()) {

            int height = entry.get().height;
            TideConfig.General.VoidHeightEntry.Type type = entry.get().type;
            // to consider: fishing above the world height? I think that would also be cool...
            switch (type) {
                case TideConfig.General.VoidHeightEntry.Type.ABSOLUTE -> {
                    return height;
                }
                case TideConfig.General.VoidHeightEntry.Type.RELATIVE_TO_BOTTOM -> {
                    return entity.level().getMinBuildHeight() + height;
                }
                case TideConfig.General.VoidHeightEntry.Type.RELATIVE_TO_TOP -> {
                    return entity.level().getMaxBuildHeight() + height;
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    private boolean inVoidFluid(Entity entity) {
        double entityY = entity.position().y;
        return entityY <= getHeightFromTideConfig(entity);
    }

    @Override
    public boolean test(Entity entity, ServerLevel level) {
        // entity types are singletons
        return entity.getType() == this.entityType
                && this.inVoidFluid(entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FishingVoidEntityTypeIngredient that = (FishingVoidEntityTypeIngredient) o;
        return Objects.equals(entityType, that.entityType);
    }

    public static class Type implements BrainsweepeeIngredientType<FishingVoidEntityTypeIngredient> {
        public static final MapCodec<FishingVoidEntityTypeIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType").forGetter(FishingVoidEntityTypeIngredient::getEntityType)
        ).apply(instance, FishingVoidEntityTypeIngredient::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, FishingVoidEntityTypeIngredient> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ENTITY_TYPE), FishingVoidEntityTypeIngredient::getEntityType,
                FishingVoidEntityTypeIngredient::new
        );

        @Override
        public MapCodec<FishingVoidEntityTypeIngredient> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FishingVoidEntityTypeIngredient> streamCodec() {
            return STREAM_CODEC;
        }
    }
}