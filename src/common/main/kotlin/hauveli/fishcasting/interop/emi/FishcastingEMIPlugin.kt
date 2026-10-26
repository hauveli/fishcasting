package hauveli.fishcasting.interop.emi

import dev.emi.emi.api.EmiEntrypoint
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import hauveli.fishcasting.Fishcasting

//  Attempt to create proxy for a non-annotation type: dev.emi.emi.api.EmiEntrypoint
// ???????? what is going on on fabric in the dev env???

@EmiEntrypoint
class FishcastingEMIPlugin : EmiPlugin {

    override fun register(registry: EmiRegistry) {
        BlessedTraderEmiPlugin.registerTrades(registry)
    }

    companion object {
        fun init() {}
    }
}