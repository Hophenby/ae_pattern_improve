package org.ae_pattern_improve.ae_pattern_improve.xmodcompat;


import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public class XModUtils {
    // This class is a placeholder for any utility methods that might be needed
    // for compatibility with other mods or future extensions.
    public static final String ALMOST_UNIFIED_MOD_ID = "almostunified";
    public static final String EXTENDED_AE_MOD_ID = "extendedae";
    public static final String AE2WTLIB_MOD_ID = "ae2wtlib";

    private XModUtils() {
    }
    public static boolean isModLoaded(String modId) {
        // This method can be used to check if a specific mod is loaded.
        if (ModList.get() == null) {
            // If ModList is not available, we assume we are in a loading phase.
            return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
        }
        return ModList.get().isLoaded(modId);
    }
    public static ICondition modRecipeCondition(String modId) {
        // This method can be used to create a condition that checks if a specific mod is loaded.
        return new ModLoadedCondition(modId);
    }
}
