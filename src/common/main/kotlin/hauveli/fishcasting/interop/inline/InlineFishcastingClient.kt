package hauveli.fishcasting.interop.inline

import com.samsthenerd.inline.api.client.InlineClientAPI
import hauveli.fishcasting.interop.inline.moon.InlineMoonRenderer

object InlineFishcastingClient {
    fun init() {
        InlineClientAPI.INSTANCE.addRenderer(InlineMoonRenderer.INSTANCE)
    }
}