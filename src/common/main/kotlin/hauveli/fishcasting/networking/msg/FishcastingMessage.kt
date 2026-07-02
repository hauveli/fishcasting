package hauveli.fishcasting.networking.msg
/*
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.networking.FishcastingNetworking
import hauveli.fishcasting.networking.handler.applyOnClient
import hauveli.fishcasting.networking.handler.applyOnServer
import io.wispforest.owo.network.ClientAccess
import io.wispforest.owo.network.OwoNetChannel
import io.wispforest.owo.network.ServerAccess
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer

sealed interface FishcastingMessage

sealed interface FishcastingMessageC2S : FishcastingMessage {
    fun <T> T.sendToServer() where T : Record {
        FishcastingNetworking.CHANNEL.clientHandle().send(this)
    }
}

sealed interface FishcastingMessageS2C : FishcastingMessage {
}

fun <T> T.sendToPlayer(player: ServerPlayer) where T : Record {
    FishcastingNetworking.CHANNEL.serverHandle(player).send( this)
}

fun <T> T.sendToPlayers(players: Iterable<ServerPlayer>) where T : Record {
    players.forEach { sendToPlayer(it) }
}

sealed interface FishcastingMessageCompanion<T> where T : FishcastingMessage, T : Record {
    val type: Class<T>

    fun apply(msg: T, access: ServerAccess): Unit {
        Fishcasting.LOGGER.debug("Server received packet from {}: {}", access.player().name.string, this)
        when (msg) {
            is FishcastingMessageC2S -> msg.applyOnServer(access)
            else -> Fishcasting.LOGGER.warn("Message not handled on server: {}", msg::class)
        }
    }

    fun apply(msg: T, access: ClientAccess): Unit {
        Fishcasting.LOGGER.debug("Client received packet: {}", this)
        when (msg) {
            is FishcastingMessageS2C -> msg.applyOnClient(access)
            else -> Fishcasting.LOGGER.warn("Message not handled on client: {}", msg::class)
        }
    }

    fun register(channel: OwoNetChannel) {
        channel.registerServerbound(type) { msg, access -> apply(msg, access) }
    }
}
*/