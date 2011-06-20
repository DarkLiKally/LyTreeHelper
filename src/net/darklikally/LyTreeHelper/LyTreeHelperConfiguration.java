// $Id$
/*
 * LyTreeHelper
 * Copyright (C) 2011 DarkLiKally <http://darklikally.net>
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

package net.darklikally.LyTreeHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author DarkLiKally
 */
public class LyTreeHelperConfiguration {

    private static final Logger logger = Logger.getLogger("Minecraft.LyTreeHelper");

    private LyTreeHelperPlugin plugin;
    
    private String world;
    private File configFile;

    /* start - Options */
    private boolean showCommandsInLog;
    private boolean decay;
    private boolean fasterDecay;
    private boolean destroyFaster;
    private boolean destroyAll;
    private boolean destroyAllWood;

    private boolean autoplantSapling;

    private boolean onlyTopDown;

    private int maxTreeSize;
    private int maxTreeRadius;

    private boolean iConomySupport;
    private double iConomyMoneyOnFullDest;

    private boolean appleDropOverTime;
    private double appleDropOverTimeChance;

    private Set<String> creaturesToSpawn;
    private double creatureSpawnChance;

    private Set<Integer> destructionTools;
    private Set<Integer> harvestTools;

    private double appleChance;
    private double goldenAppleChance;
    private double leavesChance;
    private double saplingChance;

    private Map<String, Double> customDrops = new HashMap<String, Double>();
    /* end - Options */

    public LyTreeHelperConfiguration(LyTreeHelperPlugin plugin, String world, File configFile) {
        this.plugin = plugin;
        this.world = world;
        this.configFile = configFile;

        createDefaultConfiguration(configFile, "config.yml");
        
        loadConfiguration();
    }

