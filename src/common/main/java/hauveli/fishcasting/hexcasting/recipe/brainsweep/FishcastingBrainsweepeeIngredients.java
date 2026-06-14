package hauveli.fishcasting.hexcasting.recipe.brainsweep;

import at.petrak.hexcasting.common.lib.HexBrainsweepeeIngredients;
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredientType;
import hauveli.fishcasting.Fishcasting;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class FishcastingBrainsweepeeIngredients extends HexBrainsweepeeIngredients {
    public static final BrainsweepeeIngredientType<TaggedEntityTypeIngredient> TAGGED_ENTITY_TYPE = new TaggedEntityTypeIngredient.Type();
    public static final BrainsweepeeIngredientType<HeightRangeEntityTypeIngredient> HEIGHT_RANGE_ENTITY_TYPE = new HeightRangeEntityTypeIngredient.Type();
    public static final BrainsweepeeIngredientType<BorderTypeEntityTypeIngredient> BORDER_TYPE_ENTITY_TYPE = new BorderTypeEntityTypeIngredient.Type();
    public static final BrainsweepeeIngredientType<FishingVoidEntityTypeIngredient> FISHING_VOID_ENTITY_TYPE = new FishingVoidEntityTypeIngredient.Type();

    public static void register(BiConsumer<BrainsweepeeIngredientType<?>, ResourceLocation> r) {
        r.accept(TAGGED_ENTITY_TYPE, Fishcasting.id("tagged_entity_type"));
        r.accept(HEIGHT_RANGE_ENTITY_TYPE, Fishcasting.id("height_range_entity_type"));
        r.accept(BORDER_TYPE_ENTITY_TYPE, Fishcasting.id("border_type_entity_type"));
        r.accept(FISHING_VOID_ENTITY_TYPE, Fishcasting.id("fishing_void_entity_type"));
    }
}
