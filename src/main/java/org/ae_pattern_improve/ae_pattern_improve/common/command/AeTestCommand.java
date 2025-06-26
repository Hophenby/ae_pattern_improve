package org.ae_pattern_improve.ae_pattern_improve.common.command;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

@SuppressWarnings({"unchecked", "rawtypes"})
public class AeTestCommand {
    private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(Component.translatable("commands.ae_pattern_improve.test_command.error"));

    private static final String MENU_ARGUMENT_NAME = "debug_menu";

    private static int openMenu(MenuType<?> menu, Player player) throws CommandSyntaxException {
        if (!MenuOpener.open(menu, player, MenuLocators.forHand(player, InteractionHand.MAIN_HAND))) {
            throw ERROR.create();
        }
        return 1;
    }
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register((LiteralArgumentBuilder)
                ((LiteralArgumentBuilder) Commands.literal("ae_pattern_improve").requires((stack) -> stack.hasPermission(2)))
                    .then(
                            Commands.argument(MENU_ARGUMENT_NAME, EntityArgument.entities())
                                    .executes((commandContext) -> {
                                        Player player = EntityArgument.getPlayer(commandContext, MENU_ARGUMENT_NAME);
                                        return openMenu(MenuType.GENERIC_9x1, player);
                                    })));
    }
}
