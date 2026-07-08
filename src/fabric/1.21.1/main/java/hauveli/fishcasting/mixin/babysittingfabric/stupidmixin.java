package hauveli.fishcasting.mixin.babysittingfabric;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.util.perf.Profiler;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

@Mixin(RecipeManager.class)
public class stupidmixin {

    // I'm mad at fabric for not letting me load it in the correct order (after Tide) to apply pack.mcmeta filters. This mixin exists because
    // fabric somehow didn't support pack.mcmeta features because loading in a random order is epic.
    // Fabric supports loading in specified orders after 1.21.5, I think
    private static final String fuckyou1 = "crystal_rod_smithing";
    private static final String fuckyou2 = "bobbers/amethyst_bobber";

    @Unique
    private boolean fishcasting$fuckedIfTrue(ResourceLocation id) {
        return id.getPath().equals(fuckyou1) || id.getPath().equals(fuckyou2);
    }

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void apply(Map<ResourceLocation, JsonElement> map, ResourceManager par2, ProfilerFiller par3, CallbackInfo ci) {
        Iterator<Map.Entry<ResourceLocation, JsonElement>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            ResourceLocation id = iterator.next().getKey();

            if (id.getNamespace().equals("tide") && fishcasting$fuckedIfTrue(id)) {
                iterator.remove();
            }
        }
    }
}