package hauveli.fishcasting.features.fish.profile_components

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import com.li64.tide.client.gui.screens.journal.ProfileComponent
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component


class AboveComponent(yLevel: Int) : ProfileComponent() {
    private val title: Component

    init {
        this.title = Component.literal("${yLevel-SEA_LEVEL}m ")
            .append("journal.info.above.title".asTranslatedComponent)
    }

    override fun render(
        graphics: GuiGraphics, font: Font, x: Int, y: Int,
        mouseX: Int, mouseY: Int, partialTick: Float
    ) {
        val center = x + AREA_WIDTH / 2
        val fontOffset = font.width(title) / 2
        graphics.drawString(
            font,
            title,
            center - fontOffset,
            y,
            TEXT_COLOR,
            false
        )
    }

    override fun getRequiredHeight(): Int {
        return 9
    }

    companion object {
        @JvmField
        val SEA_LEVEL: Int = 64
    }
}