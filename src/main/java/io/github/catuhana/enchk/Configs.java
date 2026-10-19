package io.github.catuhana.enchk;

import dev.xpple.betterconfig.api.Config;
import io.github.catuhana.enchk.features.EnchantmentCracker;
import io.github.catuhana.enchk.features.PlayerRandCracker;

public class Configs {

    @Config(readOnly = true, temporary = true)
    public static EnchantmentCracker.CrackState enchCrackState = EnchantmentCracker.CrackState.UNCRACKED;

    @Config(readOnly = true, temporary = true)
    public static PlayerRandCracker.CrackState playerCrackState = PlayerRandCracker.CrackState.UNCRACKED;

    @Config(temporary = true)
    public static boolean playerRNGMaintenance = true;

    @Config()
    private static int maxEnchantItemThrows = 64 * 256;
    public static int getMaxEnchantItemThrows() {
        return maxEnchantItemThrows;
    }

    @Config(temporary = true)
    private static int minEnchantBookshelves = 0;
    public static int getMinEnchantBookshelves() {
        return minEnchantBookshelves;
    }

    @Config(temporary = true)
    private static int maxEnchantBookshelves = 15;
    public static int getMaxEnchantBookshelves() {
        return maxEnchantBookshelves;
    }

    @Config(temporary = true)
    private static int minEnchantLevels = 1;
    public static int getMinEnchantLevels() {
        return minEnchantLevels;
    }

    @Config(temporary = true)
    private static int maxEnchantSlot = 3;
    public static int getMaxEnchantSlot() {
        return maxEnchantSlot;
    }

    @Config(temporary = true)
    private static int maxEnchantLevels = 30;
    public static int getMaxEnchantLevels() {
        return maxEnchantLevels;
    }

    @Config
    public static float itemThrowsPerTick = 1;
}
