package io.github.catuhana.enchk.mixin.rngevents.droppableinventory;

import io.github.catuhana.enchk.interfaces.IDroppableInventoryContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin implements IDroppableInventoryContainer {

    @Shadow
    @Final
    private Container enchantSlots;

    @Override
    public Container getDroppableInventory() {
        return enchantSlots;
    }
}
