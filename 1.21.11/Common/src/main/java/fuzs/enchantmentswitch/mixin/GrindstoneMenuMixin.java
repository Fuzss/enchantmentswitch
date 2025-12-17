package fuzs.enchantmentswitch.mixin;

import fuzs.enchantmentswitch.init.ModRegistry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
abstract class GrindstoneMenuMixin extends AbstractContainerMenu {

    protected GrindstoneMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "removeNonCursesFrom", at = @At("HEAD"))
    private void removeNonCursesFrom(ItemStack itemStack, CallbackInfoReturnable<ItemStack> callback) {
        // also remove stored enchantments in grind stone
        // no need to increase repair cost for remaining enchantments as vanilla does, that's only necessary for curses which cannot be stored
        itemStack.remove(ModRegistry.STORED_ENCHANTMENTS_DATA_COMPONENT_TYPE.value());
    }
}
