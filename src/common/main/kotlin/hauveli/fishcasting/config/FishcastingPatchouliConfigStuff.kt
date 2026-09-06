package hauveli.fishcasting.config

import hauveli.fishcasting.Fishcasting.MODID
import vazkii.patchouli.api.PatchouliAPI

object FishcastingPatchouliConfigStuff {

    fun configurePatchouliFlags(config: FishcastingConfigs?) {
    }

    fun configurePatchouliFlag(name: String?, bool: Boolean) {
        PatchouliAPI.get().setConfigFlag("$MODID:$name", bool)
    }
}