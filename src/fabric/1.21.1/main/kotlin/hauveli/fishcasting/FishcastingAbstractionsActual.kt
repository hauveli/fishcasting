@file:JvmName("FishcastingAbstractionsActual")

package hauveli.fishcasting

import hauveli.fishcasting.registry.FishcastingRegistrar
import net.minecraft.core.Registry
import net.msrandom.multiplatform.annotations.Actual

actual fun <T : Any> initRegistry(registrar: FishcastingRegistrar<T>) {
    val registry = registrar.registry
    registrar.init { id, value -> Registry.register(registry, id, value) }
}
