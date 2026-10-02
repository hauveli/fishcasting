package hauveli.fishcasting.casting.arithmetic

import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic
import at.petrak.hexcasting.api.casting.arithmetic.engine.InvalidOperatorException
import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.BooleanIota
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
import hauveli.fishcasting.casting.iota.EnvironmentValue
import hauveli.fishcasting.casting.iota.RealEnvironmentIota
import hauveli.fishcasting.registry.FishcastingIotaTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.apache.commons.lang3.function.TriFunction
import java.math.BigInteger
import java.util.function.BiFunction
import kotlin.jvm.javaClass
import kotlin.math.max


class FishcastingEnvironmentArithmetic : Arithmetic {
    override fun arithName(): String {
        return "${Fishcasting.MODID}_arithmetic_environment"
    }

    override fun opTypes(): Iterable<HexPattern> {
        return OPS
    }

    override fun getOperator(pattern: HexPattern): Operator {
        when (pattern) {
            Arithmetic.ADD -> {
                return make2In1OutEnv(
                    {
                            double: Double, double2: Double, env: CastingEnvironment ->
                        double + double2
                    }
                )
            }
            Arithmetic.SUB -> {
                return make2In1OutEnv(
                    {
                            double: Double, double2: Double, env: CastingEnvironment ->
                        double - double2
                    }
                )
            }
            Arithmetic.MUL -> {
                return make2In1OutEnv(
                    {
                            double: Double, double2: Double, env: CastingEnvironment ->
                        double * double2
                    }
                )
            }
            Arithmetic.DIV -> {
                return make2In1OutEnv(
                    {
                            double: Double, double2: Double, env: CastingEnvironment ->
                        double / double2
                    }
                )
            }
            Arithmetic.GREATER -> {
                return make2In1OutBoolean(
                    {
                            double: Double, double2: Double, env: CastingEnvironment ->
                        double > double2
                    }
                )
            }
            Arithmetic.GREATER_EQ -> {
                return make2In1OutBoolean(
                    {
                            double: Double, double2: Double, env: CastingEnvironment ->
                        double >= double2
                    }
                )
            }
            Arithmetic.LESS -> {
                return make2In1OutBoolean(
                    {
                            double: Double, double2: Double, env: CastingEnvironment ->
                        double < double2
                    }
                )
            }
            Arithmetic.LESS_EQ -> {
                return make2In1OutBoolean(
                    {
                            double: Double, double2: Double, env: CastingEnvironment ->
                        double < double2
                    }
                )
            }
            Arithmetic.AND -> {
                return make2In1OutEnv(
                    {
                            double: Double, double2: Double, env: CastingEnvironment ->
                        (BigInteger.valueOf(double.toLong())
                                and BigInteger.valueOf(double2.toLong())).toDouble()
                    }
                )
            }
            Arithmetic.OR -> {
                return make2In1OutEnv(
                    {
                            double: Double, double2: Double, env: CastingEnvironment ->
                        (BigInteger.valueOf(double.toLong())
                                or BigInteger.valueOf(double2.toLong())).toDouble()
                    }
                )
            }
            Arithmetic.XOR -> {
                return make2In1OutEnv(
                    {
                            double: Double, double2: Double, env: CastingEnvironment ->
                        (BigInteger.valueOf(double.toLong())
                                xor BigInteger.valueOf(double2.toLong())).toDouble()
                    }
                )
            }
            Arithmetic.NOT -> {
                return make1In1OutDouble(
                    {
                            double: Double, env: CastingEnvironment ->
                        (BigInteger.valueOf(double.toLong()).negate()).toDouble()
                    }
                )
            }


            Arithmetic.ABS -> {
                return make1In1OutDouble(
                    { double: Double, env: CastingEnvironment -> double }
                )
            }

            else -> {
                throw InvalidOperatorException("$pattern is not a valid operator in Arithmetic $this.")
            }
        }
    }

