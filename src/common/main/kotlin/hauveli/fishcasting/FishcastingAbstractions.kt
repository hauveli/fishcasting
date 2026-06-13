@file:JvmName("FishcastingAbstractions")

package hauveli.fishcasting

import hauveli.fishcasting.registry.FishcastingRegistrar

fun initRegistries(vararg registries: FishcastingRegistrar<*>) {
    for (registry in registries) {
        initRegistry(registry)
    }
}

expect fun <T : Any> initRegistry(registrar: FishcastingRegistrar<T>)
