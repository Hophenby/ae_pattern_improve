package org.ae_pattern_improve.ae_pattern_improve.xmodcompat.exae;

import appeng.util.inv.AppEngInternalInventory;
import com.glodblock.github.extendedae.common.me.itemhost.HostPatternModifier;
import com.glodblock.github.glodium.reflect.ReflectKit;
import com.google.common.base.Preconditions;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.XModUtils;

import java.lang.reflect.Field;

public final class ExAEReflect {

    private static Field fPatternModifierInv;
    private static Class<?> cExPatternProvider;
    private static boolean initialized = false;

    private static void init()  {
        Preconditions.checkArgument(XModUtils.isModLoaded(XModUtils.EXTENDED_AE_MOD_ID),
                "ExtendedAE is not loaded, cannot reflect ExtendedAE classes");
        try {
//            Field modifiersField = Field.class.getDeclaredField("modifiers");

            fPatternModifierInv = ReflectKit.reflectField(Class.forName("com.glodblock.github.extendedae.common.me.itemhost.HostPatternModifier"), "patternInv");
            cExPatternProvider = Class.forName("com.glodblock.github.extendedae.container.ContainerExPatternProvider");
            //            fPatternInv.setAccessible(true);
//            modifiersField.setInt(fPatternInv, fPatternInv.getModifiers() & ~Modifier.FINAL);
            //  | IllegalAccessException e

        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException("ExtendedAE reflects load failed", e);
        }
        initialized = true;
    }
    private static void checkOrInit() {
        if (XModUtils.isModLoaded(XModUtils.EXTENDED_AE_MOD_ID) && !initialized) {
            init();
        }
    }
    public static void setPatternModifierInv(HostPatternModifier hostPatternModifier, AppEngInternalInventory inv) {
        checkOrInit();
        ReflectKit.writeField(hostPatternModifier, fPatternModifierInv, inv);
    }
    public static Class<?> getExPatternProviderMenuClass() {
        checkOrInit();
        return cExPatternProvider;
    }
}
