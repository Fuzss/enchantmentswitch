package fuzs.enchantmentswitch.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.enchantmentswitch.init.ModRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
abstract class GrindstoneMenu$SlotMixin extends Slot {

    public GrindstoneMenu$SlotMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @ModifyReturnValue(method = "getExperienceFromItem", at = @At("TAIL"))
    private int getExperienceFromItem(int experienceFromItem, ItemStack itemStack) {
        // add stored enchantments experience reward
        // simply copied from vanilla method, only replacing the item enchantments component
        ItemEnchantments itemEnchantments = itemStack.getOrDefault(ModRegistry.STORED_ENCHANTMENTS_DATA_COMPONENT_TYPE.value(),
                ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            int enchantmentLevel = entry.getIntValue();
            if (!holder.is(EnchantmentTags.CURSE)) {
                experienceFromItem += holder.value().getMinCost(enchantmentLevel);
            }
        }

        return experienceFromItem;
    }
}
