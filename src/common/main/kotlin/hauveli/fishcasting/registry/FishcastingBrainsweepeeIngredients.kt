package hauveli.fishcasting.registry

import at.petrak.hexcasting.common.lib.HexBrainsweepeeIngredients
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredientType
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.casting.recipe.brainsweep.BorderTypeEntityTypeIngredient
import hauveli.fishcasting.casting.recipe.brainsweep.FishingVoidEntityTypeIngredient
import hauveli.fishcasting.casting.recipe.brainsweep.HeightRangeEntityTypeIngredient
import hauveli.fishcasting.casting.recipe.brainsweep.TaggedEntityTypeIngredient
import net.minecraft.resources.ResourceLocation
import java.util.function.BiConsumer


object FishcastingBrainsweepeeIngredients : HexBrainsweepeeIngredients() {
    val TAGGED_ENTITY_TYPE: BrainsweepeeIngredientType<TaggedEntityTypeIngredient?> = TaggedEntityTypeIngredient.Type()
    val HEIGHT_RANGE_ENTITY_TYPE: BrainsweepeeIngredientType<HeightRangeEntityTypeIngredient?> =
        HeightRangeEntityTypeIngredient.Type()
    val BORDER_TYPE_ENTITY_TYPE: BrainsweepeeIngredientType<BorderTypeEntityTypeIngredient?> =
        BorderTypeEntityTypeIngredient.Type()
    val FISHING_VOID_ENTITY_TYPE: BrainsweepeeIngredientType<FishingVoidEntityTypeIngredient?> =
        FishingVoidEntityTypeIngredient.Type()

    @JvmStatic
    fun registerBrainsweepeeIngredients(r: BiConsumer<BrainsweepeeIngredientType<*>, ResourceLocation>) {
        r.accept(TAGGED_ENTITY_TYPE, id("tagged_entity_type"))
        r.accept(HEIGHT_RANGE_ENTITY_TYPE, id("height_range_entity_type"))
        r.accept(BORDER_TYPE_ENTITY_TYPE, id("border_type_entity_type"))
        r.accept(FISHING_VOID_ENTITY_TYPE, id("fishing_void_entity_type"))
    }
}
