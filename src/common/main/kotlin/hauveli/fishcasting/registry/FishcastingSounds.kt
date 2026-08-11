package hauveli.fishcasting.registry

import hauveli.fishcasting.Fishcasting.id
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.JukeboxSong
import java.util.function.BiConsumer


object FishcastingSounds : FishcastingRegistrar<SoundEvent>(
    BuiltInRegistries.SOUND_EVENT.key() as ResourceKey<Registry<SoundEvent>>,
    { BuiltInRegistries.SOUND_EVENT }
) {
    data class MusicDiscEntry<T : SoundEvent>(
        val soundEvent: FishcastingRegistrar<SoundEvent>.Entry<T>,
        val jukeboxSong: ResourceKey<JukeboxSong>
    )

    val BLESSED_DEATH = make("blessed.death")
    val BLESSED_HURT = make("blessed.hurt")
    val BLESSED_AMBIENT = make("blessed.ambient")
    val BLESSED_TRADE = make("blessed.trade")
    val BLESSED_DRINK_MILK = make("blessed.drink_milk")
    val BLESSED_DRINK_POTION = make("blessed.drink_potion")
    val BLESSED_YES = make("blessed.yes")
    val BLESSED_NO = make("blessed.no")
    val RETURNING_TO_THE_SURFACE = makeMusicDisc("music_disc/returning_to_the_surface")

    private fun make(name: String): FishcastingRegistrar<SoundEvent>.Entry<SoundEvent> {
        return make(name, {
            SoundEvent.createVariableRangeEvent(id(name))
        })
    }

    private fun <T : SoundEvent> make(name: String, builder: () -> T): FishcastingRegistrar<SoundEvent>.Entry<T> =
        register(id(name), builder)

    private fun makeMusicDisc(name: String): MusicDiscEntry<SoundEvent> {
        val jukeboxSong = ResourceKey.create(
            Registries.JUKEBOX_SONG,
            id(name)
        )
        val soundEvent = make(name) {
            SoundEvent.createVariableRangeEvent(id(name))
        }
        return MusicDiscEntry(soundEvent, jukeboxSong)
    }
}