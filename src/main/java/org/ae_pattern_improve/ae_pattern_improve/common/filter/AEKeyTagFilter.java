package org.ae_pattern_improve.ae_pattern_improve.common.filter;

import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.include.com.google.common.collect.HashBiMap;

import java.util.*;
import java.util.regex.Pattern;

/**
 * A base class for AEKey filters that use tags.
 */
public abstract class AEKeyTagFilter<TYPE, FILTER extends AEKeyTagFilter<TYPE, FILTER>>{

    protected static final Pattern TAG_PATTERN = Pattern.compile("^([a-z0-9_.-]+):([a-z0-9_.-]+)/([a-z0-9_.-]+)$");

    protected final Map<String, String> rootSrcMaterials = HashBiMap.create();

    public AEKeyTagFilter(TagKey<TYPE> filterTag) {
        addToRootSrcMaterials(filterTag);
    }

    public AEKeyTagFilter(GenericStack stack) {
        for (TagKey<TYPE> tag : getFilterableTags(stack)) {;
            addToRootSrcMaterials(tag);
        }
    }

    protected AEKeyTagFilter(FILTER other) {
        rootSrcMaterials.putAll(other.rootSrcMaterials);
    }

    protected void addToRootSrcMaterials(String key, String value) {
        if (!this.rootSrcMaterials.containsKey(key)) {
            this.rootSrcMaterials.put(key, value);
        }
    }
    protected void addToRootSrcMaterials(TagKey<TYPE> tag) {
        addToRootSrcMaterials(tag.location());
    }
    protected void addToRootSrcMaterials(ResourceLocation location) {
        var rootSrcMaterial = RootSrcMaterial.parseResourceLocation(location);
        rootSrcMaterial.ifPresent(material -> addToRootSrcMaterials(material.root(), material.value()));
    }

    @Nullable
    public GenericStack findStack(GenericStack stack, long amount) {
        //If we don't currently have a result stack cached, calculate what the result stack is
//        Ae_test.LOGGER.debug("Finding stack for: {}", stack.what().getId());
//        Ae_test.LOGGER.debug("Root source materials: {}", rootSrcMaterials);
        if (rootSrcMaterials.isEmpty()) {
            return null;
        } else {
            //get the tags of stack that match the pattern
            List<RootSrcMaterial> filterableTags = getFilterableTags(stack).stream()
                    .map(TagKey::location)
                    .map(RootSrcMaterial::parseResourceLocation)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
//            Ae_test.LOGGER.debug("Found filterable tags: {}", filterableTags);
            if (filterableTags.isEmpty()) {
                return null;
            }
            //try concatenating the roots in rootSrcMaterials with the values in filterableTags
            for (RootSrcMaterial tag : filterableTags) {
                String value = tag.value();
                for (String root : rootSrcMaterials.keySet()) {
//                    Ae_test.LOGGER.debug("Checking root: {}, value: {}", root, value);
                    if (rootSrcMaterials.containsKey(root)) {
                        ResourceLocation fullKey = ResourceLocation.parse(root + "/" + value);
//                        Ae_test.LOGGER.debug("Finding stack for full key: {}", fullKey);
                        var found = getRegistry().getTag(TagKey.create(getRegistry().key(), fullKey));
//                        Ae_test.LOGGER.debug("Tag validated: {}, found: {}", fullKey, found.isPresent());
                        if (found.isPresent()) {
                            var found1 = found.get().stream().findAny();
//                            Ae_test.LOGGER.debug("Found stack result: {}", found1);
                            if (found1.isPresent()) {
                                TYPE type = found1.get().value();
                                return createResultStack(type, amount);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    protected abstract GenericStack createResultStack(TYPE type, long amount);

    public abstract Registry<TYPE> getRegistry();
    public abstract List<TagKey<TYPE>> getFilterableTags(GenericStack keySource);

    protected record RootSrcMaterial(String root, String value) {
        public static Optional<RootSrcMaterial> parseResourceLocation(ResourceLocation location, Pattern pattern) {
            var matcher = pattern.matcher(location.toString());
            if (matcher.matches()) {
                try {
                    // there should be 2 or 3 groups depending on the pattern
                    try {
                        return Optional.of(new RootSrcMaterial(matcher.group(1) + ":" + matcher.group(2), matcher.group(3)));
                    } catch (IndexOutOfBoundsException e) {
                        return Optional.of(new RootSrcMaterial(matcher.group(1), matcher.group(2)));
                    }
                } catch (Exception e) {
                    AePatternImprove.LOGGER.error("Failed to parse resource location: {}", location, e);
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        }
        public static Optional<RootSrcMaterial> parseResourceLocation(ResourceLocation location) {
            return parseResourceLocation(location, TAG_PATTERN);
        }
    }
    public static Optional<GenericStack> findGenericStack(GenericStack stackRoot, GenericStack stackSrcMaterial){
        if (stackRoot == null) {
            return Optional.empty();
        }
        var aeKeyType = stackRoot.what().getType();
        AePatternImprove.LOGGER.debug("Finding stack for root: {}, src: {}, type: {}", stackRoot.what().getId(), stackSrcMaterial.what().getId(), aeKeyType);
        if (Objects.equals(aeKeyType, AEKeyType.items())){
            var itemFilter = new AEItemTagFilter(stackRoot);
            return Optional.ofNullable(itemFilter.findStack(stackSrcMaterial, stackRoot.amount()));
        } else if (Objects.equals(aeKeyType, AEKeyType.fluids())) {
            var fluidFilter = new AEFluidTagFilter(stackRoot);
            return Optional.ofNullable(fluidFilter.findStack(stackSrcMaterial, stackRoot.amount()));
        }
        return Optional.empty();
    }
}
