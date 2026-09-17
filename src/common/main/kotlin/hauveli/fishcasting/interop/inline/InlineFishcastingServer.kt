package hauveli.fishcasting.interop.inline

import com.samsthenerd.inline.api.InlineAPI
import com.samsthenerd.inline.api.client.InlineClientAPI
import hauveli.fishcasting.interop.inline.moon.InlineMoonData
import hauveli.fishcasting.interop.inline.moon.InlineMoonRenderer

object InlineFishcastingServer {
    fun init() {
        InlineAPI.INSTANCE.addDataType(InlineMoonData.InlineMoonDataType.INSTANCE)
    }
}