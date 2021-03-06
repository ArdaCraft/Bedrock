package com.helion3.bedrock.commands;

import com.helion3.bedrock.Bedrock;
import com.helion3.bedrock.util.BoundedDeque;
import com.helion3.bedrock.util.Format;
import com.helion3.bedrock.util.TransientData;
import java.util.Optional;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ReturnCommand {

    private ReturnCommand() {
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Return to the last location you teleported to/from"))
                .permission("bedrock.return")
                .executor((source, args) -> {
                    if (!(source instanceof Player)) {
                        source.sendMessage(Format.error("Only players may use this command."));
                        return CommandResult.empty();
                    }

                    Player player = (Player) source;
                    if (Bedrock.getJailManager().isFrozen(player)) {
                        source.sendMessage(Format.error("You're frozen and may not travel."));
                        return CommandResult.empty();
                    }

                    TransientData data = Bedrock.getPlayerConfigManager().getPlayerConfig(player).getTransientData();
                    Optional<BoundedDeque<Location<World>>> history = data.get("teleport.history");
                    if (!(history.isPresent() && history.get().size() > 0)) {
                        player.sendMessage(Format.error("Your teleport history is empty."));
                        return CommandResult.empty();
                    }

                    Optional<Location<World>> location = getNext(player, history.get());
                    if (!location.isPresent()) {
                        player.sendMessage(Format.error("No return destinations further than 5 blocks from you"));
                        return CommandResult.empty();
                    }

                    data.get("teleport.return", () -> true);
                    player.sendMessage(Format.success("Teleporting you to your previous location."));
                    player.setLocation(location.get());
                    return CommandResult.success();
                }).build();
    }

    private static Optional<Location<World>> getNext(Player player, BoundedDeque<Location<World>> history) {
        while (!history.isEmpty()) {
            Location<World> location = history.poll();
            // different world so definitely tp to location
            if (location.getExtent() != player.getWorld()) {
                return Optional.of(location);
            }

            // location is more than 5 blocks from player
            if (location.getPosition().distance(player.getPosition()) > 5) {
                return Optional.of(location);
            }
        }
        return Optional.empty();
    }
}
