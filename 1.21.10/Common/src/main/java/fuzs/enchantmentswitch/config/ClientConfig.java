package fuzs.enchantmentswitch.config;

import fuzs.enchantmentswitch.client.handler.SlotOverlayHandler;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ClientConfig implements ConfigCore {
    @Config(description = "Maximum time in ticks it takes to open the enchantments editor for an item.")
    @Config.IntRange(min = 1, max = SlotOverlayHandler.MAX_TRIGGER_TIME)
    public int openEnchantmentsEditorTicks = 12;
}
