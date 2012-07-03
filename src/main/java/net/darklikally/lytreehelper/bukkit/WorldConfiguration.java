// $Id$
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
import java.util.ArrayList;
import java.util.List;
import com.sk89q.util.yaml.YAMLFormat;
import com.sk89q.util.yaml.YAMLProcessor;

/**
 * Holds the configuration for individual worlds.
 * 
 * @author DarkLiKally
 * @author sk89q
 */
public class WorldConfiguration {

    public static final String CONFIG_HEADER = "#\r\n"
            + "# This is one of LyTreeHelper's world configuration files\r\n"
            + "#\r\n"
            + "# This is a world configuration file. Anything placed into here will only\r\n"
            + "# affect this world. If you don't put anything in this file, then the\r\n"
            + "# settings will be inherited from the main configuration file.\r\n"
            + "#\r\n"
            + "# If you see {} below, that means that there are NO entries in this file.\r\n"
            + "# Remove the {} and add your own entries.\r\n" + "#\r\n";

    private LyTreeHelperPlugin plugin;

    private String worldName;
    private YAMLProcessor parentConfig;
    private YAMLProcessor config;

    /* Configuration data end */

    /**
     * Construct the object.
     * 
     * @param plugin
     *            The WorldGuardPlugin instance
     * @param worldName
     *            The world name that this WorldConfiguration is for.
     * @param parentConfig
     *            The parent configuration to read defaults from
     */
    public WorldConfiguration(LyTreeHelperPlugin plugin, String worldName,
            YAMLProcessor parentConfig) {
        File baseFolder = new File(plugin.getDataFolder(), "worlds/"
                + worldName);
        File configFile = new File(baseFolder, "config.yml");

        this.plugin = plugin;
        this.worldName = worldName;
        this.parentConfig = parentConfig;

        plugin.createDefaultConfiguration(configFile, "config_world.yml");

        config = new YAMLProcessor(configFile, true, YAMLFormat.EXTENDED);
        loadConfiguration();

        plugin.getLogger().info(
                "Loaded configuration for world '" + worldName + "'");
    }

    @SuppressWarnings("unused")
    private boolean getBoolean(String node, boolean def) {
        boolean val = parentConfig.getBoolean(node, def);

        if (config.getProperty(node) != null) {
            return config.getBoolean(node, def);
        } else {
            return val;
        }
    }

    @SuppressWarnings("unused")
    private String getString(String node, String def) {
        String val = parentConfig.getString(node, def);

        if (config.getProperty(node) != null) {
            return config.getString(node, def);
        } else {
            return val;
        }
    }

    @SuppressWarnings("unused")
    private int getInt(String node, int def) {
        int val = parentConfig.getInt(node, def);

        if (config.getProperty(node) != null) {
            return config.getInt(node, def);
        } else {
            return val;
        }
    }

    @SuppressWarnings("unused")
    private double getDouble(String node, double def) {
        double val = parentConfig.getDouble(node, def);

        if (config.getProperty(node) != null) {
            return config.getDouble(node, def);
        } else {
            return val;
        }
    }

    @SuppressWarnings("unused")
    private List<Integer> getIntList(String node, List<Integer> def) {
        List<Integer> res = parentConfig.getIntList(node, def);

        if (res == null || res.size() == 0) {
            parentConfig.setProperty(node, new ArrayList<Integer>());
        }

        if (config.getProperty(node) != null) {
            res = config.getIntList(node, def);
        }

        return res;
    }

    @SuppressWarnings("unused")
    private List<String> getStringList(String node, List<String> def) {
        List<String> res = parentConfig.getStringList(node, def);

        if (res == null || res.size() == 0) {
            parentConfig.setProperty(node, new ArrayList<String>());
        }

        if (config.getProperty(node) != null) {
            res = config.getStringList(node, def);
        }

        return res;
    }

    @SuppressWarnings("unused")
    private List<String> getKeys(String node) {
        List<String> res = parentConfig.getKeys(node);

        if (res == null || res.size() == 0) {
            res = config.getKeys(node);
        }
        if (res == null) {
            res = new ArrayList<String>();
        }

        return res;
    }

    @SuppressWarnings("unused")
    private Object getProperty(String node) {
        Object res = parentConfig.getProperty(node);

        if (config.getProperty(node) != null) {
            res = config.getProperty(node);
        }

        return res;
    }

    /**
     * Load the configuration.
     */
    private void loadConfiguration() {
        try {
            config.load();
        } catch (IOException e) {
            plugin.getLogger()
                    .severe("Error reading configuration for world "
                            + worldName + ": ");
            e.printStackTrace();
        }
    }
}