package hauveli.fishcasting.registry

import at.petrak.hexcasting.xplat.IXplatAbstractions
import at.petrak.hexcasting.xplat.IXplatRegister
import hauveli.fishcasting.Fishcasting.MODID
import hauveli.fishcasting.Fishcasting.id
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import java.util.function.BiConsumer


object FishcastingAttributes {
    fun registerAttributes(r: BiConsumer<Attribute, ResourceLocation>) {
        for (e in ATTRIBUTES.entries) {
            r.accept(e.value, e.key)
        }
    }

    val ATTRIBUTES: MutableMap<ResourceLocation, Attribute> = LinkedHashMap()
    private fun make(name: String, defaultValue: Double): Attribute {
        val id = id(name)
        val attribute = RangedAttribute("$MODID.attributes.$name",
            defaultValue, 0.0, Double.MAX_VALUE
        ).setSyncable(true)
        val old: Any? = ATTRIBUTES.put(id, attribute)
        require(old == null) { "Typo? Duplicate id $name" }
        return attribute
    }

    const val DEFAULT_BOBBER_RADIUS: Double = 4.0
    val BOBBER_RADIUS = make("bobber_radius", DEFAULT_BOBBER_RADIUS)
    lateinit var BOBBER_RADIUS_HOLDER: Holder<Attribute>
    val BOBBER_RADIUS_KEY = ResourceKey.create(
        Registries.ATTRIBUTE,
        id("bobber_radius")
    )

    // I don't like how I'm doing this.
    fun registerHolder(server: ServerLevel) {
        val lookup = server.registryAccess().lookupOrThrow(Registries.ATTRIBUTE)
        BOBBER_RADIUS_HOLDER = lookup.getOrThrow(BOBBER_RADIUS_KEY)
    }
}