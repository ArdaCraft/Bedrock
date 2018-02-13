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
package com.helion3.bedrock.listeners;

import com.helion3.bedrock.Bedrock;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class DisconnectListener {

    @Listener
    public void onPlayerQuit(final ClientConnectionEvent.Disconnect event) {
        // Skip if Player isn't online. Banned, non-whitelisted, etc players trigger Disconnect too
        if (!Bedrock.getGame().getServer().getOnlinePlayers().contains(event.getTargetEntity())) {
            event.setMessageCancelled(true);
            return;
        }

        // AFK
        Bedrock.getAFKManager().clear(event.getTargetEntity());

        // Config
        Bedrock.getPlayerConfigManager().unload(event.getTargetEntity());

        // Jail
        Bedrock.getJailManager().clear(event.getTargetEntity());

        // Messaging
        Bedrock.getMessageManager().clear(event.getTargetEntity());
    }
}
