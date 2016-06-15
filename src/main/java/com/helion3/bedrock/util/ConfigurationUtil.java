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
package com.helion3.bedrock.util;

import com.flowpowered.math.vector.Vector3d;
import com.helion3.bedrock.Bedrock;
import ninja.leaping.configurate.ConfigurationNode;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ConfigurationUtil {
    private ConfigurationUtil() {
    }

    /**
     * Get a child ConfigurationNode from it's parent by name. If an exact match
     * the given name is not found, search by ignoring case.
     *
     * @param config The parent node
     * @param name The path of the child node
     * @return the child ConfigurationNode. May be virtual if no matches could be found
     */
    public static ConfigurationNode findNamedNode(ConfigurationNode config, String name) {
        ConfigurationNode node = config.getNode(name);
        if (node.isVirtual()) {
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : config.getChildrenMap().entrySet()) {
                String key = entry.getKey().toString();
                if (key.equalsIgnoreCase(name)) {
                    return entry.getValue();
                }
            }
        }
        return node;
    }

    /**
     * Get a Location object for a named location.
     *
     * @param name String location name
     * @return Optional Location
     */
    public static Optional<Location<World>> getNamedLocation(ConfigurationNode config, String name) {
        ConfigurationNode node = findNamedNode(config, name);
        if (!node.isVirtual()) {
            // Build location
            double x = node.getNode("x").getDouble();
            double y = node.getNode("y").getDouble();
            double z = node.getNode("z").getDouble();
            UUID worldUuid = UUID.fromString(node.getNode("worldUuid").getString());

            Optional<World> world = Bedrock.getGame().getServer().getWorld(worldUuid);
            if (world.isPresent()) {
                return Optional.of(world.get().getLocation(x, y, z));
            }
        }

        return Optional.empty();
    }

    /**
     * Get a Transform object for a named Transform.
     *
     * @param name String Transform name
     * @return Optional Transform
     */
    public static Optional<Transform<World>> getNamedTransform(ConfigurationNode config, String name) {
        ConfigurationNode node = findNamedNode(config, name);
        if (!node.isVirtual()) {
            UUID worldUuid = UUID.fromString(node.getNode("worldUuid").getString());
            Optional<World> world = Bedrock.getGame().getServer().getWorld(worldUuid);

            if (world.isPresent()) {
                double x = node.getNode("x").getDouble();
                double y = node.getNode("y").getDouble();
                double z = node.getNode("z").getDouble();

                Vector3d position = new Vector3d(x, y, z);

                ConfigurationNode pitch = node.getNode("pitch");
                ConfigurationNode yaw = node.getNode("yaw");
                if (!pitch.isVirtual() && !yaw.isVirtual()) {
                    // x = pitch, y = yaw, z = roll
                    Vector3d rotatation = new Vector3d(pitch.getDouble(), yaw.getDouble(), 0D);
                    Transform<World> transform = new Transform<>(world.get(), position, rotatation);
                    return Optional.of(transform);
                }

                Transform<World> transform = new Transform<>(world.get(), position);
                return Optional.of(transform);
            }
        }
        return Optional.empty();
    }
}
