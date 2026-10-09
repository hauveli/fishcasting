package hauveli.fishcasting.features.food

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.common.lib.HexDataComponents
import at.petrak.hexcasting.common.lib.HexMobEffects
import com.li64.tide.registries.items.FishingBobberItem
import hauveli.fishcasting.Fishcasting
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag



class CrystalShrimpFriedRiceItem(properties: Properties) :
    Item(
        properties.food(
            FoodProperties.Builder()
                .nutrition(10)
                .saturationModifier(1f)
                .effect(
                    MobEffectInstance(HexMobEffects.ENLARGE_GRID, 20 * 60 * 13, 0),
                    1f
                )
                .effect(
                    MobEffectInstance(MobEffects.LUCK, (20 * 60 * 13) / 2, 0), // approx half dur of the grid one
                    1f
                )
                .build()
        )
    )