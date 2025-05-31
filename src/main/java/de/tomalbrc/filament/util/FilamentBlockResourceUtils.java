package de.tomalbrc.filament.util;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.soundpatcher.api.SoundPatcher;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceArrayMap;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class FilamentBlockResourceUtils {
    private static final Map<BlockModelType, Map<PolymerBlockModel, BlockState>> MODEL_CACHE = new Object2ObjectArrayMap<>();

    public static @Nullable BlockState requestBlock(BlockModelType type, PolymerBlockModel model, boolean virtual) {
        Map<PolymerBlockModel, BlockState> polymerBlockModelBlockStateMap = MODEL_CACHE.computeIfAbsent(type,  x -> new Object2ReferenceArrayMap<>());
        if (!virtual) {
            for (Map.Entry<PolymerBlockModel, BlockState> entry : polymerBlockModelBlockStateMap.entrySet()) {
                if (entry.getKey().equals(model)) {
                    return entry.getValue();
                }
            }
        }

        BlockState state;
        if (virtual) {
            state = PolymerBlockResourceUtils.requestEmpty(type);
        } else {
            state = PolymerBlockResourceUtils.requestBlock(type, model);
        }

        if (!virtual)
            polymerBlockModelBlockStateMap.put(model, state);

        if (state != null) {
            SoundPatcher.convertIntoServerSound(state.getSoundType());
        }

        return state;
    }
}