package hauveli.fishcasting.casting.recipe.brainsweep;

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

import java.util.Objects;

public class TaggedEntityTypeIngredient extends EntityTypeIngredient {
    public final String tag;

    /*
    @Override
    public Entity exampleEntity(Level level) {
        return this.entityType.create(level);
    }
     */

    public TaggedEntityTypeIngredient(EntityType<?> entityType, String tag) {
        super(entityType);
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean test(Entity entity, ServerLevel level) {
        // entity types are singletons
        return entity.getType() == this.entityType
                && entity.getTags().contains(this.tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaggedEntityTypeIngredient that = (TaggedEntityTypeIngredient) o;
        return Objects.equals(entityType, that.entityType);
    }

    public static class Type implements BrainsweepeeIngredientType<TaggedEntityTypeIngredient> {
        public static final MapCodec<TaggedEntityTypeIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType").forGetter(TaggedEntityTypeIngredient::getEntityType),
                Codec.STRING.fieldOf("tag").forGetter(TaggedEntityTypeIngredient::getTag)
        ).apply(instance, TaggedEntityTypeIngredient::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, TaggedEntityTypeIngredient> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ENTITY_TYPE), TaggedEntityTypeIngredient::getEntityType,
                ByteBufCodecs.STRING_UTF8, TaggedEntityTypeIngredient::getTag,
                TaggedEntityTypeIngredient::new
        );

        @Override
        public MapCodec<TaggedEntityTypeIngredient> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TaggedEntityTypeIngredient> streamCodec() {
            return STREAM_CODEC;
        }
    }
}