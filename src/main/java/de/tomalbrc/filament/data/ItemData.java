package de.tomalbrc.filament.data;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.filament.behaviour.BehaviourConfigMap;
import de.tomalbrc.filament.data.properties.ItemProperties;
import de.tomalbrc.filament.data.resource.ItemResource;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("unused")
public record ItemData(
        @Nullable Map<String, String> displayName,
        @NotNull ResourceLocation id,
        @Nullable Item vanillaItem,
        @Nullable ItemResource itemResource,
        @SerializedName("behaviour")
        @Nullable BehaviourConfigMap behaviourConfig,
        @Nullable ItemProperties properties,
        @Nullable DataComponentMap components,
        @SerializedName("group")
        @Nullable ResourceLocation itemGroup
) {
    @Override
    @NotNull
    public ItemProperties properties() {
        if (properties == null) {
            return ItemProperties.EMPTY;
        }
        return properties;
    }

    @Override
    @NotNull
    public DataComponentMap components() {
        if (components == null) {
            return DataComponentMap.EMPTY;
        }
        return components;
    }

    @Override
    @NotNull
    public Item vanillaItem() {
        if (vanillaItem == null) {
            return Items.PAPER;
        }
        return vanillaItem;
    }

    public Object2ObjectOpenHashMap<String, PolymerModelData> requestModels() {
        Object2ObjectOpenHashMap<String, PolymerModelData> map = new Object2ObjectOpenHashMap<>();
        if (itemResource != null) {
            itemResource.models().forEach((key, value) -> map.put(key, PolymerResourcePackUtils.requestModel(this.vanillaItem == null ? Items.PAPER : this.vanillaItem, value)));
        }
        return map.isEmpty() ? null : map;
    }
}
