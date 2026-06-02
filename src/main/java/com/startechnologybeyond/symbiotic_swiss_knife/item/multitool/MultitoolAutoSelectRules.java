package com.startechnologybeyond.symbiotic_swiss_knife.item.multitool;

import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MultitoolAutoSelectRules {

    public static final String TAG_RULES = "autoSelectRules";
    public static final String TAG_REGEX = "regex";
    public static final String TAG_TOOL_TYPE = "toolType";

    public static final List<Rule> DEFAULTS = List.of(
            new Rule("(#forge:mineable/wrench)", GTToolType.WRENCH_IV.name),
            new Rule("(#forge:mineable/wrench)", GTToolType.WRENCH_HV.name),
            new Rule("(#forge:mineable/wrench)", GTToolType.WRENCH_LV.name),
            new Rule("(#forge:mineable/wrench)", GTToolType.WRENCH.name),
            new Rule("(#forge:mineable/wire_cutter)", GTToolType.WIRE_CUTTER_IV.name),
            new Rule("(#forge:mineable/wire_cutter)", GTToolType.WIRE_CUTTER_HV.name),
            new Rule("(#forge:mineable/wire_cutter)", GTToolType.WIRE_CUTTER_LV.name),
            new Rule("(#forge:mineable/wire_cutter)", GTToolType.WIRE_CUTTER.name),
            new Rule("(#minecraft:mineable/axe)", GTToolType.CHAINSAW_IV.name),
            new Rule("(#minecraft:mineable/axe)", GTToolType.CHAINSAW_HV.name),
            new Rule("(#minecraft:mineable/axe)", GTToolType.CHAINSAW_LV.name),
            new Rule("(#minecraft:mineable/axe)", GTToolType.AXE.name),
            new Rule("(#minecraft:mineable/pickaxe)", GTToolType.DRILL_IV.name),
            new Rule("(#minecraft:mineable/pickaxe)", GTToolType.DRILL_EV.name),
            new Rule("(#minecraft:mineable/pickaxe)", GTToolType.DRILL_HV.name),
            new Rule("(#minecraft:mineable/pickaxe)", GTToolType.DRILL_MV.name),
            new Rule("(#minecraft:mineable/pickaxe)", GTToolType.DRILL_LV.name),
            new Rule("(#minecraft:mineable/pickaxe)", GTToolType.MINING_HAMMER.name),
            new Rule("(#minecraft:mineable/pickaxe)", GTToolType.PICKAXE.name),
            new Rule("(#minecraft:mineable/shovel)", GTToolType.SPADE.name),
            new Rule("(#minecraft:mineable/shovel)", GTToolType.SHOVEL.name),
            new Rule(".*grass.*|.*fern.*|.*vine.*|.*cobweb.*|.*leaves.*|.*wool.*", GTToolType.SHEARS.name),
            new Rule("(#minecraft:mineable/hoe)", GTToolType.SCYTHE.name),
            new Rule("(#minecraft:mineable/hoe)", GTToolType.HOE.name));

    // a single rule record belonging to a pattern and a type name
    public record Rule(String pattern, String toolTypeName) {

        // splits segments in the pattern to match segments as regex
        public boolean matches(String blockId, Set<String> blockTags) {
            for (String segment : splitSegments(pattern)) {
                if (matchesSegment(segment.trim(), blockId, blockTags))
                    return true;
            }
            return false;
        }

        public boolean matches(String blockId) {
            return matches(blockId, Set.of());
        }

        public boolean isValid() {
            for (String segment : splitSegments(pattern)) {
                if (!isSegmentValid(segment.trim()))
                    return false;
            }
            return true;
        }

        // split on | while respecting parenthesis
        private static List<String> splitSegments(String pattern) {
            List<String> segments = new ArrayList<>();
            int depth = 0;
            int start = 0;
            for (int i = 0; i < pattern.length(); i++) {
                char c = pattern.charAt(i);
                if (c == '(')
                    depth++;
                else if (c == ')')
                    depth--;
                else if (c == '|' && depth == 0) {
                    segments.add(unwrap(pattern.substring(start, i)));
                    start = i + 1;
                }
            }
            segments.add(unwrap(pattern.substring(start)));
            return segments;
        }

        // strips a single layer of parenthesis
        private static String unwrap(String s) {
            s = s.trim();
            if (s.startsWith("(") && s.endsWith(")")) {
                return s.substring(1, s.length() - 1).trim();
            }
            return s;
        }

        // checks if a blockID matches a segment, either a tag
        // or some regex statement
        private static boolean matchesSegment(String segment, String blockId, Set<String> blockTags) {
            if (segment.startsWith("#")) {
                return matchesTag(segment, blockTags);
            }
            try {
                return Pattern.compile(segment, Pattern.CASE_INSENSITIVE)
                        .matcher(blockId)
                        .matches();
            } catch (PatternSyntaxException e) {
                return false;
            }
        }

        // to check if a block matches a tag, check if the block contains the tag
        private static boolean matchesTag(String segment, Set<String> blockTags) {
            String path = segment.substring(1);
            try {
                ResourceLocation loc = new ResourceLocation(path);
                return blockTags.contains(loc.toString());
            } catch (Exception e) {
                return false;
            }
        }

        // a segment must start with # as a tag, or be valid regex
        private static boolean isSegmentValid(String segment) {
            if (segment.startsWith("#")) {
                try {
                    new ResourceLocation(segment.substring(1));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            try {
                Pattern.compile(segment);
                return true;
            } catch (PatternSyntaxException e) {
                return false;
            }
        }
    }

    public static List<Rule> getRules(ItemStack stack) {
        // null tag/missing tag should put default
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_RULES, Tag.TAG_LIST)) {
            return new ArrayList<>(DEFAULTS);
        }

        // else get all the rules from the tags
        List<Rule> result = new ArrayList<>();
        ListTag list = tag.getList(TAG_RULES, Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            String regex = entry.getString(TAG_REGEX);
            String toolType = entry.getString(TAG_TOOL_TYPE);
            if (!regex.isEmpty() && !toolType.isEmpty()) {
                result.add(new Rule(regex, toolType));
            }
        }
        return result;
    }

    public static void setRules(ItemStack stack, List<Rule> rules) {
        ListTag list = new ListTag();
        for (Rule rule : rules) {
            CompoundTag entry = new CompoundTag();
            entry.putString(TAG_REGEX, rule.pattern());
            entry.putString(TAG_TOOL_TYPE, rule.toolTypeName());
            list.add(entry);
        }
        stack.getOrCreateTag().put(TAG_RULES, list);
    }

    // finds the first/best mode for which matches this block id to the multitoolo
    // stack
    public static MultitoolMode findBestMode(ItemStack stack, String blockId,
            Set<String> blockTags) {
        List<Rule> rules = getRules(stack);

        // we need to ensure the mode falls into the installed tools
        List<MultitoolMode> installed = MultitoolMode.getInstalled(stack);
        if (installed.isEmpty())
            return null;

        // match against each rule
        for (Rule rule : rules) {
            if (!rule.matches(blockId, blockTags))
                continue;

            // this rool matches, we get the tol otype from the tool type name
            GTToolType desired = GTToolType.getTypes().get(rule.toolTypeName());
            if (desired == null)
                continue;

            // if the mode is installed, we can return it
            for (MultitoolMode mode : installed) {
                if (mode.toolType() == desired)
                    return mode;
            }
        }
        return null;
    }
}