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
package com.helion3.bedrock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class NamedConfiguration {

    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private ConfigurationNode rootNode = SimpleCommentedConfigurationNode.root();

    private final String name;
    private final Object lock = new Object();

    public NamedConfiguration(String name) {
        this.name = name;
    }

    public NamedConfiguration load() {
        Bedrock.getAsyncExecutor().submit(this::loadInternal);
        return this;
    }

    public void save() {
        Bedrock.getAsyncExecutor().submit(this::saveInternal);
    }

    private void loadInternal() {
        synchronized (lock) {
            try {
                File conf = new File(Bedrock.getParentDirectory().getAbsolutePath(), name + ".conf");
                boolean fileCreated = false;

                if (!conf.exists() && conf.createNewFile()) {
                    fileCreated = true;
                }

                HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder()
                        .setSource(reader(conf))
                        .setSink(writer(conf))
                        .build();

                ConfigurationNode rootNode;
                if (fileCreated) {
                    rootNode = configLoader.createEmptyNode(ConfigurationOptions.defaults());
                } else {
                    rootNode = configLoader.load();
                }

                // Save
                configLoader.save(rootNode);

                this.rootNode = rootNode;
                this.configLoader = configLoader;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void saveInternal() {
        synchronized (lock) {
            if (configLoader == null) {
                return;
            }

            try {
                configLoader.save(rootNode);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the root node.
     *
     * @return ConfigurationNode
     */
    public ConfigurationNode getRootNode() {
        synchronized (lock) {
            return rootNode;
        }
    }

    /**
     * Shortcut to rootNode.getNode().
     *
     * @param path Object[] Paths to desired node
     * @return ConfigurationNode
     */
    public ConfigurationNode getNode(Object... path) {
        synchronized (lock) {
            return rootNode.getNode(path);
        }
    }

    private static Callable<BufferedReader> reader(File file) {
        return () -> new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }

    private static Callable<BufferedWriter> writer(File file) {
        return () -> new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
    }
}
