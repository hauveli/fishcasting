package hauveli.fishcasting.networking

import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.networking.msg.FishcastingMessageCompanion
import io.wispforest.owo.network.OwoNetChannel

object FishcastingNetworking {
    val CHANNEL: OwoNetChannel = OwoNetChannel.create(Fishcasting.id("networking_channel"))

    fun init() {
        for (subclass in FishcastingMessageCompanion::class.sealedSubclasses) {
            subclass.objectInstance?.register(CHANNEL)
        }
    }
}
