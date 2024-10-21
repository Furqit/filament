package de.tomalbrc.filament.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.tomalbrc.filament.util.Util;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

import java.util.Optional;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class DyeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var dyeNode = Commands
                .literal("dye").requires(Permissions.require("filament.command.dye", 1));

        var colorArg = Commands.argument("color", StringArgumentType
                .string());

        dispatcher.register(dyeNode.then(colorArg.executes(DyeCommand::execute)));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getPlayer() != null) {
            ItemStack item = context.getSource().getPlayer().getMainHandItem();
            if (item != null && !item.isEmpty()) {
                String hexColorString = getString(context, "color");
                Optional<Integer> color = Util.validateAndConvertHexColor(hexColorString);
                if (color.isPresent()) {
                    item.set(DataComponents.DYED_COLOR, new DyedItemColor(color.get(), true));
                    return Command.SINGLE_SUCCESS;
                }
            }
        }
        return 0;
    }
}
