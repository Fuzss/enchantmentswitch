package fuzs.enchantmentswitch.client.handler;

import fuzs.enchantmentswitch.client.gui.util.EnchantmentTooltipHelper;
import fuzs.enchantmentswitch.init.ModRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StoredEnchantmentsTooltipHandler {

    public static void onItemTooltip(ItemStack itemStack, List<Component> tooltipLines, Item.TooltipContext tooltipContext, @Nullable Player player, TooltipFlag tooltipType) {
        ItemEnchantments itemEnchantments = itemStack.getOrDefault(ModRegistry.STORED_ENCHANTMENTS_DATA_COMPONENT_TYPE.value(),
                ItemEnchantments.EMPTY);
        if (!itemEnchantments.isEmpty()) {
            int index = getLastEnchantmentIndex(tooltipLines);
            List<Component> enchantmentLines = new ArrayList<>();
            itemEnchantments.addToTooltip(tooltipContext, (Component component) -> {
                enchantmentLines.add(EnchantmentTooltipHelper.applyStoredEnchantmentStyle(component));
            }, tooltipType);
            tooltipLines.addAll(index, enchantmentLines);
        }
    }

    private static int getLastEnchantmentIndex(List<Component> lines) {
        int index = lines.isEmpty() ? 0 : 1;
        for (int i = 0; i < lines.size(); i++) {
            Component component = lines.get(i);
            if (component.getContents() == PlainTextContents.EMPTY) {
                if (!component.getSiblings().isEmpty()) {
                    component = component.getSiblings().getFirst();
                }
            }
            // also matches Enchantment Descriptions format, so we append afterward
            if (component.getContents() instanceof TranslatableContents contents &&
                    contents.getKey().matches("^enchantment\\.[a-z0-9_.-]+\\.[a-z0-9/._-]+")) {
                index = i + 1;
            }
        }
        return index;
    }
}
