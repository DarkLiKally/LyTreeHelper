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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

import net.darklikally.sk89q.util.yaml.YAMLFormat;
import net.darklikally.sk89q.util.yaml.YAMLNode;
import net.darklikally.sk89q.util.yaml.YAMLProcessor;

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
    private YAMLNode parentConfig;
    private YAMLProcessor config;

    /**
     * Configuration data
     */
    public boolean enableAutoPlantSapling;
    public boolean enableCustomPopulator;
    
    public boolean enableLeavesDecay;
    public boolean enableFasterLeavesDecay;
    
    public boolean enableFasterLeavesDestruction;
    public boolean enableFullTreeDestruction;
    public boolean onlyWoodDestruction;
    
    public boolean enableOnlyTopDownDrops;
    public boolean enableAppleDropsOverTime;
    public double appleDropOverTimeChance;
    public double appleDropChance;
    public double goldenAppleDropChance;
    public double leavesBlockDropChance;
    public double saplingDropChance;
    public Map<String, Double> customDrops;
    
    public boolean enableEconomySupport;
    public double costsOnFullDestruction;
    
    public Set<EntityType> creaturesToSpawnInTrees;
    public double creatureSpawnChance;
    
    public Set<Integer> fullDestructionTools;
    public Set<Integer> harvestTools;
    
    public Map<Biome, Map<String, Double>> schematics;
    

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
            YAMLNode parentConfig) {
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
        
        // Load the settings
        enableAutoPlantSapling = getBoolean("enable-autoplant-sapling", true);
        enableCustomPopulator = getBoolean("enable-custom-populator", false);
        
        enableLeavesDecay = getBoolean("leaves-decay.enable-leaves-decay", true);
        enableFasterLeavesDecay = getBoolean("leaves-decay.enable-faster-decay", true);
        
        enableFasterLeavesDestruction = getBoolean("tree-destruction.enable-faster-leaves-destruction", false);
        enableFullTreeDestruction = getBoolean("tree-destruction.enable-full-tree-destruction", true);
        onlyWoodDestruction = getBoolean("tree-destruction.only-wood-destruction", false);
        
        enableOnlyTopDownDrops = getBoolean("drops.enable-only-top-down-drops", false);
        enableAppleDropsOverTime = getBoolean("drops.enable-apple-drops-over-time", true);
        appleDropOverTimeChance = getDouble("drops.apple-drop-over-time-chance", 1.0);
        appleDropChance = getDouble("drops.apple-drop-chance", 1.0);
        goldenAppleDropChance = getDouble("drops.golden-apple-drop-chance", 0.1);
        leavesBlockDropChance = getDouble("drops.leaves-block-drop-chance", 5.0);
        saplingDropChance = getDouble("drops.sapling-drop-chance", 8.0);
        
        if(getKeys("drops.custom-drops") != null && getKeys("drops.custom-drops").size() != 0) {
            for (String item : getKeys("drops.custom-drops")) {
                customDrops.put(item, getDouble("drops.custom-drops." + item, 10.0));
            }
        }
        
        enableEconomySupport = getBoolean("economy.enable-economy-support", false);
        costsOnFullDestruction = getDouble("economy.costs-on-full-destruction", 10.0);
        
        creaturesToSpawnInTrees = new HashSet<EntityType>();
        for (String creatureName : getStringList("creatures.creatures-to-spawn-in-trees", null)) {
            EntityType creature = EntityType.fromName(creatureName);

            if (creature == null) {
                plugin.getLogger().warning("Unknown mob type '" + creatureName + "'");
            } else if (!creature.isAlive()) {
                plugin.getLogger().warning("Entity type '" + creatureName + "' is not a creature");
            } else {
                creaturesToSpawnInTrees.add(creature);
            }
        }
        
        creatureSpawnChance = getDouble("creatures.creature-spawn-chance", 30.0);
        
        fullDestructionTools = new HashSet<Integer>(getIntList("tools.full-destruction-tools", null));
        harvestTools = new HashSet<Integer>(getIntList("tools.harvest-tools", null));
        
        schematics = new HashMap<Biome, Map<String, Double>>();
        if(getKeys("schematics") != null && getKeys("schematics").size() != 0) {
            for(String schematicName : getKeys("schematics")) {
                for(String biomeName : getStringList("schematics." + schematicName + ".biomes", null)) {
                    Biome biome = Biome.valueOf(biomeName.toUpperCase());
                    if(schematics.get(biome) == null) {
                        schematics.put(biome, new HashMap<String, Double>());
                    }
                    schematics.get(biome).put(schematicName, getDouble("schematics." + schematicName + ".chance", 10.0));
                }
            }
        }
        
        // Print the system configuration if needed
        if(plugin.getGlobalConfigurationManager().showConfigOnStart) {
            printConfigurationToConsole();
        }
    }
    
    public void printConfigurationToConsole() {
        Logger logger = plugin.getLogger();
        String lytree = "[LyTreeHelper] World " + worldName + ": ";

        logger.info(lytree + "Enable auto plant sapling: " + enableAutoPlantSapling);
        logger.info(lytree + "Enable custom populator: " + enableCustomPopulator);
        logger.info(lytree + "Enable leaves decay: " + enableLeavesDecay);
        logger.info(lytree + "Enable faster leaves decay: " + enableFasterLeavesDecay);
        logger.info(lytree + "Enable faster leaves destruction: " + enableFasterLeavesDestruction);
        logger.info(lytree + "Enable full tree destruction: " + enableFullTreeDestruction);
        logger.info(lytree + "Only wood destruction: " + onlyWoodDestruction);
        logger.info(lytree + "Enable only top-down drops: " + enableOnlyTopDownDrops);
        logger.info(lytree + "Enable apple drops over time: " + enableAppleDropsOverTime);
        logger.info(lytree + "Apple drop over time chance: " + appleDropOverTimeChance);
        logger.info(lytree + "Apple drop chance: " + appleDropChance);
        logger.info(lytree + "Golden apple drop chance: " + goldenAppleDropChance);
        logger.info(lytree + "Leaves block drop chacne: " + leavesBlockDropChance);
        logger.info(lytree + "Sapling drop chance: " + saplingDropChance);
        logger.info(lytree + "Custom drop number: " + customDrops.size());
        logger.info(lytree + "Enable economy support: " + enableEconomySupport);
        logger.info(lytree + "Costs on full destruction: " + costsOnFullDestruction);
        logger.info(lytree + "Creatures to spawn in trees count: " + creaturesToSpawnInTrees.size());
        logger.info(lytree + "Creature spawn in tree chance: " + creatureSpawnChance);
        logger.info(lytree + "Number of full destruction tools: " + fullDestructionTools.size());
        logger.info(lytree + "Number of harvest tools: " + harvestTools.size());
    }
}