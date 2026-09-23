package hauveli.fishcasting.casting.iota

import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.styledWith
import com.li64.tide.data.fishing.conditions.types.WeatherType
import com.li64.tide.data.fishing.mediums.FishingMedium
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.interop.inline.dimension.InlineDimensionData
import hauveli.fishcasting.interop.inline.medium.InlineMediumData
import hauveli.fishcasting.interop.inline.structure.InlineStructureData
import hauveli.fishcasting.interop.inline.weather.InlineWeatherData
import hauveli.fishcasting.registry.FishcastingIotaTypes
import me.fzzyhmstrs.fzzy_config.util.FcText.translation
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.dimension.DimensionType
import java.util.Locale
import java.util.function.Supplier


// https://github.com/SuperKnux/HexMod/blob/indev/1.21.1/Common/src/main/java/at/petrak/hexcasting/api/casting/iota/EntityIota.java
// https://github.com/Lightning-64/Tide-2/blob/main/src/main/java/com/li64/tide/util/MoonPhases.java
class DepthIota : DoubleIota {
    val value: Int

    constructor(yLevel: Int) : super(yLevel.toDouble()) {
        // sure, you could put a number outside of 0,7 in here, but that's not going to happen unless somebody does something silly
        this.value = yLevel
    }

    override fun toleratesOther(that: Iota?): Boolean {
        return typesMatch(this, that)
                && that is DepthIota
                && this.value == that.value
    }

    override fun isTruthy(): Boolean {
        return true
    }

    final val SHORTNAME = "depth"

    // NOTE TO SELF!!!! ONLY CALL THIS ON THE CLIENT
    fun getDepthAlias(): String {
        return "placeholder"
    }

    fun getName(): String {
        return "${Fishcasting.MODID}.environment.$SHORTNAME"
    }
    fun getNameWithFallback(): MutableComponent {
        val comp = getName().asTranslatedComponent.append(": {}m".format(value))
        // what even sets this... does translation set it forever? why?
        return comp.withStyle(comp.style.withItalic(false))
    }

    fun getNameWithColon(): MutableComponent {
        return getNameWithFallback().append(": ")
    }

    override fun equals(iotaToCompare: Any?): Boolean {
        if (this === iotaToCompare) return true
        if (javaClass != iotaToCompare?.javaClass) return false
        if (!super.equals(iotaToCompare)) return false

        iotaToCompare as DepthIota

        return value == iotaToCompare.value
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + value
        result = 31 * result + SHORTNAME.hashCode()
        return result
    }
}