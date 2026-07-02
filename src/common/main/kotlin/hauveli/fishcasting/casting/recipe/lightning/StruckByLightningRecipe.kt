package hauveli.fishcasting.casting.recipe.lightning

import at.petrak.hexcasting.common.lib.HexBrainsweepeeIngredients
import at.petrak.hexcasting.common.lib.HexStateIngredients
import at.petrak.hexcasting.common.recipe.HexRecipeStuffRegistry
import at.petrak.hexcasting.common.recipe.RecipeSerializerBase
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredient
import at.petrak.hexcasting.common.recipe.ingredient.state.StateIngredient
import com.mojang.datafixers.kinds.App
import com.mojang.datafixers.util.Function4
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.registry.FishcastingRecipeRegistry
import net.minecraft.core.HolderLookup
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
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import java.util.function.BiFunction
import java.util.function.Function


// God I am a horrible person
@JvmRecord
data class StruckByLightningRecipe(
    val entityIn: StruckByLightningIngredient,
    val mediaCost: Long
) : Recipe<RecipeInput?> {
    fun matches(victim: Entity, level: ServerLevel?): Boolean {
        return this.entityIn.test(victim, level)
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
        override fun codec(): MapCodec<StruckByLightningRecipe> {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, StruckByLightningRecipe> {
            return STREAM_CODEC
        }

        companion object {
            var CODEC: MapCodec<StruckByLightningRecipe> =
                RecordCodecBuilder.mapCodec(Function { inst: RecordCodecBuilder.Instance<StruckByLightningRecipe> ->
                    inst.group(
                        HexBrainsweepeeIngredients.TYPED_CODEC.fieldOf("entityIn")
                            .forGetter(StruckByLightningRecipe::entityIn),
                        Codec.LONG.fieldOf("cost").forGetter(StruckByLightningRecipe::mediaCost),
                    ).apply<StruckByLightningRecipe>(
                        inst,
                        { entityIn: StruckByLightningIngredient, mediaCost: Long ->
                            StruckByLightningRecipe(
                                entityIn,
                                mediaCost
                            )
                        } as App<RecordCodecBuilder.Mu<StruckByLightningRecipe>, BiFunction<BrainsweepeeIngredient, Long, StruckByLightningRecipe>>)
                }
                )
            var STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, StruckByLightningRecipe> =
                StreamCodec.composite(
                    HexBrainsweepeeIngredients.TYPED_STREAM_CODEC, StruckByLightningRecipe::entityIn,
                    ByteBufCodecs.VAR_LONG, StruckByLightningRecipe::mediaCost,
                    { entityIn: StruckByLightningIngredient, cost: Long ->
                        StruckByLightningRecipe(
                            entityIn,
                            cost
                        )
                    } as ((BrainsweepeeIngredient, Long) -> StruckByLightningRecipe)
                )
        }
    }
}