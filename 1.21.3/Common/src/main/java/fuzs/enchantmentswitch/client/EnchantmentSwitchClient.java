package fuzs.enchantmentswitch.client;

import fuzs.enchantmentswitch.EnchantmentSwitch;
import fuzs.enchantmentswitch.client.handler.StoredEnchantmentsTooltipHandler;
import fuzs.enchantmentswitch.client.handler.TriggerLockRenderHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.event.v1.gui.ItemTooltipCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderTooltipCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenEvents;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class EnchantmentSwitchClient implements ClientModConstructor {
    public static final KeyMapping EDIT_ENCHANTMENTS_KEY_MAPPING = KeyMappingHelper.registerUnboundKeyMapping(
            EnchantmentSwitch.id("edit_enchantments"));

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ItemTooltipCallback.EVENT.register(StoredEnchantmentsTooltipHandler::onItemTooltip);
        ScreenEvents.afterRender(AbstractContainerScreen.class).register(TriggerLockRenderHandler::onAfterRender);
        RenderTooltipCallback.EVENT.register(TriggerLockRenderHandler::onRenderTooltip);
    }

    @Override
    public void onRegisterKeyMappings(KeyMappingsContext context) {
        context.registerKeyMapping(EDIT_ENCHANTMENTS_KEY_MAPPING, KeyActivationContext.SCREEN);
    }
}
