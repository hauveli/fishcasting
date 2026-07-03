package hauveli.fishcasting.mixin.voidsweep;

import net.minecraft.world.entity.npc.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractVillager.class)
public interface AbstractVillagerInvoker {

    @Invoker("stopTrading")
    void invokeStopTrading();
}