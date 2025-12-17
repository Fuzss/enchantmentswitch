package fuzs.enchantmentswitch.config;

import fuzs.enchantmentswitch.client.handler.SlotOverlayHandler;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ClientConfig implements ConfigCore {
    @Config(description = {
            "Maximum time in ticks it takes to open the enchantments editor for an item.",
            "Set to zero to open the editor instantly like a normal key press."
    })
    @Config.IntRange(min = 0, max = SlotOverlayHandler.MAX_TRIGGER_TIME)
    public int openEnchantmentsEditorTicks = 12;

    public boolean openEditorInstantly() {
        return this.openEnchantmentsEditorTicks < 1;
    }
}
