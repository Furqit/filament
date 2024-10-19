# Items

## File Location

Item configuration files should be placed in the following directory:
```
MyDatapack/data/<namespace>/filament/item/myitem.json
```

---

## Contents

Item configurations have two required fields: `id` and `vanillaItem`.

---

### `id`

Your custom ID in the format `namespace:item_name`

---

### `vanillaItem`

The vanilla item to "overwrite". Filament (through Polymer) will create `custom_model_data` IDs for the generated resource pack. Your custom item will not inherit any other properties server-side from the vanilla item other than appearance, if the `itemResource` field is not set. For interaction purposes on the client, it is important to choose the appropriate vanilla item to use here.

---

## Optional Fields

The fields `itemResource`, `properties`, `group`, and `behaviour` are optional.

### `itemResource`

- **Description**: Specifies the resource(s) for the item model. Depending on the item's behaviour(s), it may use additional keys/fields in `itemResource`.
- **Fields**:
  - `models`: An object containing model definitions.
    - `default`: The default model for the item.
    - Additional keys may be required depending on the item's behaviour (e.g., `trapped` for a trap behaviour).
  - `textures` (upcoming in future versions): An object containing texture definitions.
    - `default`: The default texture for the item.
    - Additional keys may be required depending on the item's behaviour.

---

### `properties`

Defines various properties for the item. The structure and contents of this field will depend on the specific properties being set.

---

### `group`

Defines the item-group for this item. See [Item Groups](item-groups.md) for more information.

---

### `behaviour`

Defines specific behaviours or interactions for the item. The structure and contents of this field will depend on the specific behaviours being set.

See [Item Behaviours](item-behaviours.md) for more information.

---

### `components`

Defines a set of vanilla minecraft components like `minecraft:food`, `minecraft:tool`, etc. The format is the same that is used in datapacks. This is only supported in minecraft version 1.20.5 and later

---

## Example

Here is a basic example of an item configuration:

~~~admonish example
```json
{
  "id": "mynamespace:clown_horn",
  "vanillaItem": "minecraft:paper",
  "itemResource": {
    "models": {
      "default": "mynamespace:custom/misc/clown_horn"
    }
  },
  "properties": {
    ...
  },
  "behaviour": {
    ...
  },
  "components": {
    ...
  }
}
```
~~~
