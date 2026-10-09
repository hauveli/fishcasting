package hauveli.fishcasting.features.fish.profile_components

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import com.li64.tide.client.gui.screens.journal.ProfileComponent
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.Fishcasting.capitalizeFirstLetterOfEachWord
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.tags.TagKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block


class BlockNearbyComponent(blockTagKey: TagKey<Block>, radius: Int) : ProfileComponent() {
    private val title: Component
    private val subtitle: Component
    private val blocks: List<ItemStack>?
    private val TEXTURE_SIZE = 16
    private var indexToDraw = 0
    private var someKindOfCounter = 0f

    init {
        this.title = "journal.info.nearby_block.title".asTranslatedComponent
        this.subtitle = Component.translatableWithFallback(
            blockTagKey.location().toLanguageKey(),
            blockTagKey.location().path.replace("_"," ")
                .capitalizeFirstLetterOfEachWord()
        )

        //.append("journal.info.nearby_block.within".asTranslatedComponent)
            //.append(": ${radius}m")
        // how do I add all the blocks from the tag to this?
        val possiblyTag = BuiltInRegistries.BLOCK.getTagOrEmpty(blockTagKey)

        // I'd prefer to not crash disastrously because some tags weren't loaded after the game launches fine
        // but I should probably print a debug message...
        if (possiblyTag.count() > 0) {
            blocks = possiblyTag
                .map { it.value() }
                .map { BuiltInRegistries.BLOCK.getKey(it) }
                .map { BuiltInRegistries.ITEM.get(it).defaultInstance }
        } else {
            Fishcasting.LOGGER.warn("Problem encountered rendering NearbyBlockComponent: TagKey<Block> '${blockTagKey.location()}' was empty.")
            blocks = null
        }
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
            center - fontOffset, y,
            TEXT_COLOR,
            false
        )

        val translatedString = subtitle.string
        val halfTextureSize = TEXTURE_SIZE / 2
        val fontOffsetSubtitle = (font.width(translatedString)) / 2
        val subtitleX = center - fontOffsetSubtitle - halfTextureSize
        val subtitleY = y + requiredHeight / 2
        graphics.drawString(
            font,
            subtitle,
            subtitleX, subtitleY,
            TEXT_COLOR,
            false
        )

        if (blocks == null)
            return
        // I'm too lazy to figure out how to have this depend on the system time
        val levelMaybe = Minecraft.getInstance().level ?: return
        indexToDraw = ((levelMaybe.gameTime / 80L) % blocks.size).toInt()
        // holy fuck I either forgot or didn't know this existed note to future self (I keep saying this so it's searchable via note/future):
        // graphics.renderItem()
        val textureOffsetFromSubtitle = fontOffsetSubtitle - halfTextureSize + 1
        val itemPosX = center + textureOffsetFromSubtitle
        val itemPosY = subtitleY - font.lineHeight / 2
        val itemToDraw = blocks[indexToDraw]
        graphics.renderItem(itemToDraw, itemPosX, itemPosY)

        // render item name
        if (mouseX >= itemPosX && mouseX <= itemPosX + TEXTURE_SIZE
            && mouseY >= itemPosY && mouseY <= itemPosY + TEXTURE_SIZE) {
            graphics.renderTooltip(
                font, itemToDraw.hoverName,
                mouseX, mouseY
            )
        }
    }

    override fun getRequiredHeight(): Int {
        return 28
    }
}