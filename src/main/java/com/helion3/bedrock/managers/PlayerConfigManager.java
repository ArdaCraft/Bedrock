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
package com.helion3.bedrock.managers;

import com.helion3.bedrock.PlayerConfiguration;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerConfigManager {
    private final Map<UUID, PlayerConfiguration> playerConfigs = new HashMap<>();

    /**
     * Load and cache a player's configuration file.
     *
     * @param player
     */
    public void loadPlayer(Player player) {
        PlayerConfiguration config = getPlayerConfig(player.getUniqueId());
        playerConfigs.put(player.getUniqueId(), config);
    }

    /**
     * Load configuration data for a given player.
     *
     * @param player Player
     * @return PlayerConfiguration
     */
    public PlayerConfiguration getPlayerConfig(Player player) {
        return getPlayerConfig(player.getUniqueId());
    }

    /**
     * Load configuration data for a given player UUID.
     *
     * @param uuid UUID
     * @return PlayerConfiguration
     */
    public PlayerConfiguration getPlayerConfig(UUID uuid) {
        PlayerConfiguration configuration = playerConfigs.get(uuid);
        if (configuration == null) {
            playerConfigs.put(uuid, configuration = new PlayerConfiguration(uuid));
        }
        return configuration;
    }

    /**
     * Unload configuration files for a given player.
     *
     * @param player
     */
    public void unload(Player player) {
        playerConfigs.remove(player.getUniqueId()).save();
    }
}
