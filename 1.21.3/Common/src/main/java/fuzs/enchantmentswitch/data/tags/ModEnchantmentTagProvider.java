package fuzs.enchantmentswitch.data.tags;

import fuzs.enchantmentswitch.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantmentTagProvider extends AbstractTagProvider<Enchantment> {

    public ModEnchantmentTagProvider(DataProviderContext context) {
        super(Registries.ENCHANTMENT, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.add(ModRegistry.PERSISTENT_ENCHANTMENTS_ENCHANTMENT_TAG).addTag(EnchantmentTags.CURSE);
    }
}
