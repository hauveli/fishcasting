package hauveli.fishcasting.casting.recipe.brainsweep

import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredientType
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.EntityTypeIngredient
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import java.util.function.Function


class BorderTypeEntityTypeIngredient(
    entityType: EntityType<*>?,
    val upDownBorder: BorderType?,
    val northSouthBorder: BorderType?,
    val westEastBorder: BorderType?
) : EntityTypeIngredient(entityType) {
    enum class BorderType(private val nameId: String) : StringRepresentable {
        POSITIVE("positive"),
        NEGATIVE("negative"),
        ANY("any");

        override fun getSerializedName(): String {
            return this.nameId
        }

        companion object {
            val CODEC: Codec<BorderType?> =
                StringRepresentable.fromEnum({ entries.toTypedArray() })
            val STREAM_CODEC: StreamCodec<ByteBuf?, BorderType?> = ByteBufCodecs.STRING_UTF8.map<BorderType?>(
                Function { name: String? -> BorderType.valueOf(name!!) },
                Function { obj: BorderType? -> obj!!.name }) // should I use UTF8? I didn't see other String options that made sense...
        }
    }

    private fun positionAtBorder(borderType: BorderType?, coordinateLimit: Double, coordinateOnAxis: Double): Boolean {
        return when (borderType) {
            BorderType.POSITIVE -> {
                coordinateLimit <= coordinateOnAxis
            }

            BorderType.NEGATIVE -> {
                coordinateLimit >= coordinateOnAxis
            }

            BorderType.ANY -> {
                true
            }

            else -> {
                false // this is unreachable at runtime...
            }
        }
        //return false // unreachable, I think, but the compiler goes waah waah waah
    }

    private fun notAtBorder(borderType: BorderType?, minimum: Double, maximum: Double, position: Double): Boolean {
        if (borderType == BorderType.ANY) {
            return false
        }
        var limit = minimum
        if (borderType != BorderType.NEGATIVE) {
            limit = maximum
        }
        return !positionAtBorder(borderType, limit, position)
    }

    private fun entityAtBorder(entity: Entity): Boolean {
        val pos = entity.position()
        if (notAtBorder(
                this.upDownBorder,
                entity.level().minBuildHeight.toDouble(),
                entity.level().maxBuildHeight.toDouble(),
                pos.y
            )
        ) {
            return false
        }
        val worldBorder = entity.level().worldBorder
        if (notAtBorder(
                this.northSouthBorder,
                worldBorder.minZ,
                worldBorder.maxZ,
                pos.z
            )
        ) {
            return false
        }
        if (notAtBorder(
                this.westEastBorder,
                worldBorder.minX,
                worldBorder.maxX,
                pos.x
            )
        ) {
            return false
        }
        return true
    }

    override fun test(entity: Entity, level: ServerLevel?): Boolean {
        // entity types are singletons
        return entity.type === this.entityType
                && this.entityAtBorder(entity)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as BorderTypeEntityTypeIngredient
        return entityType == that.entityType
    }

    class Type : BrainsweepeeIngredientType<BorderTypeEntityTypeIngredient?> {
        override fun codec(): MapCodec<BorderTypeEntityTypeIngredient?>? {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf?, BorderTypeEntityTypeIngredient?> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<BorderTypeEntityTypeIngredient?>? =
                RecordCodecBuilder.mapCodec<BorderTypeEntityTypeIngredient?>(
                    Function { instance: RecordCodecBuilder.Instance<BorderTypeEntityTypeIngredient?>? ->
                        instance!!.group<EntityType<*>?, BorderType?, BorderType?, BorderType?>(
                            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType")
                                .forGetter<BorderTypeEntityTypeIngredient?>(
                                    { obj: BorderTypeEntityTypeIngredient? -> obj!!.getEntityType() }),
                            BorderType.CODEC.fieldOf("upDownBorder")
                                .forGetter<BorderTypeEntityTypeIngredient?>({ obj: BorderTypeEntityTypeIngredient? -> obj!!.upDownBorder }),
                            BorderType.CODEC.fieldOf("northSouthBorder")
                                .forGetter<BorderTypeEntityTypeIngredient?>({ obj: BorderTypeEntityTypeIngredient? -> obj!!.northSouthBorder }),
                            BorderType.CODEC.fieldOf("westEastBorder")
                                .forGetter<BorderTypeEntityTypeIngredient?>({ obj: BorderTypeEntityTypeIngredient? -> obj!!.westEastBorder })
                        ).apply<BorderTypeEntityTypeIngredient?>(
                            instance,
                            { entityType: EntityType<*>?, upDownBorder: BorderType?, northSouthBorder: BorderType?, westEastBorder: BorderType? ->
                                BorderTypeEntityTypeIngredient(
                                    entityType,
                                    upDownBorder,
                                    northSouthBorder,
                                    westEastBorder
                                )
                            })
                    })
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf?, BorderTypeEntityTypeIngredient?> =
                StreamCodec.composite<RegistryFriendlyByteBuf?, BorderTypeEntityTypeIngredient?, EntityType<*>?, BorderType?, BorderType?, BorderType?>(
                    ByteBufCodecs.registry(Registries.ENTITY_TYPE),
                    { obj: BorderTypeEntityTypeIngredient? -> obj!!.getEntityType() },
                    BorderType.STREAM_CODEC,
                    { obj: BorderTypeEntityTypeIngredient? -> obj!!.upDownBorder },
                    BorderType.STREAM_CODEC,
                    { obj: BorderTypeEntityTypeIngredient? -> obj!!.northSouthBorder },
                    BorderType.STREAM_CODEC,
                    { obj: BorderTypeEntityTypeIngredient? -> obj!!.westEastBorder },
                    { entityType: EntityType<*>?, upDownBorder: BorderType?, northSouthBorder: BorderType?, westEastBorder: BorderType? ->
                        BorderTypeEntityTypeIngredient(
                            entityType,
                            upDownBorder,
                            northSouthBorder,
                            westEastBorder
                        )
                    }
                )
        }
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + upDownBorder.hashCode()
        result = 31 * result + northSouthBorder.hashCode()
        result = 31 * result + westEastBorder.hashCode()
        return result
    }
}