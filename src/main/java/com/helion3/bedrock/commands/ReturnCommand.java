package com.helion3.bedrock.commands;

import com.helion3.bedrock.Bedrock;
import com.helion3.bedrock.util.BoundedDeque;
import com.helion3.bedrock.util.Format;
import com.helion3.bedrock.util.TransientData;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

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
                        return CommandResult.success();
                    }

                    data.get("teleport.return", () -> true);
                    player.sendMessage(Format.success("Teleporting you to your previous location."));
                    player.setLocation(history.get().poll());

                    return CommandResult.success();
                }).build();
    }
}
