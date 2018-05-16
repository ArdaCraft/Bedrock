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

import com.flowpowered.math.vector.Vector3d;
import com.helion3.bedrock.Bedrock;
import com.helion3.bedrock.util.Format;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

public class TeleportCommand {

    private TeleportCommand() {
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.optional(GenericArguments.player(Text.of("player1"))),
                        GenericArguments.optional(GenericArguments.player(Text.of("player2"))),
                        GenericArguments.optional(GenericArguments.vector3d(Text.of("position")))
                )
                .description(Text.of("Teleport to another player."))
                .permission("bedrock.tp")
                .executor((source, args) -> {
                    Optional<Player> player1 = getBestMatch(args.getAll("player1"));
                    Optional<Player> player2 = getBestMatch(args.getAll("player2"));
                    Optional<Vector3d> position = args.getOne("position");

                    Player from = null;
                    String description = "";
                    Transform<World> transform = null;

                    // set initial player & transform from source
                    if (source instanceof Player) {
                        from = (Player) source;
                        transform = from.getTransform();
                    }

                    if (player1.isPresent()) {
                        from = player1.get();

                        if (position.isPresent()) { // `/tp <player1> <xyz>`
                            // isn't teleporting self & doesn't have permission to teleport other players
                            if (from != source && !source.hasPermission("bedrock.othertp")) {
                                throw new CommandException(Format.error("You do not have permission to do that."));
                            }
                        } else {
                            final Player to; // compile error if we do not assign 'to'

                            if (player2.isPresent()) { // `/tp <player1> <player2>`
                                if (source.hasPermission("bedrock.othertp")) {
                                    to = player2.get();
                                } else {
                                    throw new CommandException(Format.error("You do not have permission to do that."));
                                }
                            } else if (source instanceof Player) { // `/tp (self) <player1>`
                                from = (Player) source;
                                to = player1.get();
                            } else {
                                throw new CommandException(Format.error("Must specify a second player or position to teleport the target to."));
                            }

                            // can't tp to vanished players
                            if (to.get(Keys.VANISH).orElse(false) && !source.hasPermission("bedrock.vanishtp")) {
                                return CommandResult.empty();
                            }

                            transform = to.getTransform();
                            description = to.getName();
                        }
                    }

                    // modify the transform if a position was provided
                    if (transform != null && position.isPresent()) {
                        Vector3d pos = position.get();
                        transform = transform.setPosition(pos); // set position, keep rotation & world of original transform
                        description = String.format("%f %f %f", pos.getX(), pos.getY(), pos.getZ());
                    }

                    if (from != null) {
                        if (Bedrock.getJailManager().isFrozen(from)) {
                            throw new CommandException(Format.error("Player is frozen and may not travel."));
                        }

                        if (Bedrock.getTeleportManager().teleport(from, transform, description)) {
                            return CommandResult.success();
                        }

                        throw new CommandException(Format.error("Teleportation failed!"));
                    }

                    return CommandResult.empty();
                }).build();
    }

    private static Optional<Player> getBestMatch(Collection<Player> players) {
        if (players.isEmpty()) {
            return Optional.empty();
        }
        if (players.size() == 1) {
            return Optional.ofNullable(players.iterator().next());
        }
        return players.stream().sorted(Comparator.comparingInt(p -> p.getName().length())).findFirst();
    }
}
