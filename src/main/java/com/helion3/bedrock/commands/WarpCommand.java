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
import com.helion3.bedrock.commands.element.WarpElement;
import com.helion3.bedrock.util.Format;
import java.util.Collection;
import java.util.Optional;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;

public class WarpCommand {

    private WarpCommand() {
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .arguments(
                        WarpElement.of("warp")
                )
                .description(Text.of("Teleport to a named location."))
                .permission("bedrock.warp")
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

                    Optional<Tuple<String, Transform<World>>> warp = args.getOne("warp");
                    if (warp.isPresent()) {
                        player.sendMessage(Format.heading(String.format("Teleporting to %s.", warp.get().getFirst())));
                        player.setTransform(warp.get().getSecond());
                        return CommandResult.success();
                    }

                    Collection<Tuple<String, Transform<World>>> warps = args.getAll("warp");
                    if (warps.size() > 0) {
                        PaginationList list = Bedrock.getWarpManager().buildList(Format.heading("Matching Warps"), warps.stream().map(Tuple::getFirst));
                        list.sendTo(player);
                        return CommandResult.success();
                    }

                    player.sendMessage(Format.subdued("Could not find a matching warp."));

                    return CommandResult.success();
                }).build();
    }
}
