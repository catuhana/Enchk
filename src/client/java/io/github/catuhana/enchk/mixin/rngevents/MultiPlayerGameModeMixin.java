package io.github.catuhana.enchk.mixin.rngevents;

import io.github.catuhana.enchk.features.PlayerRandCracker;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(method = "dropItem", at = @At("HEAD"))
    private void onDrop(CallbackInfo ci) {
        PlayerRandCracker.onDropItem();
    }
}
