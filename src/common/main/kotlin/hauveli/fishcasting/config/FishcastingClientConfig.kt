package hauveli.fishcasting.config

import hauveli.fishcasting.Fishcasting
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField.Companion.withListener
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedChoiceList
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedCondition
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import net.minecraft.network.chat.Component
import org.w3c.dom.Text
import java.util.function.Supplier


// guide: https://moddedmc.wiki/en/project/fzzy-config/latest/docs/config-design/New-Configs#2-config-creation
class FishcastingClientConfig : Config(Fishcasting.id("client_config")) {

    var showForbiddenPatchouliKnowledge: ValidatedBoolean = ValidatedBoolean(false).withListener {
        FishcastingPatchouliConfigStuff.configurePatchouliFlags(FishcastingConfigs)
    }
    // true = we show the entry that would be shown with jsonpatcher installed.

    //create a conditional validation with toCondition. Note that the type is no longer ValidatedInt directly.
    var castingTypeClientPreference: ValidatedCondition<FishcastingCommonConfig.CASTING_TYPE> =
        (ValidatedEnum(FishcastingCommonConfig.CASTING_TYPE.MOMENTARY))
            .toCondition(FishcastingConfigs.COMMON_CONFIG.castingTypeFreeChoice,
                Component.literal("Gate must be true"),
                { FishcastingCommonConfig.CASTING_TYPE.MOMENTARY })
            .withFailTitle(Component.literal("Condition not met"),
                Component.literal("Conditions not net"))
            .withListener {
                FishcastingPatchouliConfigStuff.configurePatchouliFlags(FishcastingConfigs)
            }
}