package com.startechnologybeyond.symbiotic_swiss_knife.api.multitool;

import com.mojang.blaze3d.platform.InputConstants;
import com.startechnologybeyond.symbiotic_swiss_knife.SymbioticSwissKnife;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolItem;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.SymbioticMultitoolItems;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolMode;
import com.startechnologybeyond.symbiotic_swiss_knife.network.SymbioticNetwork;
import com.startechnologybeyond.symbiotic_swiss_knife.network.packets.CPacketMiddleClickAutoSelect;
import com.startechnologybeyond.symbiotic_swiss_knife.network.packets.CPacketReleaseSingleBlockLock;
import com.startechnologybeyond.symbiotic_swiss_knife.network.packets.CPacketSetMultitoolMode;
import com.startechnologybeyond.symbiotic_swiss_knife.network.packets.CPacketUninstallMultitoolMode;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = SymbioticSwissKnife.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MultitoolClientEvents {

    private static final String CATEGORY = "key.categories.symbiotic_swiss_knife";
    public static final KeyMapping OPEN_SELECTOR = new KeyMapping(
            "key.symbiotic_swiss_knife.multitool_selector",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            CATEGORY);

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_SELECTOR);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // this will tint the multitool if it has an active mode
        // with the active colour of the material
        SymbioticMultitoolItems.MULTITOOLS.values().forEach(itemEntry -> {
            event.register((stack, tintIndex) -> {
                MultitoolMode active = MultitoolMode.getActive(stack);
                if (active == null)
                    return 0xFFFFFF;
                return tintIndex == 0 ? active.material().getMaterialRGB() : 0xFFFFFF;
            }, itemEntry.get());
        });
    }

    @Mod.EventBusSubscriber(modid = SymbioticSwissKnife.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null)
                return;

            // opens the new multitool radial screen if the key is pressed
            if (minecraft.screen == null && event.getAction() == GLFW.GLFW_PRESS &&
                    OPEN_SELECTOR.matches(event.getKey(), event.getScanCode())) {
                HeldMultitool held = getHeldMultitool();
                if (held != null) {
                    minecraft.setScreen(new MultitoolRadialScreen(held.stack(), held.hand()));
                    return;
                }
            }

            // single click release for keyboard attack
            if (minecraft.options.keyAttack.matches(event.getKey(), event.getScanCode()) &&
                    event.getAction() == GLFW.GLFW_RELEASE) {
                HeldMultitool held = getHeldMultitool();
                if (held != null && MultitoolMode.isSingleBlockMode(held.stack())) {
                    SymbioticNetwork.NETWORK.sendToServer(new CPacketReleaseSingleBlockLock(held.hand()));
                }
            }
        }

        @SubscribeEvent
        public static void onMouseButton(InputEvent.MouseButton event) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null)
                return;

            // single click releaese for mouse
            if (minecraft.options.keyAttack.matchesMouse(event.getButton()) &&
                    event.getAction() == GLFW.GLFW_RELEASE) {
                HeldMultitool held = getHeldMultitool();
                if (held != null && MultitoolMode.isSingleBlockMode(held.stack())) {
                    SymbioticNetwork.NETWORK.sendToServer(new CPacketReleaseSingleBlockLock(held.hand()));
                }
            }
        }

        @SubscribeEvent
        public static void onInteractionKeyMapping(InputEvent.InteractionKeyMappingTriggered event) {
            // pick block for auto select
            if (event.isPickBlock()) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player == null || minecraft.screen != null)
                    return;

                HeldMultitool held = getHeldMultitool();
                if (held == null)
                    return;

                // cancel event if we auto select successfully
                if (tryAutoSelect(minecraft, held)) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen != null || minecraft.player == null || !minecraft.player.isShiftKeyDown()) {
                return;
            }
            HeldMultitool held = getHeldMultitool();
            if (held == null)
                return;

            MultitoolMode next = MultitoolMode.offset(held.stack(), event.getScrollDelta() > 0 ? -1 : 1);
            if (next != null)
                selectMode(held.stack(), held.hand(), next);
            event.setCanceled(true);
        }

        private static boolean tryAutoSelect(Minecraft minecraft, HeldMultitool held) {
            HitResult hit = minecraft.hitResult;
            if (hit == null || hit.getType() != HitResult.Type.BLOCK)
                return false;

            // determine the id of the block the player is looking at
            BlockPos pos = ((BlockHitResult) hit).getBlockPos();
            var blockState = minecraft.level.getBlockState(pos);
            String blockId = ForgeRegistries.BLOCKS.getKey(blockState.getBlock()).toString();

            // collect all tag IDs this block belongs to
            Set<String> blockTags = blockState.getTags()
                    .map(tagKey -> tagKey.location().toString())
                    .collect(java.util.stream.Collectors.toSet());

            // mode select to server
            SymbioticNetwork.NETWORK.sendToServer(
                    new CPacketMiddleClickAutoSelect(held.hand(), blockId, blockTags));

            // send message to client
            minecraft.player.displayClientMessage(
                    Component.translatable("item.symbiotic_swiss_knife.gregtech_multitool.auto_select",
                            Component.literal(blockId).withStyle(ChatFormatting.GRAY))
                            .withStyle(ChatFormatting.AQUA),
                    true);

            return true;
        }
    }

    static HeldMultitool getHeldMultitool() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null)
            return null;
        ItemStack main = minecraft.player.getMainHandItem();
        if (main.getItem() instanceof MultitoolItem) {
            return new HeldMultitool(main, InteractionHand.MAIN_HAND);
        }
        ItemStack offhand = minecraft.player.getOffhandItem();
        if (offhand.getItem() instanceof MultitoolItem) {
            return new HeldMultitool(offhand, InteractionHand.OFF_HAND);
        }
        return null;
    }

    static void selectMode(ItemStack stack, InteractionHand hand, MultitoolMode mode) {
        SymbioticNetwork.NETWORK.sendToServer(new CPacketSetMultitoolMode(mode, hand));
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(
                    Component.translatable("item.symbiotic_swiss_knife.gregtech_multitool.selected_mode",
                            mode.displayName().withStyle(ChatFormatting.AQUA)),
                    true);
        }
    }

    static void ejectMode(ItemStack stack, InteractionHand hand, MultitoolMode mode) {
        SymbioticNetwork.NETWORK.sendToServer(new CPacketUninstallMultitoolMode(mode, hand));
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(
                    Component.translatable("item.symbiotic_swiss_knife.gregtech_multitool.ejected_mode",
                            mode.displayName().withStyle(ChatFormatting.RED)),
                    true);
        }
    }

    record HeldMultitool(ItemStack stack, InteractionHand hand) {
    }
}