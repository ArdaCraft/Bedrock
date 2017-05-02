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

import com.helion3.bedrock.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;

public class HealCommand {
    private HealCommand() {}

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
        .arguments(
            GenericArguments.playerOrSource(Text.of("player"))
        )
        .description(Text.of("Heal yourself or another player."))
        .executor((source, args) -> {
            Player player = args.<Player>getOne("player").get();
            boolean forSelf = source.equals(player);

            // Permissions
            if (!forSelf && !source.hasPermission("bedrock.heal.others")) {
                source.sendMessage(Format.error("You do not have permission to heal other players."));
                return CommandResult.empty();
            }
            else if (forSelf && !source.hasPermission("bedrock.heal.use")) {
                source.sendMessage(Format.error("Insufficient permissions."));
                return CommandResult.empty();
            }

            // Extinquish
            player.offer(Keys.FIRE_TICKS, 0);

            // Heal
            player.offer(Keys.HEALTH, player.get(Keys.MAX_HEALTH).get());

            // Feed
            player.offer(Keys.FOOD_LEVEL, 20);

            // Remove potion effects
            player.offer(Keys.POTION_EFFECTS, new ArrayList<>());

            // Message
            player.sendMessage(Format.success("Healed!"));
            if (!forSelf) {
                source.sendMessage(Format.success(String.format("Healed %s", player.getName())));
            }

            return CommandResult.success();
        }).build();
    }
}
