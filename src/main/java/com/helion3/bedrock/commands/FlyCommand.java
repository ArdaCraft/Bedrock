/**
 * This file is part of Bedrock, licensed under the MIT License (MIT).
 * <p>
 * Copyright (c) 2016 Helion3 http://helion3.com/
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.helion3.bedrock.commands;

import com.helion3.bedrock.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class FlyCommand {

    private FlyCommand() {
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.playerOrSource(Text.of("player"))
                )
                .description(Text.of("Toggle flying."))
                .executor((source, args) -> {
                    Optional<Player> player = args.<Player>getOne("player");
                    if (!player.isPresent()) {
                        source.sendMessage(Format.error("Invalid player defined."));
                        return CommandResult.empty();
                    }

                    boolean forSelf = source.equals(player.get());

                    // Permissions
                    if (!forSelf && !source.hasPermission("bedrock.fly.others")) {
                        source.sendMessage(Format.error("You do not have permission to toggle fly for other players."));
                        return CommandResult.empty();
                    } else if (forSelf && !source.hasPermission("bedrock.fly.use")) {
                        source.sendMessage(Format.error("You do not have permission to toggle fly."));
                        return CommandResult.empty();
                    }

                    // Allow flight
                    boolean canFly = player.get().get(Keys.CAN_FLY).orElse(false);
                    player.get().offer(Keys.CAN_FLY, !canFly);

                    if (canFly) {
                        player.get().offer(Keys.IS_FLYING, false);
                    }

                    // Message source
                    source.sendMessage(Format.success((canFly ? "Dis" : "En") + "abled fly" + (forSelf ? "" : " for " + player.get().getName())));

                    // Message target, if any
                    if (!forSelf) {
                        player.get().sendMessage(Format.success("Fly has been" + (canFly ? "Dis" : "En") + "abled"));
                    }

                    return CommandResult.success();
                }).build();
    }
}
