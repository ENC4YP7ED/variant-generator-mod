package net.variantgenerator.mod.mixin;

import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.ArmorMaterial;
import net.variantgenerator.mod.core.EnderiteStatCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mixin to intercept and cache material statistics from Enderite mod.
 * Captures tool and armor material properties for stat scaling.
 */
@Mixin(ToolMaterial.class)
public class EnderiteMaterialInjectorMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Mixin-Materials");

    /**
     * Injects into ToolMaterial to cache Enderite tool stats
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onToolMaterialInit(CallbackInfo ci) {
        try {
            ToolMaterial material = (ToolMaterial) (Object) this;

            // Cache Enderite material properties
            int durability = material.getDurability();
            float speed = material.getMiningSpeedMultiplier();
            float damage = material.getAttackDamageBonus();

            EnderiteStatCache.cacheToolMaterial("enderite", durability, speed, damage, 17);
            LOGGER.debug("Cached Enderite tool material: durability={}, speed={}, damage={}",
                durability, speed, damage);
        } catch (Exception e) {
            LOGGER.debug("Could not cache tool material stats", e);
        }
    }
}
