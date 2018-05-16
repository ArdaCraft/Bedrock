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
import java.util.ArrayList;
import java.util.Optional;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class HomesCommand {

    private HomesCommand() {
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.playerOrSource(Text.of("player"))
                )
                .description(Text.of("List homes for yourself or another player."))
                .executor((source, args) -> {
                    Optional<Player> player = args.<Player>getOne("player");

                    if (!player.isPresent()) {
                        source.sendMessage(Format.error("You did not specify a player."));
                        return CommandResult.empty();
                    }

                    boolean forSelf = source.equals(player.get());

                    // Permissions
                    if (!forSelf && !source.hasPermission("bedrock.homes.others")) {
                        source.sendMessage(Format.error("You do not have permission to view other players' homes."));
                        return CommandResult.empty();
                    } else if (forSelf && !source.hasPermission("bedrock.home")) {
                        source.sendMessage(Format.error("Insufficient permissions."));
                        return CommandResult.empty();
                    }

                    source.sendMessage(Format.heading((forSelf ? "Your" : player.get().getName() + "'s") + " Homes:"));

                    // Load player's config
                    PlayerConfiguration config = Bedrock.getPlayerConfigManager().getPlayerConfig(player.get());

                    // Get homes list
                    ConfigurationNode homesNode = config.getNode("homes");
                    if (homesNode.getChildrenMap().isEmpty()) {
                        source.sendMessage(Format.subdued("You have no homes."));
                        return CommandResult.success();
                    }

                    // Build paginated content
                    Optional<PaginationService> service = Bedrock.getGame().getServiceManager().provide(PaginationService.class);
                    if (service.isPresent()) {
                        PaginationList.Builder pagination = service.get().builder();
                        ArrayList<Text> homes = new ArrayList<>();

                        for (Object obj : config.getNode("homes").getChildrenMap().keySet()) {
                            Text.Builder builder = Text.builder().append(Format.message(obj));

                            Optional<Location<World>> location = ConfigurationUtil.getNamedLocation(homesNode, (String) obj);
                            builder.onClick(TextActions.executeCallback(t -> {
                                if (t instanceof Player) {
                                    if (!location.isPresent()) {
                                        source.sendMessage(Format.error("Home location is no longer valid."));
                                        return;
                                    }

                                    ((Player) t).setLocation(location.get());
                                }
                            }));

                            homes.add(builder.build());
                        }

                        pagination.contents(homes);
                        pagination.sendTo(source);
                    }

                    return CommandResult.success();
                }).build();
    }
}
