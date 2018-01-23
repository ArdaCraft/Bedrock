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
import com.helion3.bedrock.PlayerConfiguration;
import com.helion3.bedrock.util.Format;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class SetHomeCommand {
    private SetHomeCommand() {}

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
            String name = args.<String>getOne("name").get();
            PlayerConfiguration config = Bedrock.getPlayerConfigManager().getPlayerConfig(player);

            int homesLimit = -1;
            Optional<String> optionHomeLimit = player.getOption("homes");
            if (optionHomeLimit.isPresent()) {
                homesLimit = Integer.parseInt(optionHomeLimit.get());
            }

            if (homesLimit > 0 && config.getNode("homes").getChildrenMap().size() > homesLimit) {
                source.sendMessage(Format.error(String.format("You may not have any more homes. Limit: %d", homesLimit)));
                return CommandResult.empty();
            }

            // Save to config
            ConfigurationNode node = config.getNode("homes", name);
            node.getNode("x").setValue(player.getLocation().getX());
            node.getNode("y").setValue(player.getLocation().getY());
            node.getNode("z").setValue(player.getLocation().getZ());
            node.getNode("world").setValue(player.getWorld().getName());
            config.save();

            source.sendMessage(Format.heading(String.format("Home %s saved.", name)));

            return CommandResult.success();
        }).build();
    }
}
