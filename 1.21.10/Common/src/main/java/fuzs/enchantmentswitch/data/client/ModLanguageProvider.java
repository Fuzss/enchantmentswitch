package fuzs.enchantmentswitch.data.client;

import fuzs.enchantmentswitch.EnchantmentSwitch;
import fuzs.enchantmentswitch.client.EnchantmentSwitchClient;
import fuzs.enchantmentswitch.client.gui.screens.inventory.EditEnchantmentsScreen;
import fuzs.enchantmentswitch.client.util.EnchantmentTooltipHelper;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.addKeyCategory(EnchantmentSwitch.MOD_ID, EnchantmentSwitch.MOD_NAME);
        builder.add(EnchantmentSwitchClient.EDIT_ENCHANTMENTS_KEY_MAPPING, "Edit Enchantments");
        builder.add(EditEnchantmentsScreen.COMPONENT_EDIT_ENCHANTMENTS, "Edit Enchantments");
        builder.add(EnchantmentTooltipHelper.KEY_INCOMPATIBLE_ENCHANTMENTS,
                "This enchantment is incompatible with: %s");
    }
}
