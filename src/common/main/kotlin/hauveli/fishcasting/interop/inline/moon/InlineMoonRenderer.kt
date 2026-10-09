package hauveli.fishcasting.interop.inline.moon
import at.petrak.hexcasting.client.render.*
import at.petrak.hexcasting.interop.inline.InlinePatternData
import com.samsthenerd.inline.api.client.GlowHandling
import com.samsthenerd.inline.api.client.InlineRenderer
import com.samsthenerd.inline.impl.InlineStyle
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import kotlin.math.ceil
import kotlin.math.min

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/inline/InlinePatternRenderer.java

class InlineMoonRenderer : InlineRenderer<InlineMoonData> {
    override fun getId(): ResourceLocation {
        return InlineMoonData.rendererId
    }

    override fun getGlowPreference(forData: InlineMoonData?): GlowHandling {
        return GlowHandling.None()
    }

    // mostly static
    private val MOON_PHASES = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png")
    private val MOON_PHASE_ATLAS_WIDTH = 128
    private val MOON_PHASE_ATLAS_HEIGHT = 64
    private val MOON_SECTION = 32 // area dedicated to one moon phase stage
    private val COLUMNS = 4
    private val ROWS = 2

    private val DISPLAY_SIZE = 10 // scaling, probably don't change this unless needed

    // set by me
    private val MOON_DIAMETER = 8 // pixels of the moon to show. must be even

    // calculate the rest

    // I'm too lazy to recalculate every time even though I will only ever set this once, realistically...
    /*
        top right corner start
        32 wide segment
        moon is centered
        ex. 8 wide moon, pixel at 12,12 is outside the moon's top left corner, and 13,13 is the moon's top left corner
        so f(32, 8) = 12
        0 wide moon => offset is 16
        2 wide moon => offset is 15
        4 wide moon => offset is 14
        6 wide moon => offset is 13
        8 wide moon => offset is 12
        10 wide moon => offset is 11
        offset = 16 - moon_width / 2
        if moon diamater exceeds 32, then what? I blame the person who mixed in potentially.
        todo in distant future: define diameter in json.
     */
    private val MOON_OFFSET = MOON_SECTION / 2 - MOON_DIAMETER / 2 // starting offset for the corner of what I want to show the player
    private val STATES = COLUMNS * ROWS

    // https://docs.fabricmc.net/develop/rendering/gui-graphics#drawing-a-portion-of-a-texture
    // is this not relevant?
    override fun render(
        data: InlineMoonData,
        graphics: GuiGraphics,
        index: Int,
        style: Style,
        codepoint: Int,
        textRenderingContext: InlineRenderer.TextRenderingContext
    ): Int {
        graphics.pose().pushPose()

        val phase = (STATES - data.phase) % STATES // oh my god the texture is reversed so that I have to subtract from 8
        val u = (phase % COLUMNS) * MOON_SECTION + MOON_OFFSET
        val v = (phase / COLUMNS) * MOON_SECTION + MOON_OFFSET // int flooring so I dont forget

        graphics.blit(
            MOON_PHASES,
            0, 0,
            DISPLAY_SIZE, DISPLAY_SIZE,
            u.toFloat(), v.toFloat(),
            MOON_DIAMETER, MOON_DIAMETER, // 12
            MOON_PHASE_ATLAS_WIDTH, MOON_PHASE_ATLAS_HEIGHT // 128, 64
        )

        graphics.pose().popPose()
        return charWidth(data, style, codepoint)
    }

    override fun charWidth(data: InlineMoonData?, style: Style?, codepoint: Int): Int {

        return 0 // must be ZERO or it's FUCKED
    }

    companion object {
        val INSTANCE: InlineMoonRenderer = InlineMoonRenderer()
    }
}