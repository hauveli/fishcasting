package hauveli.fishcasting.registry

import hauveli.fishcasting.Fishcasting.id
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

object FishcastingTags {

    @JvmField
    var CURSED_MOSTLY_INDESTRUCTIBLE_ITEM: TagKey<Item?> = make("cursed_mostly_indestructible_item")
    @JvmField
    val NO_ENTITY_COLLISION_HOOK: TagKey<Item?> = make("hookless_hooks")
    @JvmField
    val MOB_PACIFYING_LINES: TagKey<Item?> = make("loud_lines")
    @JvmField
    val LUCK_TWEAKING_BOBBERS: TagKey<Item?> = make("blessed_bobbers")
    @JvmField
    val SLIMY_BOBBERS: TagKey<Item?> = make("slimy_bobbers")


    @JvmField
    val END_FISHING_RODS: TagKey<Item?> = make("end_fishing_rods")
    @JvmField
    val LUCK_REDUCING_RODS: TagKey<Item?> = make("luck_reducing_rods")

    val EDIFIED_TREES: TagKey<Block?> = makeB("edified_trees")
    val MUSIC_DISCS_FROM_FISHING: TagKey<Item?> = make("fishy_music_discs")
    val NO_DURABILITY_ENCHANTMENTS: TagKey<Item?> = make("no_durability_enchantments")
    val LORE_FRAGMENTS: TagKey<Item?> = make("lore_fragments")
    val UNLUCKY_MULCH: TagKey<Item?> = make("unlucky_mulch")
    @JvmField
    val ALL_FISHING_RODS_ADVANCEMENT_ROD: TagKey<Item?> = make("all_fishing_rods_advancement_rod")

    fun make(path: String): TagKey<Item?> {
        return TagKey.create(Registries.ITEM, id(path))
    }

    // what the fuck is the point of all the syntactic sugar if the compiler can't tell that TagKey<?> may be used if the type of the variable matches
    fun makeB(path: String): TagKey<Block?> {
        return TagKey.create(Registries.BLOCK, id(path))
    }
}