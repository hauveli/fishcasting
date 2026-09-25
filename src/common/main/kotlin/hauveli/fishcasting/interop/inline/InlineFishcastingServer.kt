package hauveli.fishcasting.interop.inline

import com.samsthenerd.inline.api.InlineAPI
import com.samsthenerd.inline.api.client.InlineClientAPI
import hauveli.fishcasting.interop.inline.biome.InlineBiomeData
import hauveli.fishcasting.interop.inline.climate.InlineClimateData
import hauveli.fishcasting.interop.inline.depth.InlineDepthData
import hauveli.fishcasting.interop.inline.dimension.InlineDimensionData
import hauveli.fishcasting.interop.inline.medium.InlineMediumData
import hauveli.fishcasting.interop.inline.moon.InlineMoonData
import hauveli.fishcasting.interop.inline.moon.InlineMoonRenderer
import hauveli.fishcasting.interop.inline.structure.InlineStructureData
import hauveli.fishcasting.interop.inline.weather.InlineWeatherData

object InlineFishcastingServer {
    fun init() {
        InlineAPI.INSTANCE.addDataType(InlineMoonData.InlineMoonDataType.INSTANCE)
        InlineAPI.INSTANCE.addDataType(InlineWeatherData.InlineWeatherDataType.INSTANCE)
        InlineAPI.INSTANCE.addDataType(InlineMediumData.InlineMediumDataType.INSTANCE)
        InlineAPI.INSTANCE.addDataType(InlineDimensionData.InlineDimensionDataType.INSTANCE)
        InlineAPI.INSTANCE.addDataType(InlineBiomeData.InlineBiomeDataType.INSTANCE)
        InlineAPI.INSTANCE.addDataType(InlineStructureData.InlineStructureDataType.INSTANCE)
        InlineAPI.INSTANCE.addDataType(InlineDepthData.InlineDepthDataType.INSTANCE)
        InlineAPI.INSTANCE.addDataType(InlineClimateData.InlineClimateDataType.INSTANCE)
    }
}