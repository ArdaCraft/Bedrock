package com.helion3.bedrock.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.helion3.bedrock.Bedrock;
import com.helion3.bedrock.PlayerConfiguration;
import com.helion3.bedrock.util.BoundedDeque;
import com.helion3.bedrock.util.Format;

public class ReturnCommand {
	public static final String NAMED = "RETURN_COMMAND";
	private static final Object CAUSE = new ReturnCommand();

	private ReturnCommand() {}

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

            PlayerConfiguration config = Bedrock.getPlayerConfigManager().getPlayerConfig(player);
            Optional<BoundedDeque<Location<World>>> history = config.getTransientData().get("tphistory");
            if (!history.isPresent() || history.get().isEmpty()) {
            	player.sendMessage(Format.error("You have no teleport history available!"));
            	return CommandResult.success();
            }

            Cause cause = Cause.builder().named(NamedCause.of(NAMED, CAUSE)).build();
            Transform<World> from = new Transform<>(player.getLocation());
            Transform<World> to = new Transform<>(history.get().poll());
            SpongeEventFactory.createDisplaceEntityEventTeleport(cause, from, to, player);

            player.sendMessage(Format.success("Teleporting you to your previous location."));

            return CommandResult.success();
        }).build();
    }
}
