package hauveli.fishcasting.client

import at.petrak.hexcasting.client.render.GaslightingTracker
import at.petrak.hexcasting.xplat.IClientXplatAbstractions
import hauveli.fishcasting.interop.inline.InlineFishcastingClient
import hauveli.fishcasting.registry.FishcastingItems
import net.minecraft.world.item.Item
import kotlin.math.abs


object FishcastingClient {
    fun init() {
        // todo: figure out why neoforge hates this
        // FishcastingItems.registerItemModelProperties()
        InlineFishcastingClient.init()
        registerGaslight4(FishcastingItems.SHEPHERDS_CASTING_ROD.value)
        registerGaslight4(FishcastingItems.BLESSED_FOCUS_BOBBER.value)
    }

    // private apparently
    // https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/client/RegisterClientStuff.java#L148
    fun registerGaslight4(item: Item) {
        IClientXplatAbstractions.INSTANCE.registerItemProperty(
            item,
            GaslightingTracker.GASLIGHTING_PRED,
            { stack, level, holder, holderID ->
                abs(GaslightingTracker.getGaslightingAmount() % 4f) })
    }
}