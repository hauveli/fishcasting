package hauveli.fishcasting.registry

import hauveli.fishcasting.Fishcasting.id
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.JukeboxSong
import java.util.function.BiConsumer


object FishcastingSounds {
    fun registerSounds(r: BiConsumer<SoundEvent, ResourceLocation>) {
        for (e in SOUNDS.entries) {
            e.value?.let { e.key?.let { p1 -> r.accept(it, p1) } } // why does it have to be nullable?
        }
    }

    val SOUNDS: MutableMap<ResourceLocation?, SoundEvent?> = LinkedHashMap()
    private fun make(name: String): SoundEvent {
        val id = id(name)
        val sound = SoundEvent.createVariableRangeEvent(id)
        val old: Any? = SOUNDS.put(id, sound)
        require(old == null) { "Typo? Duplicate id $name" }
        return sound
    }

    val BLESSED_DEATH: SoundEvent = make("blessed.death")
    val BLESSED_HURT: SoundEvent = make("blessed.hurt")
    val BLESSED_AMBIENT: SoundEvent = make("blessed.ambient")
    val BLESSED_TRADE: SoundEvent = make("blessed.trade")
    val BLESSED_DRINK_MILK: SoundEvent = make("blessed.drink_milk")
    val BLESSED_DRINK_POTION: SoundEvent = make("blessed.drink_potion")
    val BLESSED_YES: SoundEvent = make("blessed.yes")
    val BLESSED_NO: SoundEvent = make("blessed.no")
    val RETURNING_TO_THE_SURFACE: SoundEvent = make("music_disc.returning_to_the_surface")

    @JvmField
    val RETURNING_TO_THE_SURFACE_JUKEBOX: ResourceKey<JukeboxSong?> = ResourceKey.create(
        Registries.JUKEBOX_SONG,
        id("returning_to_the_surface")
    )
}