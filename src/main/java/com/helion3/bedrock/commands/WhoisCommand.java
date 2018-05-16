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
import com.helion3.bedrock.util.ConnectionUtil;
import com.helion3.bedrock.util.Format;
import java.time.Instant;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class WhoisCommand {

    private WhoisCommand() {
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("View player details."))
                .arguments(
                        GenericArguments.player(Text.of("player"))
                )
                .permission("bedrock.whois")
                .executor((source, args) -> {
                    Player player = args.<Player>getOne("player").get();

                    source.sendMessage(Format.heading(String.format("Who is %s:", player.getName())));
                    source.sendMessage(Format.message(String.format("UUID: %s", player.getUniqueId())));
                    source.sendMessage(Format.message(String.format("IP: %s", ConnectionUtil.ip(player))));
                    source.sendMessage(Format.message(String.format("World: %s", player.getWorld().getName())));

                    boolean isFlying = player.get(Keys.IS_FLYING).orElse(false);
                    source.sendMessage(Text.of(Format.message("Flying: "), Format.bool(isFlying)));

                    boolean isAFK = Bedrock.getAFKManager().isAFK(player);
                    source.sendMessage(Text.of(Format.message("Afk: "), Format.bool(isAFK)));

                    double health = player.get(Keys.HEALTH).orElse(-1D);
                    source.sendMessage(Format.message(String.format("Health: %f", health)));

                    Instant instant = player.lastPlayed().get();
                    source.sendMessage(Format.message(String.format("Last played: %s", instant)));

                    return CommandResult.success();
                }).build();
    }
}
