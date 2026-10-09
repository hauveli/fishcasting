package hauveli.fishcasting.interop.inline.depth
import com.li64.tide.Tide
import com.li64.tide.client.gui.screens.journal.components.DepthComponent
import com.samsthenerd.inline.api.client.GlowHandling
import com.samsthenerd.inline.api.client.InlineRenderer
import hauveli.fishcasting.Fishcasting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternRenderer.java

class InlineDepthRenderer : InlineRenderer<InlineDepthData> {
    override fun getId(): ResourceLocation {
        return InlineDepthData.rendererId
    }

    override fun getGlowPreference(forData: InlineDepthData?): GlowHandling {
        return GlowHandling.None()
    }

    // mostly static
    private val DEPTH_BAR = Tide.resource("textures/gui/journal/depth_bar.png")
    private val MOON_PHASE_ATLAS_WIDTH = 129
    private val MOON_PHASE_ATLAS_HEIGHT = 9
    private val DISPLAY_SIZE = 9 // scaling, probably don't change this unless needed

    // set by me
    private val MOON_DIAMETER = 9 // pixels of the moon to show. must be even

    private val FALLBACK = Fishcasting.id("textures/gui/environment/fallback.png")

    // https://docs.fabricmc.net/develop/rendering/gui-graphics#drawing-a-portion-of-a-texture
    // is this not relevant?
    override fun render(
        data: InlineDepthData,
        graphics: GuiGraphics,
        index: Int,
        style: Style,
        codepoint: Int,
        textRenderingContext: InlineRenderer.TextRenderingContext
    ): Int {
        graphics.pose().pushPose()

        // it'll go off the side without the padding
        val paddingOffset = DepthComponent.MAX_Y - MOON_DIAMETER
        var offsetDepth = DepthComponent.MAX_Y - data.depth
        if (offsetDepth > paddingOffset) {
            offsetDepth = paddingOffset
        }
        // MAX_Y - Z = 120 <=> Z = 57
        // val endOffset = ((MOON_PHASE_ATLAS_WIDTH - MOON_PHASE_ATLAS_HEIGHT - DepthComponent.MAX_Y) - offsetDepth)
        val u = DepthComponent.depthToFloat(offsetDepth) // clamps it for me, 1 for surface, 0 for bedrock
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

    override fun charWidth(data: InlineDepthData?, style: Style?, codepoint: Int): Int {

        return 0 // must be ZERO or it's FUCKED
    }

    companion object {
        val INSTANCE: InlineDepthRenderer = InlineDepthRenderer()
    }
}