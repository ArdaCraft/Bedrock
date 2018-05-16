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
import com.helion3.bedrock.util.Format;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class StaffCommand {

    private StaffCommand() {
    }

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("List staff currently online."))
                .executor((source, args) -> {
                    List<String> staffNames = new ArrayList<>();

                    for (Player player : Bedrock.getGame().getServer().getOnlinePlayers()) {
                        if (player.hasPermission("bedrock.staff")) {
                            staffNames.add(player.getName());
                        }
                    }

                    source.sendMessage(Format.heading("Staff currently online:"));
                    source.sendMessage(Format.message(String.join(", ", staffNames)));

                    return CommandResult.success();
                }).build();
    }
}
