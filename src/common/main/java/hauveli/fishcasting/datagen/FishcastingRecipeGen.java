package hauveli.fishcasting.datagen;

import at.petrak.hexcasting.api.advancements.HexAdvancementTriggers;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.lib.HexStateIngredients;
import at.petrak.hexcasting.datagen.HexAdvancements;
import at.petrak.hexcasting.datagen.IXplatConditionsBuilder;
import at.petrak.hexcasting.datagen.IXplatIngredients;
import at.petrak.hexcasting.datagen.recipe.HexplatRecipes;
import at.petrak.hexcasting.datagen.recipe.builders.BrainsweepRecipeBuilder;
import hauveli.fishcasting.Fishcasting;
import hauveli.fishcasting.registry.FishcastingEntityTypes;
import hauveli.fishcasting.hexcasting.recipe.brainsweep.TaggedEntityTypeIngredient;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;


// todo: I couldnt figure it out
public class FishcastingRecipeGen extends HexplatRecipes {

    public FishcastingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, IXplatIngredients ingredients, Function<RecipeBuilder, IXplatConditionsBuilder> conditions) {
        super(output, registries, ingredients, conditions);
    }


    @Override
    public void buildRecipes(RecipeOutput recipes) {
        new BrainsweepRecipeBuilder(HexStateIngredients.of(Blocks.AMETHYST_BLOCK),
            new TaggedEntityTypeIngredient(FishcastingEntityTypes.BLESSED,"fishcasting:yoinked"),
            Blocks.BUDDING_AMETHYST.defaultBlockState(), MediaConstants.CRYSTAL_UNIT * 10)
            .unlockedBy("enlightenment", new Criterion<>(HexAdvancementTriggers.OVERCAST_TRIGGER, HexAdvancements.ENLIGHTEN))
            .save(recipes, Fishcasting.id("budding_amethyst"));
    }
}
