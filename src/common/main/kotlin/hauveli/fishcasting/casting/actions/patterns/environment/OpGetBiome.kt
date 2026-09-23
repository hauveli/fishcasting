package hauveli.fishcasting.casting.actions.patterns.environment

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import hauveli.fishcasting.casting.iota.BiomeIota
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome
import java.rmi.registry.Registry


object OpGetBiome : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = MediaConstants.DUST_UNIT / 100 // should also cost something, unsure how much...

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val envWorld = env.world
        val blockPos = args.getBlockPos(0, argc)

        env.assertPosInRange(blockPos)


        val biomeRegistry = envWorld.registryAccess().registryOrThrow(Registries.BIOME)

        val biomeKey: ResourceKey<Biome> = biomeRegistry.getResourceKey(envWorld.getBiome(blockPos).value()).orElseThrow()

        return listOf(BiomeIota(biomeKey))
    }
}