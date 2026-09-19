package hauveli.fishcasting.config

import hauveli.fishcasting.Fishcasting
import me.fzzyhmstrs.fzzy_config.annotations.Translation
import me.fzzyhmstrs.fzzy_config.annotations.Version
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup
import me.fzzyhmstrs.fzzy_config.util.Walkable
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField.Companion.descriptionProvider
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField.Companion.withListener
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedAny
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import java.util.function.BiFunction


// guide: https://moddedmc.wiki/en/project/fzzy-config/latest/docs/config-design/New-Configs#2-config-creation
@Version(version = 3)
@Translation(prefix = Fishcasting.MODID + FishcastingConfigs.CONFIG_BASE_KEY + "common")
class FishcastingCommonConfig : Config(Fishcasting.id("common_config")) {

    // CASTING ROD GROUP
    var castingRodGroup = ConfigGroup("casting_rod_group", collapsedByDefault = true)
    var castingTicksToCast: ValidatedInt = ValidatedInt(5, 100, 0)
    var cooldownAfterFishing: ValidatedInt = ValidatedInt(5, 100, 0)
    // conditions should supply live values. Validated fields are a convenient mechanism to do that. A plain boolean won't update in-GUI until changes are applied.
    var castingTypeFreeChoice: ValidatedBoolean = ValidatedBoolean(false).withListener {
        FishcastingPatchouliConfigStuff.configurePatchouliFlags(FishcastingConfigs)
    }
    // a little bit weird how pop works, note to self: it grabs the entry after this one and puts it in the list.
    @ConfigGroup.Pop
    // todo: make patchouli entries update? can I do this with a mixin? low priority but dang...
    var castingType: ValidatedEnum<CASTING_TYPE> = ValidatedEnum(CASTING_TYPE.MOMENTARY).withListener {
        FishcastingPatchouliConfigStuff.configurePatchouliFlags(FishcastingConfigs)
    }
    // CASTING ROD GROUP

    // TRADER GROUP
    var traderGroup = ConfigGroup("trader_group", collapsedByDefault = true)
    // Some players may not want to deal with more mobs. I'm also not entirely satisfied with how it spawns at this time.
    var spawnFishyTraderChance: ValidatedFloat = ValidatedFloat(0.10f, 1f, 0f)
    var fishyTraderPerPlayerIntervalMinutes: ValidatedFloat = ValidatedFloat(120f, 1200f, 0f)
    @ConfigGroup.Pop
    var traderPhialWeights = TraderWeightOption()
    // TRADER GROUP

    // not grouped
    var timeSkipPerPlayerIntervalMinutes: ValidatedFloat = ValidatedFloat(0f, 1200f, 0f)

    var isLengthPurificationOnlyFish: ValidatedBoolean = ValidatedBoolean(true).withListener {
        FishcastingPatchouliConfigStuff.configurePatchouliFlags(FishcastingConfigs)
    }


    class TraderWeightOption(weightCommon: Int,
                             weightUncommon: Int,
                             weightRare: Int,
                             weightVeryRare: Int,
                             weightLegendary: Int): Walkable {
        // This is the default distribution of fish with the mod. I think this distribution is good enough.
        // This is because it roughly corresponds to the supply a player may have access to, while keeping the good phials rare.
        constructor(): this(33, 21, 27, 19, 7)
        @ValidatedInt.Restrict(min = 0, max = 100, type = ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
        var common = weightCommon
        @ValidatedInt.Restrict(min = 0, max = 100, type = ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
        var uncommon = weightUncommon
        @ValidatedInt.Restrict(min = 0, max = 100, type = ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
        var rare = weightRare
        @ValidatedInt.Restrict(min = 0, max = 100, type = ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
        var veryRare = weightVeryRare
        @ValidatedInt.Restrict(min = 0, max = 100, type = ValidatedNumber.WidgetType.TEXTBOX_WITH_BUTTONS)
        var legendary = weightLegendary
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