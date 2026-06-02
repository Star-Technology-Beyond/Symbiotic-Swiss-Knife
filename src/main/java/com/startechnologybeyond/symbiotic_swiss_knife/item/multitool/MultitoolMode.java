package com.startechnologybeyond.symbiotic_swiss_knife.item.multitool;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.common.data.GTMaterialItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MultitoolMode {
    public static final String TAG_KEY = "multitoolMode";
    public static final String TAG_INSTALLED = "installedModes";
    public static final String TAG_MATERIAL = "modeMaterial";
    public static final String TAG_SINGLE_BLOCK = "singleBlockMode";
    public static final String TAG_MAX_CHARGE = "modeMaxCharge";
    public static final String TAG_BEHAVIORS = "modeBehaviors";

    // store the tool type and material of this mode
    private final GTToolType toolType;
    private final Material material;

    public MultitoolMode(GTToolType toolType, Material material) {
        this.toolType = toolType;
        this.material = material;
    }

    public GTToolType toolType() {
        return toolType;
    }

    public Material material() {
        return material;
    }

    public MutableComponent displayName() {
        return Component.translatable("item.gtceu.tool." + toolType.name, material.getLocalizedName());
    }

    public String id() {
        return toolType.name;
    }

    public static void saveCurrentBehaviors(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        String activeName = tag.getString(TAG_KEY);
        if (activeName == null || activeName.isEmpty()) return;

        CompoundTag behaviors = tag.getCompound(ToolHelper.BEHAVIOURS_TAG_KEY);

        // store a copy of behaviours to persist them for later use
        tag.put(TAG_BEHAVIORS + activeName, behaviors.copy());
    }


    public static void restoreBehaviors(ItemStack stack, GTToolType type, Material material) {
        CompoundTag tag = stack.getOrCreateTag();
        String savedKey = TAG_BEHAVIORS + type.name;

        // check if the behaviours have been saved for this mode
        if (tag.contains(savedKey, Tag.TAG_COMPOUND)) {
            // restore behaviours from saved
            tag.put(ToolHelper.BEHAVIOURS_TAG_KEY, tag.getCompound(savedKey).copy());
        } else {
            // first time this mode exists, try restore from reference item
            var entry = GTMaterialItems.TOOL_ITEMS.get(material, type);
            if (entry == null) return;
            ItemStack reference = entry.get().get();
            if (reference.isEmpty()) return;

            CompoundTag refBehaviors = reference.getTagElement(ToolHelper.BEHAVIOURS_TAG_KEY);
            if (refBehaviors != null && !refBehaviors.isEmpty()) {
                tag.put(ToolHelper.BEHAVIOURS_TAG_KEY, refBehaviors.copy());

                // save to slot too for future use
                tag.put(savedKey, refBehaviors.copy());
            } else {
                tag.remove(ToolHelper.BEHAVIOURS_TAG_KEY);
            }
        }
    }

    // drop saved behaviours for a mode
    public static void clearSavedBehaviors(ItemStack stack, GTToolType type) {
        CompoundTag tag = stack.getTag();
        if (tag != null) tag.remove(TAG_BEHAVIORS + type.name);
    }

    public static List<MultitoolMode> getInstalled(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        List<MultitoolMode> result = new ArrayList<>();
        if (tag == null || !tag.contains(TAG_INSTALLED, Tag.TAG_LIST))
            return result;

        // Get the list of all the installed types
        ListTag list = tag.getList(TAG_INSTALLED, Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {

            // Get the name of the tool type and use that to get the actual
            // tool type
            String typeName = list.getString(i);
            GTToolType type = GTToolType.getTypes().get(typeName);
            if (type == null)
                continue;

            // Get the material name
            String matName = tag.getString(TAG_MATERIAL + typeName);
            Material mat = GTCEuAPI.materialManager.getMaterial(matName);
            if (mat == null)
                continue;

            result.add(new MultitoolMode(type, mat));
        }
        return result;
    }

    public static boolean isInstalled(ItemStack stack, GTToolType type) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_INSTALLED, Tag.TAG_LIST))
            return false;

        // Check if installed list contains the types name
        ListTag list = tag.getList(TAG_INSTALLED, Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            if (list.getString(i).equals(type.name))
                return true;
        }
        return false;
    }

    public static void install(ItemStack stack, GTToolType type, Material material) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = tag.contains(TAG_INSTALLED, Tag.TAG_LIST)
                ? tag.getList(TAG_INSTALLED, Tag.TAG_STRING)
                : new ListTag();

        // Avoid duplicates
        for (int i = 0; i < list.size(); i++) {
            if (list.getString(i).equals(type.name))
                return;
        }
        list.add(StringTag.valueOf(type.name));
        tag.put(TAG_INSTALLED, list);
        tag.putString(TAG_MATERIAL + type.name, material.getName());

        // if no active mode yet then set this as active
        if (!tag.contains(TAG_KEY, Tag.TAG_STRING)) {
            tag.putString(TAG_KEY, type.name);

            CompoundTag toolTag = ToolHelper.getToolTag(stack);
            toolTag.remove(ToolHelper.TOOL_SPEED_KEY);
            toolTag.remove(ToolHelper.ATTACK_DAMAGE_KEY);
            toolTag.remove(ToolHelper.ATTACK_SPEED_KEY);
            toolTag.remove(ToolHelper.HARVEST_LEVEL_KEY);

            // seed initial behaviours from this tool
            restoreBehaviors(stack, type, material); 
        }
    }

    public static void uninstall(ItemStack stack, GTToolType type) {
        CompoundTag tag = stack.getTag();
        if (tag == null)
            return;

        // Create a new list with this type missing
        ListTag list = tag.getList(TAG_INSTALLED, Tag.TAG_STRING);
        ListTag newList = new ListTag();
        for (int i = 0; i < list.size(); i++) {
            if (!list.getString(i).equals(type.name)) {
                newList.add(list.get(i));
            }
        }

        tag.put(TAG_INSTALLED, newList);
        tag.remove(TAG_MATERIAL + type.name);
        tag.remove(TAG_MAX_CHARGE + type.name);

        // we dont want to keep behaviours around after we drop a key
        clearSavedBehaviors(stack, type);

        // if active mode was this one then switch to first available
        String active = tag.getString(TAG_KEY);
        if (active.equals(type.name)) {
            if (!newList.isEmpty()) {
                // put the name of the next mode
                String nextName = newList.getString(0);
                tag.putString(TAG_KEY, nextName);

                // get the type of the next mode and material for behaviour syncing
                GTToolType nextType = GTToolType.getTypes().get(nextName);
                Material nextMat = GTCEuAPI.materialManager.getMaterial(tag.getString(TAG_MATERIAL + nextName));
                if (nextType != null && nextMat != null) {
                    // sync behaviours from the next tool
                    restoreBehaviors(stack, nextType, nextMat);
                }
            } else {
                tag.remove(TAG_KEY);
            }
        }

        // clear cached stats so GTCEu recalculates
        tag.remove(ToolHelper.TOOL_SPEED_KEY);
        tag.remove(ToolHelper.ATTACK_DAMAGE_KEY);
        tag.remove(ToolHelper.ATTACK_SPEED_KEY);
        tag.remove(ToolHelper.HARVEST_LEVEL_KEY);
    }

    public static Material getMaterialForType(ItemStack stack, GTToolType type) {
        // Get the material for the tool type in this stack
        CompoundTag tag = stack.getTag();
        if (tag == null)
            return null;
        String matName = tag.getString(TAG_MATERIAL + type.name);
        return GTCEuAPI.materialManager.getMaterial(matName);
    }

    public static MultitoolMode getActive(ItemStack stack) {
        List<MultitoolMode> installed = getInstalled(stack);
        if (installed.isEmpty())
            return null;

        CompoundTag tag = stack.getTag();
        if (tag == null)
            return installed.get(0);

        // The current active mode is stored under TAG_KEY
        // so filter until we find the mode that equals the active
        // mode
        String activeName = tag.getString(TAG_KEY);
        return installed.stream()
                .filter(mode -> mode.id().equals(activeName))
                .findFirst()
                .orElse(installed.get(0));
    }

    public static void setActive(ItemStack stack, MultitoolMode mode) {
        // persist behaviours from current mode
        saveCurrentBehaviors(stack); 

        // set the active mode
        stack.getOrCreateTag().putString(TAG_KEY, mode.id());
        CompoundTag toolTag = ToolHelper.getToolTag(stack);

        // let gtceu recalculate these stats
        toolTag.remove(ToolHelper.TOOL_SPEED_KEY);
        toolTag.remove(ToolHelper.ATTACK_DAMAGE_KEY);
        toolTag.remove(ToolHelper.ATTACK_SPEED_KEY);
        toolTag.remove(ToolHelper.HARVEST_LEVEL_KEY);

        // restore behaviours from new mode
        restoreBehaviors(stack, mode.toolType(), mode.material());
    }

    public static MultitoolMode offset(ItemStack stack, int amount) {
        // return an offset into the current mode of the
        // stack based on the amount
        List<MultitoolMode> installed = getInstalled(stack);
        if (installed.isEmpty())
            return null;

        MultitoolMode active = getActive(stack);
        int idx = active == null ? 0 : installed.indexOf(active);

        return installed.get(Math.floorMod(idx + amount, installed.size()));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        MultitoolMode that = (MultitoolMode) obj;

        // Compare based on the tool types name/id
        return this.id().equals(that.id());
    }

    @Override
    public int hashCode() {
        return id().hashCode();
    }

    public static boolean isSingleBlockMode(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_SINGLE_BLOCK);
    }

    public static void setSingleBlockMode(ItemStack stack, boolean enabled) {
        stack.getOrCreateTag().putBoolean(TAG_SINGLE_BLOCK, enabled);
    }

    public static void toggleSingleBlockMode(ItemStack stack) {
        setSingleBlockMode(stack, !isSingleBlockMode(stack));
    }

    // we need to track max charge as electric tools can be made from a wide variety of
    // batteries
    public static void setMaxCharge(ItemStack stack, GTToolType type, long maxCharge) {
        stack.getOrCreateTag().putLong(TAG_MAX_CHARGE + type.name, maxCharge);
    }

    public static long getMaxCharge(ItemStack stack, GTToolType type) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0L;
        return tag.getLong(TAG_MAX_CHARGE + type.name);
    }
}