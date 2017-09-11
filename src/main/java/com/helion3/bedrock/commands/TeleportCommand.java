/**
 * This file is part of Bedrock, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.helion3.bedrock.commands;

import com.helion3.bedrock.Bedrock;
import com.helion3.bedrock.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Optional;

public class TeleportCommand {
    private TeleportCommand() {}

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
        .arguments(
            GenericArguments.player(Text.of("player1")),
            GenericArguments.optional(GenericArguments.player(Text.of("player2")))
        )
        .description(Text.of("Teleport to another player."))
        .permission("bedrock.tp")
        .executor((source, args) -> {
            Optional<Player> player1 = getBestMatch(args.getAll("player1"));
            Optional<Player> player2 = getBestMatch(args.getAll("player2"));

            if (player1.isPresent()) {
                Player from = player1.get();
                Player to;

                if (player2.isPresent()) { // `/tp <player1> <player2>`
                    to = player2.get();
                } else if (source instanceof Player) {
                    to = from;
                    from = (Player) source; // `/tp <player>`
                } else {
                    source.sendMessage(Format.error("Only players may use this command."));
                    return CommandResult.empty();
                }

                if (from == to) {
                    source.sendMessage(Format.error("Cannot tp to self"));
                    return CommandResult.empty();
                }

                if (to.get(Keys.VANISH).orElse(false) && !source.hasPermission("bedrock.vanishtp")) {
                    return CommandResult.empty();
                }

                if (Bedrock.getJailManager().isFrozen(from)) {
                    source.sendMessage(Format.error("Player is frozen and may not travel."));
                    return CommandResult.empty();
                }

                Bedrock.getTeleportManager().teleport(from, to);
            }
            return CommandResult.success();
        }).build();
    }

    private static Optional<Player> getBestMatch(Collection<Player> players) {
        if (players.isEmpty()) {
            return Optional.empty();
        }
        if (players.size() == 1) {
            return Optional.ofNullable(players.iterator().next());
        }
        return players.stream().sorted((p1, p2) -> Integer.compare(p1.getName().length(), p2.getName().length())).findFirst();
    }
}
