package hauveli.fishcasting.registry
import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.common.recipe.BrainsweepRecipe
import at.petrak.hexcasting.common.recipe.SealThingsRecipe
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.recipe.lightning.StruckByLightningRecipe
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import java.util.function.BiConsumer

// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/common/recipe/HexRecipeStuffRegistry.java
object FishcastingRecipeRegistry {
    fun registerSerializers(r: BiConsumer<RecipeSerializer<*>, ResourceLocation>) {
        for (e in SERIALIZERS.entries) {
            r.accept(e.value, e.key)
        }
    }

    fun registerTypes(r: BiConsumer<RecipeType<*>, ResourceLocation>) {
        for (e in TYPES.entries) {
            r.accept(e.value, e.key)
        }
    }

    private val SERIALIZERS: MutableMap<ResourceLocation, RecipeSerializer<*>> = LinkedHashMap()
    private val TYPES: MutableMap<ResourceLocation, RecipeType<*>> = LinkedHashMap()

    val LIGHTNING: RecipeSerializer<*> = registerSerializer(
        "struck_by_lightning",
        StruckByLightningRecipe.Serializer()
    )

    var LIGHTNING_TYPE: RecipeType<StruckByLightningRecipe> = registerType("struck_by_lightning_entity_types")

    private fun <T : Recipe<*>?> registerSerializer(name: String, rs: RecipeSerializer<T>): RecipeSerializer<T> {
        val old = SERIALIZERS.put(Fishcasting.id(name), rs)
        require(old == null) { "Typo? Duplicate id $name" }
        return rs
    }

    private fun <T : Recipe<*>> registerType(name: String): RecipeType<T> {
        val type: RecipeType<T> = object : RecipeType<T> {
            override fun toString(): String {
                return Fishcasting.MODID + ":" + name
            }
        }
        // never will be a collision because it's a new object
        TYPES[Fishcasting.id(name)] = type
        return type
    }
}