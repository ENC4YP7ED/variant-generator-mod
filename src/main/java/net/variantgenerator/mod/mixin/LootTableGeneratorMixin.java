package net.variantgenerator.mod.mixin;

import net.minecraft.loot.LootTable;
import net.variantgenerator.mod.core.LootProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mixin to track loot tables for mob drops and block drops
 * Helps generate appropriate drops for variant items
 */
@Mixin(LootTable.class)
public class LootTableGeneratorMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("VariantGenerator-Mixin-Loot");

    /**
     * Hooks into loot table initialization to track drop patterns
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onLootTableInit(CallbackInfo ci) {
        try {
            LootTable lootTable = (LootTable) (Object) this;
            LOGGER.debug("Tracked loot table initialization");
            LootProfile.trackLootTable(lootTable);
        } catch (Exception e) {
            LOGGER.debug("Could not track loot table", e);
        }
    }
}
