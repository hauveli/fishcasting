package hauveli.fishcasting.casting.arithmetic

import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic
import at.petrak.hexcasting.api.casting.arithmetic.engine.InvalidOperatorException
import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import com.li64.tide.data.FishLengthHolder
import com.li64.tide.data.item.TideDataComponents
import hauveli.fishcasting.registry.FishcastingIotaTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import java.util.function.BiFunction
import kotlin.math.max


class FishcastingFishArithmetic : Arithmetic {
    override fun arithName(): String {
        return "fishcasting_arithmetic_fish"
    }

    override fun opTypes(): Iterable<HexPattern> {
        return OPS
    }

    // Any future additions for detecting length of an ENTITY (fish or otherwise) would go here
    // This includes item entities as clearly shown below
    // env is here so I can debug
    fun getFishLength(entity: Entity, env: CastingEnvironment): Double {
        if (entity is FishLengthHolder) {
            return entity.`tide$getLength`() // holyyy thank you tide dev
        }
        if (entity is ItemEntity) {
            val stack = entity.item
            // Ugh this was annoying to figure out
            val fishLength = stack.get(TideDataComponents.FISH_LENGTH)
            if (fishLength != null) {
                return fishLength
            }
            // this does not work...
            // return TideItemData.FISH_LENGTH.getOrDefault(stack, 0.0d); // FishLengthHolder.tide$LENGTH_KEY
        }
        val box = entity.boundingBox

        val largestDimension = max(
            box.xsize,
            max(box.ysize, box.zsize)
        )
        return largestDimension
    }

    override fun getOperator(pattern: HexPattern): Operator {
        if (pattern == Arithmetic.ABS) {
            return make1Double(
                { entity: Entity, env: CastingEnvironment -> getFishLength(entity, env) }
            )
        }
        throw InvalidOperatorException("$pattern is not a valid operator in Arithmetic $this.")
    }

    companion object {
        // so, I thought it would be funny.
        val OPS: List<HexPattern> = listOf(
            Arithmetic.ABS
        )

        val ACCEPTS: IotaMultiPredicate = IotaMultiPredicate.any(
            IotaPredicate.ofType(HexIotaTypes.ENTITY),
            IotaPredicate.ofType(FishcastingIotaTypes.FISH)
        )

        fun make1Double(
            op: BiFunction<Entity, CastingEnvironment, Double>
        ): OperatorBasic {
            return object : OperatorBasic(1, ACCEPTS) {
                override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
                    val entity: Entity = downcast<EntityIota>(
                        iotas.iterator().next(),
                        HexIotaTypes.ENTITY
                    ).getEntity(env.castingEntity!!.level() as ServerLevel)

                    val result: Double = op.apply(entity, env)

                    return listOf<Iota>(DoubleIota(result))
                }
            }
        }
    }
}