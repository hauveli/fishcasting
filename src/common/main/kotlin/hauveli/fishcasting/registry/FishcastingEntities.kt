package hauveli.fishcasting.registry

import com.li64.tide.registries.TideEntityAttributes
import hauveli.fishcasting.Fishcasting.id
import hauveli.fishcasting.features.chair.TackleBoxChairEntity
import hauveli.fishcasting.features.fish.CursedEntity
import hauveli.fishcasting.features.trader.BlessedEntity
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import org.apache.http.client.entity.EntityBuilder
import java.util.function.BiConsumer


object FishcastingEntities : FishcastingRegistrar<EntityType<*>>(
    BuiltInRegistries.ENTITY_TYPE.key() as ResourceKey<Registry<EntityType<*>>>,
    { BuiltInRegistries.ENTITY_TYPE }
)  {
    val ENTITIES: MutableMap<ResourceLocation, EntityType<*>> = LinkedHashMap()

    private fun <T : Entity> registerEntity(name: String, builder: () -> EntityType.Builder<T>): FishcastingRegistrar<EntityType<*>>.Entry<EntityType<T>> {

        return make(name) {
            val key = id(name)
            val built = builder().build(name)
            val old = ENTITIES.put(key, built)
            require(old == null) { "Typo? Duplicate id $name" }

            return@make built
        }
    }

    // : EntityType<CursedEntity>
    // @JvmField
    @JvmField
    val CURSED = registerEntity(
        "cursed", {
        EntityType.Builder.of(::CursedEntity, MobCategory.CREATURE)
            .sized(0.6f, 0.35f)
            .clientTrackingRange(8)
        }
    )

    // Provides access to budding amethyst (eventually)
    // @JvmField
    // : EntityType<BlessedEntity>
    val BLESSED = registerEntity(
        "blessed", {
        EntityType.Builder.of(::BlessedEntity,MobCategory.CREATURE)
            .sized(5f / 16f, 15f / 16f) // torso width, height minus hat, ish
            .clientTrackingRange(8)
            .eyeHeight(12.5f / 16f) // eye pixel height minus 0.5
            .ridingOffset(-4.5f / 16f) // lower by (height minus 0.5)
        }
    )

    // this one is, though!
    // @JvmField : EntityType<TackleBoxChairEntity>
    val TACKLEBOX_CHAIR = registerEntity(
        "tacklebox_chair", {
            EntityType.Builder.of(::TackleBoxChairEntity, MobCategory.MISC)
                .sized(11.0f / 16.0f, 8.0f / 16.0f) // eyeballing it, todo: put exact values
                .clientTrackingRange(10) // uhh enough to see it before players? idk
        }
    )

    private fun <T : EntityType<*>> make(name: String, builder: () -> T): FishcastingRegistrar<EntityType<*>>.Entry<T> =
        register(id(name), builder)
}