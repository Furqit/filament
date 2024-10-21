package de.tomalbrc.filament.behaviour.block;

import de.tomalbrc.filament.api.behaviour.BlockBehaviour;
import de.tomalbrc.filament.data.properties.BlockStateMappedProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Block behaviour for redstone power source
 */
public class Powersource implements BlockBehaviour<Powersource.PowersourceConfig> {
    private final PowersourceConfig config;

    public Powersource(PowersourceConfig config) {
        this.config = config;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return config.value.getOrDefault(blockState, 0);
    }

    @Override
    @NotNull
    public PowersourceConfig getConfig() {
        return this.config;
    }

    public static class PowersourceConfig {
        /**
         * The redstone power values
         */
        public BlockStateMappedProperty<Integer> value = BlockStateMappedProperty.of(15);
    }
}
