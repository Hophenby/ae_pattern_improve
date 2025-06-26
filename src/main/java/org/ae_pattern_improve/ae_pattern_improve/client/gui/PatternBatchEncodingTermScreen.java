package org.ae_pattern_improve.ae_pattern_improve.client.gui;

import appeng.api.config.ActionItems;
import appeng.api.stacks.GenericStack;
import appeng.client.Point;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.Icon;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.me.common.ClientDisplaySlot;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.*;
import appeng.core.localization.GuiText;
import appeng.core.network.ServerboundPacket;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantics;
import com.google.common.primitives.Longs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.ae_pattern_improve.ae_pattern_improve.config.ClientConfig;
import org.ae_pattern_improve.ae_pattern_improve.client.AECustomSemantics;
import org.ae_pattern_improve.ae_pattern_improve.client.ButtonUtils;
import org.ae_pattern_improve.ae_pattern_improve.client.gui.widgets.MirrorScrollBar;
import org.ae_pattern_improve.ae_pattern_improve.common.menu.PatternBatchEncodingTermMenu;
import org.ae_pattern_improve.ae_pattern_improve.mixin_helpers.IMoveSlot;
import org.ae_pattern_improve.ae_pattern_improve.mixins.client.MixinAbstractContainerScreen;

import java.util.Objects;

public class PatternBatchEncodingTermScreen<MENU extends PatternBatchEncodingTermMenu> extends MEStorageScreen<MENU> implements IMoveSlot {
    private static final Blitter SIDE_PANEL = Blitter.texture("guis/batch_encoder.png",384 ,384).src(202, 0, 85, 139);

    private final ActionButton clearFilteringBtn;
    private final ActionButton clearEncodingBtn;
    private final ActionButton cycleOutputBtn;
    private final AE2Button encodeBtn;
//    private final AE2Button switchToEncodingPreferenceScreenButton;

    private final MirrorScrollBar scrollbarUpper;
    private final MirrorScrollBar scrollbarLower;
    private final Button[] multiplierButtons = new Button[6];

    private int slotYShift = 0;

    public PatternBatchEncodingTermScreen(MENU menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        widgets.add("sidePanel", new ICompositeWidget() {
            private int x = 0;
            private int y = 0;
            @Override
            public void setPosition(Point position) {
                x = position.getX();
                y = position.getY();
            }
            @Override
            public void setSize(int width, int height) {
            }
            @Override
            public Rect2i getBounds() {
                return new Rect2i(x, y, 77, 139);
            }
            @Override
            public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
                SIDE_PANEL.dest(bounds.getX() + 195, bounds.getY() + bounds.getHeight() - 227).blit(guiGraphics);
            }
            @Override
            public boolean onMouseWheel(Point mousePos, double delta) {
                return scrollbarUpper.onMouseWheel(mousePos, delta);
            }
        });

        clearFilteringBtn = new ActionButton(ActionItems.S_CLOSE, act -> menu.clearFilteringArea());
        clearFilteringBtn.setHalfSize(true);
        clearFilteringBtn.setDisableBackground(true);
        widgets.add("clearFiltering", clearFilteringBtn);

        clearEncodingBtn = new ActionButton(ActionItems.S_CLOSE, act -> menu.clearEncodingArea());
        clearEncodingBtn.setHalfSize(true);
        clearEncodingBtn.setDisableBackground(true);
        widgets.add("clearEncoding", clearEncodingBtn);

        this.cycleOutputBtn = new ActionButton(
                ActionItems.S_CYCLE_PROCESSING_OUTPUT,
                act -> menu.cycleProcessingOutput());
        this.cycleOutputBtn.setHalfSize(true);
        this.cycleOutputBtn.setDisableBackground(true);
        widgets.add("processingCycleOutput", this.cycleOutputBtn);

        this.scrollbarUpper = new MirrorScrollBar(Scrollbar.SMALL);
        // The scrollbar ranges from 0 to the number of rows not visible
        this.scrollbarUpper.setRange(0, menu.getProcessingOutputSlots().length / 3 - 3, 1);
        this.scrollbarUpper.setCaptureMouseWheel(false);
        widgets.add("upperScrollbar", this.scrollbarUpper);

        this.scrollbarLower = this.scrollbarUpper.getOrCreateOther(Scrollbar.SMALL);
        // The scrollbar ranges from 0 to the number of rows not visible
        this.scrollbarLower.setRange(0, menu.getProcessingOutputSlots().length / 3 - 3, 1);
        this.scrollbarLower.setHeight((int) (16 * 0.32));
        this.scrollbarLower.setCaptureMouseWheel(false);
        widgets.add("lowerScrollbar", this.scrollbarLower);

        encodeBtn = widgets.addButton("batchEncode", Component.translatable("gui.ae_pattern_improve.batch_pattern_encoder.batch_encode"), act -> menu.encode());
        encodeBtn.setSize(80, 20);

