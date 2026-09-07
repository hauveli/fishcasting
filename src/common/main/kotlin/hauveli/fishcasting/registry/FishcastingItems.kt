package hauveli.fishcasting.registry

import at.petrak.hexcasting.api.addldata.ItemDelegatingEntityIotaHolder
import com.google.common.base.Suppliers
import com.li64.tide.client.TideItemModelProperties
import com.li64.tide.data.rods.CustomRodManager
import com.li64.tide.registries.entities.misc.fishing.HookAccessor
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import com.li64.tide.registries.items.FishingHookItem
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.features.paraphernalia.HexyRodItem
import hauveli.fishcasting.features.paraphernalia.TideyFocusItem
import hauveli.fishcasting.registry.FishcastingCreativeTabs.FISHCASTING
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents.BUCKET_EMPTY_FISH
import net.minecraft.world.item.*
import net.minecraft.world.level.material.Fluids.WATER
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier


object FishcastingItems : FishcastingRegistrar<Item>(
    BuiltInRegistries.ITEM.key() as ResourceKey<Registry<Item>>,
    { BuiltInRegistries.ITEM }
) {

    private val ITEM_TABS: MutableMap<CreativeModeTab, MutableList<() -> TabEntry>> =
        LinkedHashMap<CreativeModeTab, MutableList<() -> TabEntry>>()

    @JvmStatic
    fun registerItemCreativeTab(r: CreativeModeTab.Output, tab: CreativeModeTab) {
        if (ITEM_TABS.isEmpty()) {
            for (item in ITEMS) {
                ITEM_TABS.computeIfAbsent(FISHCASTING.value) { t: CreativeModeTab -> ArrayList() }
                    .add({ TabEntry.ItemEntry(item.value) })
            }
        }
        for (item in ITEM_TABS.getOrDefault(tab, mutableListOf<() -> TabEntry?>())) {
            item()!!.register(r)
        }
    }



    private val ITEMS: MutableList<Entry<*>> = mutableListOf()

    private fun <T : Item> make(name: String, builder: () -> T): FishcastingRegistrar<Item>.Entry<T> {
        val registered = register(id(name), builder)
        ITEMS.add(registered)
        return registered
    }

    /*
    fun props(): Item.Properties {
        return Item.Properties()
    }

    fun unstackable(): Item.Properties {
        return props().stacksTo(1)
    }

    fun unstackableUncommon(): Item.Properties {
        return unstackable().rarity(Rarity.UNCOMMON)
    }

    fun newItem(): Item {
        return Item(props())
    }

     */

    @JvmField
    val BLESSED_FOCUS_BOBBER = make(
        "blessed_focus_bobber", {TideyFocusItem(Item.Properties())}
    )

    @JvmField
    val AMETHYST_FOCUS_BOBBER = make(
        "amethyst_focus_bobber", {TideyFocusItem(Item.Properties())}
    )

    @JvmField
    val AMETHYST_FOCUS_BOBBER2 = make(
        "amethyst_focus_bobber2", {TideyFocusItem(Item.Properties())}
    )


    @JvmField
    val AMETHYST_FOCUS_BOBBER3 = make(
        "amethyst_focus_bobber3", {TideyFocusItem(Item.Properties())}
    )

    private abstract class TabEntry {
        abstract fun register(r: CreativeModeTab.Output?)

        class ItemEntry(private val item: Item) : TabEntry() {
            override fun register(r: CreativeModeTab.Output?) {
                r?.accept(item)
            }
        }

        class StackEntry(private val stack: Supplier<ItemStack>) : TabEntry() {
            override fun register(r: CreativeModeTab.Output?) {
                r?.accept(stack.get())
            }
        }
    }

    // Oh my god thank you hexmod for this api I was going to pull my hair out if I had to re-implement
    // the TideFishingHook class and the IotaHolder thingermabob
    class ToTideFishingHookEntity(entity: TideFishingHook) : ItemDelegatingEntityIotaHolder(
        Supplier {
            val bobber = entity.bobber
            if (bobber.item is TideyFocusItem) {
                return@Supplier bobber
            }
            ItemStack.EMPTY
        },
        Consumer { stack: ItemStack? ->
            if (stack!!.item is TideyFocusItem) {
                CustomRodManager.setBobber(
                    HookAccessor.getHook(
                        entity.playerOwner
                    ).rod(),
                    stack
                )
            }
        })

}