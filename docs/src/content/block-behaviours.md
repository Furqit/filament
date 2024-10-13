# Block Behaviours

Block behaviours can be customized to add unique properties to blocks. All behaviours are optional, and some can be applied to both, blocks and decorations.

Blocks also work with most item-behaviours to give the blocks' item special features.

Some block behaviours provide blockstate properties you will have to provide models for, like `axis`, `repeater`, `crop`, `directional`, etc.

Example of a blockResource entry for the `repeater` behaviour (can be found as relay block in the example datapack):
```json
{
  "blockResource": {
    "models": {
      "facing=up,powered=false": "minecraft:custom/block/arcanery/relay/relay_up_off",
      "facing=down,powered=false": "minecraft:custom/block/arcanery/relay/relay_down_off",
      "facing=north,powered=false": "minecraft:custom/block/arcanery/relay/relay_north_off",
      "facing=south,powered=false": "minecraft:custom/block/arcanery/relay/relay_south_off",
      "facing=east,powered=false": "minecraft:custom/block/arcanery/relay/relay_east_off",
      "facing=west,powered=false": "minecraft:custom/block/arcanery/relay/relay_west_off",
      "facing=up,powered=true": "minecraft:custom/block/arcanery/relay/relay_up_on",
      "facing=down,powered=true": "minecraft:custom/block/arcanery/relay/relay_down_on",
      "facing=north,powered=true": "minecraft:custom/block/arcanery/relay/relay_north_on",
      "facing=south,powered=true": "minecraft:custom/block/arcanery/relay/relay_south_on",
      "facing=east,powered=true": "minecraft:custom/block/arcanery/relay/relay_east_on",
      "facing=west,powered=true": "minecraft:custom/block/arcanery/relay/relay_west_on"
    }
  }
}
```

Example of a block with behaviours set:
```json
{
  "id": "mynamespace:myblock",
  "blockResource": {
    "models": {
      "...": "mynamespace:custom/block/myblock",
      "...": "..."
    }
  },
  "blockModelType": "full_block",
  "properties": {
    "destroyTime": 0,
    "blockBase": "minecraft:stone",
    "itemBase": "minecraft:paper"
  },
  "behaviour": {
    "powersource": {
      "value": 15
    },
    "repeater": {
      "delay": 1,
      "loss": 1
    },
    "fuel": {
      "value": 10
    },
    "food": {
      "hunger": 2,
      "saturation": 1.0,
      "canAlwaysEat": true,
      "fastfood": true
    },
    "cosmetic": {
      "slot": "chest",
      "model": "mynamespace:custom/models/clown_backpack_animated",
      "autoplay": "idle",
      "scale": [1.5, 1.5, 1.5],
      "translation": [0.0, 0.5, 0.0]
    },
    "strippable": {
      "replacement": "minecraft:stone"
    }
  }
}
```
This creates a block + item that can be worn and when worn shows an animated blockbench model on the player. The item is also a food and can be used as fuel source in furnaces.

The block acts as a redstone powersource of level 15 and a repeater/relay and is strippable (turns to stone when stripped with an axe or an item with the `stripper` item behaviour)

While possible, you probably don't want to combine `powersource` with `repeater` for actual blocks/items for obvious reasons.

---
# Behaviours

### `axis` behaviour

Gives the block an `axis` property/block-state similar to wooden logs/pillars and handles placement.

- **Block-State-Properties to provide models for**:
  - `axis`: x, y, z

---

### `count` behaviour

Gives the block a `count` property/block-state.

Works similar to turtle eggs or candles, allows you to place "multiple blocks/items" into one block.

- **Block-State-Properties to provide models for**:
  - `count`: 1...max

---

### `facing` behaviour

Gives the block a `facing` property/block-state similar to wooden logs/pillars and handles placement.

- **Block-State-Properties to provide models for**:
  - `facing`: north, east, south, west, up, down

---

### `horizontal_facing` behaviour

Gives the block a `facing` property/block-state similar to furnaces and handles placement.
Does not support up and down facing directions.

- **Block-State-Properties to provide models for**:
  - `facing`: north, east, south, west

---

### `crop` behaviour

Makes the block behave like a crop, bonemeable, growing, minimum light requirement, etc.
Also gives a growth bonus similar to vanilla crops, which check for farmland blocks in a 3x3 area centered below the crop block.
The bonus block and radius can be configured.

You probably want to use this behaviour together with the `can_survive` behaviour.

If you want your custom crop block to *not* turn farmland into dirt *without* moisture, like vanilla crops do, add your block to the block tag `maintains_farmland`.

In order for the farmland to not turn into dirt when placing the crop on top of it, make sure the `solid` property is set to `false`

For bee pollination, use the block tag `bee_growables`.

You can make farmer villagers able to plant the seeds using the item tag `villager_plantable_seeds`. Villagers will only work on crops that are on top of farmland blocks (vanilla limitation).

- **Block-State-Properties to provide models for**:
  - `age`: 0...maxAge-1

- **Fields**:
  - `maxAge`: maximum age steps of this block (from 0 to maxAge-1). Defaults to 4.
  - `minLightLevel`: Minimum light level this crop needs to survive. Defaults to 8.
  - `bonusRadius`: Radius to check for bonus blocks for. Defaults to 1.
  - `bonusBlock`: Bonus block to check for. More bonus blocks means faster growth. Defaults to `minecraft:farmland`.
  - `villagerInteraction`: Allows farmer villagers to break and plant the custom crop. Defaults to `true`.
  - `beeInteraction`: Allows bees to pollinate the crop to increase its age. Defaults to `true`.

