package io.github.catuhana.enchk.mixin.rngevents.droppableinventory;

import io.github.catuhana.enchk.interfaces.IDroppableInventoryContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.LoomMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LoomMenu.class)
public class LoomMenuMixin implements IDroppableInventoryContainer {

    @Shadow
    @Final
    private Container inputContainer;

    @Override
    public Container getDroppableInventory() {
        return inputContainer;
    }
}
