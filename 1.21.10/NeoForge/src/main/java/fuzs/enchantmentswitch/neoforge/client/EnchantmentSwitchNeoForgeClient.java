package fuzs.enchantmentswitch.neoforge.client;

import fuzs.enchantmentswitch.EnchantmentSwitch;
import fuzs.enchantmentswitch.client.EnchantmentSwitchClient;
import fuzs.enchantmentswitch.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = EnchantmentSwitch.MOD_ID, dist = Dist.CLIENT)
public class EnchantmentSwitchNeoForgeClient {

    public EnchantmentSwitchNeoForgeClient() {
        ClientModConstructor.construct(EnchantmentSwitch.MOD_ID, EnchantmentSwitchClient::new);
        DataProviderHelper.registerDataProviders(EnchantmentSwitch.MOD_ID, ModLanguageProvider::new);
    }
}
