package fuzs.enchantmentswitch.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import fuzs.enchantmentswitch.EnchantmentSwitch;
import fuzs.enchantmentswitch.client.EnchantmentSwitchClient;
import fuzs.enchantmentswitch.client.gui.screens.inventory.EditEnchantmentsScreen;
import fuzs.enchantmentswitch.config.ClientConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TriggerLockRenderHandler {
    private static final ItemStack ENCHANTED_BOOK_ITEM_STACK = new ItemStack(Items.ENCHANTED_BOOK);
    public static final int MAX_TRIGGER_TIME = 72_000;
    public static final int CIRCLE_RADIUS = 12;
    private static final float COLOR_RED = 0.0F;
    private static final float COLOR_GREEN = 0.875F;
    private static final float COLOR_BLUE = 0.325F;

    private static float triggerTime;
    @Nullable
    private static Slot hoveredSlot;

    public static EventResult onRenderTooltip(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, List<ClientTooltipComponent> components, ClientTooltipPositioner positioner) {
        if (triggerTime > 0.0F && Minecraft.getInstance().screen instanceof AbstractContainerScreen<?>) {
            if (hoveredSlot != null && hoveredSlot.hasItem()) {
                if (!components.isEmpty() && components.getFirst() instanceof ClientTextTooltip textTooltip) {
                    StringBuilder builder = new StringBuilder();
                    textTooltip.text.accept((int width, Style style, int codePoint) -> {
                        builder.append(Character.toChars(codePoint));
                        return true;
                    });
                    Component hoverName = hoveredSlot.getItem().getHoverName();
                    if (hoverName.getString().contentEquals(builder)) {
                        return EventResult.INTERRUPT;
                    }
                }
            }
        }

        return EventResult.PASS;
    }

    public static void onAfterRender(AbstractContainerScreen<?> screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (screen.getMenu().getCarried().isEmpty() &&
                TriggerLockRenderHandler.isKeyDown(EnchantmentSwitchClient.EDIT_ENCHANTMENTS_KEY_MAPPING)) {
            Slot hoveredSlot = screen.hoveredSlot;
            if (TriggerLockRenderHandler.hoveredSlot != hoveredSlot) {
                // reset trigger time when the hovered slot changes
                resetTriggerValues(hoveredSlot);
            }
            if (isValidSlot(hoveredSlot, screen.minecraft.player)) {
                incrementTriggerTime(screen, hoveredSlot, partialTick);
                // high z offset to render in front of carried item stack
                renderLockTrigger(screen.minecraft, guiGraphics, mouseX, mouseY, partialTick, 600);
            }
        } else {
            resetTriggerValues(null);
        }
    }

    public static void resetTriggerValues(@Nullable Slot hoveredSlot) {
        TriggerLockRenderHandler.triggerTime = 0.0F;
        TriggerLockRenderHandler.hoveredSlot = hoveredSlot;
    }

    private static boolean isValidSlot(@Nullable Slot slot, Player player) {
        if (slot != null && slot.allowModification(player)) {
            ItemStack itemStack = slot.getItem();
            return itemStack.isEnchanted();
        } else {
            return false;
        }
    }

    private static void incrementTriggerTime(AbstractContainerScreen<?> screen, Slot slot, float partialTick) {
        if ((triggerTime += partialTick) >=
                EnchantmentSwitch.CONFIG.get(ClientConfig.class).openEnchantmentsEditorTicks) {
            // just make sure we only trigger once when the max time is reached, then set to some arbitrary value, so we do not trigger again
            if (triggerTime < MAX_TRIGGER_TIME) {
                screen.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                executeTriggerAction(screen, slot);
            }
        }
    }

    private static void executeTriggerAction(AbstractContainerScreen<?> screen, Slot slot) {
        resetTriggerValues(null);
        screen.minecraft.setScreen(new EditEnchantmentsScreen(screen,
                screen.getMenu().containerId,
                slot.getItem(),
                getSlotIndex(slot)));
    }

    public static int getSlotIndex(Slot slot) {
        // creative mode inventory tab uses different slot ids :(
        return slot instanceof CreativeModeInventoryScreen.SlotWrapper slotWrapper ? slotWrapper.target.index :
                slot.index;
    }

    public static boolean isKeyDown(KeyMapping keyMapping) {
        // we need to listen to repeat events for the key press, this is not possible using the key mapping instance
        if (keyMapping.key.getType() == InputConstants.Type.KEYSYM &&
                keyMapping.key.getValue() != InputConstants.UNKNOWN.getValue()) {
            return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keyMapping.key.getValue());
        } else {
            return false;
        }
    }

    /**
     * Copied from Patchouli's <a
     * href="https://github.com/VazkiiMods/Patchouli/blob/1.20.x/Xplat/src/main/java/vazkii/patchouli/client/handler/TooltipHandler.java">TooltipHandler</a>,
     * thanks!
     */
    private static void renderLockTrigger(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int zOffset) {

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        BufferBuilder bufferBuilder = Tesselator.getInstance()
                .begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

        // just some tick counter for the pulsing effect
        float alpha = 0.5F + 0.2F * ((float) Math.cos(minecraft.player.tickCount + partialTick / 10) * 0.5F + 0.5F);
        bufferBuilder.addVertex(mouseX, mouseY, zOffset)
                .setColor(COLOR_RED / 2.0F, COLOR_GREEN / 2.0F, COLOR_BLUE / 2.0F, alpha);

        float angles = Math.min(1.0F,
                triggerTime / EnchantmentSwitch.CONFIG.get(ClientConfig.class).openEnchantmentsEditorTicks) * 360.0F;
        for (float f = angles; f >= 0.0F; f--) {
            float rad = (f - 90.0F) / 180.0F * Mth.PI;
            bufferBuilder.addVertex(mouseX + Mth.cos(rad) * (float) CIRCLE_RADIUS,
                    mouseY + Mth.sin(rad) * (float) CIRCLE_RADIUS,
                    zOffset).setColor(COLOR_RED, COLOR_GREEN, COLOR_BLUE, 1.0F);
        }

        bufferBuilder.addVertex(mouseX, mouseY, zOffset).setColor(COLOR_RED, COLOR_GREEN, COLOR_BLUE, 0.0F);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, zOffset);
        guiGraphics.renderFakeItem(ENCHANTED_BOOK_ITEM_STACK, mouseX - 8, mouseY - 8);
        guiGraphics.pose().popPose();
    }
}
