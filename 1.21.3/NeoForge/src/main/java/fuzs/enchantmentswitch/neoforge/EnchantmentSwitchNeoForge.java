package fuzs.enchantmentswitch.neoforge;

import fuzs.enchantmentswitch.EnchantmentSwitch;
import fuzs.enchantmentswitch.data.tags.ModEnchantmentTagProvider;
import fuzs.enchantmentswitch.data.tags.ModItemTagProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.fml.common.Mod;

@Mod(EnchantmentSwitch.MOD_ID)
public class EnchantmentSwitchNeoForge {

    public EnchantmentSwitchNeoForge() {
        ModConstructor.construct(EnchantmentSwitch.MOD_ID, EnchantmentSwitch::new);
        DataProviderHelper.registerDataProviders(EnchantmentSwitch.MOD_ID,
                ModItemTagProvider::new,
                ModEnchantmentTagProvider::new);
    }
}
