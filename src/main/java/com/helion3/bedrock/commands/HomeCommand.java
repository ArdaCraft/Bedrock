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

import com.helion3.bedrock.Bedrock;
import com.helion3.bedrock.PlayerConfiguration;
import com.helion3.bedrock.util.ConfigurationUtil;
import com.helion3.bedrock.util.Format;
import java.util.Optional;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class HomeCommand {

    private HomeCommand() {
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.string(Text.of("name"))
                )
                .description(Text.of("Sets a home."))
                .permission("bedrock.home")
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

                    String name = args.<String>getOne("name").get();

                    // Grab home from config
                    PlayerConfiguration config = Bedrock.getPlayerConfigManager().getPlayerConfig(player);
                    ConfigurationNode node = config.getNode("homes", name);
                    if (node.isVirtual()) {
                        source.sendMessage(Format.error("No home with that name."));
                        return CommandResult.empty();
                    }

                    Optional<Location<World>> location = ConfigurationUtil.getNamedLocation(config.getNode("homes"), name);
                    if (!location.isPresent()) {
                        source.sendMessage(Format.error("Home location is no longer valid."));
                        return CommandResult.empty();
                    }

                    // Teleport!
                    player.setLocation(location.get());

                    // Message
                    player.sendMessage(Format.success(String.format("Teleporting you to home \"%s\"", name)));

                    return CommandResult.success();
                }).build();
    }
}