        int[] multipliers = {2, 3, 5};
        for (int i = 0; i < 6; i++) {
            boolean isDiv = i < 3;
            int multiplier = isDiv ? multipliers[i] : multipliers[i - 3];
            multiplierButtons[i] = ButtonUtils.getMultiplyButton(menu, multiplier, isDiv);
            if (ClientConfig.isMultiplierButtonEnabled()) {
                widgets.add("multiplierButton" + (isDiv ? "Div" : "Mul") + multiplier, multiplierButtons[i]);
            }
        }

        this.addToLeftToolbar(ButtonUtils.createEncodingPreferenceScreenButton(this));
    }
    // used by the mixin to adjust the slot position
    @Override
    public int getSlotYShift() {
        return slotYShift;
    }

    @Override
    public void updateBeforeRender() {
        // Update the processing slot position/visibility
        slotYShift = -scrollbarUpper.getCurrentScroll() * 18 * 3;
        repositionSlots(SlotSemantics.PROCESSING_INPUTS);
        slotYShift = -scrollbarUpper.getCurrentScroll() * 18;
        repositionSlots(SlotSemantics.PROCESSING_OUTPUTS);
        slotYShift = 0;
        repositionSlots(AECustomSemantics.BATCH_FILTER);

        cycleOutputBtn.setVisibility(menu.canCycleProcessingOutputs());

        for (int i = 0; i < menu.getProcessingInputSlots().length; i++) {
            var slot = menu.getProcessingInputSlots()[i];
            var effectiveRow = (i / 3) - scrollbarUpper.getCurrentScroll() * 3;

            slot.setActive(effectiveRow >= 0 && effectiveRow < 3);
        }
        for (int i = 0; i < menu.getProcessingOutputSlots().length; i++) {
            var slot = menu.getProcessingOutputSlots()[i];
            var effectiveRow = i - scrollbarUpper.getCurrentScroll() * 3;

            slot.setActive(effectiveRow >= 0 && effectiveRow < 3);
        }
        updateTooltipVisibility();
    }
    private void updateTooltipVisibility() {
        widgets.setTooltipAreaEnabled("processing-primary-output", scrollbarUpper.getCurrentScroll() == 0);
        widgets.setTooltipAreaEnabled("processing-optional-output1", scrollbarUpper.getCurrentScroll() > 0);
        widgets.setTooltipAreaEnabled("processing-optional-output2", true);
        widgets.setTooltipAreaEnabled("processing-optional-output3", true);
        widgets.setTooltipAreaEnabled("processing-input", true);
        widgets.setTooltipAreaEnabled("filter-input", true);
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        Objects.requireNonNull(this.minecraft, "Minecraft instance is null, this should not happen :(");
        if (this.minecraft.options.keyPickItem.matchesMouse(btn)) {
            var slot = ((MixinAbstractContainerScreen) this).ae_pattern_improve$findSlot(xCoord, yCoord);
            if (menu.isProcessingPatternSlot(slot) && slot.hasItem()) {
                var currentStack = GenericStack.fromItemStack(slot.getItem());
                if (currentStack != null) {
                    // Open a sub-screen to set the amount of the pattern
                    var screen = new AESubScreen<MENU, PatternBatchEncodingTermScreen<MENU>>(
                            this,"/screens/set_processing_pattern_amount.json"){
                        private NumberEntryWidget amount;
                        @Override
                        public void init() {

                            widgets.addButton("save", GuiText.Set.text(), this::confirm);

                            var icon = getMenu().getHost().getMainMenuIcon();
                            var button = new TabButton(Icon.BACK, icon.getHoverName(), btn -> returnToParent());
                            widgets.add("back", button);

                            this.amount = widgets.addNumberEntryWidget("amountToStock", NumberEntryType.of(currentStack.what()));
                            this.amount.setLongValue(currentStack.amount());
                            this.amount.setMaxValue(Integer.MAX_VALUE);
                            this.amount.setTextFieldStyle(style.getWidget("amountToStockInput"));
                            this.amount.setMinValue(0);
                            this.amount.setHideValidationIcon(true);
                            this.amount.setOnConfirm(this::confirm);

                            addClientSideSlot(new ClientDisplaySlot(currentStack), SlotSemantics.MACHINE_OUTPUT);
                            super.init();
                            setSlotsHidden(SlotSemantics.TOOLBOX, true);

                        }
                        private void confirm() {
                            this.amount.getLongValue().ifPresent(newAmount -> {
                                newAmount = Longs.constrainToRange(newAmount, 0, Integer.MAX_VALUE);

                                if (newAmount <= 0) {
                                    setter(null);
                                } else {
                                    setter(new GenericStack(currentStack.what(), newAmount));
                                }
                                returnToParent();
                            });
                        }
                        private void setter(GenericStack stack) {
                            ServerboundPacket message = new InventoryActionPacket(
                                    InventoryAction.SET_FILTER, slot.index,
                                    GenericStack.wrapInItemStack(stack));
                            PacketDistributor.sendToServer(message);
                        }
                    };
                    switchToScreen(screen);
                    return true;
                }
            }
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }
}
