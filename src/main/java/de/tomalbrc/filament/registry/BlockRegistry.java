package de.tomalbrc.filament.registry;

import de.tomalbrc.filament.Filament;
import de.tomalbrc.filament.behaviour.BehaviourUtil;
import de.tomalbrc.filament.block.SimpleBlock;
import de.tomalbrc.filament.block.SimpleBlockItem;
import de.tomalbrc.filament.data.BlockData;
import de.tomalbrc.filament.data.properties.BlockProperties;
import de.tomalbrc.filament.util.Constants;
import de.tomalbrc.filament.util.Json;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.Registry;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

public class BlockRegistry {
    public static int REGISTERED_BLOCKS = 0;

    public static void register(InputStream inputStream) throws IOException {
        register(Json.GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), BlockData.class));
    }

    public static void register(BlockData data) throws IOException {
        if (BuiltInRegistries.BLOCK.containsKey(data.id())) return;

        BlockProperties properties = data.properties();
        BlockBehaviour.Properties blockProperties = properties.toBlockProperties();

        SimpleBlock customBlock = BlockRegistry.registerBlock(key(data.id()), (props)-> new SimpleBlock(props, data), blockProperties);

        Item.Properties itemProperties = data.properties().toItemProperties();
        for (TypedDataComponent component : data.components()) {
            itemProperties.component(component.type(), component.value());
        }

        SimpleBlockItem item = ItemRegistry.registerItem(ItemRegistry.key(data.id()), (newProps) -> new SimpleBlockItem(newProps, customBlock, data), itemProperties, data.itemGroup() != null ? data.itemGroup() : Constants.BLOCK_GROUP_ID);
        BehaviourUtil.postInitItem(item, item, data.behaviourConfig());
        BehaviourUtil.postInitBlock(item, customBlock, customBlock, data.behaviourConfig());

        customBlock.postRegister();

        REGISTERED_BLOCKS++;
    }

    public static ResourceKey<Block> key(ResourceLocation id) {
        return ResourceKey.create(Registries.BLOCK, id);
    }

    public static <T extends Block> T registerBlock(ResourceKey<Block> resourceKey, Function<BlockBehaviour.Properties, T> function, BlockBehaviour.Properties properties) {
        T block = function.apply(properties.setId(resourceKey));
        return Registry.register(BuiltInRegistries.BLOCK, resourceKey, block);
    }

    public static class BlockDataReloadListener implements SimpleSynchronousResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, Constants.MOD_ID);
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            var resources = resourceManager.listResources("filament/block", path -> path.getPath().endsWith(".json"));
            for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {

                try (var reader = new InputStreamReader(entry.getValue().open())) {
                    BlockData data = Json.GSON.fromJson(reader, BlockData.class);
                    BlockRegistry.register(data);
                } catch (IOException | IllegalStateException e) {
                    Filament.LOGGER.error("Failed to load block resource \"{}\".", entry.getKey());
                }
            }
        }
    }
}
