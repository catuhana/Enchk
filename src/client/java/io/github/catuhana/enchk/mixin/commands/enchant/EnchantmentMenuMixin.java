package io.github.catuhana.enchk.mixin.commands.enchant;

import io.github.catuhana.enchk.features.EnchantmentCracker;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin extends AbstractContainerMenuMixin {
    @Shadow
    @Final
    private DataSlot enchantmentSeed;

    @Override
    protected void enchk_onEnchantmentSetData(DataSlot slot) {
        if (slot == enchantmentSeed) {
            EnchantmentCracker.checkXpSeedState((EnchantmentMenu) (Object) this);
        }
    }
}
