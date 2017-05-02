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
import com.helion3.bedrock.util.ConfigurationUtil;
import com.helion3.bedrock.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class BackCommand {
    private BackCommand() {}

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
        .description(Text.of("Teleport to the location of your previous death."))
        .permission("bedrock.back")
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

            Optional<Location<World>> death = ConfigurationUtil.getNamedLocation(config.getNode("deaths"), "last");
            if (!death.isPresent()) {
                player.sendMessage(Format.error("Either no death location saved, or it's no longer valid."));
                return CommandResult.success();
            }

            player.setLocation(death.get());
            player.sendMessage(Format.success("Teleporting you to your last death location."));

            return CommandResult.success();
        }).build();
    }
}
