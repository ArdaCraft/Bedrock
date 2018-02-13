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
package com.helion3.bedrock.managers;

import com.helion3.bedrock.Bedrock;
import com.helion3.bedrock.PlayerConfiguration;
import com.helion3.bedrock.util.URLUtils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

public class MessageManager {

    private final Map<CommandSource, CommandSource> lastSenders = new WeakHashMap<>();

    /**
     * Clear any entries with a given CommandSource as the sender or recipient.
     *
     * @param source CommandSource
     */
    public void clear(CommandSource source) {
        lastSenders.remove(source);

        Iterator<Map.Entry<CommandSource, CommandSource>> iterator = lastSenders.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CommandSource, CommandSource> entry = iterator.next();
            if (entry.getValue().equals(source)) {
                iterator.remove();
            }
        }
    }

    /**
     * Send a message.
     *
     * @param sender CommandSource sender
     * @param recipient CommandSource recipient
     * @param rawMessage String message
     */
    public void message(CommandSource sender, CommandSource recipient, String rawMessage) {
        // Send to recipient
        Text message = URLUtils.replaceURLs(rawMessage);
        recipient.sendMessage(Text.of(TextStyles.ITALIC, TextColors.GRAY, sender.getName(), ": ", message));

        // Send to watchers
        MutableMessageChannel channel = MessageChannel.permission("bedrock.spy").asMutable();

        // Determine members who may/are not spying
        ArrayList<MessageReceiver> toRemove = new ArrayList<>();
        for (MessageReceiver receiver : channel.getMembers()) {
            if (receiver instanceof Player) {

                if (receiver.equals(sender) || receiver.equals(recipient) ||
                        !Bedrock.getMessageManager().playerIsSpying((Player) receiver)) {
                    toRemove.add(receiver);
                }
            }
        }

        // Remove invalid recipients
        toRemove.forEach(channel::removeMember);
        Text notification = Text.of(TextStyles.ITALIC, TextColors.GRAY, sender.getName(), " -> ", recipient.getName(), ": ", message);

        // Notify sender
        sender.sendMessage(notification);

        // Message any spies
        channel.send(notification);

        // Log to console
        Bedrock.getGame().getServer().getConsole().sendMessage(notification);

        // Store sender for easy reply
        setLastSender(sender, recipient);

        // Store recipient for easy /r to the same person
        setLastSender(recipient, sender);
    }

    /**
     * Get the last message sender, if any.
     *
     * @param recipient CommandSource recipient
     * @return Optional CommandSource
     */
    public Optional<CommandSource> getLastSender(CommandSource recipient) {
        return Optional.ofNullable(lastSenders.get(recipient));
    }

    /**
     * Sets the last known sender for messages.
     *
     * @param sender CommandSource sender
     * @param recipient CommandSource recipient
     */
    public void setLastSender(CommandSource sender, CommandSource recipient) {
        lastSenders.put(recipient, sender);
    }

    /**
     * Get whether this player has spy enabled.
     *
     * @param player
     * @return If spy enabled.
     */
    public boolean playerIsSpying(Player player) {
        PlayerConfiguration config = Bedrock.getPlayerConfigManager().getPlayerConfig(player.getUniqueId());
        return config.getNode("messaging", "spy").getBoolean();
    }
}
