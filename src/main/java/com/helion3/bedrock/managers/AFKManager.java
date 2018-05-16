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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

public class AFKManager {

    private final Map<Player, Long> afkPlayers = new HashMap<>();
    private final Map<Player, Long> lastActivity = new HashMap<>();
    private Task afkKick;
    private Task inactivity;

    public AFKManager() {
        if (Bedrock.getConfig().getNode("afk", "timers", "enabled").getBoolean()) {
            scheduleActivityChecks();
            scheduleAFKKicks();
        }
    }

    /**
     * Cancel running tasks when closing manager.
     */
    public void close() {
        if (afkKick != null) {
            afkKick.cancel();
        }
        if (inactivity != null) {
            inactivity.cancel();
        }
    }

    /**
     * Silently removed a player's AFK status. Usually used on player disconnect.
     *
     * @param player Player
     */
    public void clear(Player player) {
        afkPlayers.remove(player);
        lastActivity.remove(player);
    }

    /**
     * Check if player is currently AFK.
     *
     * @param player Player
     * @return boolean Is AFK
     */
    public boolean isAFK(Player player) {
        return afkPlayers.containsKey(player);
    }

    /**
     * Update the timestamp of the last activity for a player.
     *
     * @param player
     */
    public void lastActivity(Player player) {
        // Chat is async and could theoretically arrive after the player has left
        if (!Bedrock.getGame().getServer().getOnlinePlayers().contains(player)) {
            return;
        }

        setAfk(player, false);
        lastActivity.put(player, now());
    }

    /**
     * Set a specific AFK value for a player.
     *
     * @param player Player
     * @param isAFK New AFK value
     */
    public void setAfk(Player player, boolean isAFK) {
        boolean isCurrentlyAFK = afkPlayers.containsKey(player);

        if (isCurrentlyAFK == isAFK) {
            return;
        }

        if (isCurrentlyAFK) {
            clear(player);

            // Broadcast
            MessageChannel.TO_ALL.send(Text.of(TextColors.YELLOW, player.getName() + " is no longer AFK."));
        } else {
            afkPlayers.put(player, now());

            // Broadcast
            MessageChannel.TO_ALL.send(Text.of(TextColors.YELLOW, player.getName() + " is now AFK."));
        }
    }

    /**
     * Toggle AFK status for a player.
     *
     * @param player Player
     */
    public void toggleAfk(Player player) {
        setAfk(player, !afkPlayers.containsKey(player));
    }

    /**
     * Grab the current epoch.
     *
     * @return Long epoch
     */
    private Long now() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * Run scheduled tasks which check for last activity.
     */
    private void scheduleActivityChecks() {
        inactivity = Bedrock.getGame().getScheduler().createTaskBuilder()
                .delay(10L, TimeUnit.SECONDS)
                .interval(10L, TimeUnit.SECONDS)
                .execute(() -> {
                    int activityThreshhold = Bedrock.getConfig().getNode("afk", "timers", "inactiveAfter").getInt();

                    // Iterate all inactive players
                    Iterator<Map.Entry<Player, Long>> iterator = lastActivity.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Player, Long> entry = iterator.next();

                        if (now() - entry.getValue() >= activityThreshhold) {
                            // Remove entry
                            iterator.remove();

                            // Set AFK
                            setAfk(entry.getKey(), true);
                        }
                    }
                }).submit(Bedrock.getPlugin());
    }

    /**
     * Run scheduled tasks which kick players who AFK too long.
     */
    private void scheduleAFKKicks() {
        afkKick = Bedrock.getGame().getScheduler().createTaskBuilder()
                .delay(10L, TimeUnit.SECONDS)
                .interval(10L, TimeUnit.SECONDS)
                .execute(() -> {
                    int kickThreshhold = Bedrock.getConfig().getNode("afk", "timers", "kickAfter").getInt();

                    // Iterate all AFK players
                    Iterator<Map.Entry<Player, Long>> iterator = afkPlayers.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Player, Long> entry = iterator.next();

                        // Never kick exempt players
                        if (entry.getKey().hasPermission("bedrock.afk.exempt")) {
                            continue;
                        }

                        if (now() - entry.getValue() >= kickThreshhold) {
                            // Remove entry
                            iterator.remove();

                            // Kick player
                            entry.getKey().kick(Text.of(TextColors.GOLD, "Kicked for being AFK too long."));
                        }
                    }
                }).submit(Bedrock.getPlugin());
    }
}
