package de.tomalbrc.filament.registry;

import de.tomalbrc.filament.Filament;
import de.tomalbrc.filament.behaviour.BehaviourUtil;
import de.tomalbrc.filament.behaviour.Behaviours;
import de.tomalbrc.filament.behaviour.decoration.Container;
import de.tomalbrc.filament.data.DecorationData;
import de.tomalbrc.filament.decoration.DecorationItem;
import de.tomalbrc.filament.decoration.block.ComplexDecorationBlock;
import de.tomalbrc.filament.decoration.block.DecorationBlock;
import de.tomalbrc.filament.decoration.block.SimpleDecorationBlock;
import de.tomalbrc.filament.decoration.block.entity.DecorationBlockEntity;
import de.tomalbrc.filament.util.Constants;
import de.tomalbrc.filament.util.Json;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

public class DecorationRegistry {
    private static final Object2ObjectOpenHashMap<ResourceLocation, DecorationData> decorations = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<ResourceLocation, Block> decorationBlocks = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<Block, BlockEntityType<DecorationBlockEntity>> decorationBlockEntities = new Object2ObjectOpenHashMap<>();
    public static int REGISTERED_BLOCK_ENTITIES = 0;
    public static int REGISTERED_DECORATIONS = 0;

    public static void register(InputStream inputStream) throws IOException {
        register(Json.GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), DecorationData.class));
    }

    static public void register(DecorationData data) {
        if (decorations.containsKey(data.id())) {
            decorations.put(data.id(), data); // replace anyway for /reload
            return;
        }
        decorations.put(data.id(), data);

        BlockBehaviour.Properties blockProperties = data.properties().toBlockProperties();

        var block = BlockRegistry.registerBlock(BlockRegistry.key(data.id()), getBlockCreator(data), blockProperties);
        decorationBlocks.put(data.id(), block);

        Item.Properties properties = data.properties().toItemProperties();
        for (TypedDataComponent component : data.components()) {
            properties.component(component.type(), component.value());
        }

        if (data.behaviourConfig() != null && data.isContainer()) {
            Container.ContainerConfig container = data.behaviourConfig().get(Behaviours.CONTAINER);
            if (container.canPickup)
                properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        }

        if (data.vanillaItem() == Items.LEATHER_HORSE_ARMOR || data.vanillaItem() == Items.FIREWORK_STAR) {
            properties.component(DataComponents.DYED_COLOR, new DyedItemColor(0xdaad6d, false));
        }

        var item = ItemRegistry.registerItem(ItemRegistry.key(data.id()), (newProps) -> new DecorationItem(block, data, newProps), properties, data.itemGroup() != null ? data.itemGroup() : Constants.DECORATION_GROUP_ID);
        BehaviourUtil.postInitItem(item, item, data.behaviourConfig());

        REGISTERED_DECORATIONS++;
    }

    @NotNull
    private static Function<BlockBehaviour.Properties, Block> getBlockCreator(DecorationData data) {
        Function<BlockBehaviour.Properties, Block> gen;
        if (!data.isSimple()) {
            gen = (x) -> {
                var block = new ComplexDecorationBlock(x.pushReaction(PushReaction.BLOCK), data.id());

                BlockEntityType<DecorationBlockEntity> DECORATION_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(DecorationBlockEntity::new, block).build();
                EntityRegistry.registerBlockEntity(EntityRegistry.key(data.id()), DECORATION_BLOCK_ENTITY);

                decorationBlockEntities.put(block, DECORATION_BLOCK_ENTITY);
                REGISTERED_BLOCK_ENTITIES++;

                return block;
            };
        } else {
            gen = (x) -> new SimpleDecorationBlock(x, data.id());
        }
        return gen;
    }

    public static DecorationData getDecorationDefinition(ResourceLocation resourceLocation) {
        return decorations.get(resourceLocation);
    }

    public static boolean isDecoration(Block block) {
        return decorationBlocks.containsValue(block);
    }

    public static boolean isDecoration(BlockState blockState) {
        return isDecoration(blockState.getBlock());
    }

    public static DecorationBlock getDecorationBlock(ResourceLocation resourceLocation) {
        return (DecorationBlock) decorationBlocks.get(resourceLocation);
    }

    public static BlockEntityType<DecorationBlockEntity> getBlockEntityType(BlockState blockState) {
        return decorationBlockEntities.get(blockState.getBlock());
    }


    public static class DecorationDataReloadListener implements SimpleSynchronousResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "decorations");
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            var resources = resourceManager.listResources("filament/decoration", path -> path.getPath().endsWith(".json"));
            for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
                try (var reader = new InputStreamReader(entry.getValue().open())) {
                    DecorationData data = Json.GSON.fromJson(reader, DecorationData.class);
                    DecorationRegistry.register(data);
                } catch (IOException | IllegalStateException e) {
                    Filament.LOGGER.error("Failed to load decoration resource \"{}\".", entry.getKey());
                }
            }
        }
    }
}
