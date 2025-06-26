package org.ae_pattern_improve.ae_pattern_improve.mixins;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.integration.modules.itemlists.EncodingHelper;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.slot.FakeSlot;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.ae_pattern_improve.ae_pattern_improve.config.ClientComparatorConfig;
import org.ae_pattern_improve.ae_pattern_improve.ae_entry_comparator.EntryComparators;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(EncodingHelper.class)
public interface MixinEncodingHelper {
    @Accessor("ENTRY_COMPARATOR")
    static Comparator<GridInventoryEntry> entryComparator() {
        throw new AssertionError();
    }

    @Invoker("encodeBestMatchingStacksIntoSlots")
    static void encodeBestMatchingStacksIntoSlots(List<List<GenericStack>> possibleInputsBySlot,
                                                         Map<AEKey, Integer> ingredientPriorities,
                                                         FakeSlot[] slots){}
    @Invoker("isUndamaged")
    static Boolean isUndamaged(GridInventoryEntry entry){
        throw new AssertionError();
    }

    @Mixin(EncodingHelper.class)
    abstract class MixinEncodingHelperModifier {
        // This mixin is used to modify the EncodingHelper class to use the Almost Unified adapter for item stacks.

        @ModifyVariable(
                method = "getIngredientPriorities",
                at = @At("HEAD"),
                argsOnly = true,
                index = 1
        )
        private static Comparator<GridInventoryEntry> modifyComparator(Comparator<GridInventoryEntry> entry) {
            if (Objects.equals(entry, entryComparator())){
                return ClientComparatorConfig.assembleOrderedComparators();
            }
            return entry;
        }
        @ModifyExpressionValue(
                method = "getIngredientPriorities",
                at = @At(value = "INVOKE", target = "Lappeng/menu/me/common/IClientRepo;getAllEntries()Ljava/util/Set;"))
        private static Set<GridInventoryEntry> modifyMEStorageEntriesForSorting(Set<GridInventoryEntry> entries) {
            // ensure that the MEStorageMenu entries are sorted by the configured comparator
//            Ae_test.LOGGER.debug("Before sorting MEStorage entries: {}", entries.size());
            var newEntries = ClientComparatorConfig.getComparatorList().stream()
                    .filter(EntryComparators.class::isInstance)
                    .map(EntryComparators.class::cast)
                    .map(EntryComparators::getAEKey)
                    .filter(Objects::nonNull)
                    .map(aeKey -> new GridInventoryEntry(-1, aeKey, 0, 0, false))
                    .collect(Collectors.toSet());
            if (!newEntries.isEmpty()) {
                // Prevent modifying the original entries set
                var copiedEntries = new HashSet<>(entries);
                copiedEntries.addAll(newEntries);
//                Ae_test.LOGGER.debug("After sorting MEStorage entries: {}", copiedEntries.size());
                return copiedEntries;
            }
            return entries;
        }
    }
}
