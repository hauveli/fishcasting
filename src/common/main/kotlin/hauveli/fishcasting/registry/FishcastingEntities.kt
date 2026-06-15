package hauveli.fishcasting.registry

import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.features.chair.TackleBoxChairEntity
import hauveli.fishcasting.features.fish.CursedEntity
import hauveli.fishcasting.features.trader.BlessedEntity
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import java.util.function.BiConsumer


object FishcastingEntities {
    @JvmStatic
    fun registerEntities(r: BiConsumer<EntityType<*>, ResourceLocation>) {
        for (e in ENTITIES.entries) {
            r.accept(e.value, e.key)
        }
    }

    val ENTITIES: MutableMap<ResourceLocation, EntityType<*>> = LinkedHashMap()

    private fun <T : Entity> register(name: String, type: EntityType<T>): EntityType<T> {
        val old: Any? = ENTITIES.put(id(name), type)
        require(old == null) { "Typo? Duplicate id $name" }
        return type
    }

    /*
        public static final EntityType<CursedEntity> CURSED =
            EntityType.Builder
                    .of(CursedEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.35F)
                    .clientTrackingRange(8)
                    .build("cursed");

    // Provides access to budding amethyst (eventually)
    public static final EntityType<BlessedEntity> BLESSED =
            EntityType.Builder.<BlessedEntity> // guh
                    of(BlessedEntity::new, MobCategory.CREATURE)
                    .sized(5F/16F, 15F/16F) // torso width, height minus hat, ish
                    .clientTrackingRange(8)
                    .eyeHeight(12.5F/16F) // eye pixel height minus 0.5
                    .ridingOffset(-4.5F/16F) // lower by (height minus 0.5)
                    .build("blessed");

    // this one is, though!
    public static final EntityType<TackleBoxChairEntity> TACKLEBOX_CHAIR =
            EntityType.Builder.<TackleBoxChairEntity> // guh
                    of(TackleBoxChairEntity::new, MobCategory.MISC)
                    .sized(11.0F/16.0F, 8.0F/16.0F) // eyeballing it, todo: put exact values
                    .clientTrackingRange(10) // uhh enough to see it before players? idk
                    .build("tacklebox_chair");



    public static final EntityType<CursedEntity> CURSED = register("cursed",
            EntityType.Builder
                    .of(CursedEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.35F)
                    .clientTrackingRange(8)
                    .build("cursed")
    );

    // Provides access to budding amethyst (eventually)
    public static final EntityType<BlessedEntity> BLESSED = register("blessed",
            EntityType.Builder.<BlessedEntity> // guh
                    of(BlessedEntity::new, MobCategory.CREATURE)
                    .sized(5F/16F, 15F/16F) // torso width, height minus hat, ish
                    .clientTrackingRange(8)
                    .eyeHeight(12.5F/16F) // eye pixel height minus 0.5
                    .ridingOffset(-4.5F/16F) // lower by (height minus 0.5)
                    .build("blessed")
    );

    // this one is, though!
    public static final EntityType<TackleBoxChairEntity> TACKLEBOX_CHAIR = register("tacklebox_chair",
            EntityType.Builder.<TackleBoxChairEntity> // guh
                    of(TackleBoxChairEntity::new, MobCategory.MISC)
                    .sized(11.0F/16.0F, 8.0F/16.0F) // eyeballing it, todo: put exact values
                    .clientTrackingRange(10) // uhh enough to see it before players? idk
                    .build("tacklebox_chair")
    );
     */

    // Provides access to All (I fucking forgot what this said so I'm leaving this in case i remember)
    /*
    @JvmField
    val CURSED: EntityType<CursedEntity> = FishcastingEntities.CURSED
    @JvmField
    val BLESSED: EntityType<BlessedEntity> = FishcastingEntities.BLESSED
    @JvmField
    val TACKLEBOX_CHAIR: EntityType<TackleBoxChairEntity> = FishcastingEntities.TACKLEBOX_CHAIR
*/

    @JvmField
    val CURSED: EntityType<CursedEntity> = register(
        "cursed",
        EntityType.Builder
            .of(::CursedEntity, MobCategory.CREATURE)
            .sized(0.6f, 0.35f)
            .clientTrackingRange(8)
            .build("cursed")
    )

    // Provides access to budding amethyst (eventually)
    @JvmField
    val BLESSED: EntityType<BlessedEntity> = register(
        "blessed",
        EntityType.Builder.of(::BlessedEntity,MobCategory.CREATURE)
            .sized(5f / 16f, 15f / 16f) // torso width, height minus hat, ish
            .clientTrackingRange(8)
            .eyeHeight(12.5f / 16f) // eye pixel height minus 0.5
            .ridingOffset(-4.5f / 16f) // lower by (height minus 0.5)
            .build("blessed")
    )

    // this one is, though!
    @JvmField
    val TACKLEBOX_CHAIR: EntityType<TackleBoxChairEntity> = register(
        "tacklebox_chair",
        EntityType.Builder.of(::TackleBoxChairEntity, MobCategory.MISC)
            .sized(11.0f / 16.0f, 8.0f / 16.0f) // eyeballing it, todo: put exact values
            .clientTrackingRange(10) // uhh enough to see it before players? idk
            .build("tacklebox_chair")
    )
    /*
    AHHHHHHHHHH I DONT UNDERSTAND WHY CANT I BUILD IN KOTLIN BUT I CAN IN JAVA AHHHHHHHHHHHHHHHHHHHHHHHHHHH
    i fucking give up this is so stupid I can't even begin to imagine why it works if I build it in java, what the fuck
     */

}