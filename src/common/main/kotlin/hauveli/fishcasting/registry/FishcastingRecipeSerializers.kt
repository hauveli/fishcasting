package hauveli.fishcasting.registry
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.casting.recipe.lightning.StruckByLightningRecipe
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import java.util.function.BiConsumer

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/common/recipe/HexRecipeStuffRegistry.java
object FishcastingRecipeSerializers : FishcastingRegistrar<RecipeSerializer<*>>(
    BuiltInRegistries.RECIPE_SERIALIZER.key() as ResourceKey<Registry<RecipeSerializer<*>>>,
    { BuiltInRegistries.RECIPE_SERIALIZER }
)  {
    private val SERIALIZERS: MutableMap<ResourceLocation, RecipeSerializer<*>> = LinkedHashMap()

    val LIGHTNING = registerSerializer(
        "struck_by_lightning",
        StruckByLightningRecipe.Serializer()
    )

    private fun <T : Recipe<*>> registerSerializer(name: String, rs: RecipeSerializer<T>): FishcastingRegistrar<RecipeSerializer<*>>.Entry<RecipeSerializer<T>> {
        val old = SERIALIZERS.put(Fishcasting.id(name), rs)
        require(old == null) { "Typo? Duplicate id $name" }

        return make(name) { rs }
    }

    private fun <T : RecipeSerializer<*>> make(name: String, builder: () -> T): FishcastingRegistrar<RecipeSerializer<*>>.Entry<T> =
        register(id(name), builder)
}