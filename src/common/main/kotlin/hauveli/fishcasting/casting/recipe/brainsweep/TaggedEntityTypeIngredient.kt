package hauveli.fishcasting.casting.recipe.brainsweep

import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredientType
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.EntityTypeIngredient
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import java.util.function.BiFunction
import java.util.function.Function


class TaggedEntityTypeIngredient(
    entityType: EntityType<*>?, val tag: String?) : EntityTypeIngredient(entityType) {
    override fun test(entity: Entity, level: ServerLevel?): Boolean {
        return entity.type === this.entityType
                && entity.tags.contains(this.tag)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as TaggedEntityTypeIngredient
        return entityType == that.entityType
    }

    class Type : BrainsweepeeIngredientType<TaggedEntityTypeIngredient?> {
        override fun codec(): MapCodec<TaggedEntityTypeIngredient?>? {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf?, TaggedEntityTypeIngredient?> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<TaggedEntityTypeIngredient?>? =
                RecordCodecBuilder.mapCodec<TaggedEntityTypeIngredient?>(
                    Function { instance: RecordCodecBuilder.Instance<TaggedEntityTypeIngredient?>? ->
                        instance!!.group(
                            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType")
                                .forGetter<TaggedEntityTypeIngredient?>(
                                    { obj: TaggedEntityTypeIngredient? -> obj!!.getEntityType() }),
                            Codec.STRING.fieldOf("tag")
                                .forGetter<TaggedEntityTypeIngredient?>({ obj: TaggedEntityTypeIngredient? -> obj!!.tag })
                        ).apply<TaggedEntityTypeIngredient?>(
                            instance,
                            { entityType: EntityType<*>?, tag: String? ->
                                TaggedEntityTypeIngredient(
                                    entityType,
                                    tag
                                )
                            })
                    })
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf?, TaggedEntityTypeIngredient?> =
                StreamCodec.composite<RegistryFriendlyByteBuf?, TaggedEntityTypeIngredient?, EntityType<*>?, String?>(
                    ByteBufCodecs.registry(Registries.ENTITY_TYPE),
                    { obj: TaggedEntityTypeIngredient? -> obj!!.getEntityType() },
                    ByteBufCodecs.STRING_UTF8,
                    { obj: TaggedEntityTypeIngredient? -> obj!!.tag },
                    { entityType: EntityType<*>?, tag: String? ->
                        TaggedEntityTypeIngredient(
                            entityType,
                            tag
                        )
                    }
                )
        }
    }
}