package hauveli.fishcasting.mixin.advancements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.li64.tide.Tide;
import hauveli.fishcasting.Fishcasting;
import hauveli.fishcasting.registry.FishcastingItems;
import hauveli.fishcasting.registry.FishcastingTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ServerAdvancementManager.class)
public class AllFishingRodsAdvancementMixin {

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

        // adding mine manually because I'm making this addon and I think this makes sense, it is obtainable via fishing alone, and serves a fishing purpose beyond just hexcasting.
        fishcasting$addItemIdToAdvancementRequirement(advancement, criteria, requirements, FishcastingItems.SHEPHERDS_CASTING_ROD.toString());

        fishcasting$checkAllItemsAndAddEveryFishingRod(advancement, criteria, requirements, resourceManager);
    }


    @Unique
    private static @NotNull List<Resource> fishcasting$getResourceStackFromTagId(ResourceManager resourceManager, ResourceLocation tagId) {
        ResourceLocation tagPath = ResourceLocation.fromNamespaceAndPath(
                tagId.getNamespace(),
                "tags/item/" + tagId.getPath() + ".json"
        );
        return resourceManager.getResourceStack(tagPath);
    }

    private static ArrayList<String> VISITED_TAGS = new ArrayList<>();

    @Unique
    private static List<Holder<Item>> fishcasting$loadMyFuckingTags(ResourceManager resourceManager, ResourceLocation tagId) {
        List<Resource> resources = fishcasting$getResourceStackFromTagId(resourceManager, tagId);

        ArrayList<Holder<Item>> listOfItems = new ArrayList<>();
        resources.forEach(resource -> {
            try (Reader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);

                JsonArray values = json.getAsJsonArray("values");
                if (values == null) return;

                for (JsonElement element : values) {
                    String value = element.getAsString();

                    if (value.startsWith("#")) {
                        Fishcasting.LOGGER.info("Found tag {}", value);
                        if (VISITED_TAGS.contains(value)) {
                            return; // do NOT recurse
                        }
                        VISITED_TAGS.add(value);
                        ResourceLocation targetTag = ResourceLocation.parse(value.replaceFirst("#", ""));
                        listOfItems.addAll(fishcasting$loadMyFuckingTags(resourceManager, targetTag));
                    } else {
                        ResourceLocation itemId = ResourceLocation.parse(value);
                        Fishcasting.LOGGER.debug("Found item {}", itemId);
                        if (BuiltInRegistries.ITEM.getHolder(itemId).isPresent()) {
                            Holder<Item> itemHolder = BuiltInRegistries.ITEM.getHolder(itemId).get();
                            listOfItems.add(itemHolder);
                        }
                    }
                }
            } catch (IOException e) {
                Fishcasting.LOGGER.error("Failed to read tag {}", tagId, e);
            }
        });

        return listOfItems.stream().toList();
    }

    @Unique
    private static void fishcasting$checkAllItemsAndAddEveryFishingRod(JsonObject advancement, JsonObject criteria, JsonArray requirements, ResourceManager resourceManager) {
        List<Holder<Item>> itemList;
        if (BuiltInRegistries.ITEM.getTag(FishcastingTags.ALL_FISHING_RODS_ADVANCEMENT_ROD).isEmpty()) {
            // runs on first load of world and not subsequent ones within the same runtime....
            // I don't know how to get access to tags earlier because they only load after first world load which is after advancements somehow (??)
            Fishcasting.LOGGER.debug("Checking tags manually for all_fishing_rods advancement");
            ResourceLocation tagId = FishcastingTags.ALL_FISHING_RODS_ADVANCEMENT_ROD.location();
            itemList = fishcasting$loadMyFuckingTags(resourceManager, tagId);
        } else {
            Fishcasting.LOGGER.debug("Tags already existed! We are OK!");
            itemList = BuiltInRegistries.ITEM.getTag(FishcastingTags.ALL_FISHING_RODS_ADVANCEMENT_ROD).get().stream().toList();
        }
        if (itemList.isEmpty()) return;
        for (Holder<Item> itemHolder : itemList) {
            Item item = itemHolder.value();
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