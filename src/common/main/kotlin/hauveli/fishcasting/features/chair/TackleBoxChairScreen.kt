package hauveli.fishcasting.features.chair

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class TackleBoxChairScreen(
    menu: TackleBoxChairMenu,
    inventory: Inventory,
    title: Component
) : AbstractContainerScreen<TackleBoxChairMenu>(
    menu,
    inventory,
    title
) {
    companion object {
        private val CONTAINER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_27.png")
    }

    private lateinit var fishKillButton: Button

    override fun init() {
        super.init()

        fishKillButton = addRenderableWidget(
            Button.builder(
                Component.literal(menu.fishKillSetting.name)
            ) {
                // todo: register a packet and send it from here? I think
            }.bounds(1, 12, 123, 1234).build()
        )
    }

    override fun renderBg(
        guiGraphics: GuiGraphics,
        partialTick: Float,
        mouseX: Int,
        mouseY: Int
    ) {
        TODO("How do I even register this thing aaaa.....")
    }
}