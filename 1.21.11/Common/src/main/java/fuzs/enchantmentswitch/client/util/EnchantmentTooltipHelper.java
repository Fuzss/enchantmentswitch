package fuzs.enchantmentswitch.client.util;

import fuzs.enchantmentswitch.EnchantmentSwitch;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EnchantmentTooltipHelper {
    public static final String KEY_INCOMPATIBLE_ENCHANTMENTS =
            "gui." + EnchantmentSwitch.MOD_ID + ".tooltip.incompatible";

    public static Component getItemDisplayName(ItemStack itemStack) {
        MutableComponent mutableComponent = Component.empty()
                .append(itemStack.getHoverName())
                .withStyle(itemStack.getRarity().color());
        if (itemStack.has(DataComponents.CUSTOM_NAME)) {
            mutableComponent.withStyle(ChatFormatting.ITALIC);
        }
        return mutableComponent;
    }

    public static List<Component> getIncompatibleEnchantmentsTooltip(Collection<Holder<Enchantment>> incompatibleEnchantments) {
        Component component = Component.translatable(KEY_INCOMPATIBLE_ENCHANTMENTS,
                incompatibleEnchantments.stream()
                        .map(EnchantmentTooltipHelper::getDisplayName)
                        .reduce((MutableComponent o1, MutableComponent o2) -> o1.append(", ").append(o2))
                        .orElse(Component.empty())
                        .withStyle(ChatFormatting.GRAY));
        return Collections.singletonList(component);
    }

    public static List<Component> getEnchantmentTooltip(Holder<Enchantment> enchantment, int enchantmentLevel) {
        List<Component> tooltipLines = new ArrayList<>();
        tooltipLines.add(Enchantment.getFullname(enchantment, enchantmentLevel));
        String translationKey = getEnchantmentDescriptionKey(enchantment);
        if (translationKey != null) {
            tooltipLines.add(Component.translatable(translationKey).withStyle(ChatFormatting.GRAY));
        }
        return tooltipLines;
    }

    @Nullable
    private static String getEnchantmentDescriptionKey(Holder<Enchantment> enchantment) {
        String translationKey = enchantment.unwrapKey().map((ResourceKey<Enchantment> resourceKey) -> {
            return Util.makeDescriptionId(resourceKey.registry().getPath(), resourceKey.location());
        }).orElse(null);
        if (translationKey == null) {
            return null;
        } else if (Language.getInstance().has(translationKey + ".desc")) {
            return translationKey + ".desc";
        } else if (Language.getInstance().has(translationKey + ".description")) {
            return translationKey + ".description";
        } else {
            return null;
        }
    }

    public static MutableComponent getDisplayName(Holder<Enchantment> enchantment) {
        return enchantment.value().description().copy().setStyle(Style.EMPTY);
    }

    public static Component applyStoredEnchantmentStyle(Component component) {
        return Component.empty().append(component).withStyle(ChatFormatting.STRIKETHROUGH);
    }
}
