package hauveli.fishcasting.registry

import hauveli.fishcasting.Fishcasting.id
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object FishcastingTags {
    @JvmField
    var CURSED_MOSTLY_INDESTRUCTIBLE_ITEM: TagKey<Item?> = make("cursed_mostly_indestructible_item")
    val FUN: TagKey<Item?> = make("artifact_grade_u")
    @JvmField
    val NO_ENTITY_COLLISION_HOOK: TagKey<Item?> = make("hookless_hooks")
    @JvmField
    val MOB_PACIFYING_LINES: TagKey<Item?> = make("loud_lines")
    @JvmField
    val LUCK_TWEAKING_BOBBERS: TagKey<Item?> = make("blessed_bobbers")
    val MUSIC_DISCS_FROM_FISHING: TagKey<Item?> = make("fishy_music_discs")
    val NO_DURABILITY_ENCHANTMENTS: TagKey<Item?> = make("no_durability_enchantments")
    val LORE_FRAGMENTS: TagKey<Item?> = make("lore_fragments")
    val UNLUCKY_MULCH: TagKey<Item?> = make("unlucky_mulch")
    @JvmField
    val ALL_FISHING_RODS_ADVANCEMENT_ROD: TagKey<Item?> = make("all_fishing_rods_advancement_rod")

    fun make(path: String): TagKey<Item?> {
        return TagKey.create(Registries.ITEM, id(path))
    }
}