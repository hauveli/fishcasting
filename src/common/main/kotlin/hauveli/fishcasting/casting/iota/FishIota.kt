package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import com.li64.tide.data.fishing.FishData
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.registry.FishcastingIotaTypes
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import java.util.*
import java.util.function.Function
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
class FishIota : Iota {
    val fishData: Optional<FishData>
    val resourceLocation: ResourceLocation

    constructor(fishData: FishData) : super(Supplier { FishcastingIotaTypes.FISH }) {
        this.fishData = Optional.ofNullable(fishData)
        checkNotNull(fishData)
        val fishActually = fishData.fish().value()
        this.resourceLocation = BuiltInRegistries.ITEM.getKey(fishActually)
    }

    constructor(resourceLocation: ResourceLocation) : super(Supplier { FishcastingIotaTypes.FISH }) {
        this.resourceLocation = resourceLocation
        this.fishData = FishData.get(BuiltInRegistries.ITEM.get(resourceLocation))
    }

    val namespacedId: String
        get() = this.resourceLocation.namespace

    public override fun toleratesOther(that: Iota?): Boolean {
        return typesMatch(this, that)
                && that is FishIota
                && this.fishData === that.fishData
    }

    override fun isTruthy(): Boolean {
        return true
    }

    override fun display(): Component {
        val fish = this.fishData
        if (fish.isPresent) {
            val fishItem = fish.get().fish().value()
            return fishItem.getName(fishItem.getDefaultInstance()).copy().withStyle(ChatFormatting.DARK_BLUE)
        }
        return Component.translatable("fishcasting.spelldata.fish.whoknows")
    }

    override fun hashCode(): Int {
        return resourceLocation.hashCode() // should be fine?
    }

    companion object {
        // TODO: just yoink the entityIota getEntityNameWithInline by creating a fake entity then discarding it hehe...
        private fun getFishNameWithInline(fishData: FishData): Component {
            val fishItem = fishData.fish().value()
            val fishStack = fishItem.getDefaultInstance()
            val baseName = fishItem.getName(fishStack) as MutableComponent
            var inlineEnt: Component?
            //inlineEnt = EntityInlineData.fromType(entity.getType()).asText(false);
            // hmm maybe later
            return baseName.append(Component.literal(": ")).append(fishItem.getTooltipImage(fishStack).get().toString())
        }


        var TYPE: IotaType<FishIota> = object : IotaType<FishIota>() {
            val CODEC: MapCodec<FishIota> =
                RecordCodecBuilder.mapCodec(Function { inst: RecordCodecBuilder.Instance<FishIota> ->
                    inst.group(
                        ResourceLocation.CODEC.fieldOf("namespacedId")
                            .forGetter({ obj: FishIota -> obj.resourceLocation })
                    ).apply(
                        inst,
                        { resourceLocation: ResourceLocation -> FishIota(resourceLocation) })
                }
                )
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, FishIota> =
                StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC,
                    { obj: FishIota -> obj.resourceLocation },
                    { resourceLocation: ResourceLocation -> FishIota(resourceLocation) }
                )

            override fun validate(iota: FishIota?, level: ServerLevel): Boolean {
                val fish = iota!!.fishData
                return fish.isPresent
            }

            override fun codec(): MapCodec<FishIota> {
                return CODEC
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, FishIota> {
                return STREAM_CODEC
            }

            override fun color(): Int {
                return -0x96bdf1
            }
        }
    }
}