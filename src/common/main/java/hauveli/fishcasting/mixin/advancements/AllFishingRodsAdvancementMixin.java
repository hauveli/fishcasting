package hauveli.fishcasting.mixin.advancements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.li64.tide.Tide;
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
        if (!(element instanceof JsonObject adv)) return;

        JsonObject criteria = adv.has("criteria")
                ? adv.getAsJsonObject("criteria")
                : new JsonObject(); // failsafe just in case and because it's nullable but I guess I should actually throw an error if I was being serious

        JsonArray requirements = adv.has("requirements")
                ? adv.getAsJsonArray("requirements")
                : new JsonArray(); // and again

        JsonArray bonusRequirement = new JsonArray();
        for (JsonElement e : requirements) bonusRequirement.add(e);

        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof FishingRodItem) {
                String someItemId = item.toString();
                if (fishcasting$jsonArrayContainsString(requirements, someItemId)) {
                    continue;
                }

                fishcasting$addItemIdToAdvancementRequirement(
                        adv,
                        criteria,
                        bonusRequirement,
                        someItemId
                );
            }
        }
    }

    @Unique
    private static void fishcasting$addItemIdToAdvancementRequirement(
            JsonObject advancement,
            JsonObject criteria,
            JsonArray bonusRequirement,
            String itemId) {
        JsonObject getDaRod = fishcasting$makeItemCriteria(itemId);
        criteria.add(itemId, getDaRod);
        advancement.add("criteria", criteria);

        JsonArray extra = new JsonArray();
        extra.add(itemId);
        bonusRequirement.add(extra);
        advancement.add("requirements", bonusRequirement);
    }

    @Unique
    private static boolean fishcasting$jsonArrayContainsString(JsonArray thisArray, String thisString) {
        for (JsonElement e : thisArray) {
            if (e.isJsonPrimitive() && e.getAsString().equals(thisString)) {
                return true;
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

    /*
            JsonElement element = jsonMap.get(Tide.resource("all_fishing_rods"));
        if (!(element instanceof JsonObject adv)) return;

        JsonObject criteria = adv.has("criteria")
                ? adv.getAsJsonObject("criteria")
                : new JsonObject(); // failsafe just in case and because it's nullable but I guess I should actually throw an error if I was being serious

        JsonObject getDaRod = new JsonObject();
        getDaRod.addProperty("trigger", "minecraft:inventory_changed");

        JsonObject itemEntry = new JsonObject();
        itemEntry.addProperty("items", FishcastingItems.SHEPHERDS_CASTING_ROD.toString());

        JsonArray itemsArray = new JsonArray();
        itemsArray.add(itemEntry);

        JsonObject conditions = new JsonObject();
        conditions.add("items", itemsArray);
        getDaRod.add("conditions", conditions);
        criteria.add("shepherds", getDaRod);
        adv.add("criteria", criteria);

        JsonArray requirements = adv.has("requirements")
                ? adv.getAsJsonArray("requirements")
                : new JsonArray(); // and again

        JsonArray bonusRequirement = new JsonArray();
        for (JsonElement e : requirements) bonusRequirement.add(e);

        JsonArray extra = new JsonArray();
        extra.add("shepherds");
        bonusRequirement.add(extra);

        adv.add("requirements", bonusRequirement);
     */
}