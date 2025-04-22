package fuzs.enchantmentswitch;

import fuzs.enchantmentswitch.config.ClientConfig;
import fuzs.enchantmentswitch.init.ModRegistry;
import fuzs.enchantmentswitch.network.client.ServerboundSetEnchantmentsMessage;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnchantmentSwitch implements ModConstructor {
    public static final String MOD_ID = "enchantmentswitch";
    public static final String MOD_NAME = "Enchantment Switch";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).client(ClientConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
    }

    @Override
    public void onRegisterPayloadTypes(PayloadTypesContext context) {
        context.playToServer(ServerboundSetEnchantmentsMessage.class, ServerboundSetEnchantmentsMessage.STREAM_CODEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
