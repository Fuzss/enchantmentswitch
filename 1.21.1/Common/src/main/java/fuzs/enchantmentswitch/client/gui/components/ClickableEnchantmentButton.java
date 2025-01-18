package fuzs.enchantmentswitch.client.gui.components;

import fuzs.enchantmentswitch.client.gui.screens.inventory.EditEnchantmentsScreen;
import fuzs.enchantmentswitch.client.gui.screens.inventory.EnchantmentState;
import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import fuzs.puzzleslib.api.client.gui.v2.components.tooltip.TooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;

public class ClickableEnchantmentButton extends SpritelessImageButton {
    private final boolean isPresent;

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
                enchantmentState.getDisplayName(enchantment));
        this.isPresent = enchantmentState.isPresent();
        this.setTextureLayout(LEGACY_TEXTURE_LAYOUT);
        this.active = !enchantmentState.isInactive();
        TooltipBuilder.create(enchantmentState.getTooltip(enchantment)).splitLines().build(this);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(this.resourceLocation,
                this.getX() + 2,
                this.getY() + 2,
                147 + (this.isPresent ? 0 : 16),
                166 + 16 * this.getTextureY(),
                16,
                16,
                this.textureWidth,
                this.textureHeight);
        this.renderString(guiGraphics,
                Minecraft.getInstance().font,
                this.getFontColor() | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    public void renderString(GuiGraphics guiGraphics, Font font, int color) {
        this.renderScrollingString(guiGraphics, font, 2, color);
    }

    @Override
    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, int borderGap, int color) {
        int startX = this.getX() + 20 + borderGap;
        int endX = this.getX() + this.getWidth() - borderGap;
        renderScrollingString(guiGraphics,
                font,
                this.getMessage(),
                startX,
                this.getY(),
                endX,
                this.getY() + this.getHeight(),
                color);
    }

    private int getFontColor() {
        return !this.isActive() ? 0x685E4A : this.isHoveredOrFocused() ? ChatFormatting.YELLOW.getColor() : 0xFFFFFF;
    }
}
