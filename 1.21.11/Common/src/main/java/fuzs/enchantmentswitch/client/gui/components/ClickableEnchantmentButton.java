package fuzs.enchantmentswitch.client.gui.components;

import fuzs.enchantmentswitch.client.gui.screens.inventory.EditEnchantmentsScreen;
import fuzs.enchantmentswitch.client.gui.screens.inventory.EnchantmentState;
import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.TooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.enchantment.Enchantment;

public class ClickableEnchantmentButton extends SpritelessImageButton {
    private final boolean isPresent;
    private Component hoveredMessage = CommonComponents.EMPTY;

    public ClickableEnchantmentButton(int x, int y, Holder<Enchantment> enchantment, EnchantmentState enchantmentState, OnPress onPress) {
        super(x,
                y,
                126,
                20,
                0,
                166,
                20,
                EditEnchantmentsScreen.EDIT_ENCHANTMENTS_TEXTURE,
                256,
                256,
                onPress,
                CommonComponents.EMPTY);
        this.isPresent = enchantmentState.isPresent();
        this.setTextureLayout(LEGACY_TEXTURE_LAYOUT);
        this.active = !enchantmentState.isInactive();
        this.setMessage(enchantmentState.getDisplayName(enchantment));
        TooltipBuilder.create(enchantmentState.getTooltip(enchantment)).splitLines().build(this);
    }

    @Override
    public Component getMessage() {
        if (!this.isActive()) {
            return this.inactiveMessage;
        } else if (this.isHoveredOrFocused()) {
            return this.hoveredMessage;
        } else {
            return this.message;
        }
    }

    @Override
    public void setMessage(Component message) {
        this.message = message;
        this.inactiveMessage = ComponentUtils.mergeStyles(message, Style.EMPTY.withColor(0x685E4A));
        this.hoveredMessage = ComponentUtils.mergeStyles(message, Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderContents(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                this.identifier,
                this.getX() + 2,
                this.getY() + 2,
                147 + (this.isPresent ? 0 : 16),
                166 + 16 * this.getTextureY(),
                16,
                16,
                this.textureWidth,
                this.textureHeight);
        this.renderScrollingStringOverContents(guiGraphics.textRendererForWidget(this,
                GuiGraphics.HoveredTextEffects.NONE), this.getMessage(), 2);
    }

    @Override
    public void renderScrollingStringOverContents(ActiveTextCollector activeTextCollector, Component component, int textBorder) {
        int startX = this.getX() + 20 + textBorder;
        int endX = this.getX() + this.getWidth() - textBorder;
        int startY = this.getY();
        int endY = this.getY() + this.getHeight();
        activeTextCollector.acceptScrollingWithDefaultCenter(component, startX, endX, startY, endY);
    }
}
