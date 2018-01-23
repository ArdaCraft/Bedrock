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

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

public class JailManager {
    private List<Player> frozenPlayers = new ArrayList<>();

    /**
     * Handle disconnects a player.
     * @param player
     */
    public void clear(Player player) {
        thaw(player);
    }

    /**
     * Lock players in place.
     *
     * Movement/block changes will be cancelled, so the ice is for effect.
     *
     * @param player Player
     */
    public void freeze(Player player) {
        frozenPlayers.add(player);

        Location<World> loc = player.getLocation();
        setIce(loc.getRelative(Direction.NORTH));
        setIce(loc.getRelative(Direction.EAST));
        setIce(loc.getRelative(Direction.SOUTH));
        setIce(loc.getRelative(Direction.WEST));
        setIce(loc.add(0, 2, 0)); // above head
    }

    /**
     * Check if a player is currently frozen.
     *
     * @param player Player
     * @return boolean If frozen
     */
    public boolean isFrozen(Player player) {
        return frozenPlayers.contains(player);
    }

    /**
     * Unfreeze player and remove any packed ice around them.
     *
     * @param player Player
     */
    public void thaw(Player player) {
        if (frozenPlayers.remove(player)) {
            Location<World> loc = player.getLocation();
            removeIce(loc.getRelative(Direction.NORTH));
            removeIce(loc.getRelative(Direction.EAST));
            removeIce(loc.getRelative(Direction.SOUTH));
            removeIce(loc.getRelative(Direction.WEST));
            removeIce(loc.add(0, 2, 0)); // above head
        }
    }

    private void setIce(Location<World> loc) {
        if (loc.getBlock().getType().equals(BlockTypes.AIR)) {
            loc.setBlockType(BlockTypes.PACKED_ICE);
        }
    }

    private void removeIce(Location<World> loc) {
        if (loc.getBlock().getType().equals(BlockTypes.PACKED_ICE)) {
            loc.setBlockType(BlockTypes.AIR);
        }
    }
}