---

### `can_survive` behaviour

Checks for the block below with one of the configured block tags or blocks list.
The block will break off, similar to flowers or crops, when the block below them is not supported.

Useful for bushes/plants/crops/flowers and more

- **Fields**:
  - `blocks`: List of blocks this block can survive on. 
    - Example: `blocks: ["minecraft:stone", "minecraft:sand"]`
  - `tags`: List of block-tags this block can survive on. 
    - Example: `tags: ["minecraft:dirt", "minecraft:sculk_replaceable"]`

---

### `powersource` behaviour

Defines the block as a redstone power source.

The `value` field can map to a block-state, like many other block-related fields.

Example:
```
"value": {
  "age=0": 0,
  "age=1": 15
}
```

- **Fields**:
    - `value`: The redstone power value the block emits (can be mapped). Defaults to 15

---

### `repeater` behaviour

Defines the block as a redstone repeater with configurable delay and loss.

- **Fields**:
    - `delay`: Delay in ticks. Defaults to 0
    - `loss`: Power loss during transfer. Defaults to 0


- **Block-State-Properties to provide models for**:
  - `powered`: true, false
  - `facing`: north, east, south, west, up, down

---

### `powerlevel` behaviour

Supplies a `powerlevel` blockstate and changes to it depending on the input redstone signal.

- **Fields**:
  - `max`: Maximum powerlevel this block can display


- **Block-State-Properties to provide models for**:
  - `powerlevel`: 0...max-1

---

### `strippable` behaviour

Defines the block as strippable, replacing it with another block when interacted with an axe.

- **Fields**:
    - `replacement`: The identifier of the block to replace the current block with (e.g., "minecraft:stone").

---

### `slab` behaviour

Defines the block as slab, top, bottom, double, with placements, waterloggable.

- **Block-State-Properties to provide models for**:
  - `type`: top, bottom, double
  - `waterlogged`: true, false

---

### `trapdoor` behaviour

Trapdoor like block.

- **Block-State-Properties to provide models for**:
  - `facing`: north, south, east, west, up, down
  - `half`: top, bottom
  - `open`: true, false
  - `waterlogged`: true, false


- **Fields**:
  - `canOpenByWindCharge`: Whether the trapdoor can be opened by a wind charge. Defaults to `true`
  - `canOpenByHand`: Whether the trapdoor can be opened by hand. Defaults to `true`
  - `openSound`: Open sound. Defaults to wooden trapdoor open sound.
  - `closeSound` = Close sound. Defaults to wooden trapdoor close sound.

---

### `door` behaviour

Door-like "block" that is 2 blocks high.
Comes with all door block state properties (hinge, open, powered, etc)

- **Block-State-Properties to provide models for**:
  - `facing`: north, south, east, west, up, down
  - `half`: lower, upper
  - `open`: true, false
  - `hinge`: left, right


- **Fields**:
  - `canOpenByWindCharge`: Whether the door can be opened by a wind charge. Defaults to `true`
  - `canOpenByHand`: Whether the door can be opened by hand. Defaults to `true`
  - `openSound`: Open sound. Defaults to wooden door open sound.
  - `closeSound` = Close sound. Defaults to wooden door close sound.

---

### `simple_waterloggable` behaviour

Simple waterloggable block.

---

### `drop_xp` behaviour

Makes the block drop xp when being mined without the silk-touch enchantment.

The values of the `min` and `max` fields can be mapped to block-states.

Example:
```
{
  ...,
  "behaviour": {
    "drop_xp": {
      "min": {
        "age=0": 0,
        "age=1": 0,
        "age=2": 4
      },
      "max": {
        "age=0": 0,
        "age=1": 0,
        "age=2": 6
      }
    }
  }
}
```

- **Fields**:
  - `min`: Minimum amount of XP to drop
  - `max`: Maximum amount of XP to drop

### `oxidizable` behaviour

Defines the block as oxidizing block, similar to the vanilla copper blocks, randomly replacing it with another block when it "ages". Can be reverted/scraped by axes and resets with lightning bolts like vanilla copper blocks.

- **Fields**:
  - `replacement`: The identifier of the block to replace the current block with (e.g., "minecraft:stone").
  - `weatherState`: The current weathering state of this block. Can be `unaffected`, `exposed`, `weathered`, `oxidized`. Defaults to `unaffected`. A `weatherState` of `oxidized` will not oxidize any further.

### `budding` behaviour

With this behaviour the blocks grows other blocks, similar to budding amethyst blocks.
The sides, blocks and chance can be configured.

If the blocks in `grows` have directional/facing block state properties, they direction of the side the block is growing from will be set.

- **Fields**:
  - `chance`: Chance of the block to grow another block or move a block to the next growth stage in percent, from 0 to 100. Defaults to 20
  - `sides`: List of sides blocks can grow out. Can be `north`, `south`, `east`, `west`, `up` or `down`. Defaults to all directions
  - `grows`: List of id's of blocks for the grow stages. Example: `["minecraft:chain", "minecraft:end_rod"]`
