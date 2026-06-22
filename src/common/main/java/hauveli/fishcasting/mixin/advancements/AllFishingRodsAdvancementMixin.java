package hauveli.fishcasting.mixin.advancements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.li64.tide.Tide;
import hauveli.fishcasting.registry.FishcastingItems;
import io.wispforest.owo.offline.DataSavedEvents;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Mixin(ServerAdvancementManager.class)
public class AllFishingRodsAdvancementMixin {

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void inject(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager par2, ProfilerFiller par3, CallbackInfo ci) {

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
    }
}