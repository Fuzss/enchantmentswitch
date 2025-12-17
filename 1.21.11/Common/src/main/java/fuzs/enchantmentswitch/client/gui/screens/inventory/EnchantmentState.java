package fuzs.enchantmentswitch.client.gui.screens.inventory;

import com.google.common.collect.ImmutableSet;
import fuzs.enchantmentswitch.client.util.EnchantmentTooltipHelper;
import fuzs.enchantmentswitch.init.ModRegistry;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record EnchantmentState(int enchantmentLevel,
                               boolean isPresent,
                               Collection<Holder<Enchantment>> incompatibleEnchantments,
                               boolean isPersistent) {

    public static EnchantmentState create(Holder<Enchantment> enchantment, boolean isPresent, int enchantmentLevel, ItemEnchantments itemEnchantments) {
        Set<Holder<Enchantment>> incompatibleEnchantments = new HashSet<>();
        for (Holder<Enchantment> holder : itemEnchantments.keySet()) {
            if (!enchantment.is(holder) && !Enchantment.areCompatible(enchantment, holder)) {
                incompatibleEnchantments.add(holder);
            }
        }
        return new EnchantmentState(enchantmentLevel,
                isPresent,
                ImmutableSet.copyOf(incompatibleEnchantments),
                enchantment.is(ModRegistry.PERSISTENT_ENCHANTMENTS_ENCHANTMENT_TAG));
    }

    public boolean isIncompatible() {
        return !this.incompatibleEnchantments.isEmpty();
    }

    public boolean isInactive() {
        return this.isPersistent() || this.isIncompatible();
    }

    public Component getDisplayName(Holder<Enchantment> enchantment) {
        return EnchantmentTooltipHelper.getDisplayName(enchantment);
    }

    public List<Component> getTooltip(Holder<Enchantment> enchantment) {
        if (this.isIncompatible()) {
            return EnchantmentTooltipHelper.getIncompatibleEnchantmentsTooltip(this.incompatibleEnchantments);
        } else {
            return EnchantmentTooltipHelper.getEnchantmentTooltip(enchantment, this.enchantmentLevel);
        }
    }
}
