@file:JvmName("FishcastingAbstractionsActual")

package hauveli.fishcasting

import hauveli.fishcasting.registry.FishcastingRegistrar
import net.msrandom.multiplatform.annotations.Actual
import net.neoforged.neoforge.registries.RegisterEvent

actual fun <T : Any> initRegistry(registrar: FishcastingRegistrar<T>) {
        NeoForgeFishcasting.container.eventBus!!.addListener { event: RegisterEvent ->
            event.register(registrar.registryKey) { helper ->
                registrar.init(helper::register)
            }
        }
}