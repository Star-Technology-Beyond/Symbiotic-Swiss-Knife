---
navigation:
  title: Auto Select
  parent: index.md
  icon: symbiotic_swiss_knife:multitool_pickaxe
  position: 4
---

# Auto Select

Auto select allows the Symbiotic Swiss Knife to automatically switch to the best installed
tool for a block when you pick-block/middle-click it. Which tool gets selected is controlled by a
list of rules you configure per-tool.

## How It Works

Each rule has two parts:

- A **pattern**: a way to express a match against a block's ID or its block tags
- A **tool**: the installed tool to switch to when the pattern matches

When you pick-block, the rules are checked from top to bottom. The first rule
whose pattern matches the block's ID or any of its tags is used to select the tool.
If no rule matches, a message appears in chat.

## Help! Why do my patterns not match?

If you want some more information about the block you're looking at for debugging information,
you can modify the client configuration of Symbiotic Swiss Knife. 

The `isDebugNoBlockFound` option when turned to true will display the short ID, block ID and
tags of the non-matched block.

## Opening the Rule Editor

Open the radial selector with <KeyBind id="key.symbiotic_swiss_knife.multitool_selector" />,
then click the **Auto Select Rules** button to open the rule editor.

## Managing Rules

### Adding a Rule

Type a pattern into the text field at the bottom of the editor. A **✔** appears when the
pattern is a valid, and a **✘** when it is not. Select a tool from the
dropdown next to the field, then click **Add** or press **Enter** to add the rule.

### Reordering Rules

Drag a rule by its **≡** handle on the left to reorder it. Rules are checked top to bottom,
so more specific patterns should go above more general ones.

While dragging, press <KeyBind id="key.forward" /> to jump the list to the top, or
<KeyBind id="key.back" /> to jump to the bottom.

### Removing a Rule

Click the **✕** button on the right side of a rule to delete it.

### Copying a Pattern

Click the **❐** button next to the drag handle to copy that rule's pattern to your clipboard.

## Saving

Click **Save** at the bottom of the editor to save your rules. Rules are stored on the
multitool item itself, so each tool can have its own independent set of rules.

## Writing Patterns

Patterns support two syntaxes which can be freely combined using `|` which means or.

### Grouping with Parentheses

A single layer of parentheses is stripped before a segment is evaluated, so these are
identical:

```
#minecraft:mineable/pickaxe
(#minecraft:mineable/pickaxe)
```

This is mainly useful for clarity when combining tag and regex segments:

```
(#minecraft:mineable/pickaxe)|(.*_ore)
```

Note that only **one** layer of parentheses are stripped. Nested parentheses are left intact and treated
as part of the regex.

### Tag Matching

Prefix a tag ID with `#` to match any block that belongs to that tag. These
are some examples of tags:

| Tag Pattern | Tool |
|---|---|
| `#forge:mineable/wrench` | Wrench (IV) |
| `#forge:mineable/wire_cutter` | Wire Cutter (IV) |

### Regex Matching

Any pattern without a leading `#` is treated as a Java regular expression matched against
the block's full or short ID (e.g. `minecraft:stone` or just `stone`):

| Regex Pattern | Tool |
|---|---|
| `.*grass.*\|.*fern.*\|.*vine.*\|.*cobweb.*\|.*leaves.*\|.*wool.*` | Shears |
| `ae2:.*` | Wrench |

