package net.variantgenerator.mod.mixin;

import net.minecraft.item.SwordItem;
import net.variantgenerator.mod.core.WeaponProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mixin to track weapon damage properties from Enderite mod items
 * Captures attack damage and speed statistics for variant scaling
 */
@Mixin(SwordItem.class)
public class WeaponDamageTrackerMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Mixin-Weapons");

    /**
     * Hooks into sword item creation to track damage statistics
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onSwordInit(CallbackInfo ci) {
        try {
            SwordItem sword = (SwordItem) (Object) this;
            String itemName = sword.getTranslationKey();

            if (itemName.contains("enderite")) {
                // Extract and cache weapon properties
                float attackDamage = sword.getAttackDamage();
                WeaponProfile.trackWeapon(sword, "sword", attackDamage);
                LOGGER.debug("Tracked Enderite sword: damage={}", attackDamage);
            }
        } catch (Exception e) {
            LOGGER.debug("Could not track weapon damage", e);
        }
    }
}
