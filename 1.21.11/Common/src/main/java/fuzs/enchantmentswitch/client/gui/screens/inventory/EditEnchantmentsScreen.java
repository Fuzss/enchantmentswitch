package fuzs.enchantmentswitch.client.gui.screens.inventory;

import fuzs.enchantmentswitch.EnchantmentSwitch;
import fuzs.enchantmentswitch.client.gui.components.AbstractMenuSelectionList;
import fuzs.enchantmentswitch.client.gui.components.ClickableEnchantmentButton;
import fuzs.enchantmentswitch.client.util.EnchantmentTooltipHelper;
import fuzs.enchantmentswitch.init.ModRegistry;
import fuzs.enchantmentswitch.network.client.ServerboundSetEnchantmentsMessage;
import fuzs.puzzleslib.api.client.gui.v2.ScreenHelper;
import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.ClientComponentSplitter;
import fuzs.puzzleslib.api.client.gui.v2.tooltip.TooltipRenderHelper;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import net.minecraft.resources.Identifier;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditEnchantmentsScreen extends Screen {
    public static final Component COMPONENT_EDIT_ENCHANTMENTS = Component.translatable("enchantments.edit");
    public static final Identifier EDIT_ENCHANTMENTS_TEXTURE = EnchantmentSwitch.id(
            "textures/gui/enchantments.png");
    private static final Identifier TEXT_FIELD_SPRITE = Identifier.withDefaultNamespace(
            "container/anvil/text_field");
    private static final Identifier TEXT_FIELD_DISABLED_SPRITE = Identifier.withDefaultNamespace(
            "container/anvil/text_field_disabled");

    @Nullable
    private final Screen lastScreen;
    private final int containerId;
    private final ItemStack itemStack;
    private final int slotIndex;
    private final ItemEnchantments enchantmentLookup;
    private final ItemEnchantments.Mutable itemEnchantments;
    private final ItemEnchantments.Mutable storedEnchantments;
    public int imageWidth = 176;
    public int imageHeight = 166;
    public int leftPos;
    public int topPos;
    private EditBox name;
    private EnchantmentSelectionList scrollingList;
    private List<? extends ClientTooltipComponent> itemTooltip;

    public EditEnchantmentsScreen(@Nullable Screen lastScreen, int containerId, ItemStack itemStack, int slotIndex) {
        super(COMPONENT_EDIT_ENCHANTMENTS);
        this.lastScreen = lastScreen;
        this.containerId = containerId;
        this.itemStack = itemStack.copy();
        this.slotIndex = slotIndex;
        this.enchantmentLookup = ServerboundSetEnchantmentsMessage.createEnchantmentLookup(itemStack);
        this.itemEnchantments = new ItemEnchantments.Mutable(this.itemStack.getOrDefault(DataComponents.ENCHANTMENTS,
                ItemEnchantments.EMPTY));
        this.storedEnchantments = new ItemEnchantments.Mutable(this.itemStack.getOrDefault(ModRegistry.STORED_ENCHANTMENTS_DATA_COMPONENT_TYPE.value(),
                ItemEnchantments.EMPTY));
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.addRenderableWidget(new SpritelessImageButton(this.leftPos + this.imageWidth - 3 - 26 + 5,
                this.topPos - 23 + 5,
                16,
                16,
                this.imageWidth + 5,
                5,
                16 + 7,
                EDIT_ENCHANTMENTS_TEXTURE,
                256,
                256,
                (Button button) -> {
                    this.onClose();
                }));
        this.name = new EditBox(this.font, this.leftPos + 62, this.topPos + 24, 103, 12, COMPONENT_EDIT_ENCHANTMENTS);
        this.name.setFocused(false);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(false);
        this.name.setMaxLength(50);
        this.name.setValue(this.itemStack.getHoverName().getString());
        this.name.setEditable(false);
        this.addWidget(this.name);
        this.scrollingList = new EnchantmentSelectionList(this.leftPos + 18, this.topPos + 64);
        this.addRenderableWidget(this.scrollingList);
        this.refreshScrollingList();
    }

    private void refreshScrollingList() {
        this.refreshItemTooltip();
        int size = this.scrollingList.children().size();
        this.scrollingList.clearEntries();
        HolderLookup.Provider registries = this.minecraft.getConnection().registryAccess();
        HolderSet<Enchantment> holders = registries.lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(EnchantmentTags.TOOLTIP_ORDER);
        for (Holder<Enchantment> enchantment : holders) {
            int enchantmentLevel = this.enchantmentLookup.getLevel(enchantment);
            if (enchantmentLevel > 0) {
                EnchantmentState enchantmentState = EnchantmentState.create(enchantment,
                        this.itemEnchantments.getLevel(enchantment) > 0,
                        enchantmentLevel,
                        this.itemEnchantments.toImmutable());
                this.scrollingList.addEntry(this.scrollingList.new Entry(enchantment, enchantmentState));
            }
        }
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : this.enchantmentLookup.entrySet()) {
            Holder<Enchantment> enchantment = entry.getKey();
            if (!holders.contains(enchantment)) {
                EnchantmentState enchantmentState = EnchantmentState.create(enchantment,
                        this.itemEnchantments.getLevel(enchantment) > 0,
                        entry.getIntValue(),
                        this.itemEnchantments.toImmutable());
                this.scrollingList.addEntry(this.scrollingList.new Entry(enchantment, enchantmentState));
            }
        }
        if (size != this.scrollingList.children().size()) {
            this.scrollingList.setScrollAmount(0.0);
        }
    }

    private void refreshItemTooltip() {
        List<Component> tooltipLines = new ArrayList<>();
        tooltipLines.add(EnchantmentTooltipHelper.getItemDisplayName(this.itemStack));
        Item.TooltipContext tooltipContext = Item.TooltipContext.of(this.minecraft.level);
        this.itemEnchantments.toImmutable()
                .addToTooltip(tooltipContext, tooltipLines::add, TooltipFlag.NORMAL, this.itemStack);
        this.storedEnchantments.toImmutable().addToTooltip(tooltipContext, (Component component) -> {
            tooltipLines.add(EnchantmentTooltipHelper.applyStoredEnchantmentStyle(component));
        }, TooltipFlag.NORMAL, this.itemStack);
        this.itemTooltip = ClientComponentSplitter.splitTooltipLines(tooltipLines)
                .map(ClientTooltipComponent::create)
                .toList();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, this.title, this.leftPos + 62, this.topPos + 8, 0xFF404040, false);
        this.name.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(2.0F, 2.0F);
        guiGraphics.renderFakeItem(this.itemStack, (this.leftPos + 17) / 2, (this.topPos + 8) / 2);
        guiGraphics.pose().popMatrix();
        if (this.itemTooltip != null && ScreenHelper.isHovering(this.leftPos + 17,
                this.topPos + 8,
                32,
                32,
                mouseX,
                mouseY)) {
            TooltipRenderHelper.renderTooltipComponents(guiGraphics, mouseX, mouseY, this.itemTooltip);
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                EDIT_ENCHANTMENTS_TEXTURE,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                256,
                256);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                EDIT_ENCHANTMENTS_TEXTURE,
                this.leftPos + this.imageWidth - 3 - 26,
                this.topPos - 23,
                this.imageWidth,
                0,
                26,
                23,
                256,
                256);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                TEXT_FIELD_SPRITE,
                this.leftPos + 59,
                this.topPos + 20,
                110,
                16);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (super.keyPressed(keyEvent)) {
            return true;
        } else if (KeyMappingHelper.isKeyActiveAndMatches(this.minecraft.options.keyInventory, keyEvent)) {
            this.onClose();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClose() {
        ItemEnchantments storedEnchantments = this.itemStack.getOrDefault(ModRegistry.STORED_ENCHANTMENTS_DATA_COMPONENT_TYPE.value(),
                ItemEnchantments.EMPTY);
        if (!Objects.equals(this.storedEnchantments.toImmutable(), storedEnchantments)) {
            // setting this on the client is important for the creative inventory,
            // since for tabs other than inventory the slot index does not match on the server,
            // and it depends on client data being synced
            ServerboundSetEnchantmentsMessage.setEnchantments(this.minecraft.player,
                    this.containerId,
                    this.slotIndex,
                    this.storedEnchantments.keySet());
            MessageSender.broadcast(new ServerboundSetEnchantmentsMessage(this.containerId,
                    this.slotIndex,
                    this.storedEnchantments.keySet()));
        }

        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean moveEnchantmentToOppositeList(Holder<Enchantment> enchantment) {
        int enchantmentLevel = this.itemEnchantments.getLevel(enchantment);
        if (enchantmentLevel > 0) {
            this.itemEnchantments.set(enchantment, 0);
            this.storedEnchantments.set(enchantment, enchantmentLevel);
            return true;
        }
        enchantmentLevel = this.storedEnchantments.getLevel(enchantment);
        if (enchantmentLevel > 0) {
            if (EnchantmentHelper.isEnchantmentCompatible(this.itemEnchantments.keySet(), enchantment)) {
                this.storedEnchantments.set(enchantment, 0);
                this.itemEnchantments.set(enchantment, enchantmentLevel);
                return true;
            }
        }
        return false;
    }

    private class EnchantmentSelectionList extends AbstractMenuSelectionList<EnchantmentSelectionList.Entry> {

        public EnchantmentSelectionList(int x, int y) {
            super(EditEnchantmentsScreen.this.minecraft, x, y, 126, 90, 20, 9);
        }

        class Entry extends AbstractMenuSelectionList.Entry<Entry> {

            public Entry(Holder<Enchantment> enchantment, EnchantmentState enchantmentState) {
                this.addRenderableWidget(new ClickableEnchantmentButton(EnchantmentSelectionList.this.getX(),
                        EnchantmentSelectionList.this.getY(),
                        enchantment,
                        enchantmentState,
                        (Button button) -> {
                            if (EditEnchantmentsScreen.this.moveEnchantmentToOppositeList(enchantment)) {
                                EditEnchantmentsScreen.this.refreshScrollingList();
                            }
                        }));
            }
        }
    }
}
