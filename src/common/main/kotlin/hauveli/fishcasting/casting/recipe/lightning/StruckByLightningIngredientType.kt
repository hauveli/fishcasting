package hauveli.fishcasting.casting.recipe.lightning
import com.mojang.serialization.MapCodec
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec



interface StruckByLightningIngredientType<T : StruckByLightningIngredient?> {
    fun codec(): MapCodec<T?>?

    fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf?, T?>?
}