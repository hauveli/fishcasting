package hauveli.fishcasting.casting.recipe.lightning

import at.petrak.hexcasting.common.recipe.RecipeSerializerBase
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.registry.FishcastingRecipeRegistry
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Function


// God I am a horrible person
@JvmRecord
data class StruckByLightningRecipe(
    @JvmField val exchange: StruckByLightningIngredient
) : Recipe<RecipeInput?> {
    fun matches(victim: Entity, level: ServerLevel?): Boolean {
        return this.exchange.test(victim, level)
    }

    override fun getType(): RecipeType<*> {
        return FishcastingRecipeRegistry.LIGHTNING_TYPE
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return FishcastingRecipeRegistry.LIGHTNING
    }

    // in order to get this to be a "Recipe" we need to do a lot of bending-over-backwards
    // to get the implementation to be satisfied even though we never use it
    override fun matches(input: RecipeInput?, level: Level): Boolean {
        return false
    }

    override fun assemble(input: RecipeInput?, registries: HolderLookup.Provider): ItemStack {
        return ItemStack.EMPTY
    }
    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean {
        return false
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return ItemStack.EMPTY.copy()
    }


    class Serializer : RecipeSerializerBase<StruckByLightningRecipe>() {

        override fun codec() = CODEC

        override fun streamCodec() = STREAM_CODEC

        companion object {
            val TYPED_CODEC: Codec<StruckByLightningIngredient> =
                RecordCodecBuilder.create { inst ->
                    inst.group(
                        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityIn")
                            .forGetter(StruckByLightningIngredient::entityIn),

                        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityOut")
                            .forGetter(StruckByLightningIngredient::entityOut)
                    ).apply(inst, ::StruckByLightningIngredient)
                }

            val TYPED_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, StruckByLightningIngredient> =
                StreamCodec.composite(
                    ByteBufCodecs.registry(Registries.ENTITY_TYPE),
                    StruckByLightningIngredient::entityIn,

                    ByteBufCodecs.registry(Registries.ENTITY_TYPE),
                    StruckByLightningIngredient::entityOut,

                    ::StruckByLightningIngredient
                )

            val CODEC: MapCodec<StruckByLightningRecipe> =
                RecordCodecBuilder.mapCodec { inst ->
                    inst.group(
                        TYPED_CODEC
                            .fieldOf("exchange")
                            .forGetter(StruckByLightningRecipe::exchange),

                    ).apply(inst) { exchange ->
                        StruckByLightningRecipe(exchange)
                    }
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, StruckByLightningRecipe> =
                StreamCodec.composite(
                    TYPED_STREAM_CODEC,
                    StruckByLightningRecipe::exchange,

                    ::StruckByLightningRecipe
                )
        }
    }
    
}