    /**
     * Create a default configuration file from the .jar.
     *
     * @param name
     */
    public static void createDefaultConfiguration(File actual, String defaultName) {

        if (!actual.exists()) {

            InputStream input =
                LyTreeHelperPlugin.class.getResourceAsStream("/defaults/" + defaultName);
            if (input != null) {
                FileOutputStream output = null;

                try {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;
                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }

                    logger.info("[LyTreeHelper] Configuration file written: " + defaultName);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException e) {
                    }

                    try {
                        if (output != null) {
                            output.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

   /**
     * Load the configuration.
     */
    private void loadConfiguration() {
        Configuration config = new Configuration(this.configFile);
        config.load();

        if(config.getBoolean("copy-other-configuration", false)) {
            String worldToCopy = config.getString("copy-from", null);
            File otherWorldsConfigFile = new File(this.plugin.getDataFolder(), worldToCopy + ".yml" );
            if(otherWorldsConfigFile.exists()) {
                this.configFile = otherWorldsConfigFile;
                config = new Configuration(this.configFile);
                config.load();
            }
        }

        this.showCommandsInLog = config.getBoolean("show-commands-in-log", true);
        this.decay = config.getBoolean("enable-leaves-decay", true);
        this.fasterDecay = config.getBoolean("enable-faster-decay", false);
        this.destroyFaster = config.getBoolean("enable-faster-leave-destruction", false);
        this.destroyAll = config.getBoolean("enable-full-tree-destruction", true);
        this.destroyAllWood = config.getBoolean("enable-full-wood-destruction", true);

        this.autoplantSapling = config.getBoolean("enable-autoplant-sapling", false);

        this.onlyTopDown = config.getBoolean("enable-only-top-down-drops", false);

        int maxTreeSizePreset = (config.getBoolean("enable-high-stack-size", false) ? 6000 : 1800 );
        this.maxTreeSize = Math.min(maxTreeSizePreset, config.getInt("max-tree-size", 1800));
        this.maxTreeRadius = Math.min(config.getInt("max-tree-radius", 5), 50);

        this.iConomySupport = config.getBoolean("enable-iconomy-support", false);
        this.iConomyMoneyOnFullDest = config.getDouble("iconomy-money-on-full-destruction", 0.0);

        this.appleDropOverTime = config.getBoolean("enable-apple-drops-over-time", false);
        this.appleDropOverTimeChance = config.getDouble("apple-drops-over-time-chance", 1.0);

        this.creaturesToSpawn = new HashSet<String>(config.getStringList("creatures-to-spawn-in-trees", null));
        this.creatureSpawnChance = config.getDouble("creature-spawn-chance", 50.0);

        this.destructionTools = new HashSet<Integer>(config.getIntList("full-destruction-tools", null));
        this.harvestTools = new HashSet<Integer>(config.getIntList("harvest-tools", null));

        this.appleChance = config.getDouble("apple-drop-chance", 1.0);
        this.goldenAppleChance = config.getDouble("golden-apple-drop-chance", 0.1);
        this.leavesChance = config.getDouble("leaves-block-drop-chance", 5.0);
        this.saplingChance = config.getDouble("sapling-drop-chance", 8.0);

        if(config.getKeys("custom-drops") != null && config.getKeys("custom-drops").size() != 0) {
            for (String item : config.getKeys("custom-drops")) {
                this.customDrops.put(item, config.getDouble("custom-drops." + item, 10.0));
            }
        }

        // Print an overview of settings
        if (config.getBoolean("show-config-on-start", true)) {
            logger.log(Level.INFO, "[LyTreeHelper] ===========================");
            logger.log(Level.INFO, "[LyTreeHelper] Configuration for " + world + ":");
            logger.log(Level.INFO, config.getBoolean("copy-other-configuration", false) ?
                    "[LyTreeHelper] Configuration copied from " + config.getString("copy-from", "")
                    : "[LyTreeHelper] Use " + this.configFile.getName() + " for configuration.");
            logger.log(Level.INFO, this.decay ?
                    "[LyTreeHelper] Leave decay is enabled."
                    : "[LyTreeHelper] Leave decay is disabled.");
            logger.log(Level.INFO, this.fasterDecay ?
                    "[LyTreeHelper] Faster leave decay is enabled."
                    : "[LyTreeHelper] Faster leave decay is disabled.");
            logger.log(Level.INFO, this.destroyFaster ?
                    "[LyTreeHelper] Faster leave destruction is enabled."
                    : "[LyTreeHelper] Faster leave destruction is disabled.");
            logger.log(Level.INFO, this.destroyAll ?
                    "[LyTreeHelper] Full tree destruction is enabled."
                    : "[LyTreeHelper] Full tree destruction is disabled.");
            logger.log(Level.INFO, this.destroyAllWood ?
                    "[LyTreeHelper] Full wood destruction is enabled."
                    : "[LyTreeHelper] Full wood destruction is disabled.");
            logger.log(Level.INFO, this.autoplantSapling ?
                    "[LyTreeHelper] Autoplant-Sapling enabled."
                    : "[LyTreeHelper] Autoplant-Sapling disabled.");
            logger.log(Level.INFO, "[LyTreeHelper] === === === === === === ===");
            logger.log(Level.INFO, "[LyTreeHelper] Max. tree size set to " + this.maxTreeSize);
            logger.log(Level.INFO, "[LyTreeHelper] Max. tree radius set to " + this.maxTreeRadius);
            logger.log(Level.INFO, "[LyTreeHelper] === === === === === === ===");
            logger.log(Level.INFO, this.appleDropOverTime ?
                    "[LyTreeHelper] Apple drops over time enabled with a chance of " + this.appleDropOverTimeChance + " %."
                    : "[LyTreeHelper] Apple drops over time disabled.");
            logger.log(Level.INFO, "[LyTreeHelper] === === === === === === ===");
            logger.log(Level.INFO, this.creaturesToSpawn.size() > 0 ?
                    "[LyTreeHelper] Number of creatures which can spawn in a tree: " + this.creaturesToSpawn.size()
                    : "[LyTreeHelper] No creatures can spawn in a tree.");
            logger.log(Level.INFO, "[LyTreeHelper] Creature spawn chance in a tree set to " + this.creatureSpawnChance);
            logger.log(Level.INFO, "[LyTreeHelper] === === === === === === ===");
            logger.log(Level.INFO, this.destructionTools.size() > 0 ?
                    "[LyTreeHelper] Full destruction is only enabled for " + this.destructionTools.size() + " tools."
                    : "[LyTreeHelper] Full destruction is enabled for alle tools.");
            logger.log(Level.INFO, this.harvestTools.size() > 0 ?
                    "[LyTreeHelper] Harvesting is only enabled for " + this.harvestTools.size() + " tools."
                    : "[LyTreeHelper] Harvesting is enabled for alle tools.");
            logger.log(Level.INFO, "[LyTreeHelper] === === === === === === ===");
            logger.log(Level.INFO, "[LyTreeHelper] Apple drop chance set to " + this.appleChance);
            logger.log(Level.INFO, "[LyTreeHelper] Golden apple drop chance set to " + this.goldenAppleChance);
            logger.log(Level.INFO, "[LyTreeHelper] Leave-Block drop chance set to " + this.leavesChance);
            logger.log(Level.INFO, "[LyTreeHelper] Sapling drop chance set to " + this.saplingChance);
            logger.log(Level.INFO, "[LyTreeHelper] === === === === === === ===");
            logger.log(Level.INFO, "[LyTreeHelper] Added " + this.customDrops.size() + " custom drops.");
            logger.log(Level.INFO, "[LyTreeHelper] ===========================");
        }
    }

    public String getWorld() {
        return this.world;
    }

    /**
     * @return show commands in log
     */
    public boolean isShowCommandsInLog() {
        return showCommandsInLog;
    }

    /**
     * @return the decay
     */
    public boolean isDecay() {
        return decay;
    }

    /**
     * @return the fasterDecay
     */
    public boolean isFasterDecay() {
        return fasterDecay;
    }

    /**
     * @return the destroyFaster
     */
    public boolean isDestroyFaster() {
        return destroyFaster;
    }

    /**
     * @return the destroyAll
     */
    public boolean isDestroyAll() {
        return destroyAll;
    }

    /**
     * @return the destroyAllWood
     */
    public boolean isDestroyAllWood() {
        return destroyAllWood;
    }

    /**
     * @return the autoplantSapling
     */
    public boolean isAutoplantSapling() {
        return autoplantSapling;
    }

    /**
     * @return the onlyTopDown
     */
    public boolean isOnlyTopDown() {
        return onlyTopDown;
    }

    /**
     * @return the maxTreeSize
     */
    public int getMaxTreeSize() {
        return maxTreeSize;
    }

    /**
     * @return the maxTreeRadius
     */
    public int getMaxTreeRadius() {
        return maxTreeRadius;
    }

    /**
     * @return the maxTreeRadius
     */
    public boolean isiConomySupport() {
        return iConomySupport;
    }

    /**
     * @return the maxTreeRadius
     */
    public double getiConomyMoneyOnFullDest() {
        return iConomyMoneyOnFullDest;
    }

    /**
     * @return the appleDropOverTime
     */
    public boolean isAppleDropOverTime() {
        return appleDropOverTime;
    }

    /**
     * @return the maxTreeRadius
     */
    public double getAppleDropOverTimeChance() {
        return appleDropOverTimeChance;
    }

    /**
     * @return the creaturesToSpawn
     */
    public Set<String> getCreaturesToSpawn() {
        return creaturesToSpawn;
    }

    /**
     * @return the creatureSpawnChance
     */
    public double getCreatureSpawnChance() {
        return creatureSpawnChance;
    }

    /**
     * @return the destructionTools
     */
    public Set<Integer> getDestructionTools() {
        return destructionTools;
    }

    /**
     * @return the harvestTools
     */
    public Set<Integer> getHarvestTools() {
        return harvestTools;
    }

    /**
     * @return the appleChance
     */
    public double getAppleChance() {
        return appleChance;
    }

    /**
     * @return the goldenAppleChance
     */
    public double getGoldenAppleChance() {
        return goldenAppleChance;
    }

    /**
     * @return the leavesChance
     */
    public double getLeavesChance() {
        return leavesChance;
    }

    /**
     * @return the saplingChance
     */
    public double getSaplingChance() {
        return saplingChance;
    }

    /**
     * @return the customDrops
     */
    public Map<String, Double> getCustomDrops() {
        return customDrops;
    }

}
