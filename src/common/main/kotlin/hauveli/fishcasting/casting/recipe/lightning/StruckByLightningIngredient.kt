package hauveli.fishcasting.casting.recipe.lightning

import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredient
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredientType
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.EntityTypeIngredient
import com.li64.tide.Tide
import com.li64.tide.config.TideConfig
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import java.util.function.Function


class StruckByLightningIngredient(
    val entityIn: EntityType<*>?,
    val entityOut: EntityType<*>?
) : BrainsweepeeIngredient() {
    override fun getType(): BrainsweepeeIngredientType<*>? {
        return entityIn as BrainsweepeeIngredientType<*>?
    }

    override fun test(entity: Entity, level: ServerLevel?): Boolean {
        return entity.type === this.type
    }

    override fun getName(): Component? {
        return entityIn?.description
    }

    override fun getTooltip(p0: Boolean): List<Component?>? {
        return listOf(entityIn?.description)
    }

    fun getTooltip(idk: Boolean, inputTooltip: Boolean): List<Component?>? {
        return if (inputTooltip) {
            listOf(entityIn?.description)
        } else {
            listOf(entityOut?.description)
        }
    }

    override fun getSomeKindOfReasonableIDForEmi(): String? {
        return entityIn?.description?.string
    }

    override fun exampleEntities(level: Level?): List<Entity?> {
        return listOf(entityIn?.create(level), entityOut?.create(level))
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as StruckByLightningIngredient
        return this.entityOut == that.entityOut
    }

    class Type : StruckByLightningIngredientType<StruckByLightningIngredient?> {
        override fun codec(): MapCodec<StruckByLightningIngredient?>? {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf?, StruckByLightningIngredient?> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<StruckByLightningIngredient?>? =
                RecordCodecBuilder.mapCodec { instance ->
                    instance.group(
                        BuiltInRegistries.ENTITY_TYPE.byNameCodec()
                            .fieldOf("entityIn")
                            .forGetter { it?.entityIn },

                        BuiltInRegistries.ENTITY_TYPE.byNameCodec()
                            .fieldOf("entityOut")
                            .forGetter { it?.entityOut }
                    ).apply(instance, ::StruckByLightningIngredient)
                }
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf?, StruckByLightningIngredient?> =
                StreamCodec.composite(
                    ByteBufCodecs.registry(Registries.ENTITY_TYPE),
                    { it?.entityIn },

                    ByteBufCodecs.registry(Registries.ENTITY_TYPE),
                    { it?.entityOut },

                    ::StruckByLightningIngredient
                )
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}