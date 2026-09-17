package hauveli.fishcasting.interop.inline

import com.samsthenerd.inline.api.client.InlineClientAPI
import hauveli.fishcasting.interop.inline.dimension.InlineDimensionRenderer
import hauveli.fishcasting.interop.inline.medium.InlineMediumRenderer
import hauveli.fishcasting.interop.inline.moon.InlineMoonRenderer
import hauveli.fishcasting.interop.inline.weather.InlineWeatherRenderer

object InlineFishcastingClient {
    fun init() {
        InlineClientAPI.INSTANCE.addRenderer(InlineMoonRenderer.INSTANCE)
        InlineClientAPI.INSTANCE.addRenderer(InlineWeatherRenderer.INSTANCE)
        InlineClientAPI.INSTANCE.addRenderer(InlineMediumRenderer.INSTANCE)
        InlineClientAPI.INSTANCE.addRenderer(InlineDimensionRenderer.INSTANCE)
    }
}