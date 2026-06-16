package hauveli.fishcasting.registry

import hauveli.fishcasting.Fishcasting.MODID
import hauveli.fishcasting.Fishcasting.id
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import java.util.function.BiConsumer


object FishcastingCreativeTabs {

    //@JvmStatic
    fun registerCreativeTabs(r: BiConsumer<CreativeModeTab, ResourceLocation>) {
        for (e in TABS.entries) {
            r.accept(e.value, e.key)
        }
    }

    val TABS: MutableMap<ResourceLocation, CreativeModeTab> = LinkedHashMap()

    // Fishex would have been a good addon name, too
    val FISHCASTING: CreativeModeTab = register(
        MODID, CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon({ ItemStack(FishcastingItems.LOUD_FISHING_LINE) })
    )
    // hee heee heeee
    val CreativeModeTab.key: ResourceKey<CreativeModeTab>?
        get() = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(this).orElse(null)

    private fun register(name: String, tabBuilder: CreativeModeTab.Builder): CreativeModeTab {
        val tab = tabBuilder.title(Component.translatable("$name.creative_tab.title")).build()
        val old = TABS.put(id(name), tab)
        require(old == null) { "Typo? Duplicate id $name" }
        return tab
    }
}