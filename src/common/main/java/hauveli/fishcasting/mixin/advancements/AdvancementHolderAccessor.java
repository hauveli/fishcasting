package hauveli.fishcasting.mixin.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementHolder.class)
public interface AdvancementHolderAccessor {

    @Mutable
    @Accessor("value")
    void setValue(Advancement advancement);
}