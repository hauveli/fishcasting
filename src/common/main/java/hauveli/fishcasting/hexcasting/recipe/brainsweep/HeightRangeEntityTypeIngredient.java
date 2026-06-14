package hauveli.fishcasting.hexcasting.recipe.brainsweep;

import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredientType;
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.EntityTypeIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class HeightRangeEntityTypeIngredient extends EntityTypeIngredient {
    public final float minY;
    public final float maxY;

    //@Override
    public Entity exampleEntity(Level level) {
        return this.entityType.create(level);
    }

    public HeightRangeEntityTypeIngredient(EntityType<?> entityType, float minY, float maxY) {
        super(entityType);
        this.minY = minY;
        this.maxY = maxY;
    }

    public float getMinY() {
        return minY;
    }
    public float getMaxY() {
        return maxY;
    }

    private boolean inRange(Entity entity) {
        double entityY = entity.position().y;
        return entityY >= this.getMinY()
                && entityY <= this.getMaxY();
    }

    @Override
    public boolean test(Entity entity, ServerLevel level) {
        // entity types are singletons
        return entity.getType() == this.entityType
                && this.inRange(entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeightRangeEntityTypeIngredient that = (HeightRangeEntityTypeIngredient) o;
        return Objects.equals(entityType, that.entityType);
    }

    public static class Type implements BrainsweepeeIngredientType<HeightRangeEntityTypeIngredient> {
        public static final MapCodec<HeightRangeEntityTypeIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType").forGetter(HeightRangeEntityTypeIngredient::getEntityType),
                Codec.FLOAT.fieldOf("minY").forGetter(HeightRangeEntityTypeIngredient::getMinY),
                Codec.FLOAT.fieldOf("maxY").forGetter(HeightRangeEntityTypeIngredient::getMaxY)
        ).apply(instance, HeightRangeEntityTypeIngredient::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, HeightRangeEntityTypeIngredient> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ENTITY_TYPE), HeightRangeEntityTypeIngredient::getEntityType,
                ByteBufCodecs.FLOAT, HeightRangeEntityTypeIngredient::getMinY,
                ByteBufCodecs.FLOAT, HeightRangeEntityTypeIngredient::getMaxY,
                HeightRangeEntityTypeIngredient::new
        );

        @Override
        public MapCodec<HeightRangeEntityTypeIngredient> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HeightRangeEntityTypeIngredient> streamCodec() {
            return STREAM_CODEC;
        }
    }
}