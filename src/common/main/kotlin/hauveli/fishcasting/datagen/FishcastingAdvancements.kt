package hauveli.fishcasting.datagen

import com.li64.tide.Tide
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.registry.FishcastingItems
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementType
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.critereon.ImpossibleTrigger
import net.minecraft.core.HolderLookup
import net.minecraft.data.advancements.AdvancementProvider
import net.minecraft.data.advancements.AdvancementSubProvider
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import java.util.function.Consumer


class FishcastingAdvancements : AdvancementSubProvider {
    val ADVANCEMENTS = mutableMapOf<ResourceLocation, Advancement.Builder>()

    /*
    val TACKLEBOX_CHAIR_FISHER = make("instructional/fished_in_tacklebox_chair",
        FishcastingItems.TACKLEBOX_CHAIR,
        "Title example",
        "Granted only by code")
     */

    override fun generate(
        registries: HolderLookup.Provider,
        consumer: Consumer<AdvancementHolder?>
    ) {
        ADVANCEMENTS.forEach({
            map ->
            map.value.save(
                    consumer,
                    map.key.toString()
                )
        })
    }

    fun make(path: String,
             itemToDisplay: Item,
             title: String,
             description: String,
             root: ResourceLocation = Tide.resource("root"),
             taskType: AdvancementType = AdvancementType.TASK,
             toast: Boolean = true,
             announce: Boolean = true,
             hidden: Boolean = false): AdvancementHolder {
        val builder = Advancement.Builder.advancement()
        builder.addCriterion(
            "granted",
            CriteriaTriggers.IMPOSSIBLE.createCriterion(
                ImpossibleTrigger.TriggerInstance()
            )
        )
        builder.requirements(
            AdvancementRequirements.allOf(
                mutableListOf<String?>("granted")
            )
        )
        builder.display(
            itemToDisplay, // icon
            Component.literal(title),
            Component.literal(description),
            null, // source advancement
            taskType, // TASK, GOAL, CHALLENGE
            toast,
            announce,
            hidden
        )
        builder.parent(AdvancementHolder(root, Advancement.Builder().build(root).value()))

        val resLoc = Fishcasting.id(path)
        ADVANCEMENTS[resLoc] = builder

        val advancement = builder.build(resLoc) // does a builder get destroyed if this happens?

        return advancement
    }
}