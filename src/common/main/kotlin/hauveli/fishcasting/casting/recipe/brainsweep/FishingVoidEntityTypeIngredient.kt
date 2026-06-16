package hauveli.fishcasting.casting.recipe.brainsweep

import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredientType
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.EntityTypeIngredient
import com.li64.tide.Tide
import com.li64.tide.TideConfig
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


class FishingVoidEntityTypeIngredient(
    entityType: EntityType<*>?) : EntityTypeIngredient(entityType) {
    fun isFishableDimension(entity: Entity): Boolean {
        val level = entity.level()

        return Tide.CONFIG.general.fishableVoidHeights.stream().anyMatch { entry: TideConfig.General.VoidHeightEntry? ->
            entry!!.dimension == level.dimension().location().toString()
        }
    }

    private fun getHeightFromTideConfig(entity: Entity): Int {
        // check dimension too, void height may vary
        val entry = Tide.CONFIG.general.fishableVoidHeights.stream()
            .filter { e: TideConfig.General.VoidHeightEntry? ->
                e!!.dimension == entity.level().dimension().location().toString()
            }
            .findFirst()

        if (entry.isPresent) {
            val height = entry.get().height
            val type = entry.get().type
            // to consider: fishing above the world height? I think that would also be cool...
            return when (type) {
                TideConfig.General.VoidHeightEntry.Type.ABSOLUTE -> {
                    height
                }

                TideConfig.General.VoidHeightEntry.Type.RELATIVE_TO_BOTTOM -> {
                    entity.level().minBuildHeight + height
                }

                TideConfig.General.VoidHeightEntry.Type.RELATIVE_TO_TOP -> {
                    entity.level().maxBuildHeight + height
                }
            }
        }
        return Int.MIN_VALUE
    }

    private fun inVoidFluid(entity: Entity): Boolean {
        val entityY = entity.position().y
        return entityY <= getHeightFromTideConfig(entity)
    }

    override fun test(entity: Entity, level: ServerLevel?): Boolean {
        return entity.type === this.entityType
                && this.inVoidFluid(entity)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as FishingVoidEntityTypeIngredient
        return entityType == that.entityType
    }

    class Type : BrainsweepeeIngredientType<FishingVoidEntityTypeIngredient?> {
        override fun codec(): MapCodec<FishingVoidEntityTypeIngredient?>? {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf?, FishingVoidEntityTypeIngredient?> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<FishingVoidEntityTypeIngredient?>? =
                RecordCodecBuilder.mapCodec<FishingVoidEntityTypeIngredient?>(
                    Function { instance: RecordCodecBuilder.Instance<FishingVoidEntityTypeIngredient?>? ->
                        instance!!.group(
                            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType")
                                .forGetter<FishingVoidEntityTypeIngredient?>(
                                    { obj: FishingVoidEntityTypeIngredient? -> obj!!.getEntityType() })
                        ).apply<FishingVoidEntityTypeIngredient?>(
                            instance,
                            { entityType: EntityType<*>? -> FishingVoidEntityTypeIngredient(entityType) })
                    })
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf?, FishingVoidEntityTypeIngredient?> =
                StreamCodec.composite<RegistryFriendlyByteBuf?, FishingVoidEntityTypeIngredient?, EntityType<*>?>(
                    ByteBufCodecs.registry(Registries.ENTITY_TYPE),
                    { obj: FishingVoidEntityTypeIngredient? -> obj!!.getEntityType() },
                    { entityType: EntityType<*>? -> FishingVoidEntityTypeIngredient(entityType) }
                )
        }
    }
}