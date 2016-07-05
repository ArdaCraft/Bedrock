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

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class Configuration {
    private ConfigurationNode rootNode = null;

    /**
     * Loads/creates configuration file.
     *
     * @param defaultConfig
     * @param configManager
     */
    public Configuration(File defaultConfig, ConfigurationLoader<CommentedConfigurationNode> configManager) {
        try {
            // If file does not exist, we must create it
            if (!defaultConfig.exists()) {
                defaultConfig.getParentFile().mkdirs();
                defaultConfig.createNewFile();
                rootNode = configManager.createEmptyNode(ConfigurationOptions.defaults());
            } else {
                rootNode = configManager.load();
            }

            ConfigurationNode welcomeNewUser = rootNode.getNode("messages", "welcome");
            if (welcomeNewUser.isVirtual()) {
                welcomeNewUser.setValue("&aWelcome &3%s &ato the server!");
            }

            ConfigurationNode warpAccuracy = rootNode.getNode("warps", "search_accuracy");
            if (warpAccuracy.isVirtual()) {
                warpAccuracy.setValue(0.5F);
            }

            ConfigurationNode afkEnabled = rootNode.getNode("afk", "timers", "enabled");
            if (afkEnabled.isVirtual()) {
                afkEnabled.setValue(true);
            }

            ConfigurationNode afkInterval = rootNode.getNode("afk", "timers", "inactiveAfter");
            if (afkInterval.isVirtual()) {
                afkInterval.setValue(120);
            }

            ConfigurationNode afkKick = rootNode.getNode("afk", "timers", "kickAfter");
            if (afkKick.isVirtual()) {
                afkKick.setValue(120);
            }

            ConfigurationNode returnTp = rootNode.getNode("return", "historySize");
            if (returnTp.isVirtual()) {
            	returnTp.setValue(20);
            }

            // Save
            try {
                configManager.save(rootNode);
            } catch(IOException e) {
                e.printStackTrace();
            }
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
}
