package hauveli.fishcasting.mixin.environment_spells;

import com.li64.tide.data.fishing.conditions.types.BiomeWhitelistCondition;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(BiomeWhitelistCondition.class)
public interface BiomeWhitelistConditionAccessor {

    @Accessor("tags")
    List<TagKey<Biome>> tide$getTags();

    @Accessor("biomes")
    List<ResourceLocation> tide$getBiomes();
}
