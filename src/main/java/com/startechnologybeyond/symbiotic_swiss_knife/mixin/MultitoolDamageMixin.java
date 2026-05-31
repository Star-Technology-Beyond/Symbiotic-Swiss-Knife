package com.startechnologybeyond.symbiotic_swiss_knife.mixin;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IElectricItem;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.ToolProperty;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolItem;
import com.startechnologybeyond.symbiotic_swiss_knife.item.multitool.MultitoolMode;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ToolHelper.class, remap = false)
public class MultitoolDamageMixin {

    // intercept the damaging gt tools for
    // multitool damage which should only
    // deal damage by reducing energy.
    @Inject(method = "damageItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;I)V", at = @At("HEAD"), cancellable = true)
    private static void startcore$interceptMultitoolDamage(
            ItemStack stack,
            @Nullable LivingEntity user,
            int damage,
            CallbackInfo ci) {

        // must be a multitoolo
        if (!(stack.getItem() instanceof MultitoolItem multitool))
            return;
        ci.cancel();

        // dont consume durability in creative
        if (user instanceof Player player && player.isCreative()) {
            return;
        }

        // check if current active is unbreakable
        MultitoolMode active = MultitoolMode.getActive(stack);
        if (active != null) {
            ToolProperty toolProperty = active.material().getProperty(PropertyKey.TOOL);
            if (toolProperty != null && toolProperty.isUnbreakable())
                return;
        }

        // get the electric item from the multitool and deal damage to that.
        IElectricItem electricItem = GTCapabilityHelper.getElectricItem(stack);
        if (electricItem == null)
            return;

        int euCost = damage * ConfigHolder.INSTANCE.machines.energyUsageMultiplier;
        electricItem.discharge(euCost, multitool.getElectricTier(), true, false, false);
    }
}