package hauveli.fishcasting.interop.inline.structure
import com.li64.tide.Tide
import com.samsthenerd.inline.api.client.GlowHandling
import com.samsthenerd.inline.api.client.InlineRenderer
import hauveli.fishcasting.Fishcasting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.data.worldgen.Structures
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.structures.StrongholdStructure

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternRenderer.java

class InlineStructureRenderer : InlineRenderer<InlineStructureData> {
    override fun getId(): ResourceLocation {
        return InlineStructureData.rendererId
    }

    override fun getGlowPreference(forData: InlineStructureData?): GlowHandling {
        return GlowHandling.None()
    }


    // mostly static
    private val DIMENSION_TYPES = Tide.resource("textures/gui/journal/structure.png")
    private val MOON_PHASE_ATLAS_WIDTH = 30
    private val MOON_PHASE_ATLAS_HEIGHT = 10
    private val FALLBACK_SIZE = 10
    private val MOON_SECTION = 10 // area dedicated to one moon phase stage
    private val COLUMNS = 3
    private val ROWS = 1

    private val DISPLAY_SIZE = 10 // scaling, probably don't change this unless needed

    // set by me
    private val MOON_DIAMETER = 10 // pixels of the moon to show. must be even

    private val MOON_OFFSET = MOON_SECTION / 2 - MOON_DIAMETER / 2 // starting offset for the corner of what I want to show the player
    private val STATES = COLUMNS * ROWS

    private val FALLBACK = Fishcasting.id("textures/gui/environment/fallback.png")

    // https://docs.fabricmc.net/develop/rendering/gui-graphics#drawing-a-portion-of-a-texture
    // is this not relevant?
    override fun render(
        data: InlineStructureData,
        graphics: GuiGraphics,
        index: Int,
        style: Style,
        codepoint: Int,
        textRenderingContext: InlineRenderer.TextRenderingContext
    ): Int {
        graphics.pose().pushPose()

        // does this make sense to do?
        // it might check every time it needs to draw and I don't know how much that is.....................
        // todo: determine how often it draws, if it only has to read once to cache it, this is ok imo
        val resMan = Minecraft.getInstance().resourceManager
        val resLoc = Fishcasting.id("textures/gui/environment/structure/${data.structure.replace(":","/")}.png")
        if (resMan.getResource(resLoc).isPresent) {
            graphics.blit(
                resLoc,
                0, -1,
                DISPLAY_SIZE, DISPLAY_SIZE,
                0f, 0f,
                FALLBACK_SIZE, FALLBACK_SIZE,
                FALLBACK_SIZE, FALLBACK_SIZE
            )
        } else {
            graphics.blit(
                FALLBACK,
                0, -1,
                DISPLAY_SIZE, DISPLAY_SIZE,
                0f, 0f,
                FALLBACK_SIZE, FALLBACK_SIZE,
                FALLBACK_SIZE, FALLBACK_SIZE
            )
        }

        graphics.pose().popPose()
        return charWidth(data, style, codepoint)
    }

    override fun charWidth(data: InlineStructureData?, style: Style?, codepoint: Int): Int {

        return 0 // must be ZERO or it's FUCKED
    }

    companion object {
        val INSTANCE: InlineStructureRenderer = InlineStructureRenderer()
    }
}