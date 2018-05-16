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
import java.util.Optional;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class TimeCommand {

    private TimeCommand() {
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.firstParsing(
                                GenericArguments.string(Text.of("timeOfDay")),
                                GenericArguments.integer(Text.of("time"))
                        )
                )
                .description(Text.of("Change the time."))
                .permission("bedrock.time")
                .executor((source, args) -> {
                    if (!(source instanceof Player)) {
                        source.sendMessage(Format.error("Only players may use this command."));
                        return CommandResult.empty();
                    }

                    Player player = (Player) source;
                    Optional<String> timeOfDay = args.<String>getOne("timeOfDay");
                    Optional<Integer> time = args.<Integer>getOne("time");

                    if (timeOfDay.isPresent()) {
                        switch (timeOfDay.get().toLowerCase()) {
                            case "dawn":
                            case "am":
                            case "morning":
                                player.getWorld().getProperties().setWorldTime(0);
                                break;
                            case "day":
                                player.getWorld().getProperties().setWorldTime(1000);
                                break;
                            case "noon":
                            case "pm":
                                player.getWorld().getProperties().setWorldTime(6000);
                                break;
                            case "dusk":
                            case "sunset":
                                player.getWorld().getProperties().setWorldTime(12000);
                                break;
                            case "night":
                                player.getWorld().getProperties().setWorldTime(14000);
                                break;
                            case "midnight":
                                player.getWorld().getProperties().setWorldTime(18000);
                                break;
                            default:
                                source.sendMessage(Format.error("Invalid time. May be dawn, day, noon, dusk, night, or midnight."));
                                return CommandResult.empty();

                        }

                        source.sendMessage(Format.success(String.format("Changed the time to %s", timeOfDay.get().toLowerCase())));
                    } else if (time.isPresent()) {
                        player.getWorld().getProperties().setWorldTime(time.get());
                        source.sendMessage(Format.success(String.format("Changed the time to %d", time.get())));
                    }

                    return CommandResult.success();
                }).build();
    }
}
