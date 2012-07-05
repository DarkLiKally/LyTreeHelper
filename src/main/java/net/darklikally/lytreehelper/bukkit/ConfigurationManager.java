//$Id$
/*
 * LyTreeHelper
 * Copyright (C) 2012 DarkLiKally <http://darklikally.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.darklikally.lytreehelper.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.darklikally.sk89q.util.yaml.YAMLFormat;
import net.darklikally.sk89q.util.yaml.YAMLProcessor;

import org.bukkit.World;

/**
 * Represents the global configuration and also delegates configuration for
 * individual worlds.
 * 
 * @author DarkLiKally
 * @author sk89q
 */
public class ConfigurationManager {

    private static final String CONFIG_HEADER = "#\r\n"
            + "# This is LyTreeHelper's main configuration file\r\n"
            + "#\r\n"
            + "# This is the global configuration file. This file contains the global\r\n"
            + "# settings and default settings for all your worlds. You can configure each\r\n"
            + "# setting for each world in its own configuration file to allow you to\r\n"
            + "# replace most settings in here for that world only.\r\n"
            + "#\r\n"
            + "# About editing this file:\r\n"
            + "# - DO NOT USE TABS. You MUST use spaces or Bukkit will complain. If\r\n"
            + "# you use an editor like Notepad++ (recommended for Windows users), you\r\n"
            + "# must configure it to \"replace tabs with spaces.\" In Notepad++, this can\r\n"
            + "# be changed in Settings > Preferences > Language Menu.\r\n"
            + "# - Don't get rid of the indents. They are indented so some entries are\r\n"
            + "# in categories (like \"enforce-single-session\" is in the \"protection\"\r\n"
            + "# category.\r\n"
            + "# - If you want to check the format of this file before putting it\r\n"
            + "# into WorldGuard, paste it into http://yaml-online-parser.appspot.com/\r\n"
            + "# and see if it gives \"ERROR:\".\r\n"
            + "# - Lines starting with # are comments and so they are ignored.\r\n"
            + "#\r\n";

    /**
     * Reference to the plugin.
     */
    private LyTreeHelperPlugin plugin;

    /**
     * The global configuration for use when loading worlds
     */
    private YAMLProcessor config;

    /**
     * Holds configurations for different worlds.
     */
    private Map<String, WorldConfiguration> worlds;

    /**
     * Settings
     */
    public boolean showConfigOnStart;
    public boolean showCommandsInLog;
    public boolean enableHighStackSize;
    public int maxTreeSize;
    public int maxTreeRadius;
    public String version;

    /**
     * Construct the object.
     * 
     * @param plugin
     *            The plugin instance
     */
    public ConfigurationManager(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
        this.worlds = new HashMap<String, WorldConfiguration>();
    }

    /**
     * Load the configuration.
     */
    public void load() {
        // Create the default configuration file
        plugin.createDefaultConfiguration(new File(plugin.getDataFolder(),
                "config.yml"), "config.yml");

        config = new YAMLProcessor(new File(plugin.getDataFolder(),
                "config.yml"), true, YAMLFormat.EXTENDED);
        try {
            config.load();
        } catch (IOException e) {
            plugin.getLogger().severe(
                    "Error reading configuration for global config: ");
            e.printStackTrace();
        }

        // Load the settings
        showConfigOnStart = config.getBoolean("system.show-config-on-start",
                false);
        showCommandsInLog = config.getBoolean("system.show-commands-in-log",
                false);
        enableHighStackSize = config.getBoolean("system.enable-high-stack-size", false);
        maxTreeSize = config.getInt("system.max-tree-size", 1600);
        maxTreeRadius = config.getInt("system.max-tree-radius", 5);
        version = config.getString("version", "unknown");
        
        // Adjust the maxTreeSize
        if(enableHighStackSize && maxTreeSize > 6000) {
            maxTreeSize = 6000;
        }
        if(!enableHighStackSize && maxTreeSize > 1800) {
            maxTreeSize = 1800;
        }
        
        // Print the system configuration if needed
        if(showConfigOnStart) {
            printConfigurationToConsole();
        }

        // Load configurations for each world
        for (World world : plugin.getServer().getWorlds()) {
            getWorldConfig(world);
        }

        config.setHeader(CONFIG_HEADER);

        if (!config.save()) {
            plugin.getLogger().severe("Error saving configuration!");
        }
    }

    /**
     * Unload the configuration.
     */
    public void unload() {
        worlds.clear();
    }

    /**
     * Get the configuration for a world.
     * 
     * @param world
     *            The world to get the configuration for
     * @return {@code world}'s configuration
     */
    public WorldConfiguration getWorldConfig(World world) {
        String worldName = world.getName();
        WorldConfiguration config = worlds.get(worldName);

        if (config == null) {
            config = new WorldConfiguration(plugin, worldName,
                    this.config.getNode("default-world-configuration"));
            worlds.put(worldName, config);
        }

        return config;
    }
    
    public void printConfigurationToConsole() {
        Logger logger = plugin.getLogger();
        String lytree = "[LyTreeHelper] ";
        
        logger.info(lytree + "Show commands in log: " + showCommandsInLog);
        logger.info(lytree + "Enable high stack size: " + enableHighStackSize);
        logger.info(lytree + "Max tree size: " + maxTreeSize);
        logger.info(lytree + "Max tree radius: " + maxTreeRadius);
        logger.info(lytree + "Configuration version: " + version);
    }
}