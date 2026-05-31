package com.startechnologybeyond.symbiotic_swiss_knife.network.packets;

import com.lowdragmc.lowdraglib.networking.IHandlerContext;
import com.lowdragmc.lowdraglib.networking.IPacket;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolItem;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.SymbioticMultitoolItems;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolMode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CPacketSetMultitoolMode implements IPacket {

    private String toolTypeName;
    private int handOrdinal;

    public CPacketSetMultitoolMode(MultitoolMode mode, InteractionHand hand) {
        this.toolTypeName = mode.toolType().name;
        this.handOrdinal = hand.ordinal();
    }

    public CPacketSetMultitoolMode() {
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(toolTypeName);
        buf.writeVarInt(handOrdinal);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        toolTypeName = buf.readUtf();
        handOrdinal = buf.readVarInt();
    }

    @Override
    public void execute(IHandlerContext handler) {
        Player player = handler.getPlayer();
        if (player == null)
            return;

        if (handOrdinal < 0 || handOrdinal >= InteractionHand.values().length)
            return;

        // get the item in the player hand that executed this
        InteractionHand hand = InteractionHand.values()[handOrdinal];
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof MultitoolItem))
            return;

        // ensure that its a valid multitool mode that we can
        // even swap to so the player doesnt cheat in neutronium evil
        // obliterator 9000 in ueuvuvluv
        GTToolType type = GTToolType.getTypes().get(toolTypeName);
        if (type == null)
            return;
        if (!MultitoolMode.isInstalled(stack, type))
            return;

        // fetch the next item for swapping id over to
        var nextItemEntry = SymbioticMultitoolItems.MULTITOOLS.get(type);
        if (nextItemEntry == null)
            return;

        // create new item and copy over nbt
        ItemStack newStack = new ItemStack(nextItemEntry.get());
        if (stack.hasTag()) {
            newStack.setTag(stack.getTag().copy());
        }

        // update the active one in the nbt
        MultitoolMode.setActive(newStack, new MultitoolMode(type,
                MultitoolMode.getMaterialForType(newStack, type)));

        // give to player
        player.setItemInHand(hand, newStack);

        // sync the updated item data back to the client so the client-side stack
        // has the correct behaviors tag and the actual updated item
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.inventoryMenu.sendAllDataToRemote();
        }
    }
}