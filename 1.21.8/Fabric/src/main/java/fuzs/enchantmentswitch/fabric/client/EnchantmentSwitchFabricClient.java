package fuzs.enchantmentswitch.fabric.client;

import fuzs.enchantmentswitch.EnchantmentSwitch;
import fuzs.enchantmentswitch.client.EnchantmentSwitchClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class EnchantmentSwitchFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(EnchantmentSwitch.MOD_ID, EnchantmentSwitchClient::new);
    }
}
