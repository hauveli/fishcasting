package hauveli.fishcasting.common.registries;

import at.petrak.hexcasting.api.addldata.ItemDelegatingEntityIotaHolder;
import com.google.common.base.Suppliers;
import com.li64.tide.data.rods.CustomRodManager;
import com.li64.tide.registries.entities.misc.fishing.HookAccessor;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import com.li64.tide.registries.items.FishingHookItem;
import hauveli.fishcasting.Fishcasting;
import hauveli.fishcasting.common.FishcastingLoreFragment;
import hauveli.fishcasting.common.paraphernalia.LoudFishingLineItem;
import hauveli.fishcasting.common.chair.TackleBoxChairItem;
import hauveli.fishcasting.common.gacha.GachaBottleItem;
import hauveli.fishcasting.common.paraphernalia.HexyRodItem;
import hauveli.fishcasting.common.paraphernalia.TideyFocusItem;
import hauveli.fishcasting.registry.FishcastingCreativeTabs;
import hauveli.fishcasting.registry.FishcastingEntities;
import hauveli.fishcasting.registry.FishcastingSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static hauveli.fishcasting.Fishcasting.id;

public class FishcastingItems {

    public static void registerItems(BiConsumer<Item, ResourceLocation> r) {
        for (var e : ITEMS.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    public static void registerItemCreativeTab(CreativeModeTab.Output r, CreativeModeTab tab) {
        for (var item : ITEM_TABS.getOrDefault(tab, Collections.emptyList())) {
            item.register(r);
        }
    }

    private static final Map<ResourceLocation, Item> ITEMS = new LinkedHashMap<>(); // preserve insertion order
    private static final Map<CreativeModeTab, List<TabEntry>> ITEM_TABS = new LinkedHashMap<>();

    private static <T extends Item> T make(ResourceLocation id, T item, @Nullable CreativeModeTab tab) {
        var old = ITEMS.put(id, item);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + id);
        }
        if (tab != null) {
            ITEM_TABS.computeIfAbsent(tab, t -> new ArrayList<>()).add(new TabEntry.ItemEntry(item));
        }
        return item;
    }

    private static <T extends Item> T make(String name, T item, @Nullable CreativeModeTab tab) {
        return make(id(name), item, tab);
    }

    private static <T extends Item> T make(String name, T item) {
        return make(id(name), item, FishcastingCreativeTabs.INSTANCE.getFISHCASTING());
    }

    private static Supplier<ItemStack> addToTab(Supplier<ItemStack> stack, CreativeModeTab tab) {
        var memoised = Suppliers.memoize(stack::get);
        ITEM_TABS.computeIfAbsent(tab, t -> new ArrayList<>()).add(new TabEntry.StackEntry(memoised));
        return memoised;
    }

    private static abstract class TabEntry {
        abstract void register(CreativeModeTab.Output r);

        static class ItemEntry extends TabEntry {
            private final Item item;

            ItemEntry(Item item) {
                this.item = item;
            }

            @Override
            void register(CreativeModeTab.Output r) {
                r.accept(item);
            }
        }

        static class StackEntry extends TabEntry {
            private final Supplier<ItemStack> stack;

            StackEntry(Supplier<ItemStack> stack) {
                this.stack = stack;
            }

            @Override
            void register(CreativeModeTab.Output r) {
                r.accept(stack.get());
            }
        }
    }

    public static Item.Properties props() {
        return new Item.Properties();
    }

    public static Item.Properties unstackable() {
        return props().stacksTo(1);
    }

    public static Item.Properties unstackableUncommon() {
        return unstackable().rarity(Rarity.UNCOMMON);
    }

    public static Item newItem() {
        return new Item(props());
    }

    // Oh my god thank you hexmod for this api I was going to pull my hair out if I had to re-implement
    // the TideFishingHook class and the IotaHolder thingermabob
    public static class ToTideFishingHookEntity extends ItemDelegatingEntityIotaHolder {
        public ToTideFishingHookEntity(TideFishingHook entity) {
            super(() -> {
                        ItemStack bobber = entity.getBobber();
                        if (bobber.getItem() instanceof TideyFocusItem) {
                            return bobber;
                        }
                        return ItemStack.EMPTY;
                    },
                    stack -> {
                        if (stack.getItem() instanceof TideyFocusItem) {
                            CustomRodManager.setBobber(
                                    HookAccessor.getHook(
                                            entity.getPlayerOwner()
                                    ).rod(),
                                    stack
                            );
                        }
                    });
        }
    }

    public static final HexyRodItem SHEPHERDS_CASTING_ROD = make("shepherds_casting_rod", new HexyRodItem(3, 0, unstackableUncommon()));
    public static final TideyFocusItem BLESSED_FOCUS_BOBBER = make("blessed_focus_bobber", new TideyFocusItem(unstackableUncommon()));
    public static final Item UNLUCKY_BAIT = make("unlucky_bait", newItem());
    public static final LoudFishingLineItem LOUD_FISHING_LINE = make("loud_fishing_line", new LoudFishingLineItem(props()));
    public static final FishingHookItem HOOKLESS_FISHING_HOOK = make("hookless_fishing_hook", new FishingHookItem(props(),"item.fishcasting.hookless_fishing_hook.desc")); // tide does this
    public static final Item DISC = make("music_disc_returning_to_the_surface", new Item(unstackableUncommon()
            .jukeboxPlayable(FishcastingSounds.RETURNING_TO_THE_SURFACE_JUKEBOX)));
    public static final Item CURSED = make("cursed", new Item(props().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final MobBucketItem CURSED_BUCKET = make("cursed_bucket", new MobBucketItem(
            FishcastingEntities.CURSED, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, unstackableUncommon()));
    public static final SpawnEggItem CURSED_SPAWN_EGG = make("cursed_spawn_egg", new SpawnEggItem(
            FishcastingEntities.CURSED, 16499171, 10890612, (new SpawnEggItem.Properties()))); // from axolotl thingy
    public static final SpawnEggItem BLESSED_SPAWN_EGG = make("blessed_spawn_egg", new SpawnEggItem(
            FishcastingEntities.BLESSED, 9433559, 7969893, (new SpawnEggItem.Properties()))); // from drowned thingy
    public static final Item MESSAGE_IN_A_BOTTLE = make("message_in_a_bottle", new GachaBottleItem(unstackableUncommon()));
    public static final Item TACKLEBOX_CHAIR = make("tacklebox_chair", new TackleBoxChairItem(unstackable()));
    public static final Item GLASS_SHARD = make("glass_shard", new Item(props().stacksTo(16)));
    public static final Item FISHCASTING_LORE_FRAGMENT = make("fishy_fragment", new FishcastingLoreFragment(unstackable().rarity(Rarity.RARE)));

}