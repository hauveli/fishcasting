package hauveli.fishcasting.registry

import hauveli.fishcasting.Fishcasting.MODID
import hauveli.fishcasting.Fishcasting.id
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import java.util.function.BiConsumer


object FishcastingCreativeTabs : FishcastingRegistrar<CreativeModeTab>(
    BuiltInRegistries.CREATIVE_MODE_TAB.key() as ResourceKey<Registry<CreativeModeTab>>,
    { BuiltInRegistries.CREATIVE_MODE_TAB }
)   {
    val TABS: MutableMap<ResourceLocation, CreativeModeTab> = LinkedHashMap()

    // Fishex would have been a good addon name, too
    val FISHCASTING = register(MODID,
        CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon({ FishcastingItems.LOUD_FISHING_LINE.value.defaultInstance })
    )
    // hee heee heeee
    val CreativeModeTab.key: ResourceKey<CreativeModeTab>?
        get() = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(this).orElse(null)

    private fun register(name: String, tabBuilder: CreativeModeTab.Builder): FishcastingRegistrar<CreativeModeTab>.Entry<CreativeModeTab> {
        val tab = tabBuilder.title(Component.translatable("$name.creative_tab.title")).build()
        val old = TABS.put(id(name), tab)
        require(old == null) { "Typo? Duplicate id $name" }
        return make(name) {tab}
    }

    private fun <T : CreativeModeTab> make(name: String, builder: () -> T): FishcastingRegistrar<CreativeModeTab>.Entry<T> =
        register(id(name), builder)
}