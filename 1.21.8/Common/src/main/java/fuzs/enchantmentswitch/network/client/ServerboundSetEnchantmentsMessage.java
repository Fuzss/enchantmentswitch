package fuzs.enchantmentswitch.network.client;

import com.google.common.collect.Sets;
import fuzs.enchantmentswitch.init.ModRegistry;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Set;

public record ServerboundSetEnchantmentsMessage(int containerId,
                                                int slotIndex,
                                                Set<Holder<Enchantment>> storedEnchantments) implements ServerboundPlayMessage {
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetEnchantmentsMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundSetEnchantmentsMessage::containerId,
            ByteBufCodecs.VAR_INT,
            ServerboundSetEnchantmentsMessage::slotIndex,
            Enchantment.STREAM_CODEC.apply(ByteBufCodecs.collection(Sets::newHashSetWithExpectedSize)),
            ServerboundSetEnchantmentsMessage::storedEnchantments,
            ServerboundSetEnchantmentsMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                setEnchantments(context.player(),
                        ServerboundSetEnchantmentsMessage.this.containerId,
                        ServerboundSetEnchantmentsMessage.this.slotIndex,
                        ServerboundSetEnchantmentsMessage.this.storedEnchantments);
            }
        };
    }

    public static void setEnchantments(Player player, int containerId, int slotIndex, Set<Holder<Enchantment>> storedEnchantments) {

        AbstractContainerMenu menu = player.containerMenu;
        if (containerId == menu.containerId && slotIndex < menu.slots.size()) {

            Slot slot = menu.getSlot(slotIndex);
            if (slot.hasItem() && slot.allowModification(player)) {

                ItemStack itemStack = slot.getItem();
                ItemEnchantments enchantmentLookup = createEnchantmentLookup(itemStack);
                ItemEnchantments.Mutable mutableItemEnchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                ItemEnchantments.Mutable mutableStoredEnchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

                for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantmentLookup.entrySet()) {
                    Holder<Enchantment> enchantment = entry.getKey();
                    // once again verify we don't allow curses to be stored nor incompatible enchantments to be enabled
                    if (!enchantment.is(ModRegistry.PERSISTENT_ENCHANTMENTS_ENCHANTMENT_TAG) &&
                            (storedEnchantments.contains(enchantment) || !EnchantmentHelper.isEnchantmentCompatible(
                                    mutableItemEnchantments.keySet(),
                                    enchantment))) {
                        mutableStoredEnchantments.set(enchantment, entry.getIntValue());
                    } else {
                        mutableItemEnchantments.set(enchantment, entry.getIntValue());
                    }
                }

                itemStack.set(DataComponents.ENCHANTMENTS, mutableItemEnchantments.toImmutable());
                itemStack.set(ModRegistry.STORED_ENCHANTMENTS_DATA_COMPONENT_TYPE.value(),
                        mutableStoredEnchantments.toImmutable());

                // required for the container to save
                slot.set(itemStack);
            }
        }
    }

    public static ItemEnchantments createEnchantmentLookup(ItemStack itemStack) {
        ItemEnchantments itemEnchantments = itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments storedEnchantments = itemStack.getOrDefault(ModRegistry.STORED_ENCHANTMENTS_DATA_COMPONENT_TYPE.value(),
                ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(storedEnchantments);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
            mutable.set(entry.getKey(), entry.getIntValue());
        }
        return mutable.toImmutable();
    }
}
