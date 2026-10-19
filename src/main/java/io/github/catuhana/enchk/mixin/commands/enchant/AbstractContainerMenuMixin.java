package io.github.catuhana.enchk.mixin.commands.enchant;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Shadow
    @Final
    private List<DataSlot> dataSlots;

    @Inject(method = "setData", at = @At("RETURN"))
    private void onSetData(int id, int value, CallbackInfo ci) {
        enchk_onEnchantmentSetData(dataSlots.get(id));
    }

    protected void enchk_onEnchantmentSetData(DataSlot slot) {
    }
}
