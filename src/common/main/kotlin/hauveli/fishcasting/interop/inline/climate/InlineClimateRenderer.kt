package hauveli.fishcasting.interop.inline.climate
import com.li64.tide.Tide
import com.li64.tide.client.gui.screens.journal.components.DepthComponent
import com.samsthenerd.inline.api.client.GlowHandling
import com.samsthenerd.inline.api.client.InlineRenderer
import hauveli.fishcasting.Fishcasting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternRenderer.java

class InlineClimateRenderer : InlineRenderer<InlineClimateData> {
    override fun getId(): ResourceLocation {
        return InlineClimateData.rendererId
    }

    override fun getGlowPreference(forData: InlineClimateData?): GlowHandling {
        return GlowHandling.None()
    }

    // mostly static
    private val DEPTH_BAR = Tide.resource("textures/gui/journal/temp_bar.png")
    private val MOON_PHASE_ATLAS_WIDTH = 129
    private val MOON_PHASE_ATLAS_HEIGHT = 9
    private val DISPLAY_SIZE = 9 // scaling, probably don't change this unless needed

    // set by me
    private val MOON_DIAMETER = 9 // pixels of the moon to show. must be even

    private val FALLBACK = Fishcasting.id("textures/gui/environment/fallback.png")

    // https://docs.fabricmc.net/develop/rendering/gui-graphics#drawing-a-portion-of-a-texture
    // is this not relevant?
    override fun render(
        data: InlineClimateData,
        graphics: GuiGraphics,
        index: Int,
        style: Style,
        codepoint: Int,
        textRenderingContext: InlineRenderer.TextRenderingContext
    ): Int {
        graphics.pose().pushPose()

        // -35c to +50c
        val u = Mth.clamp(Mth.inverseLerp(data.temperature, -35f, 50f), 0f, 1f); // 1 for 50c, 0 for -35c
        val v = 0f

        graphics.blit(
            DEPTH_BAR,
            0, -1,
            DISPLAY_SIZE, DISPLAY_SIZE,
            MOON_PHASE_ATLAS_WIDTH * u, v,
            MOON_DIAMETER, MOON_DIAMETER, // 12
            MOON_PHASE_ATLAS_WIDTH, MOON_PHASE_ATLAS_HEIGHT // 128, 64
        )

        graphics.pose().popPose()
        return charWidth(data, style, codepoint)
    }

    override fun charWidth(data: InlineClimateData?, style: Style?, codepoint: Int): Int {

        return 0 // must be ZERO or it's FUCKED
    }

    companion object {
        val INSTANCE: InlineClimateRenderer = InlineClimateRenderer()
    }
}