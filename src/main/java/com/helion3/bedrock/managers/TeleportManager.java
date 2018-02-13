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

import com.flowpowered.math.vector.Vector3d;
import com.helion3.bedrock.Bedrock;
import com.helion3.bedrock.util.Format;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;

public class TeleportManager {

    private final Map<Player, Teleport> pendingRequests = new HashMap<>();

    public static class Teleport {

        private final Player source;
        private final Player target;
        private Player recipient;
        private Player sender;

        public Teleport(Player source, Player target) {
            this(source, target, source, target);
        }

        public Teleport(Player source, Player target, Player sender, Player recipient) {
            this.source = source;
            this.target = target;
            this.sender = sender;
            this.recipient = recipient;
        }

        public Player getRecipient() {
            return recipient;
        }

        public Player getSender() {
            return sender;
        }

        public Player getSource() {
            return source;
        }

        public Player getTarget() {
            return target;
        }
    }

    /**
     * Request a teleport to another player.
     *
     * @param teleport Teleport
     */
    public void request(Teleport teleport) {
        Player sender = teleport.getSender();
        Player recipient = teleport.getRecipient();

        // Store
        pendingRequests.put(recipient, teleport);

        recipient.sendMessage(Text.of(TextColors.YELLOW,
                String.format("%s is requesting to teleport %s\n", sender.getName(), (sender.equals(teleport.getSource()) ? "to you" : "you to them")),
                TextColors.WHITE, "Use /tpaccept or /tpdeny within 30 seconds"));

        // Handle request
        sender.sendMessage(Format.subdued("Sending request..."));
        Bedrock.getGame().getScheduler().createTaskBuilder().delayTicks(600L).execute(() -> {
            if (pendingRequests.remove(teleport.getTarget()) != null) {
                sender.sendMessage(Format.subdued("Your request did not receive a response."));
            }
        }).submit(Bedrock.getPlugin());
    }

    /**
     * Accept a pending teleport request.
     *
     * @param player Player request was made to.
     */
    public void accept(Player player) {
        if (!pendingRequests.containsKey(player)) {
            player.sendMessage(Format.error("You do not have any pending requests."));
        } else {
            Teleport teleport = pendingRequests.get(player);
            Player source = teleport.getSource();
            Player target = teleport.getTarget();

            target.sendMessage(Format.success(String.format("Teleporting %s....", teleport.getSource().getName())));
            source.sendMessage(Format.message(String.format("Teleporting you to %s", teleport.getTarget().getName())));

            teleport(source, target);
            pendingRequests.remove(player);
        }
    }

    /**
     * Deny a pending teleport request.
     *
     * @param player Player request was made to.
     */
    public void deny(Player player) {
        if (!pendingRequests.containsKey(player)) {
            player.sendMessage(Format.error("You do not have any pending requests."));
        } else {
            Teleport teleport = pendingRequests.get(player);
            Player sender = teleport.getSender();

            sender.sendMessage(Format.message("Sorry, your request was denied."));
            player.sendMessage(Format.success(String.format("Denied %s's tp request.", sender.getName())));

            pendingRequests.remove(player);
        }
    }

    /**
     * Teleport a player to another.
     *
     * @param source Player to teleport
     * @param target Player teleporting to
     */
    public boolean teleport(Player source, Player target) {
        return teleport(source, target.getTransform(), target.getName());
    }

    /**
     * Teleport a player to a specific location.
     *
     * @param source Player to teleport
     * @param position Vector3d teleporting to
     */
    public boolean teleport(Player source, Vector3d position) {
        Transform<World> transform = source.getTransform().setPosition(position);
        String name = String.format("%f %f %f", position.getX(), position.getY(), position.getZ());
        return teleport(source, transform, name);
    }

    /**
     * Teleport a player to another world.
     *
     * @param source Player to teleport
     * @param world World teleporting to
     */
    public boolean teleport(Player source, World world) {
        Transform<World> transform = source.getTransform().setLocation(world.getSpawnLocation());
        return teleport(source, transform, world.getName());
    }

    public boolean teleport(Player source, Transform<World> transform, String description) {
        // Teleport
        boolean result = source.setTransform(transform);

        if (result) {
            // Message
            source.sendMessage(Format.success(String.format("Teleporting you to %s", description)));
        }

        return result;
    }
}
