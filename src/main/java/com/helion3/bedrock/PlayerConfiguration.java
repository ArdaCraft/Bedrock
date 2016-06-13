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
package com.helion3.bedrock;

import com.helion3.bedrock.util.TransientData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerConfiguration {
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private ConfigurationNode rootNode = null;
    private final TransientData transientData = new TransientData();

    public PlayerConfiguration(UUID playerID) {
        try {
            // If files do not exist, we must create them
            File playerDir = new File(Bedrock.getParentDirectory().getAbsolutePath() + "/players");
            if (!playerDir.exists()) {
                playerDir.mkdirs();
                Bedrock.getLogger().info("Creating new player config directory at mods/bedrock/players");
            }

            File playerConf = new File(playerDir.getAbsolutePath() + "/" + playerID.toString() + ".conf");
            boolean fileCreated = false;

            if (!playerConf.exists()) {
                playerConf.createNewFile();
                Bedrock.getLogger().info(
                        "Creating new player config file at mods/bedrock/players/" + playerID.toString() + ".conf");
                fileCreated = true;
            }

            configLoader = HoconConfigurationLoader.builder().setFile(playerConf).build();
            if (fileCreated) {
                rootNode = configLoader.createEmptyNode(ConfigurationOptions.defaults());
            } else {
                rootNode = configLoader.load();
            }

            ConfigurationNode spyEnabled = rootNode.getNode("messaging", "spy");
            if (spyEnabled.isVirtual()) {
                spyEnabled.setValue(false);
            }

            // Save
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            configLoader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shortcut to rootNode.getNode().
     *
     * @param path Object[] Paths to desired node
     * @return ConfigurationNode
     */
    public ConfigurationNode getNode(Object... path) {
        return rootNode.getNode(path);
    }

    public TransientData getTransientData() {
    	return transientData;
    }
}
