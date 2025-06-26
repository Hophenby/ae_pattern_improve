package org.ae_pattern_improve.ae_pattern_improve.common.menu;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.Icon;
import appeng.core.definitions.AEItems;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.slot.FakeSlot;
import appeng.menu.slot.RestrictedInputSlot;
import appeng.util.ConfigInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.hosts.IPatternBatchTermMenuHost;
import org.ae_pattern_improve.ae_pattern_improve.ae_pattern.PatternBatchEncodingLogic;
import org.ae_pattern_improve.ae_pattern_improve.ae_pattern.PatternUtils;
import org.ae_pattern_improve.ae_pattern_improve.client.AECustomSemantics;
import org.ae_pattern_improve.ae_pattern_improve.common.filter.AEItemTagFilter;
import org.ae_pattern_improve.ae_pattern_improve.common.filter.AEKeyTagFilter;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IMulableTermMenu;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PatternBatchEncodingTermMenu extends MEStorageMenu implements IMulableTermMenu {
    private static final int PATTERN_SLOTS = 9;

    private static final String ACTION_ENCODE = "encode";
    private static final String ACTION_CLEAR_ENCODING_AREA = "clearEncodingArea";
    private static final String ACTION_CLEAR_FILTERING_AREA = "clearFilteringArea";
    private static final String ACTION_CYCLE_PROCESSING_OUTPUT = "cycleProcessingOutput";

    private final PatternBatchEncodingLogic logic;

    public FakeSlot[] getProcessingInputSlots() {
        return processingInputSlots;
    }

    public FakeSlot[] getProcessingOutputSlots() {
        return processingOutputSlots;
    }

    private final FakeSlot[] processingInputSlots = new FakeSlot[AEProcessingPattern.MAX_INPUT_SLOTS];
    private final FakeSlot[] processingOutputSlots = new FakeSlot[AEProcessingPattern.MAX_OUTPUT_SLOTS];
    private final FakeSlot[] filteringInputSlots = new FakeSlot[PATTERN_SLOTS];
    private final ConfigInventory encodedInputsInv;
    private final ConfigInventory encodedOutputsInv;
    private final ConfigInventory srcMaterialsInv;
    private final RestrictedInputSlot blankPatternSlot;
    private final RestrictedInputSlot[] encodedPatternSlot = new RestrictedInputSlot[PATTERN_SLOTS];

    public static final MenuType<PatternBatchEncodingTermMenu> TYPE = MenuTypeBuilder
            .create(PatternBatchEncodingTermMenu::new, IPatternBatchTermMenuHost.class)
            .build("pattern_batch_encoding_terminal");
    public PatternBatchEncodingTermMenu(int id, Inventory ip, IPatternBatchTermMenuHost host) {
        this(TYPE, id, ip, host, true);
    }

    public PatternBatchEncodingTermMenu(MenuType<?> menuType, int id, Inventory ip, IPatternBatchTermMenuHost host, boolean bindInventory) {
        super(menuType, id, ip, host, bindInventory);
        this.logic = host.getLogic();
        this.encodedInputsInv = logic.getEncodedInputInv();
        this.encodedOutputsInv = logic.getEncodedOutputInv();
        this.srcMaterialsInv = logic.getSrcMaterialInv();

        // Wrappers for use with slots
        var encodedInputs = encodedInputsInv.createMenuWrapper();
        var encodedOutputs = encodedOutputsInv.createMenuWrapper();
        var srcMaterials = srcMaterialsInv.createMenuWrapper();


        // Create as many slots as needed for processing inputs and outputs
        for (int i = 0; i < processingInputSlots.length; i++) {
            this.addSlot(this.processingInputSlots[i] = new FakeSlot(encodedInputs, i),
                    SlotSemantics.PROCESSING_INPUTS);
        }
        for (int i = 0; i < this.processingOutputSlots.length; i++) {
            this.addSlot(this.processingOutputSlots[i] = new FakeSlot(encodedOutputs, i),
                    SlotSemantics.PROCESSING_OUTPUTS);
        }
        this.processingOutputSlots[0].setIcon(Icon.BACKGROUND_PRIMARY_OUTPUT);
        for (int i = 0; i < filteringInputSlots.length; i++) {
            this.addSlot(this.filteringInputSlots[i] = new FakeSlot(srcMaterials, i),
                    AECustomSemantics.BATCH_FILTER);
        }

        this.addSlot(this.blankPatternSlot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.BLANK_PATTERN,
                logic.getBlankPatternInv(), 0), SlotSemantics.BLANK_PATTERN);
        for (int i = 0; i < PATTERN_SLOTS; i++) {
            this.addSlot(
                    this.encodedPatternSlot[i] = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN,
                            logic.getEncodedPatternInv(), i),
                    SlotSemantics.ENCODED_PATTERN);

            this.encodedPatternSlot[i].setStackLimit(1);
        }
        registerClientAction(ACTION_ENCODE, this::encode);
        registerClientAction(ACTION_CLEAR_ENCODING_AREA, this::clearEncodingArea);
        registerClientAction(ACTION_CLEAR_FILTERING_AREA, this::clearFilteringArea);
        registerClientAction(ACTION_CYCLE_PROCESSING_OUTPUT, this::cycleProcessingOutput);

        var multipliers = new int[]{2, 3, 5};
        for (int multiplier : multipliers) {
            registerClientAction(getMulPatternEncodingActionName(multiplier, false), () -> mulPatternEncoding(multiplier, false));
            registerClientAction(getMulPatternEncodingActionName(multiplier, true), () -> mulPatternEncoding(multiplier, true));
        }
    }

    public void encode() {
        if (isClientSide()) {
            sendClientAction(ACTION_ENCODE);
            return;
        }
        for (int i = 0; i < PATTERN_SLOTS; i++)
        {
            ItemStack encodedPattern = encodePattern(i);
            if (encodedPattern != null) {
                var encodeOutput = this.encodedPatternSlot[i].getItem();

                // first check the output slots, should either be null, or a pattern (encoded or otherwise)
                if (!encodeOutput.isEmpty()
                        && !PatternDetailsHelper.isEncodedPattern(encodeOutput)
                        && !AEItems.BLANK_PATTERN.is(encodeOutput)) {
                    continue;
                } // if nothing is there we should snag a new pattern.
                else if (encodeOutput.isEmpty()) {
                    var blankPattern = this.blankPatternSlot.getItem();
                    if (!isPattern(blankPattern)) {
                        continue; // no blanks.
                    }

                    // remove one, and clear the input slot.
                    blankPattern.shrink(1);
                    if (blankPattern.getCount() <= 0) {
                        this.blankPatternSlot.set(ItemStack.EMPTY);
                    }
                }

                this.encodedPatternSlot[i].set(encodedPattern);
            } else {
                clearPattern(i);
            }
        }
    }
    private boolean isPattern(ItemStack output) {
        if (output.isEmpty()) {
            return false;
        }

        return AEItems.BLANK_PATTERN.is(output);
    }

    private boolean isEncodingAreaValid() {
        boolean valid = false;
        for (int slot = 0; slot < encodedInputsInv.size(); slot++) {
            if (encodedInputsInv.getStack(slot) != null) {
                // At least one input must be set, but it doesn't matter which one
                valid = true;
            }
        }
        if (!valid) {
            return false;
        }
        return encodedOutputsInv.getStack(0) != null;
        // The first output slot is required
    }

    @Nullable
    private ItemStack encodePattern(int slotIndex) {
        if (!isEncodingAreaValid()) {
            return null; // Invalid encoding area
        }
        var inputs = new GenericStack[encodedInputsInv.size()];
        var outputs = new GenericStack[encodedOutputsInv.size()];
        var srcMaterial = srcMaterialsInv.getStack(slotIndex);
        if (srcMaterial == null) {
            return null; // No source material set
        }
        for (int i = 0; i < encodedInputsInv.size(); i++) {
            inputs[i] = AEKeyTagFilter.findGenericStack(encodedInputsInv.getStack(i), srcMaterial).orElse(encodedInputsInv.getStack(i));
        }
        for (int i = 0; i < encodedOutputsInv.size(); i++) {
            outputs[i] = AEItemTagFilter.findGenericStack(encodedOutputsInv.getStack(i), srcMaterial).orElse(encodedOutputsInv.getStack(i));
        }

        return PatternDetailsHelper.encodeProcessingPattern(Arrays.asList(inputs), Arrays.asList(outputs));
    }

    /**
     * Clears the pattern in the encoded pattern slot.
     */
    private void clearPattern(int slotIndex) {
        var encodedPattern = this.encodedPatternSlot[slotIndex].getItem();
        if (PatternDetailsHelper.isEncodedPattern(encodedPattern)) {
            this.encodedPatternSlot[slotIndex].set(
                    AEItems.BLANK_PATTERN.stack(encodedPattern.getCount()));
        }
    }
    public void clearEncodingArea() {
        if (isClientSide()) {
            sendClientAction(ACTION_CLEAR_ENCODING_AREA);
            return;
        }

        encodedInputsInv.clear();
        encodedOutputsInv.clear();

        this.broadcastChanges();
    }
    public void clearFilteringArea() {
        if (isClientSide()) {
            sendClientAction(ACTION_CLEAR_FILTERING_AREA);
            return;
        }
        srcMaterialsInv.clear();

        this.broadcastChanges();
    }

    public void cycleProcessingOutput() {
        if (isClientSide()) {
            sendClientAction(ACTION_CYCLE_PROCESSING_OUTPUT);
        } else {

            var newOutputs = new ItemStack[processingOutputSlots.length];
            for (int i = 0; i < processingOutputSlots.length; i++) {
                newOutputs[i] = ItemStack.EMPTY;
                if (!processingOutputSlots[i].getItem().isEmpty()) {
                    // Search for the next, skipping empty slots
                    for (int j = 1; j < processingOutputSlots.length; j++) {
                        var nextItem = processingOutputSlots[(i + j) % processingOutputSlots.length].getItem();
                        if (!nextItem.isEmpty()) {
                            newOutputs[i] = nextItem;
                            break;
                        }
                    }
                }
            }

            for (int i = 0; i < newOutputs.length; i++) {
                processingOutputSlots[i].set(newOutputs[i]);
            }
        }
    }
    // Can cycle if there is more than 1 processing output encoded
    public boolean canCycleProcessingOutputs() {
        return Arrays.stream(processingOutputSlots).filter(s -> !s.getItem().isEmpty()).count() > 1;
    }
    @Override
    protected int transferStackToMenu(ItemStack input) {
        int initialCount = input.getCount();

        // try refilling the blank pattern slot
        if (blankPatternSlot.mayPlace(input)) {
            input = blankPatternSlot.safeInsert(input);
            if (input.isEmpty()) {
                return initialCount;
            }
        }

        // try refilling the encoded pattern slot
        for (RestrictedInputSlot slot : encodedPatternSlot)
        {
            if (slot.mayPlace(input)) {
                input = slot.safeInsert(input);
                if (input.isEmpty()) {
                    return initialCount;
                }
            }
        }

        int transferred = initialCount - input.getCount();
        return transferred + super.transferStackToMenu(input);
    }

    public boolean isProcessingPatternSlot(Slot slot) {

        for (var pSlot : processingOutputSlots) {
            if (pSlot == slot) {
                return true;
            }
        }

        for (var pSlot : processingInputSlots) {
            if (pSlot == slot) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void mulPatternEncoding(int multiplier, boolean divMode) {
        if (isClientSide()) {
            sendClientAction(getMulPatternEncodingActionName(multiplier, divMode));
        } else {
            PatternUtils.mulPatternEncodingArea(
                    processingInputSlots,
                    processingOutputSlots,
                    multiplier,
                    divMode
            );
        }
    }

    private String getMulPatternEncodingActionName(int multiplier, boolean divMode) {
        if (divMode) {
            return "patternEncodingDivide" + multiplier;
        } else {
            return "patternEncodingMultiply" + multiplier;
        }
    }
}
