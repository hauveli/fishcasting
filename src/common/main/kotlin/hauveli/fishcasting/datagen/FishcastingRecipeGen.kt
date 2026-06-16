package hauveli.fishcasting.datagen

import at.petrak.hexcasting.api.advancements.HexAdvancementTriggers
import at.petrak.hexcasting.api.advancements.OvercastTrigger
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.lib.HexStateIngredients
import at.petrak.hexcasting.datagen.HexAdvancements
import at.petrak.hexcasting.datagen.IXplatConditionsBuilder
import at.petrak.hexcasting.datagen.IXplatIngredients
import at.petrak.hexcasting.datagen.recipe.HexplatRecipes
import at.petrak.hexcasting.datagen.recipe.builders.BrainsweepRecipeBuilder
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.casting.recipe.brainsweep.TaggedEntityTypeIngredient
import hauveli.fishcasting.registry.FishcastingEntities
import net.minecraft.advancements.Criterion
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.CompletableFuture
import java.util.function.Function


// todo: I couldnt figure it out
class FishcastingRecipeGen(
    output: PackOutput?,
    registries: CompletableFuture<HolderLookup.Provider?>?,
    ingredients: IXplatIngredients?,
    conditions: Function<RecipeBuilder?, IXplatConditionsBuilder?>?
) : HexplatRecipes(output, registries, ingredients, conditions) {
    override fun buildRecipes(recipes: RecipeOutput) {
        BrainsweepRecipeBuilder(
            HexStateIngredients.of(Blocks.AMETHYST_BLOCK),
            TaggedEntityTypeIngredient(FishcastingEntities.BLESSED, "fishcasting:yoinked"),
            Blocks.BUDDING_AMETHYST.defaultBlockState(), MediaConstants.CRYSTAL_UNIT * 10
        )
            .unlockedBy(
                "enlightenment",
                Criterion<OvercastTrigger.Instance?>(HexAdvancementTriggers.OVERCAST_TRIGGER, HexAdvancements.ENLIGHTEN)
            )
            .save(recipes, id("budding_amethyst"))
    }
}
