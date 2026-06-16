package hauveli.fishcasting.registry

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.xplat.IXplatAbstractions
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.casting.iota.FishIota
import net.minecraft.resources.ResourceLocation
import java.rmi.registry.Registry
import java.util.function.BiConsumer

// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/common/lib/hex/HexIotaTypes.java
object FishcastingIotaTypes {
    val REGISTRY = IXplatAbstractions.INSTANCE.iotaTypeRegistry
    const val MAX_SERIALIZATION_DEPTH: Int = 256
    const val MAX_SERIALIZATION_TOTAL: Int = 1024


    private val TYPES: MutableMap<ResourceLocation, IotaType<*>> = LinkedHashMap()

    val FISH: IotaType<FishIota> = type<FishIota, IotaType<FishIota>>("fish", FishIota.TYPE)

    fun registerTypes(r: BiConsumer<IotaType<*>, ResourceLocation>) {
        for (e in TYPES.entries) {
            r.accept(e.value, e.key)
        }
    }

    private fun <U : Iota, T : IotaType<U>> type(name: String, type: T): T {
        val old = TYPES.put(id(name), type)
        require(old == null) { "Typo? Duplicate id $name" }
        return type
    }
}