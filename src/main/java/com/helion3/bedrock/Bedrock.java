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

import com.google.inject.Inject;
import com.helion3.bedrock.commands.*;
import com.helion3.bedrock.data.invincibility.ImmutableInvincibilityData;
import com.helion3.bedrock.data.invincibility.InvincibilityData;
import com.helion3.bedrock.data.invincibility.InvincibilityDataManipulatorBuilder;
import com.helion3.bedrock.listeners.*;
import com.helion3.bedrock.managers.AFKManager;
import com.helion3.bedrock.managers.JailManager;
import com.helion3.bedrock.managers.MessageManager;
import com.helion3.bedrock.managers.PlayerConfigManager;
import com.helion3.bedrock.managers.TeleportManager;
import com.helion3.bedrock.managers.WarpManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;

@Plugin(id = "bedrock", name = "bedrock", version = "1.0")
public class Bedrock {
    private static AFKManager afkManager;
    private static Configuration config;
    private static Game game;
    private static final JailManager jailManager = new JailManager();
    private static Logger logger;
    private static final MessageManager messageManager = new MessageManager();
    private static File parentDirectory;
    private static final PlayerConfigManager playerConfigManager = new PlayerConfigManager();
    private static Bedrock plugin;
    private static TeleportManager teleportManager;
    private static WarpManager warpManager;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        Sponge.getDataManager().register(InvincibilityData.class, ImmutableInvincibilityData.class, new InvincibilityDataManipulatorBuilder());
    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        plugin = this;
        parentDirectory = defaultConfig.getParentFile();

        // Load configuration files
        load();

        // Commands
        game.getCommandManager().register(this, AFKCommand.getCommand(), "afk");
        game.getCommandManager().register(this, BackCommand.getCommand(), "back");
        game.getCommandManager().register(this, BedrockCommands.getCommand(), "br", "bedrock");
        game.getCommandManager().register(this, BroadcastCommand.getCommand(), "broadcast");
        game.getCommandManager().register(this, ClearInventoryCommand.getCommand(), "clear", "ci", "clearinventory", "clearinv");
        game.getCommandManager().register(this, DeleteHomeCommand.getCommand(), "delhome");
        game.getCommandManager().register(this, DeleteWarpCommand.getCommand(), "delwarp");
        game.getCommandManager().register(this, FeedCommand.getCommand(), "feed");
        game.getCommandManager().register(this, FreezeCommand.getCommand(), "freeze");
        game.getCommandManager().register(this, FlyCommand.getCommand(), "fly");
        game.getCommandManager().register(this, GodCommand.getCommand(), "god");
        game.getCommandManager().register(this, HealCommand.getCommand(), "heal");
        game.getCommandManager().register(this, HomeCommand.getCommand(), "home");
        game.getCommandManager().register(this, HomesCommand.getCommand(), "homes");
        game.getCommandManager().register(this, InvCommand.getCommand(), "inv", "openinv");
        game.getCommandManager().register(this, KickCommand.getCommand(), "kick");
        game.getCommandManager().register(this, KickAllCommand.getCommand(), "kickall");
        game.getCommandManager().register(this, ListCommand.getCommand(), "list");
        game.getCommandManager().register(this, MessageCommand.getCommand(), "message", "m", "msg");
        game.getCommandManager().register(this, PerformanceCommand.getCommand(), "performance", "perf", "gc");
        game.getCommandManager().register(this, PingCommand.getCommand(), "ping");
        game.getCommandManager().register(this, ReplyCommand.getCommand(), "r", "reply");
        game.getCommandManager().register(this, SetHomeCommand.getCommand(), "sethome");
        game.getCommandManager().register(this, SetSpawnCommand.getCommand(), "setspawn");
        game.getCommandManager().register(this, SetWarpCommand.getCommand(), "setwarp");
        game.getCommandManager().register(this, SpawnCommand.getCommand(), "spawn");
        game.getCommandManager().register(this, SpyCommand.getCommand(), "spy");
        game.getCommandManager().register(this, StaffCommand.getCommand(), "staff");
        game.getCommandManager().register(this, TeleportCommand.getCommand(), "tp", "teleport");
        game.getCommandManager().register(this, TeleportAcceptCommand.getCommand(), "tpaccept");
        game.getCommandManager().register(this, TeleportDenyCommand.getCommand(), "tpdeny");
        game.getCommandManager().register(this, TeleportHereCommand.getCommand(), "tphere");
        game.getCommandManager().register(this, TeleportHereRequestCommand.getCommand(), "tpahere");
        game.getCommandManager().register(this, TeleportPositionCommand.getCommand(), "tppos");
        game.getCommandManager().register(this, TeleportRequestCommand.getCommand(), "tpa");
        game.getCommandManager().register(this, TeleportWorldCommand.getCommand(), "tpworld");
        game.getCommandManager().register(this, TimeCommand.getCommand(), "time");
        game.getCommandManager().register(this, UnfreezeCommand.getCommand(), "unfreeze", "thaw");
        game.getCommandManager().register(this, VanishCommand.getCommand(), "vanish", "v");
        game.getCommandManager().register(this, WarpCommand.getCommand(), "warp", "w");
        game.getCommandManager().register(this, WarpsCommand.getCommand(), "warps");
        game.getCommandManager().register(this, WorldCommand.getCommand(), "world");
        game.getCommandManager().register(this, WeatherCommand.getCommand(), "weather");
        game.getCommandManager().register(this, WhoisCommand.getCommand(), "whois", "who");

