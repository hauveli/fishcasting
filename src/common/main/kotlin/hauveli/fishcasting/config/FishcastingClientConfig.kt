package hauveli.fishcasting.config

import hauveli.fishcasting.Fishcasting
import me.fzzyhmstrs.fzzy_config.config.Config

// guide: https://moddedmc.wiki/en/project/fzzy-config/latest/docs/config-design/New-Configs#2-config-creation
class FishcastingClientConfig : Config(Fishcasting.id("client_config")) {

    var testValue = 2.0

}