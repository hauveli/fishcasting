package hauveli.fishcasting.config

import hauveli.fishcasting.Fishcasting.MODID
import vazkii.patchouli.api.PatchouliAPI

object FishcastingPatchouliConfigStuff {

    fun configurePatchouliFlags(config: FishcastingConfigs) {
        configurePatchouliFlag("fallback", false)
        if (config.COMMON_CONFIG.castingTypeFreeChoice.get()) {
            configurePatchouliFlag("momentary_casting", config.CLIENT_CONFIG.castingTypeClientPreference.get() == FishcastingCommonConfig.CASTING_TYPE.MOMENTARY)
        } else {
            configurePatchouliFlag("momentary_casting", config.COMMON_CONFIG.castingIsMomentary())
        }
        configurePatchouliFlag("length_purification_only_fish", config.COMMON_CONFIG.isLengthPurificationOnlyFish.get())
        configurePatchouliFlag("forbidden_patchouli_knowledge", config.CLIENT_CONFIG.showForbiddenPatchouliKnowledge.get())
    }

    fun configurePatchouliFlag(name: String?, bool: Boolean) {
        PatchouliAPI.get().setConfigFlag("$MODID:$name", bool)
    }
}