package hauveli.fishcasting.mixin.hexxy5;


import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TideFishingHook.class)
public interface Hexxy5TideFishingHookAccessor {

    @Invoker("updateOwnerInfo")
    void invokeUpdateOwnerInfo(TideFishingHook hook);
}