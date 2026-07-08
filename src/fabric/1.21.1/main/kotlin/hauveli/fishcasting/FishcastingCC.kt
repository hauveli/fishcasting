package hauveli.fishcasting

import at.petrak.hexcasting.api.HexAPI.modLoc
import at.petrak.hexcasting.api.addldata.ItemDelegatingEntityIotaHolder
import at.petrak.hexcasting.common.lib.HexDataComponents
import at.petrak.hexcasting.fabric.cc.*
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents.IOTA_HOLDER
import at.petrak.hexcasting.fabric.cc.adimpl.CCEntityIotaHolder
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook
import hauveli.fishcasting.registry.FishcastingItems
import net.minecraft.world.entity.Entity
import org.ladysnake.cca.api.v3.component.ComponentFactory
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer
import org.ladysnake.cca.api.v3.item.ItemComponentInitializer
import org.ladysnake.cca.api.v3.item.ItemComponentMigrationRegistry
import java.util.function.Function


object FishcastingCC : EntityComponentInitializer, ItemComponentInitializer {

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerFor(
            TideFishingHook::class.java,
            IOTA_HOLDER,
            wrapItemEntityDelegatePublic({hook: TideFishingHook -> FishcastingItems.ToTideFishingHookEntity(hook)})
        )
    }

    fun <E : Entity?> wrapItemEntityDelegatePublic(make: Function<E, ItemDelegatingEntityIotaHolder>): ComponentFactory<E, CCEntityIotaHolder.Wrapper> {
        return ComponentFactory { e: E -> CCEntityIotaHolder.Wrapper(make.apply(e)) }
    }

    fun init() {}
    override fun registerItemComponentMigrations(itemComponentMigrationRegistry: ItemComponentMigrationRegistry) {

    }
}