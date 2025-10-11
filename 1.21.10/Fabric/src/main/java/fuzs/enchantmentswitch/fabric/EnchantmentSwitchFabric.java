package fuzs.enchantmentswitch.fabric;

import fuzs.enchantmentswitch.EnchantmentSwitch;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class EnchantmentSwitchFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(EnchantmentSwitch.MOD_ID, EnchantmentSwitch::new);
    }
}
