package net.earthcomputer.clientcommands;

import dev.xpple.betterconfig.api.Config;
import net.earthcomputer.clientcommands.features.EnchantmentCracker;
import net.earthcomputer.clientcommands.features.PlayerRandCracker;
import net.earthcomputer.clientcommands.features.ServerBrandManager;
import net.minecraft.util.Mth;

public class Configs {

    @Config(readOnly = true, temporary = true)
    public static EnchantmentCracker.CrackState enchCrackState = EnchantmentCracker.CrackState.UNCRACKED;

    @Config(readOnly = true, temporary = true)
    public static PlayerRandCracker.CrackState playerCrackState = PlayerRandCracker.CrackState.UNCRACKED;

    @Config(onChange = "onChangeEnchantingPrediction", temporary = true)
    public static boolean enchantingPrediction = false;
    private static void onChangeEnchantingPrediction(boolean oldEnchantingPrediction, boolean enchantingPrediction) {
        if (enchantingPrediction) {
            ServerBrandManager.rngWarning();
        } else {
            EnchantmentCracker.resetCracker();
        }
    }

    @Config(temporary = true)
    public static boolean playerRNGMaintenance = true;

    @Config(setter = @Config.Setter("setMaxEnchantItemThrows"))
    private static int maxEnchantItemThrows = 64 * 256;
    public static int getMaxEnchantItemThrows() {
        return maxEnchantItemThrows;
    }
    public static void setMaxEnchantItemThrows(int maxEnchantItemThrows) {
        Configs.maxEnchantItemThrows = Mth.clamp(maxEnchantItemThrows, 0, 1000000);
    }

    @Config(setter = @Config.Setter("setMinEnchantBookshelves"), temporary = true)
    private static int minEnchantBookshelves = 0;
    public static int getMinEnchantBookshelves() {
        return minEnchantBookshelves;
    }
    public static void setMinEnchantBookshelves(int minEnchantBookshelves) {
        Configs.minEnchantBookshelves = Mth.clamp(minEnchantBookshelves, 0, 15);
        Configs.maxEnchantBookshelves = Math.max(Configs.maxEnchantBookshelves, Configs.minEnchantBookshelves);
    }

    @Config(setter = @Config.Setter("setMaxEnchantBookshelves"), temporary = true)
    private static int maxEnchantBookshelves = 15;
    public static int getMaxEnchantBookshelves() {
        return maxEnchantBookshelves;
    }
    public static void setMaxEnchantBookshelves(int maxEnchantBookshelves) {
        Configs.maxEnchantBookshelves = Mth.clamp(maxEnchantBookshelves, 0, 15);
        Configs.minEnchantBookshelves = Math.min(Configs.minEnchantBookshelves, Configs.maxEnchantBookshelves);
    }

    @Config(setter = @Config.Setter("setMinEnchantLevels"), temporary = true)
    private static int minEnchantLevels = 1;
    public static int getMinEnchantLevels() {
        return minEnchantLevels;
    }
    public static void setMinEnchantLevels(int minEnchantLevels) {
        Configs.minEnchantLevels = Mth.clamp(minEnchantLevels, 1, 30);
        Configs.maxEnchantLevels = Math.max(Configs.maxEnchantLevels, Configs.minEnchantLevels);
    }

    @Config(setter = @Config.Setter("setMaxEnchantSlot"), temporary = true)
    private static int maxEnchantSlot = 3;

    public static int getMaxEnchantSlot() {
        return maxEnchantSlot;
    }

    public static void setMaxEnchantSlot(int maxEnchantSlot) {
        Configs.maxEnchantSlot = Mth.clamp(maxEnchantSlot, 1, 3);
    }

    @Config(setter = @Config.Setter("setMaxEnchantLevels"), temporary = true)
    private static int maxEnchantLevels = 30;
    public static int getMaxEnchantLevels() {
        return maxEnchantLevels;
    }
    public static void setMaxEnchantLevels(int maxEnchantLevels) {
        Configs.maxEnchantLevels = Mth.clamp(maxEnchantLevels, 1, 30);
        Configs.minEnchantLevels = Math.min(Configs.minEnchantLevels, Configs.maxEnchantLevels);
    }

    @Config
    public static float itemThrowsPerTick = 1;
}
