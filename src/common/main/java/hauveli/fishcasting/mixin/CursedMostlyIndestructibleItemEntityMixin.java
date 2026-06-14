package hauveli.fishcasting.mixin;

import hauveli.fishcasting.common.cursed.CursedEntity;
import hauveli.fishcasting.registry.FishcastingEntityTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static hauveli.fishcasting.common.registries.FishcastingItems.CURSED;
import static hauveli.fishcasting.common.registries.FishcastingItems.CURSED_BUCKET;

@Mixin(ItemEntity.class)
public class CursedMostlyIndestructibleItemEntityMixin {
    // I couldn't figure out a better way.
    // Hopefully injecting into ItemEntity's hurt method is negligible, as it is called (usually) at most once
    // per item, and I don't see people getting too many CURSEDs (base rate of 1.3% ish in void fluid)

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void makeIndestructible(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemEntity entity = (ItemEntity) (Object) this;
        ItemStack stack = entity.getItem();
        if (stack.is(CURSED) || stack.is(CURSED_BUCKET)) {
            cir.setReturnValue(CursedEntity.relevantDamageSource(source));
            CursedEntity.doAllaySpawnOnLightningHitItem(entity, source);

            // hurt but not by lightning
            if (!cir.getReturnValue()
                    && stack.is(CURSED_BUCKET)) {
                CursedEntity cursed = new CursedEntity(FishcastingEntityTypes.CURSED, entity.level());
                CustomData fishData = stack.get(DataComponents.BUCKET_ENTITY_DATA);
                cursed.loadFromBucketTag(fishData.copyTag());
                cursed.setPos(entity.position());
                entity.level().addFreshEntity(cursed);
                entity.kill();
                entity.discard();
            }
        }
    }
}