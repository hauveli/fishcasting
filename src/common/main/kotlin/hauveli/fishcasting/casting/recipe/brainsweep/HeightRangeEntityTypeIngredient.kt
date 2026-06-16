package hauveli.fishcasting.casting.recipe.brainsweep

import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredientType
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.EntityTypeIngredient
import com.mojang.datafixers.util.Function3
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
import net.minecraft.world.level.Level
import java.util.function.Function


class HeightRangeEntityTypeIngredient(entityType: EntityType<*>?, val minY: Float, val maxY: Float) :
    EntityTypeIngredient(entityType) {

    private fun inRange(entity: Entity): Boolean {
        val entityY = entity.position().y
        return entityY >= this.minY
                && entityY <= this.maxY
    }

    override fun test(entity: Entity, level: ServerLevel?): Boolean {
        return entity.type === this.entityType
                && this.inRange(entity)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as HeightRangeEntityTypeIngredient
        return entityType == that.entityType
    }

    class Type : BrainsweepeeIngredientType<HeightRangeEntityTypeIngredient?> {
        override fun codec(): MapCodec<HeightRangeEntityTypeIngredient?>? {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf?, HeightRangeEntityTypeIngredient?> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<HeightRangeEntityTypeIngredient?>? =
                RecordCodecBuilder.mapCodec<HeightRangeEntityTypeIngredient?>(
                    Function { instance: RecordCodecBuilder.Instance<HeightRangeEntityTypeIngredient?>? ->
                        instance!!.group(
                            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType")
                                .forGetter<HeightRangeEntityTypeIngredient?>(
                                    { obj: HeightRangeEntityTypeIngredient? -> obj!!.getEntityType() }),
                            Codec.FLOAT.fieldOf("minY")
                                .forGetter<HeightRangeEntityTypeIngredient?>({ obj: HeightRangeEntityTypeIngredient? -> obj!!.minY }),
                            Codec.FLOAT.fieldOf("maxY")
                                .forGetter<HeightRangeEntityTypeIngredient?>({ obj: HeightRangeEntityTypeIngredient? -> obj!!.maxY })
                        ).apply<HeightRangeEntityTypeIngredient?>(
                            instance,
                            { entityType: EntityType<*>?, minY: Float?, maxY: Float? ->
                                HeightRangeEntityTypeIngredient(
                                    entityType,
                                    minY!!,
                                    maxY!!
                                )
                            })
                    })
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf?, HeightRangeEntityTypeIngredient?> =
                StreamCodec.composite<RegistryFriendlyByteBuf?, HeightRangeEntityTypeIngredient?, EntityType<*>?, Float?, Float?>(
                    ByteBufCodecs.registry(Registries.ENTITY_TYPE),
                    { obj: HeightRangeEntityTypeIngredient? -> obj!!.getEntityType() },
                    ByteBufCodecs.FLOAT,
                    { obj: HeightRangeEntityTypeIngredient? -> obj!!.minY },
                    ByteBufCodecs.FLOAT,
                    { obj: HeightRangeEntityTypeIngredient? -> obj!!.maxY },
                    { entityType: EntityType<*>?, minY: Float?, maxY: Float? ->
                        HeightRangeEntityTypeIngredient(
                            entityType,
                            minY!!,
                            maxY!!
                        )
                    }
                )
        }
    }
}