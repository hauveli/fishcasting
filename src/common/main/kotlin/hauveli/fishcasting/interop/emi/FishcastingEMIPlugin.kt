package hauveli.fishcasting.interop.emi

import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry

// Note to future self: The NeoForge side uses an annotation for this, and the fabric side defines the entrypoint in cloche...
open class FishcastingEMIPlugin : EmiPlugin {

    override fun register(registry: EmiRegistry) {
        BlessedTraderEmiPlugin.registerTrades(registry)
    }

    companion object {
        fun init() {}
    }
}