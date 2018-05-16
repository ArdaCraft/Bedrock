package com.helion3.bedrock.listeners;

import java.util.List;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;

/**
 * @author dags <dags@dags.me>
 */
public class SignListener {

    @Listener
    public void interactSign(InteractBlockEvent.Secondary event, @Root Player player) {
        Optional<List<Text>> signLines = event.getTargetBlock().get(Keys.SIGN_LINES);
        if (signLines.isPresent() && player.hasPermission("bedrock.commandsign.use")) {
            List<Text> lines = signLines.get();
            for (int i = 0; i < lines.size(); i++) {
                Text line = lines.get(i);
                String plain = line.toPlain();
                if (plain.startsWith("/")) {
                    StringBuilder builder = new StringBuilder();
                    if (plain.endsWith("  ")) {
                        for (int j = i + 1; plain.endsWith("  ") && j < lines.size(); j++) {
                            if (plain.length() > 1) {
                                builder.append(plain.substring(0, plain.length() - 1));
                            }
                            plain = lines.get(j).toPlain();
                        }
                        builder.append(plain);
                    } else {
                        builder.append(plain);
                    }
                    String command = builder.substring(1);
                    Sponge.getCommandManager().process(player, command);
                }
            }
        }
    }
}
