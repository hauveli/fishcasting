package hauveli.fishcasting.config

import hauveli.fishcasting.Fishcasting
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField.Companion.withListener
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import net.minecraft.world.InteractionHand


// guide: https://moddedmc.wiki/en/project/fzzy-config/latest/docs/config-design/New-Configs#2-config-creation
class FishcastingCommonConfig : Config(Fishcasting.id("common_config")) {

    var castingTicksToCast: ValidatedInt = ValidatedInt(5, 100, 0) // must be greater than or equal to 0
    var cooldownAfterFishing: ValidatedInt = ValidatedInt(5, 100, 0) // must be greater than or equal to 0
    // conditions should supply live values. Validated fields are a convenient mechanism to do that. A plain boolean won't update in-GUI until changes are applied.
    var castingTypeFreeChoice: ValidatedBoolean = ValidatedBoolean(false).withListener {
        FishcastingPatchouliConfigStuff.configurePatchouliFlags(FishcastingConfigs)
    }

    var castingType: ValidatedEnum<CASTING_TYPE> = ValidatedEnum(CASTING_TYPE.MOMENTARY).withListener {
        FishcastingPatchouliConfigStuff.configurePatchouliFlags(FishcastingConfigs)
    }
    var isLengthPurificationOnlyFish: ValidatedBoolean = ValidatedBoolean(true).withListener {
        FishcastingPatchouliConfigStuff.configurePatchouliFlags(FishcastingConfigs)
    }

    fun atLeastZero(value: Int): Int {
        return if (value > 0) { value } else { 0 }
    }


    fun getCooldownAfterFishingMinigame(): Int {
        return cooldownAfterFishing.get()
    }

    fun setCooldownAfterFishingMinigame(cd: Int) {
        cooldownAfterFishing.trySet(cd)
    }

    enum class CASTING_TYPE {
        OFFHAND_ONLY,
        MOMENTARY
    }

    fun shouldHexMomentary(charge: Int, chargeStartValue: Int): Boolean {
        val heldDuration = chargeStartValue - charge
        return castingIsMomentary() && heldDurationShortEnoughToHex(heldDuration)
    }

    // how many ticks you can hold down your mouse
    // note: this means there's a minimum value before your rod even begins charging.
    fun heldDurationShortEnoughToHex(heldDuration: Int): Boolean {
        // heldDuration in [0, 5]
        return castingTicksToCast.get() >= heldDuration && heldDuration > -1
    }

    fun shouldHexOffhand(hand: InteractionHand?): Boolean {
        return castingIsOffhandOnly() && InteractionHand.OFF_HAND == hand
    }

    fun getCastingDelay(): Int {
        if (castingIsMomentary()) {
            return castingTicksToCast.get()
        }
        return 0
    }

    // default
    fun castingIsMomentary(): Boolean {
        if (FishcastingConfigs.COMMON_CONFIG.castingTypeFreeChoice.get()) {
            return FishcastingConfigs.CLIENT_CONFIG.castingTypeClientPreference.get() == CASTING_TYPE.MOMENTARY
        }
        return castingType.get() == CASTING_TYPE.MOMENTARY
    }

    fun castingIsOffhandOnly(): Boolean {
        if (FishcastingConfigs.COMMON_CONFIG.castingTypeFreeChoice.get()) {
            return FishcastingConfigs.CLIENT_CONFIG.castingTypeClientPreference.get() == CASTING_TYPE.OFFHAND_ONLY
        }
        return castingType.get() == CASTING_TYPE.OFFHAND_ONLY
    }


    /*
    companion object {
        // whe config is updated, patchouliFlags must be configured again:
        // some descriptions must change based on the config
        // all my flags will go here if I have more
        fun configurePatchouliFlags(config: FishcastingClientConfig) {
            /*
            fallback should always be false. Used for making items point
            SOMEWHERE if there's a page with branching options based on config
         */
            configurePatchouliFlag("fallback", false)
            configurePatchouliFlag("momentary_casting", config.gameplay.castingIsMomentary())
            configurePatchouliFlag(
                "length_purification_only_fish",
                config.gameplay.isLengthPurificationOnlyFish
            )
            configurePatchouliFlag("forbidden_patchouli_knowledge", config.client.isShowForbiddenPatchouliKnowledge())
        }

        private fun configurePatchouliFlag(name: String?, bool: Boolean) {
            PatchouliAPI.get().setConfigFlag("$MODID:$name", bool)
        }
    }
    */
}