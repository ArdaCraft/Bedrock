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
import com.helion3.bedrock.util.BoundedDeque;
import com.helion3.bedrock.util.TransientData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class MoveListener {
    @Listener
    public void onPlayerMove(MoveEntityEvent event) {
        if (event.getTargetEntity() instanceof Player) {
            Bedrock.getAFKManager().lastActivity((Player) event.getTargetEntity());

            if (Bedrock.getJailManager().isFrozen((Player) event.getTargetEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @Listener (order = Order.POST)
    public void onPlayerTeleport(MoveEntityEvent.Teleport event) {
        if (!(event.getTargetEntity() instanceof Player)) {
            return;
        }

    	// Ignore event if the teleport has been caused by the /return command itself
    	Player player = (Player) event.getTargetEntity();
        TransientData data = Bedrock.getPlayerConfigManager().getPlayerConfig(player).getTransientData();
        if (data.remove("teleport.return")) {
            return;
        }

    	int size = Bedrock.getConfig().getNode("return", "historySize").getInt();
    	BoundedDeque<Location<World>> history = data.get("teleport.history", () -> new BoundedDeque<>(size));
    	history.add(event.getFromTransform().getLocation());
    	history.add(event.getToTransform().getLocation());
    }
}
