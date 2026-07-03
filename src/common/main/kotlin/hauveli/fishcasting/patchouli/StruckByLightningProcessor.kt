package hauveli.fishcasting.patchouli

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.lib.HexItems
import hauveli.fishcasting.Fishcasting
import hauveli.fishcasting.casting.recipe.lightning.StruckByLightningRecipe
import hauveli.fishcasting.registry.FishcastingRecipeRegistry
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import vazkii.patchouli.api.IComponentProcessor
import vazkii.patchouli.api.IVariable
import vazkii.patchouli.api.IVariableProvider
import java.util.*

// based on:
// https://github.com/FallingColors/HexMod/blob/1.21/Common/src/main/java/at/petrak/hexcasting/interop/patchouli/BrainsweepProcessor.java

class StruckByLightningProcessor : IComponentProcessor {
    private var recipe: StruckByLightningRecipe? = null
    private var exampleEntityList: MutableList<IVariable>? = null

    override fun setup(level: Level, vars: IVariableProvider) {
        val id = ResourceLocation.parse(vars.get("recipe", level.registryAccess()).asString())

        Fishcasting.LOGGER.info("WOWOWOWOWOW FUCK!")
        val recman = level.recipeManager
        val brainsweepings =
            recman.getAllRecipesFor(FishcastingRecipeRegistry.LIGHTNING_TYPE)
        for (poisonApples in brainsweepings) {
            Fishcasting.LOGGER.info("WOWOWOWOWOW FUCK! {}", poisonApples)
            if (poisonApples.id() == id) {
                this.recipe = poisonApples.value()
                break
            }
        }
    }

    override fun process(level: Level, key: String): IVariable {
        if (this.recipe == null) {
            return IVariable.empty()
        }

        when (key) {
            "entityIn" -> {
                /*
                if (this.exampleEntityList == null) {
                    val entities = this.recipe!!.exchange.exampleEntities(Minecraft.getInstance().level)
                    if (entities.isNullOrEmpty()) {
                        // oh dear
                        return IVariable.empty()
                    }

                    this.exampleEntityList = entities.stream().map { entity: Entity? ->
                        val bob = StringBuilder()
                        bob.append(BuiltInRegistries.ENTITY_TYPE.getKey(entity!!.type))

                        val tag = CompoundTag()
                        entity.save(tag)
                        bob.append(tag.toString())
                        IVariable.wrap(bob.toString())
                    }.toList()
                }

                //return IVariable.wrapList(this.exampleEntityList);
                // patchouli does not currently support cycling entity displays
                // https://github.com/VazkiiMods/Patchouli/issues/852
                return this.exampleEntityList!![0]
                */
                val entityType = this.recipe!!.exchange.entityIn
                val bob = StringBuilder()
                bob.append(BuiltInRegistries.ENTITY_TYPE.getKey(entityType))

                val tag = CompoundTag()
                bob.append(tag.toString())
                IVariable.wrap(bob.toString())
                return IVariable.wrap(bob.toString())
            }

            "entityOut" -> {
                /*
                if (this.exampleEntityList == null) {
                    val entities = this.recipe!!.exchange.exampleEntities(Minecraft.getInstance().level)
                    if (entities.isNullOrEmpty()) {
                        // oh dear
                        return IVariable.empty()
                    }

                    this.exampleEntityList = entities.stream().map { entity: Entity? ->
                        val bob = StringBuilder()
                        bob.append(BuiltInRegistries.ENTITY_TYPE.getKey(entity!!.type))

                        val tag = CompoundTag()
                        entity.save(tag)
                        bob.append(tag.toString())
                        IVariable.wrap(bob.toString())
                    }.toList()
                }

                //return IVariable.wrapList(this.exampleEntityList);
                // patchouli does not currently support cycling entity displays
                // https://github.com/VazkiiMods/Patchouli/issues/852
                return this.exampleEntityList!![0]
                 */

                val entityType = this.recipe!!.exchange.entityOut
                val bob = StringBuilder()
                bob.append(BuiltInRegistries.ENTITY_TYPE.getKey(entityType))

                val tag = CompoundTag()
                bob.append(tag.toString())
                IVariable.wrap(bob.toString())
                return IVariable.wrap(bob.toString())
            }

            "entityTooltip" -> {
                val mc = Minecraft.getInstance()
                return IVariable.wrapList(
                    this.recipe!!.exchange
                        .getTooltip(mc.options.advancedItemTooltips)!!
                        .stream()
                        .map<IVariable?> { v: Component? -> IVariable.from<Component?>(v, level.registryAccess()) }
                        .toList(), level.registryAccess())
            }

            "mediaCost" -> {
                data class ItemCost(val item: Item?, val cost: Int) {
                    fun dividesEvenly(dividend: Int): Boolean {
                        return dividend % cost == 0
                    }
                }

                val costs = arrayOf<ItemCost?>(
                    ItemCost(HexItems.AMETHYST_DUST, MediaConstants.DUST_UNIT.toInt()),
                    ItemCost(Items.AMETHYST_SHARD, MediaConstants.SHARD_UNIT.toInt()),
                    ItemCost(HexItems.CHARGED_AMETHYST, MediaConstants.CRYSTAL_UNIT.toInt()),
                )

                // get evenly divisible ItemStacks
                val validItemStacks = Arrays.stream<ItemCost?>(costs)
                    .filter { itemCost: ItemCost? -> itemCost!!.dividesEvenly(this.recipe!!.mediaCost.toInt()) }
                    .map<ItemStack?> { validItemCost: ItemCost? ->
                        ItemStack(
                            validItemCost?.item as ItemLike,
                            this.recipe!!.mediaCost.toInt() / validItemCost.cost
                        )
                    }
                    .map<IVariable?> { v: ItemStack? -> IVariable.from<ItemStack?>(v, level.registryAccess()) }
                    .toList()

                if (!validItemStacks.isEmpty()) {
                    return IVariable.wrapList(validItemStacks, level.registryAccess())
                }
                // fallback: display in terms of dust
                return IVariable.from(
                    ItemStack(
                        HexItems.AMETHYST_DUST,
                        (this.recipe!!.mediaCost / MediaConstants.DUST_UNIT).toInt()
                    ), level.registryAccess()
                )
            }

            else -> {
                return IVariable.empty()
            }
        }
    }
}

