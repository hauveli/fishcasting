package hauveli.fishcasting

import hauveli.fishcasting.client.NeoForgeFishcastingClient
import hauveli.fishcasting.datagen.NeoForgeFishcastingDatagen
import hauveli.fishcasting.registry.FishcastingSounds
import hauveli.fishcasting.registry.FishcastingSounds.registerSounds
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.RegisterEvent
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier


@Mod(Fishcasting.MODID)
class NeoForgeFishcasting(modBus: IEventBus, container: ModContainer) {

    init {
        // thank you
        // https://github.com/VazkiiMods/Botania/blob/1.18.x/Forge/src/main/java/vazkii/botania/forge/ForgeCommonInitializer.java
        fun <T> bind(
            registry: ResourceKey<out Registry<T>>,
            source: Consumer<BiConsumer<T, ResourceLocation>>
        ) {
            modBus.addListener { event: RegisterEvent ->
                if (registry == event.registryKey) {
                    source.accept(BiConsumer { t, rl ->
                        event.register(registry, rl) { t }
                    })
                }
            }
        }

        modBus.apply {
            addListener(NeoForgeFishcastingClient::init)
            addListener(NeoForgeFishcastingDatagen::init)
            addListener(NeoForgeFishcastingServer::init)
        }
        bind(Registries.SOUND_EVENT, FishcastingSounds::registerSounds)
        Fishcasting.init()
    }

    companion object {
        internal val container: ModContainer
            get() = ModList.get().getModContainerById(Fishcasting.MODID).get()
    }
}
