package org.ae_pattern_improve.ae_pattern_improve.common.filter;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class AEFluidTagFilter extends AEKeyTagFilter<Fluid, AEFluidTagFilter> {
//    private final static List<Pattern> CONFIGURABLE_FLUID_PATTERNS = new CopyOnWriteArrayList<>(
//            new Pattern[]{
//                    Pattern.compile("^(c:)(?!molten_|liquid)([a-z0-9_.-]+)(?!_plasma)$"), // Matches all fluids in the mod
//                    Pattern.compile("^(c:molten_)([a-z0-9_.-]+)$"), // Matches all forge fluids
//            }
//    );

    public AEFluidTagFilter(GenericStack stack) {
        super(stack);
//        for (TagKey<Fluid> tag : getFilterableTags(stack)) {
//            for (Pattern regex : CONFIGURABLE_FLUID_PATTERNS) {
//                var rootSrcMaterial = RootSrcMaterial.parseResourceLocation(tag.location(), regex);
//                rootSrcMaterial.ifPresent(material -> addToRootSrcMaterials(material.root(), material.value()));
//            }
//        }

    }

    @Override
    public Registry<Fluid> getRegistry() {
        return BuiltInRegistries.FLUID;
    }

    @Override
    protected GenericStack createResultStack(Fluid fluid, long amount) {
        var key = AEFluidKey.of(fluid);
        return new GenericStack(key, amount);
    }

    public List<TagKey<Fluid>> getFilterableTags(GenericStack keySource) {
        var fluid = getRegistry().getOptional(keySource.what().getId()).orElse(null);
        if (fluid != null) {
            return new FluidStack(fluid, 1).getTags().toList();
        }
        return List.of();
    }
}