    companion object {
        val OPS: List<HexPattern> = listOf(
            Arithmetic.ADD,
            Arithmetic.SUB,
            Arithmetic.MUL,
            Arithmetic.DIV,
            Arithmetic.GREATER,
            Arithmetic.GREATER_EQ,
            Arithmetic.LESS,
            Arithmetic.LESS_EQ,
            Arithmetic.AND,
            Arithmetic.OR,
            Arithmetic.XOR,
            Arithmetic.NOT,

            Arithmetic.ABS, // convert to double
        )

        // downcast(j, DOUBLE).double.roundToLong()
        // todo: should I downcast? (for the https://github.com/FallingColors/HexMod/blob/4db93abc91a60cc4ca340dd2ebdda70aee7c867a/Common/src/main/java/at/petrak/hexcasting/common/casting/arithmetic/BitwiseSetArithmetic.kt#L22

        fun getDoubleFromIota(iota: Iota): Double {
            return when (iota) {
                is DoubleIota -> iota.double
                is RealEnvironmentIota -> iota.getDouble()
                else -> 0.0
            }
        }


        private fun getEnvIotaFromIota(
            iota: Iota,
            iotaTwo: Iota
        ): RealEnvironmentIota {
            if (iota is RealEnvironmentIota)
                return iota
            return iotaTwo as RealEnvironmentIota
        }

        fun make1In1OutDouble(
            op: BiFunction<Double, CastingEnvironment, Double>
        ): OperatorBasic {

            // huh...
            val ACCEPTS = IotaMultiPredicate.any(
                IotaPredicate.ofType(FishcastingIotaTypes.ENVIRONMENT.value),
                IotaPredicate.ofType(FishcastingIotaTypes.ENVIRONMENT.value)
            )

            return object : OperatorBasic(1, ACCEPTS) {
                override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
                    val it = iotas.iterator()
                    val iota = it.next()
                    val double = getDoubleFromIota(iota)

                    val result = op.apply(double, env)

                    return listOf<Iota>(DoubleIota(result))
                }
            }
        }

        fun make2In1OutEnv(
            op: TriFunction<Double, Double, CastingEnvironment, Double>
        ): OperatorBasic {

            val ACCEPTS: IotaMultiPredicate = IotaMultiPredicate.any(
                IotaPredicate.ofType(HexIotaTypes.DOUBLE.get()),
                IotaPredicate.ofType(FishcastingIotaTypes.ENVIRONMENT.value)
            )

            return object : OperatorBasic(2, ACCEPTS) {
                override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
                    val it = iotas.iterator()
                    val iota = it.next()
                    val iotaTwo = it.next()
                    val double = getDoubleFromIota(iota)
                    val doubleTwo = getDoubleFromIota(iotaTwo)

                    val result = op.apply(double, doubleTwo, env)
                    val theEnvironmentValue = getEnvIotaFromIota(iota, iotaTwo).value

                    val newEnvIota = theEnvironmentValue.of(result)
                    return listOf<Iota>(RealEnvironmentIota(newEnvIota))
                }
            }
        }

        fun make2In1OutBoolean(
            op: TriFunction<Double, Double, CastingEnvironment, Boolean>
        ): OperatorBasic {

            val ACCEPTS: IotaMultiPredicate = IotaMultiPredicate.any(
                IotaPredicate.ofType(HexIotaTypes.DOUBLE.get()),
                IotaPredicate.ofType(FishcastingIotaTypes.ENVIRONMENT.value)
            )

            return object : OperatorBasic(2, ACCEPTS) {
                override fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
                    val it = iotas.iterator()
                    val iota = it.next()
                    val double = getDoubleFromIota(iota)
                    val iotaTwo = it.next()
                    val doubleTwo = getDoubleFromIota(iotaTwo)

                    val result = op.apply(double, doubleTwo, env)

                    return listOf<Iota>(BooleanIota(result))
                }
            }
        }
    }
}