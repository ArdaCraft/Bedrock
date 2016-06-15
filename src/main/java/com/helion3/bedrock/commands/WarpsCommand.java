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

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import com.helion3.bedrock.Bedrock;
import com.helion3.bedrock.util.Format;

public class WarpsCommand {
    private WarpsCommand() {}

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
        .description(Text.of("List warps."))
        .permission("bedrock.warp")
        .executor((source, args) -> {
            // Build warp list
            List<String> warps = Bedrock.getWarpManager().listWarps();
            if (warps.isEmpty()) {
                source.sendMessage(Format.subdued("There are no warps."));
                return CommandResult.success();
            }

            // Build pagination
            PaginationService service = Bedrock.getGame().getServiceManager().provide(PaginationService.class).get();
            PaginationList.Builder pagination = service.builder();

            // Changed to 'runCommand(/warp ..)' so that proper permission checks etc can be carried out
        	// if the user clicks a warp in the future when they may be in jail, or have had permissions removed.
            ArrayList<Text> contents = new ArrayList<>();
            for (String warpName : warps) {
                Text.Builder builder = Text.builder().append(Format.message(warpName));
                builder.onClick(TextActions.runCommand("/warp " + warpName));
                contents.add(builder.build());
            }

            pagination.contents(contents);
            pagination.sendTo(source);

            return CommandResult.success();
        }).build();
    }
}
