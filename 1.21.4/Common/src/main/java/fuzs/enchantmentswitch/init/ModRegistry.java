package fuzs.enchantmentswitch.init;

import fuzs.enchantmentswitch.EnchantmentSwitch;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(EnchantmentSwitch.MOD_ID);
    public static final Holder.Reference<DataComponentType<ItemEnchantments>> STORED_ENCHANTMENTS_DATA_COMPONENT_TYPE = REGISTRIES.registerDataComponentType(
            "stored_enchantments",
            builder -> builder.persistent(ItemEnchantments.CODEC)
                    .networkSynchronized(ItemEnchantments.STREAM_CODEC)
                    .cacheEncoding());

    static final TagFactory TAGS = TagFactory.make(EnchantmentSwitch.MOD_ID);
    public static final TagKey<Item> PERSISTENT_ENCHANTMENTS_ITEM_TAG = TAGS.registerItemTag("persistent_enchantments");
    public static final TagKey<Enchantment> PERSISTENT_ENCHANTMENTS_ENCHANTMENT_TAG = TAGS.registerEnchantmentTag(
            "persistent_enchantments");

    public static void bootstrap() {
        // NO-OP
    }
}
