package hauveli.fishcasting.registry

import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.casting.recipe.lightning.StruckByLightningRecipe
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/common/recipe/HexRecipeStuffRegistry.java
object FishcastingRecipeTypes : FishcastingRegistrar<RecipeType<*>>(
    BuiltInRegistries.RECIPE_TYPE.key() as ResourceKey<Registry<RecipeType<*>>>,
    { BuiltInRegistries.RECIPE_TYPE }
)  {
    private val TYPES: MutableMap<ResourceLocation, RecipeType<*>> = LinkedHashMap()

    var LIGHTNING: FishcastingRegistrar<RecipeType<*>>.Entry<RecipeType<StruckByLightningRecipe>> =
        registerType("struck_by_lightning_entity_types")

    private fun <T : Recipe<*>> registerType(name: String): FishcastingRegistrar<RecipeType<*>>.Entry<RecipeType<T>> {
        val type: RecipeType<T> = object : RecipeType<T> {
            override fun toString(): String =
                "${Fishcasting.MODID}:$name"
        }
        // never will be a collision because it's a new object, I think HexMod was doing other stuff where it might have been more important to check
        TYPES[id(name)] = type

        return make(name) { type }
    }

    private fun <T : RecipeType<*>> make(name: String, builder: () -> T): FishcastingRegistrar<RecipeType<*>>.Entry<T> =
        register(id(name), builder)
}