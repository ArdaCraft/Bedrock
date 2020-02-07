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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;
import java.util.Optional;

public class WorldLoad {

    private WorldLoad() {
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .arguments(GenericArguments.string(Text.of("name")))
                .description(Text.of("Load the specified world if it isn't already loaded"))
                .permission("bedrock.world.load")
                .executor((source, args) -> {
                    String name = args.<String>getOne(Text.of("name")).orElse("");
                    if (name.isEmpty()) {
                        source.sendMessage(Format.error("Invalid world name defined."));
                        return CommandResult.empty();
                    }

                    try {
                        WorldProperties properties = Sponge.getServer().createWorldProperties(name, WorldArchetype.builder()
                                .loadsOnStartup(true)
                                .enabled(true)
                                .build(name.toLowerCase(), name));

                        Optional<World> world = Sponge.getServer().loadWorld(properties);
                        if (!world.isPresent()) {
                            source.sendMessage(Format.error("Unable to load world: ", name));
                            return CommandResult.empty();
                        }

                        source.sendMessage(Format.success("World loaded!"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        source.sendMessage(Format.error("Unable to load world: ", name));
                        return CommandResult.empty();
                    }

                    return CommandResult.success();
                }).build();
    }

}
