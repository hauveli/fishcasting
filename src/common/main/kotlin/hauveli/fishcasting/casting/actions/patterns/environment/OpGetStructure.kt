package hauveli.fishcasting.casting.actions.patterns.environment

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.li64.tide.data.fishing.conditions.types.WeatherType
import com.li64.tide.util.TideUtils
import hauveli.fishcasting.casting.iota.MoonPhaseIota
import hauveli.fishcasting.casting.iota.StructureIota
import hauveli.fishcasting.casting.iota.WeatherIota
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureStart

object OpGetStructure : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = MediaConstants.DUST_UNIT // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val blockPos = args.getBlockPos(0, argc)
        env.assertPosInRange(blockPos)

        val structuresAt = getStructuresAt(env.world, blockPos)

        return listOf(ListIota(getStructuresAt(env.world, blockPos).map(::StructureIota)))
    }

    // oh my fucking god there has to be a better way to do this, this mega sucks I feel like
    fun getStructuresAt(
        level: ServerLevel,
        pos: BlockPos
    ): List<ResourceKey<Structure>> {
        val lookup = level.registryAccess()
            .lookupOrThrow(Registries.STRUCTURE)

        return level.structureManager()
            .getAllStructuresAt(pos)
            .keys
            .mapNotNull { structure ->
                lookup.listElements()
                    .filter { it.value() === structure }
                    .findFirst()
                    .orElse(null)
                    ?.key()
            }
    }
}