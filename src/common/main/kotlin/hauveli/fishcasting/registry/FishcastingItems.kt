package hauveli.fishcasting.registry

import at.petrak.hexcasting.api.addldata.ItemDelegatingEntityIotaHolder
import com.google.common.base.Suppliers
import com.li64.tide.data.rods.CustomRodManager
import com.li64.tide.registries.entities.misc.fishing.HookAccessor
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import com.li64.tide.registries.items.FishingHookItem
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.common.FishcastingLoreFragment
import hauveli.fishcasting.features.chair.TackleBoxChairItem
import hauveli.fishcasting.features.gacha.GachaBottleItem
import hauveli.fishcasting.features.paraphernalia.HexyRodItem
import hauveli.fishcasting.features.paraphernalia.LoudFishingLineItem
import hauveli.fishcasting.features.paraphernalia.TideyFocusItem
import hauveli.fishcasting.registry.FishcastingCreativeTabs.FISHCASTING
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents.BUCKET_EMPTY_FISH
import net.minecraft.world.item.*
import net.minecraft.world.level.material.Fluids.WATER
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier


object FishcastingItems {
    @JvmStatic
    fun registerItems(r: BiConsumer<Item, ResourceLocation>) {
        for (e in ITEMS.entries) {
            r.accept(e.value, e.key)
        }
    }

    @JvmStatic
    fun registerItemCreativeTab(r: CreativeModeTab.Output, tab: CreativeModeTab) {
        for (item in ITEM_TABS.getOrDefault(tab, mutableListOf<TabEntry?>())) {
            item!!.register(r)
        }
    }

    private val ITEMS: MutableMap<ResourceLocation, Item> =
        LinkedHashMap() // preserve insertion order
    private val ITEM_TABS: MutableMap<CreativeModeTab, MutableList<TabEntry>> =
        LinkedHashMap<CreativeModeTab, MutableList<TabEntry>>()

    private fun <T : Item> make(id: ResourceLocation, item: T, tab: CreativeModeTab): T {
        val old = ITEMS.put(id, item)
        require(old == null) { "Typo? Duplicate id $id" }
        ITEM_TABS.computeIfAbsent(tab) { t: CreativeModeTab -> ArrayList() }
            .add(TabEntry.ItemEntry(item))
        return item
    }

    private fun <T : Item> make(name: String, item: T, tab: CreativeModeTab): T {
        return make(id(name), item, tab)
    }

    private fun <T : Item> make(name: String, item: T): T {
        return make(id(name), item, FISHCASTING)
    }

    private fun addToTab(stack: Supplier<ItemStack>, tab: CreativeModeTab): Supplier<ItemStack?> {
        val memoised = Suppliers.memoize({ stack.get() })
        ITEM_TABS.computeIfAbsent(tab) { t: CreativeModeTab -> ArrayList() }
            .add(TabEntry.StackEntry(memoised))
        return memoised
    }

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

    @JvmField
    val SHEPHERDS_CASTING_ROD =
        make("shepherds_casting_rod", HexyRodItem(3, 0.0, unstackableUncommon()))

    @JvmField
    val BLESSED_FOCUS_BOBBER = make(
        "blessed_focus_bobber", TideyFocusItem(
            unstackableUncommon()
        )
    )
    val UNLUCKY_BAIT = make("unlucky_bait", newItem())
    val LOUD_FISHING_LINE = make(
        "loud_fishing_line", LoudFishingLineItem(
            props()
        )
    )
    val HOOKLESS_FISHING_HOOK = make(
        "hookless_fishing_hook",
        FishingHookItem(props(), "item.fishcasting.hookless_fishing_hook.desc")
    ) // tide does this
    val DISC = make(
        "music_disc_returning_to_the_surface", Item(
            unstackableUncommon()
                .jukeboxPlayable(FishcastingSounds.RETURNING_TO_THE_SURFACE_JUKEBOX)
        )
    )
    @JvmField
    val CURSED = make("cursed", Item(props().rarity(Rarity.UNCOMMON).fireResistant()))
    @JvmField
    val CURSED_BUCKET = make(
        "cursed_bucket", MobBucketItem(
            FishcastingEntities.CURSED,
            WATER,
            BUCKET_EMPTY_FISH,
            unstackableUncommon()
        )
    )
    val CURSED_SPAWN_EGG = make(
        "cursed_spawn_egg", SpawnEggItem(
            FishcastingEntities.CURSED, 16499171, 10890612, (Item.Properties())
        )
    ) // from axolotl thingy
    val BLESSED_SPAWN_EGG = make(
        "blessed_spawn_egg", SpawnEggItem(
            FishcastingEntities.BLESSED, 9433559, 7969893, (Item.Properties())
        )
    ) // from drowned thingy
    val MESSAGE_IN_A_BOTTLE =
        make("message_in_a_bottle", GachaBottleItem(unstackableUncommon()))
    val TACKLEBOX_CHAIR = make("tacklebox_chair", TackleBoxChairItem(unstackable()))
    val GLASS_SHARD = make("glass_shard", Item(props().stacksTo(16)))
    val FISHCASTING_LORE_FRAGMENT = make(
        "fishy_fragment", FishcastingLoreFragment(
            unstackable().rarity(Rarity.RARE)
        )
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