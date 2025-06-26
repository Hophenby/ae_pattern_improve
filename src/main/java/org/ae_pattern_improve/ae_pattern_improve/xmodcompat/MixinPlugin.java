package org.ae_pattern_improve.ae_pattern_improve.xmodcompat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    private static final SetMultimap<String, String> MIXIN_MAP = HashMultimap.create();
    static {
        MIXIN_MAP.put("org.ae_pattern_improve.ae_pattern_improve.mixins.xmod.extendedae.MixinPartExPatternProvider", XModUtils.EXTENDED_AE_MOD_ID);
        MIXIN_MAP.put("org.ae_pattern_improve.ae_pattern_improve.mixins.xmod.extendedae.MixinTileExPatternProvider", XModUtils.EXTENDED_AE_MOD_ID);
        MIXIN_MAP.put("org.ae_pattern_improve.ae_pattern_improve.mixins.xmod.extendedae.MixinHostPatternModifier", XModUtils.EXTENDED_AE_MOD_ID);
        MIXIN_MAP.put("org.ae_pattern_improve.ae_pattern_improve.mixins.xmod.extendedae.MixinGuiPatternModifier", XModUtils.EXTENDED_AE_MOD_ID);
        MIXIN_MAP.put("org.ae_pattern_improve.ae_pattern_improve.mixins.xmod.extendedae.MixinItemPatternModifier", XModUtils.EXTENDED_AE_MOD_ID);
    }
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return !MIXIN_MAP.containsKey(mixinClassName) || MIXIN_MAP.get(mixinClassName).stream().allMatch(XModUtils::isModLoaded);
    }
    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

}