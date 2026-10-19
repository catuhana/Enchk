package io.github.catuhana.enchk.mixin.rngevents.droppableinventory;

import io.github.catuhana.enchk.interfaces.IDroppableInventoryContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.GrindstoneMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GrindstoneMenu.class)
public class GrindstoneMenuMixin implements IDroppableInventoryContainer {

    @Shadow @Final Container repairSlots;

    @Override
    public Container getDroppableInventory() {
        return repairSlots;
    }
}
