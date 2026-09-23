package hauveli.fishcasting.casting.actions.patterns.environment

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getDouble
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.client.gui.screens.journal.components.DepthComponent
import com.li64.tide.data.fishing.conditions.types.WeatherType
import hauveli.fishcasting.casting.iota.DepthIota
import hauveli.fishcasting.casting.iota.DimensionIota
import hauveli.fishcasting.casting.iota.MoonPhaseIota
import hauveli.fishcasting.casting.iota.WeatherIota
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel

object OpGetDepth : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = MediaConstants.DUST_UNIT / 100 // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val maybePos = args.getVec3(0, argc)

        val seaLevel = DepthComponent.MAX_Y
        val positionY = maybePos.y.coerceIn(DepthComponent.MIN_Y.toDouble(), DepthComponent.MAX_Y.toDouble())

        // this is perhaps mean, but I'm leaving this without a safety check because if there is an error, I want to know.
        val relativeDepth = seaLevel - positionY

        return listOf(DepthIota(relativeDepth.toInt()))
    }
}