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
package com.helion3.bedrock.listeners;

import com.helion3.bedrock.Bedrock;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class JoinListener {
    @Listener
    public void onPlayerJoin(final ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();

        // Create/load config
        Bedrock.getPlayerConfigManager().loadPlayer(player);

        // Is brand new?
        if (player.get(Keys.FIRST_DATE_PLAYED).get().equals(player.get(Keys.LAST_DATE_PLAYED).get())) {
            // Force spawn position because vanilla has a "fuzz" radius around spawn
            Location<World> spawn = player.getWorld().getLocation(player.getWorld().getProperties().getSpawnPosition());

            player.setLocation(spawn);

            String raw = String.format(Bedrock.getConfig().getNode("messages", "welcome").getString(), player.getName());
            Text message = TextSerializers.formattingCode('&').deserialize(raw);
            MessageChannel.TO_ALL.send(message);
        }
    }
}
