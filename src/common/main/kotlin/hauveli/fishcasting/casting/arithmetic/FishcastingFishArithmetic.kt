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
import com.li64.tide.data.fishing.FishData
import com.li64.tide.data.fishing.SizeData
import com.li64.tide.data.item.TideDataComponents
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.registry.FishcastingIotaTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
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
    fun getFishLength(entity: Entity): Double {
        if (entity is FishLengthHolder) {
            // dividing by 100 because fish length is in cm, it seems?
            return entity.`tide$getLength`() / 100 // holyyy thank you tide dev
        }
        if (entity is ItemEntity) {
            val stack = entity.item
            // Ugh this was annoying to figure out
            val fishLength = stack.get(TideDataComponents.FISH_LENGTH)
            if (fishLength != null) {
                // dividing by 100 because fish length is in cm, it seems?
                return fishLength / 100
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

    fun getFishSizeData(entity: Entity): SizeData {
        Fishcasting.LOGGER.info(FishData.get(entity))
        return FishData.get(entity).get().size().get()
    }

    fun getFishSizeData(item: ItemStack): SizeData {
        return FishData.get(item).get().size().get()
    }

    fun getFishMinLength(entity: Entity): Double {
        if (entity is FishLengthHolder ) {
            // dividing by 100 because fish length is in cm, it seems?
            val minimumLength = getFishSizeData(entity).recordLowCm().get()
            return minimumLength / 100.0 // holyyy thank you tide dev
        }
        if (entity is ItemEntity) {
            val stack = entity.item
            // Ugh this was annoying to figure out
            val fishLength = stack.get(TideDataComponents.FISH_LENGTH)
            if (fishLength != null) {
                // dividing by 100 because fish length is in cm, it seems?
                val minimumLength = getFishSizeData(entity.item).recordLowCm().get()
                return minimumLength / 100.0 // holyyy thank you tide dev
            }
        }
        return 0.0
    }

    fun getFishMaxLength(entity: Entity): Double {
        if (entity is FishLengthHolder) {
            // dividing by 100 because fish length is in cm, it seems?
            val minimumLength = getFishSizeData(entity).recordHighCm()
            return minimumLength / 100.0 // holyyy thank you tide dev
        }
        if (entity is ItemEntity) {
            val stack = entity.item
            // Ugh this was annoying to figure out
            val fishLength = stack.get(TideDataComponents.FISH_LENGTH)
            if (fishLength != null) {
                // dividing by 100 because fish length is in cm, it seems?
                val minimumLength = getFishSizeData(entity).recordHighCm()
                return minimumLength / 100.0 // holyyy thank you tide dev
            }
        }
        return 0.0
    }

    override fun getOperator(pattern: HexPattern): Operator {
        when (pattern) {
            Arithmetic.ABS -> {
                return make1Double(
                    { entity: Entity, env: CastingEnvironment -> getFishLength(entity) }
                )
            }

            Arithmetic.FLOOR -> {
                return make1Double(
                    { entity: Entity, env: CastingEnvironment -> getFishMinLength(entity) }
                )
            }

            Arithmetic.CEIL -> {
                return make1Double(
                    { entity: Entity, env: CastingEnvironment -> getFishMaxLength(entity) }
                )
            }

            else -> {
                throw InvalidOperatorException("$pattern is not a valid operator in Arithmetic $this.")
            }
        }
    }

    companion object {
        // so, I thought it would be funny.
        val OPS: List<HexPattern> = listOf(
            Arithmetic.ABS,
            Arithmetic.FLOOR,
            Arithmetic.CEIL
        )

        /*
        val ACCEPTS: IotaMultiPredicate = IotaMultiPredicate.any(
            IotaPredicate.ofType(HexIotaTypes.ENTITY.get()),
            IotaPredicate.ofType(FishcastingIotaTypes.FISH)
        )
         */

        fun make1Double(
            op: BiFunction<Entity, CastingEnvironment, Double>
        ): OperatorBasic {

            val ACCEPTS: IotaMultiPredicate = IotaMultiPredicate.any(
                IotaPredicate.ofType(HexIotaTypes.ENTITY.get()),
                IotaPredicate.ofType(FishcastingIotaTypes.FISH.value)
            )

            return object : OperatorBasic(1, ACCEPTS) {
                override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
                    val entity: Entity = downcast<EntityIota>(
                        iotas.iterator().next(),
                        HexIotaTypes.ENTITY.get()
                    ).getEntity(env.world)

                    val result: Double = op.apply(entity, env)

                    return listOf<Iota>(DoubleIota(result))
                }
            }
        }
    }
}