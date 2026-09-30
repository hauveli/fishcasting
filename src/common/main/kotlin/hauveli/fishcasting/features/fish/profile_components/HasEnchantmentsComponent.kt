package hauveli.fishcasting.features.fish.profile_components

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import com.li64.tide.client.gui.screens.journal.ProfileComponent
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.Fishcasting.capitalizeFirstLetterOfEachWord
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.EnchantmentTags
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents
import java.util.Optional


class HasEnchantmentsComponent(enchantmentResourceKey: List<ResourceKey<Enchantment>>) : ProfileComponent() {
    private val title: Component
    private val enchantments: List<Component>
    private val TEXTURE_SIZE = 16
    private var indexToDraw = 0
    private var someKindOfCounter = 0f

    init {
        this.title = "journal.info.has_enchantments.title".asTranslatedComponent

        this.enchantments = enchantmentResourceKey
            .map {
                Component.translatableWithFallback(
                it.location().toLanguageKey(),
                it.location().path.replace("_"," ")
                .capitalizeFirstLetterOfEachWord()
            ).withColor(
                    if (isBeneficial(it) )
                        ChatFormatting.BLUE.color!!
                    else
                        ChatFormatting.RED.color!!)
            }
    }

    // this is all only ever run on client as a reminder to myself to not stress about Minecraft
    fun isBeneficial(enchantmentResourceKey: ResourceKey<Enchantment>): Boolean {
        val level = Minecraft.getInstance().level ?: return true

        val registry = level.registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)

        val enchantment: Optional<Holder.Reference<Enchantment>> =
            registry.get(enchantmentResourceKey)

        if (enchantment.isPresent) {
            val holder = enchantment.get()

            // is there a more generic way to check? idk...
            val isDetrimental = holder.`is`(EnchantmentTags.CURSE)

            return !isDetrimental
        }
        return true
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

        if (enchantments.isEmpty())
            return
        // I'm too lazy to figure out how to have this depend on the system time
        val levelMaybe = Minecraft.getInstance().level ?: return
        indexToDraw = ((levelMaybe.gameTime / 80L) % enchantments.size).toInt()

        val subtitle = enchantments[indexToDraw]
        val fontOffsetSubtitle = font.width(subtitle) / 2
        graphics.drawString(
            font,
            subtitle,
            center - fontOffsetSubtitle,
            y + requiredHeight / 2,
            subtitle.style.color!!.value,
            false
        )
    }

    override fun getRequiredHeight(): Int {
        return 24
    }
}