package hauveli.fishcasting.features.fish.edified

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import com.li64.tide.client.gui.screens.journal.ProfileComponent
import hauveli.fishcasting.Fishcasting.capitalizeFirstLetterOfEachWord
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

class BlockNearbyComponent(block: TagKey<Block>, radius: Int) : ProfileComponent() {
    private val text: Component

    // todo: maybe open a PR to tide with something like this but rendering the blocks within the TagKey?
    init {
        this.text = "journal.info.nearby_block.title".asTranslatedComponent
            .append(": ")
            .append(block.location().path.replace("_"," ").capitalizeFirstLetterOfEachWord())
            //.append("journal.info.nearby_block.within".asTranslatedComponent)
            //.append(": ${radius}m")
    }

    override fun render(
        graphics: GuiGraphics, font: Font, x: Int, y: Int,
        mouseX: Int, mouseY: Int, partialTick: Float
    ) {
        val center = x + AREA_WIDTH / 2
        graphics.drawString(
            font,
            text,
            center - font.width(text) / 2,
            y,
            TEXT_COLOR,
            false
        )
    }

    override fun getRequiredHeight(): Int {
        return 9
    }
}