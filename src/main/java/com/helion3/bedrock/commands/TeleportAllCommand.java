package com.helion3.bedrock.commands;

import com.helion3.bedrock.Bedrock;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * @author dags <dags@dags.me>
 */
public final class TeleportAllCommand {

    private TeleportAllCommand() {

    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .arguments(GenericArguments.optional(GenericArguments.player(Text.of("target"))))
                .description(Text.of("Teleport all players to you."))
                .permission("bedrock.tpall")
                .executor((source, args) -> {
                    Optional<Player> playerOptional = args.getOne(Text.of("target"));
                    final Player target;

                    if (playerOptional.isPresent()) {
                        target = playerOptional.get();
                    } else if (source instanceof Player) {
                        target = (Player) source;
                    } else {
                        throw new CommandException(Text.of("Target player required!"));
                    }

                    for (Player player : Sponge.getServer().getOnlinePlayers()) {
                        if (player == target) {
                            continue;
                        }
                        Bedrock.getTeleportManager().teleport(player, target);
                    }

                    return CommandResult.success();
                }).build();
    }
}
