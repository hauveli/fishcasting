package hauveli.fishcasting.client
import com.li64.tide.Tide
import com.li64.tide.client.gui.TideToasts
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.toasts.Toast
import net.minecraft.client.gui.components.toasts.ToastComponent
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack


class FishcastingToasts{


    fun showToast(title: Component?, description: Component?, display: ItemStack?) {
        if (Tide.CONFIG.journal.showToasts) TideToasts.display(TideToasts.NewPageToast(title, description, display))
    }

    class ItemToast(
        private val title: Component,
        private val message: Component,
        private val icon: ItemStack
    ) : Toast {
        val TEXTURE: ResourceLocation = ResourceLocation.fromNamespaceAndPath("minecraft", "toast/recipe")

        override fun render(
            guiGraphics: GuiGraphics,
            toastComponent: ToastComponent,
            timeVisible: Long
        ): Toast.Visibility {
            guiGraphics.blitSprite(
                TEXTURE,
                0,
                0,
                width(),
                height()
            )

            val font = Minecraft.getInstance().font

            guiGraphics.drawString(font, title, 30, 7, 0xFFFF00, false)
            guiGraphics.drawString(font, message, 30, 18, 0xFFFFFF, false)

            guiGraphics.renderItem(icon, 8, 8)

            return if (timeVisible >= 5000L)
                Toast.Visibility.HIDE
            else
                Toast.Visibility.SHOW
        }
    }
}