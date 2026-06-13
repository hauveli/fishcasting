package hauveli.fishcasting.config

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType

object FishcastingConfigs {
    //instance of your config loaded from file and automatically registered to the SyncedConfigRegistry and ClientConfigRegistry using the getId() method
    val COMMON_CONFIG = ConfigApi.registerAndLoadConfig(::FishcastingCommonConfig)

    //adding the registerType, you can register a config as client-only. No syncing will occur. Useful for client-only mods.
    val CLIENT_CONFIG = ConfigApi.registerAndLoadConfig(::FishcastingClientConfig, RegisterType.CLIENT)

    fun init() {}
}