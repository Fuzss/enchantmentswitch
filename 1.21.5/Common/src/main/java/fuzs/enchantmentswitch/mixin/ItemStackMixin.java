package fuzs.enchantmentswitch.mixin;

import fuzs.enchantmentswitch.init.ModRegistry;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
abstract class ItemStackMixin implements DataComponentHolder {

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    public void isEnchantable(CallbackInfoReturnable<Boolean> callback) {
        // items with only stored enchantments are still enchanted, prevents them from being enchanted at enchanting table again
        if (!this.getOrDefault(ModRegistry.STORED_ENCHANTMENTS_DATA_COMPONENT_TYPE.value(), ItemEnchantments.EMPTY)
                .isEmpty()) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "isEnchanted", at = @At("HEAD"), cancellable = true)
    public void isEnchanted(CallbackInfoReturnable<Boolean> callback) {
        // items with only stored enchantments are still enchanted, prevents them from being enchanted at enchanting table again
        if (!this.getOrDefault(ModRegistry.STORED_ENCHANTMENTS_DATA_COMPONENT_TYPE.value(), ItemEnchantments.EMPTY)
                .isEmpty()) {
            callback.setReturnValue(true);
        }
    }
}
