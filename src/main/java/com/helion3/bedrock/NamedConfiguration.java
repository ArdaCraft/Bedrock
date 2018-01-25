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
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class NamedConfiguration {
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private ConfigurationNode rootNode = null;;

    public NamedConfiguration(String name) {
        try {
            File conf = new File(Bedrock.getParentDirectory().getAbsolutePath() + "/" + name + ".conf");
            boolean fileCreated = false;

            if (!conf.exists()) {
                conf.createNewFile();
                fileCreated = true;
            }

            configLoader = HoconConfigurationLoader.builder().setFile(conf).build();
            if (fileCreated) {
                rootNode = configLoader.createEmptyNode(ConfigurationOptions.defaults());
            } else {
                rootNode = configLoader.load();
            }

            // Save
            save();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            configLoader.save(rootNode);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the root node.
     *
     * @return ConfigurationNode
     */
    public ConfigurationNode getRootNode() {
        return rootNode;
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
