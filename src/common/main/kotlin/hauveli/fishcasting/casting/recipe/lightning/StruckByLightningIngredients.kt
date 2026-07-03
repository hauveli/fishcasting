package hauveli.fishcasting.casting.recipe.lightning
import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.*
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.registry.FishcastingRecipeRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.entity.npc.VillagerType
import net.minecraft.world.level.Level
import java.util.function.BiConsumer
import java.util.function.Function
import java.util.function.Supplier
object StruckByLightningIngredients {

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
}