package io.github.catuhana.enchk.mixin.rngevents;

import io.github.catuhana.enchk.interfaces.ICreativeSlot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen$CustomCreativeSlot")
public class CustomCreativeSlotMixin implements ICreativeSlot {
}
