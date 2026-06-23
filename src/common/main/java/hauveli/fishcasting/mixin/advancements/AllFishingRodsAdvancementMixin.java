package hauveli.fishcasting.mixin.advancements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.li64.tide.Tide;
import com.li64.tide.registries.items.TideFishingRodItem;
import hauveli.fishcasting.registry.FishcastingItems;
import hauveli.fishcasting.registry.FishcastingTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ServerAdvancementManager.class)
public class AllFishingRodsAdvancementMixin {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void inject(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller par3, CallbackInfo ci) {
        JsonElement element = jsonMap.get(Tide.resource("all_fishing_rods"));
        if (!(element instanceof JsonObject advancement)) return;

        JsonObject criteria = advancement.has("criteria")
                ? advancement.getAsJsonObject("criteria")
                : new JsonObject(); // failsafe just in case and because it's nullable but I guess I should actually throw an error if I was being serious

        JsonArray requirements = advancement.has("requirements")
                ? advancement.getAsJsonArray("requirements")
                : new JsonArray(); // and again

        Class<? extends Item> rodClass = Tide.CONFIG.general.overrideVanillaRod
                ? TideFishingRodItem.class
                : FishingRodItem.class;

        fishcasting$addItemIdToAdvancementRequirement(
                advancement,
                criteria,
                requirements,
                FishcastingItems.SHEPHERDS_CASTING_ROD.toString()
        );

        fishcasting$checkAllItemsAndAddEveryFishingRod(advancement, rodClass, criteria, requirements);
    }

    @Unique
    private static void fishcasting$checkAllItemsAndAddEveryFishingRod(JsonObject advancement, Class<? extends Item> rodClass, JsonObject criteria, JsonArray requirements) {
        for (Item item : BuiltInRegistries.ITEM) {
            //if (rodClass.isInstance(item)) {
            if (item.getDefaultInstance().is(FishcastingTags.ALL_FISHING_RODS_ADVANCEMENT_ROD)) {
                String someItemId = item.toString();
                if (fishcasting$criteriaContainsItemId(criteria, someItemId)) {
                    continue;
                }

                fishcasting$addItemIdToAdvancementRequirement(
                        advancement,
                        criteria,
                        requirements,
                        someItemId
                );
            }
        }
    }

    @Unique
    private static void fishcasting$addItemIdToAdvancementRequirement(
            JsonObject advancement,
            JsonObject criteria,
            JsonArray requirements,
            String itemId) {

        JsonObject getDaRod = fishcasting$makeItemCriteria(itemId);
        criteria.add(itemId, getDaRod);
        advancement.add("criteria", criteria);

        JsonArray extra = new JsonArray();
        extra.add(itemId);

        //JsonArray bonusRequirement = new JsonArray();
        requirements.add(extra);
        advancement.add("requirements", requirements);
    }

    @Unique
    private static boolean fishcasting$criteriaContainsItemId(JsonObject thisCriteria, String targetId) {
        for (Map.Entry<String, JsonElement> entry : thisCriteria.entrySet()) {
            if (!entry.getValue().isJsonObject()) continue;

            JsonObject criterion = entry.getValue().getAsJsonObject();
            if (!criterion.has("conditions")) continue;

            JsonObject conditions = criterion.getAsJsonObject("conditions");
            if (!conditions.has("items")) continue;

            JsonArray items = conditions.getAsJsonArray("items");
            for (JsonElement e : items) {
                if (!e.isJsonObject()) continue;

                JsonObject obj = e.getAsJsonObject();
                if (obj.has("items")
                        && targetId.equals(obj.get("items").getAsString())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Unique
    private static @NotNull JsonObject fishcasting$makeItemCriteria(String itemId) {
        JsonObject hadItemCriteria = new JsonObject();
        hadItemCriteria.addProperty("trigger", "minecraft:inventory_changed");

        JsonObject itemEntry = new JsonObject();
        itemEntry.addProperty("items", itemId);

        JsonArray itemsArray = new JsonArray();
        itemsArray.add(itemEntry);

        JsonObject conditions = new JsonObject();
        conditions.add("items", itemsArray);
        hadItemCriteria.add("conditions", conditions);
        return hadItemCriteria;
    }
}