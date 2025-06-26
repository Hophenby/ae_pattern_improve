package org.ae_pattern_improve.ae_pattern_improve.config;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.client.gui.style.Blitter;
import appeng.core.AppEng;
import appeng.menu.me.common.GridInventoryEntry;
import com.google.common.collect.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.ae_pattern_improve.ae_pattern_improve.AePatternImprove;
import org.ae_pattern_improve.ae_pattern_improve.ae_entry_comparator.EntryComparators;
import org.ae_pattern_improve.ae_pattern_improve.ae_entry_comparator.IComparatorItem;
import org.ae_pattern_improve.ae_pattern_improve.mixins.MixinEncodingHelper;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.almostunified.AlmostUnifiedAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public final class ClientComparatorConfig {
    private static final Logger LOGGER = LogManager.getLogger(ClientComparatorConfig.class);

    private static final Pattern VALID_COMPARATOR_NAME = Pattern.compile("^([a-zA-Z0-9_]+):?([a-zA-Z0-9_\\-./:]+)?$");

    public static final ModConfigSpec CONFIG_SPEC;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> COMPARATOR_LIST;

    private static final Map<String, ComparatorRegistry> registeredComparators = new HashMap<>();

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        registerOnetimeComparator("isCraftable", (IComparatorItem<Boolean>) GridInventoryEntry::isCraftable,
                () -> Blitter.texture(AppEng.makeId("textures/guis/states.png")).src(48, 144, 16, 16),
                Component.translatable("ae_pattern_improve.listbox.icon.is_craftable"));
        registerOnetimeComparator("isUndamaged", (IComparatorItem<Boolean>) MixinEncodingHelper::isUndamaged,
                () -> Blitter.texture(AppEng.makeId("textures/guis/states.png")).src(0, 96, 16, 16),
                Component.translatable("ae_pattern_improve.listbox.icon.is_undamaged"));
        registerOnetimeComparator("isAlmostUnifiedPreferred", (IComparatorItem<Boolean>) AlmostUnifiedAdapter::isAUPreferredItem,
                () -> Blitter.guiSprite(AePatternImprove.getRL("almost_unified_ingot")),
                Component.translatable("ae_pattern_improve.listbox.icon.almost_unified.preferred"));
        registerOnetimeComparator("storedAmount", (IComparatorItem<Long>) GridInventoryEntry::getStoredAmount,
                () -> Blitter.texture(AppEng.makeId("textures/guis/states.png")).src(16, 64, 16, 16),
                Component.translatable("ae_pattern_improve.listbox.icon.stored_amount"));
        registerOnetimeComparator("storedAmountInfinity", (IComparatorItem<Boolean>) (entry -> entry.getStoredAmount() >= Integer.MAX_VALUE),
                () -> Blitter.guiSprite(AePatternImprove.getRL("infinity_stored")),
                Component.translatable("ae_pattern_improve.listbox.icon.infinity_stored"));
        registerInfinityUseComparator("item", () -> EntryComparators.fromAEKey(AEItemKey.of(Items.STONE)));
        registerInfinityUseComparator("fluid", () -> EntryComparators.fromAEKey(AEFluidKey.of(Fluids.WATER)));

        COMPARATOR_LIST = builder
                .comment(" List of comparators to use in the AE2 entry comparator. \n" +
                        " Each comparator is defined by a string in the format '<comparator_name>' or \n" +
                        " '<comparator_name>:<comparator_value>'. The comparators are identified by their name, \n" +
                        " where the available names are listed below. \n" +
                        " And the optional value can be used to specify additional parameters for the comparator. \n" +
                        " If a comparator name is not recognized, or if the value is not valid, \n" +
                        " the comparator will be ignored. \n" +
                        " comparator names are defined by the regex: " + VALID_COMPARATOR_NAME.pattern() + "\n" +
                        " Available comparators: " + registeredComparators.keySet()
                )
                .defineList(
                        "comparatorList", //
                        java.util.List.of("isCraftable", "isUndamaged", "isAlmostUnifiedPreferred", "storedAmount"), //
                        () -> "item:ae2:certus_quartz_crystal", // default value
                        o -> o instanceof String && VALID_COMPARATOR_NAME.matcher((String) o).matches()
                );

        CONFIG_SPEC = builder.build();
    }
    private static void registerComparator(String name, int maxOccurrences, Supplier<IComparatorItem.ConfigWritable<?>> supplier,
                                           @Nullable Supplier<Blitter> icon, @Nullable Component tooltip) {
        if (registeredComparators.containsKey(name)) {
            LOGGER.warn("Comparator '{}' is already registered, skipping.", name);
            return;
        }
        registeredComparators.put(name, new ComparatorRegistry(name, maxOccurrences, supplier, icon, tooltip));
    }
    private static void registerOnetimeComparator(String name, IComparatorItem<?> method,
                                                  @Nullable Supplier<Blitter> icon, @Nullable Component tooltip) {
        registerComparator(name, 1, () -> IComparatorItem.wrapSimpleWritable(name, method), icon, tooltip);
    }
    private static void registerInfinityUseComparator(String name, Supplier<IComparatorItem.ConfigWritable<?>> supplier) {
        registerComparator(name, -1, supplier, null, null);
    }
    public static Map<String, ComparatorRegistry> getRegisteredComparators() {
        // Return a copy of the registered comparators to avoid external modifications
        return ImmutableMap.copyOf(registeredComparators);
    }
    public static List<IComparatorItem.ConfigWritable<?>> getComparatorList() {
        // occurrence of a comparator map
        Multiset<String> comparatorOccurrence = HashMultiset.create();
        List<IComparatorItem.ConfigWritable<?>> comparatorItems = Lists.newArrayList();
        var comparatorList = COMPARATOR_LIST.get();
        for (String comparator : comparatorList) {
            // validate the comparator name
            var matcher = VALID_COMPARATOR_NAME.matcher(comparator);
            if (!matcher.matches()) {
                // skip invalid comparator names
                LOGGER.warn("Invalid comparator name: {}", comparator);
                continue;
            }

            // extract the comparator name and value
            var name = matcher.group(1);
            @Nullable String value;
            try {
                value = matcher.group(2);
            } catch (IndexOutOfBoundsException e) {
                // no value specified, use default
                value = null;
            }

            // find the comparator registry by name
            var registry = registeredComparators.get(name);
            if (registry == null) {
                // skip unknown comparator names
                LOGGER.warn("Unknown comparator name: {}", name);
                continue;
            }

            // check if the maximum occurrences of the comparator is reached
            if (registry.maxOccurrences != -1 && comparatorOccurrence.count(name) > registry.maxOccurrences) {
                // skip if the maximum occurrences of the comparator is reached
                LOGGER.warn("Maximum occurrences of comparator '{}' reached: {}", name, registry.maxOccurrences);
                continue;
            }
            comparatorOccurrence.add(name);

            // create the comparator item and parse the value if present
            IComparatorItem.ConfigWritable<?> comparatorItem = registry.supplier.get();
            if (!comparatorItem.parseFromConfig(value)) {
                // skip if the value is not valid for the comparator
                LOGGER.warn("Invalid value '{}' for comparator '{}'", value, name);
                continue;
            }
            comparatorItems.add(comparatorItem);
        }
        return comparatorItems;
    }
    public static void saveConfig(List<IComparatorItem.ConfigWritable<?>> list) {
        // Save the comparator list to the config
        List<String> comparatorList = list.stream()
                .map(IComparatorItem.ConfigWritable::toConfigString)
                .toList();
        // Remove duplicates while preserving order
        comparatorList = Lists.newArrayList(ImmutableSet.copyOf(comparatorList));
        COMPARATOR_LIST.set(comparatorList);
        CONFIG_SPEC.save();
    }

    public static Comparator<GridInventoryEntry> assembleOrderedComparators() {
//        Ae_test.LOGGER.info(getComparatorList().stream()
//                .map(IComparatorItem.ConfigWritable::toConfigString)
//                .collect(Collectors.joining(", ", "Assembling comparators: ", "")));
        return getComparatorList().stream()
                .map(IComparatorItem::getComparator)
                .reduce(Comparator::thenComparing)
                .orElse(Comparator.comparing((GridInventoryEntry e) -> 0)); // Default comparator if no comparators are set
    }

    public static void resetToDefault() {
        COMPARATOR_LIST.set(COMPARATOR_LIST.getDefault());
        CONFIG_SPEC.save();
    }

    public record ComparatorRegistry(String key, int maxOccurrences, Supplier<IComparatorItem.ConfigWritable<?>> supplier, @Nullable Supplier<Blitter> icon, @Nullable Component tooltip) {

        public @Nullable Blitter getIcon() {
            return Optional.ofNullable(icon)
                    .map(Supplier::get)
                    .orElse(null);
        }
    }
}
