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

import com.google.common.collect.Iterables;
import com.helion3.bedrock.Bedrock;
import com.helion3.bedrock.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public class PerformanceCommand {

    private PerformanceCommand() {
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Display server performance statistics."))
                .permission("bedrock.performance")
                .executor((source, args) -> {
                    source.sendMessage(Format.heading("Performance Stats"));
                    source.sendMessage(Format.message("TPS: " + Bedrock.getGame().getServer().getTicksPerSecond()));

                    source.sendMessage(Format.message("Worlds:"));
                    for (World world : Bedrock.getGame().getServer().getWorlds()) {
                        source.sendMessage(Text.of(TextColors.WHITE, world.getName() + "\n", TextColors.GRAY,
                                " Entities: ", TextColors.YELLOW, world.getEntities().size(), TextColors.GRAY,
                                " Chunks: ", TextColors.YELLOW, Iterables.size(world.getLoadedChunks())));
                    }

                    return CommandResult.success();
                }).build();
    }
}
