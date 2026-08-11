package hauveli.fishcasting.registry

import at.petrak.hexcasting.xplat.IXplatAbstractions
import at.petrak.hexcasting.xplat.IXplatRegister
import hauveli.fishcasting.Fishcasting.MODID
import hauveli.fishcasting.Fishcasting.id
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import java.util.function.BiConsumer



object FishcastingAttributes : FishcastingRegistrar<Attribute>(
    BuiltInRegistries.ATTRIBUTE.key() as ResourceKey<Registry<Attribute>>,
    { BuiltInRegistries.ATTRIBUTE }
)  {
    val ATTRIBUTES: MutableMap<ResourceLocation, Attribute> = LinkedHashMap()

    private fun make(name: String, defaultValue: Double): FishcastingRegistrar<Attribute>.Entry<Attribute> {
        val id = id(name)
        val attribute = RangedAttribute("$MODID.attributes.$name",
            defaultValue, 0.0, Double.MAX_VALUE
        ).setSyncable(true)
        val old: Any? = ATTRIBUTES.put(id, attribute)
        require(old == null) { "Typo? Duplicate id $name" }
        return make(name) {
            return@make attribute
        }
    }

    @JvmField
    val BOBBER_AMBIT_RADIUS = make("bobber_ambit_radius", defaultValue = 4.0)

    private fun <T : Attribute> make(name: String, builder: () -> T): FishcastingRegistrar<Attribute>.Entry<T> =
        register(id(name), builder)
}