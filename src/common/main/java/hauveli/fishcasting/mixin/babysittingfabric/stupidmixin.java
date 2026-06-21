package hauveli.fishcasting.mixin.babysittingfabric;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.util.perf.Profiler;

import java.util.HashMap;
import java.util.Map;

@Mixin(RecipeManager.class)
public class stupidmixin {

    private static final String fuckyou1 = "crystal_rod_smithing";
    private static final String fuckyou2 = "amethyst_bobber";

    @Inject(method = "apply*", at = @At("RETURN"))
    private void onApply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        stupidaccessor accessor = (stupidaccessor)(Object)this;
        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> oldRecipes = accessor.getRecipes();

        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newRecipes = new HashMap<>();
        for (Map.Entry<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> entry : oldRecipes.entrySet()) {
            Map<ResourceLocation, Recipe<?>> filtered = new HashMap<>();
            for (Map.Entry<ResourceLocation, Recipe<?>> inner : entry.getValue().entrySet()) {
                String path = inner.getKey().getPath();
                if (!path.equals(fuckyou1) && !path.equals(fuckyou2)) {
                    filtered.put(inner.getKey(), inner.getValue());
                }
            }
            newRecipes.put(entry.getKey(), filtered);
        }

        accessor.setRecipes(newRecipes);
    }
}