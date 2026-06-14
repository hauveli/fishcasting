package hauveli.fishcasting.hexcasting.recipe.brainsweep;

import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredientType;
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.EntityTypeIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class BorderTypeEntityTypeIngredient extends EntityTypeIngredient {
    public final BorderType upDownBorder;
    public final BorderType northSouthBorder;
    public final BorderType westEastBorder;

    /*
    @Override
    public Entity exampleEntity(Level level) {
        return this.entityType.create(level);
    }
     */

    // public final EntityType<?> entityType ;

    public BorderTypeEntityTypeIngredient(EntityType<?> entityType,
                                          BorderType upDownBorder,
                                          BorderType northSouthBorder,
                                          BorderType westEastBorder) {
        super(entityType);
        this.upDownBorder = upDownBorder;
        this.northSouthBorder = northSouthBorder;
        this.westEastBorder = westEastBorder;
    }

    public BorderType getUpDownBorder() {
        return this.upDownBorder;
    }
    public BorderType getNorthSouthBorder() {
        return this.northSouthBorder;
    }
    public BorderType getWestEastBorder() {
        return this.westEastBorder;
    }

    enum BorderType implements StringRepresentable {
        POSITIVE("positive"),
        NEGATIVE("negative"),
        ANY("any");

        private final String name;

        BorderType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static final Codec<BorderType> CODEC = StringRepresentable.fromEnum(BorderType::values);
        public static final StreamCodec<ByteBuf, BorderType> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(BorderType::valueOf, BorderType::name); // should I use UTF8? I didn't see other String options that made sense...
    }

    private boolean positionAtBorder(BorderType borderType, double coordinateLimit, double coordinateOnAxis) {
        switch (borderType) {
            case POSITIVE -> {
                return coordinateLimit <= coordinateOnAxis;
            }
            case NEGATIVE -> {
                return coordinateLimit >= coordinateOnAxis;
            }
            case ANY -> {
                return true;
            }
        }
        return false; // unreachable, I think, but the compiler goes waah waah waah
    }

    private boolean notAtBorder(BorderType borderType, double minimum, double maximum, double position) {
        if (borderType == BorderType.ANY) {
            return false;
        }
        double limit = minimum;
        if (borderType != BorderType.NEGATIVE) {
            limit = maximum;
        }
        return !positionAtBorder(borderType, limit, position);
    }

    private boolean entityAtBorder(Entity entity) {
        Vec3 pos = entity.position();
        if (notAtBorder(getUpDownBorder(),
                entity.level().getMinBuildHeight(),
                entity.level().getMaxBuildHeight(),
                pos.y)) {
            return false;
        }
        WorldBorder worldBorder = entity.level().getWorldBorder();
        if (notAtBorder(getNorthSouthBorder(),
                worldBorder.getMinZ(),
                worldBorder.getMaxZ(),
                pos.z)) {
            return false;
        }
        if (notAtBorder(getWestEastBorder(),
                worldBorder.getMinX(),
                worldBorder.getMaxX(),
                pos.x)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean test(Entity entity, ServerLevel level) {
        // entity types are singletons
        return entity.getType() == this.entityType
                && this.entityAtBorder(entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorderTypeEntityTypeIngredient that = (BorderTypeEntityTypeIngredient) o;
        return Objects.equals(entityType, that.entityType);
    }

    public static class Type implements BrainsweepeeIngredientType<BorderTypeEntityTypeIngredient> {
        public static final MapCodec<BorderTypeEntityTypeIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType").forGetter(BorderTypeEntityTypeIngredient::getEntityType),
                BorderType.CODEC.fieldOf("upDownBorder").forGetter(BorderTypeEntityTypeIngredient::getUpDownBorder),
                BorderType.CODEC.fieldOf("northSouthBorder").forGetter(BorderTypeEntityTypeIngredient::getNorthSouthBorder),
                BorderType.CODEC.fieldOf("westEastBorder").forGetter(BorderTypeEntityTypeIngredient::getWestEastBorder)
        ).apply(instance, BorderTypeEntityTypeIngredient::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, BorderTypeEntityTypeIngredient> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ENTITY_TYPE), BorderTypeEntityTypeIngredient::getEntityType,
                BorderType.STREAM_CODEC, BorderTypeEntityTypeIngredient::getUpDownBorder,
                BorderType.STREAM_CODEC, BorderTypeEntityTypeIngredient::getNorthSouthBorder,
                BorderType.STREAM_CODEC, BorderTypeEntityTypeIngredient::getWestEastBorder,
                BorderTypeEntityTypeIngredient::new
        );

        @Override
        public MapCodec<BorderTypeEntityTypeIngredient> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BorderTypeEntityTypeIngredient> streamCodec() {
            return STREAM_CODEC;
        }
    }
}