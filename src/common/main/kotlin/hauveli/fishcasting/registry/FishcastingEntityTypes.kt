package hauveli.fishcasting.registry

import hauveli.fishcasting.features.blessed.BlessedEntity
import hauveli.fishcasting.common.chair.TackleBoxChairEntity
import hauveli.fishcasting.common.cursed.CursedEntity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory

object FishcastingEntityTypes {
    // Neither of these entities are finished...
    // Provides access to Allay
    @JvmField
    val CURSED: EntityType<CursedEntity?> = EntityType.Builder.of(::CursedEntity, MobCategory.CREATURE)
        .sized(0.6f, 0.35f)
        .clientTrackingRange(8)
        .build("cursed")

    // Provides access to budding amethyst (eventually)
    @JvmField
    val BLESSED: EntityType<BlessedEntity?> = EntityType.Builder.of(::BlessedEntity, MobCategory.CREATURE)
    .sized(5f / 16f, 15f / 16f) // torso width, height minus hat, ish
    .clientTrackingRange(8)
    .eyeHeight(12.5f / 16f) // eye pixel height minus 0.5
    .ridingOffset(-4.5f / 16f) // lower by (height minus 0.5)
    .build("blessed")

    // this one is, though!
    @JvmField
    val TACKLEBOX_CHAIR: EntityType<TackleBoxChairEntity?> =  EntityType.Builder.of(::TackleBoxChairEntity, MobCategory.CREATURE)
    .sized(11.0f / 16.0f, 8.0f / 16.0f) // eyeballing it, todo: put exact values
    .clientTrackingRange(10) // uhh enough to see it before players? idk
    .build("tacklebox_chair")
}