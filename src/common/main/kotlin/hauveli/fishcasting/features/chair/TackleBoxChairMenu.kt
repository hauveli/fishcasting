package hauveli.fishcasting.features.chair

import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType

class TackleBoxChairMenu(
    menuType: MenuType<*>,
    syncId: Int,
    playerInventory: Inventory,
    container: Container
) : ChestMenu(
    menuType,
    syncId,
    playerInventory,
    container,
    3 // 3 rows, I think?
) {
    val fishKillSetting: TackleBoxChairEntity.FishKillSetting
        get() = (container as TackleBoxChairEntity).fishKillSetting
}