        // Event Listeners
        game.getEventManager().registerListeners(this, new ChangeBlockListener());
        game.getEventManager().registerListeners(this, new DamageEntityListener());
        game.getEventManager().registerListeners(this, new DeathListener());
        game.getEventManager().registerListeners(this, new DisconnectListener());
        game.getEventManager().registerListeners(this, new FishingListener());
        game.getEventManager().registerListeners(this, new JoinListener());
        game.getEventManager().registerListeners(this, new MessageChannelListener());
        game.getEventManager().registerListeners(this, new MoveListener());

        logger.info("Bedrock started.");
    }

    public static AFKManager getAFKManager() {
        return afkManager;
    }

    /**
     * Get the config
     *
     * @return Configuration
     */
    public static Configuration getConfig() {
        return config;
    }

    /**
     * Get the Game instance.
     *
     * @return Game
     */
    public static Game getGame() {
        return game;
    }

    /**
     * Injected Game instance.
     *
     * @param injectGame Game
     */
    @Inject
    public void setGame(Game injectGame) {
        game = injectGame;
    }

    /**
     * Get the jail manager.
     *
     * @return JailManager
     */
    public static JailManager getJailManager() {
        return jailManager;
    }

    /**
     * Get the logger.
     *
     * @return Logger
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Injects the Logger instance for this plugin.
     *
     * @param log Logger
     */
    @Inject
    private void setLogger(Logger log) {
        logger = log;
    }

    /**
     * Get the message manager.
     *
     * @return MessageManager
     */
    public static MessageManager getMessageManager() {
        return messageManager;
    }

    /**
     * Get parent directory.
     *
     * @return File
     */
    public static File getParentDirectory() {
        return parentDirectory;
    }

    /**
     * Get player configuration manager.
     *
     * @return PlayerConfigManager
     */
    public static PlayerConfigManager getPlayerConfigManager() {
        return playerConfigManager;
    }

    /**
     * Get the plugin instance.
     *
     * @return Bedrock
     */
    public static Bedrock getPlugin() {
        return plugin;
    }

    /**
     * Get the teleport manager.
     *
     * @return TeleportManager
     */
    public static TeleportManager getTeleportManager() {
        return teleportManager;
    }

    /**
     * Get warp manager.
     *
     * @return WarpManager
     */
    public static WarpManager getWarpManager() {
        return warpManager;
    }

    /**
     * Loads configuration-based classes.
     */
    public void load() {
        // Load configuration file
        config = new Configuration(defaultConfig, configManager);

        if (afkManager != null) {
            afkManager.close();
        }

        // Init here so Game is available
        afkManager = new AFKManager();
        teleportManager = new TeleportManager();
        warpManager = new WarpManager();
    }
}
