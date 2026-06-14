package hauveli.fishcasting.registry

import at.petrak.hexcasting.xplat.IXplatAbstractions
import at.petrak.hexcasting.xplat.IXplatRegister
import hauveli.fishcasting.Fishcasting.MODID
import hauveli.fishcasting.Fishcasting.id
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import java.util.function.BiConsumer


object FishcastingAttributes {
    fun registerAttributes(r: BiConsumer<Attribute, ResourceLocation>) {
        for (e in ATTRIBUTES.entries) {
            e.value?.let { e.key?.let { p1 -> r.accept(it, p1) } } // why does it have to be nullable?
        }
    }

    val ATTRIBUTES: MutableMap<ResourceLocation?, Attribute?> = LinkedHashMap()
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
}