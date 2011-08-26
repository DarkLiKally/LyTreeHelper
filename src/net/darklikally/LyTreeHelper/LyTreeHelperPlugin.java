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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.darklikally.LyTreeHelper.BOB2.BOB2Manager;
import net.darklikally.LyTreeHelper.BOB2.BOB2Populator;
import net.darklikally.LyTreeHelper.editor.EditSession;
import net.darklikally.LyTreeHelper.listeners.LyTreeHelperBlockListener;
import net.darklikally.LyTreeHelper.listeners.LyTreeHelperPlayerListener;
import net.darklikally.LyTreeHelper.listeners.LyTreeHelperServerListener;
import net.darklikally.LyTreeHelper.listeners.LyTreeHelperWorldListener;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijikokun.register.payment.Method;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/**
 *
 * @author DarkLiKally
 */
public class LyTreeHelperPlugin extends JavaPlugin {

    /**
     * The global LyTreeHelper logger
     */
    private static final Logger logger = Logger.getLogger("Minecraft.LyTreeHelper");

    /**
     * The command handler
     */
    protected final LyTreeHelperCommands commandHandler = new LyTreeHelperCommands(this);
    
    /**
     * The command manager
     */
    protected net.darklikally.minecraft.utils.commands.CommandManager<Player> commandManager;

    /**
     * BOB2 Tree Populator
     */
    private BOB2Populator treePopulator = null;
    
    /**
     * The listeners (events registered inside the listeners)
     */
    private final LyTreeHelperServerListener serverListener = new LyTreeHelperServerListener(this);
    private final LyTreeHelperWorldListener worldListener = new LyTreeHelperWorldListener(this);
    private final LyTreeHelperPlayerListener playerListener = new LyTreeHelperPlayerListener(this);
    private final LyTreeHelperBlockListener blockListener = new LyTreeHelperBlockListener(this);

    /**
     * The plugin's directory
     */
    private String pluginDir = "plugins/LyTreeHelper/";
    
    /**
     * The world configuration directory
     */
    private String configDir = "plugins/LyTreeHelper/worlds/";
    
    /**
     * The forest database directory
     */
    private String forestDbDir = this.pluginDir;

    /**
     * A map consisting of all configuration objects for the worlds.
     */
    private Map<String, LyTreeHelperConfiguration> worldConfigurations;

    /**
     * The forest database
     */
    private net.darklikally.LyTreeHelper.database.Database database;

    /**
     * This HashMap contains all the debugees (Name - Debug enabled?)
     */
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

    /**
     *  The handler for permissions plugin
     */
    public LyTreeHelperPermissions Permissions = null;

    /**
     * The Register Method
     */
    private Method economy = null;
    
    /**
     * The WorldGuardPlugin
     */
    private WorldGuardPlugin worldGuard = null;

    /**
     * The active editSessions (Playername - EditSession object)
     */
    private HashMap<String, EditSession> editSessions =
        new HashMap<String, EditSession>();

    /**
     * Returns the global logger for LyTreeHelper
     * @return
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Called on plugin enable.
     */
    public void onEnable() {
        getDataFolder().mkdirs();
        
        // Setup Permissions
        this.Permissions = new LyTreeHelperPermissions(this);

        // Init the commandManager
        this.commandManager = new net.darklikally.minecraft.utils.commands.CommandManager<Player>(this) {
            @Override
            public boolean hasPermission(Player player, String perm) {
                return this.plugin.getPermissions().hasPermission(player, perm, false, false);
            }
        };
        
        // Register the commands to the commandManager
        this.commandManager.register(net.darklikally.LyTreeHelper.commands.ForestCommands.class);
        
        // Set the command executor to the commandHandler
        this.commandHandler.registerCommands();

        // Setup the BOB 2 Tree Populator and the BOB2Manager
        this.treePopulator = new BOB2Populator();
        BOB2Manager.init(this, this.pluginDir + "bo2 trees/");

        // Register the neccessary events
        this.serverListener.registerEvents();
        this.worldListener.registerEvents();
        this.playerListener.registerEvents();
        this.blockListener.registerEvents();

        // Clear the world configurations on startup / reload
        this.worldConfigurations = new HashMap<String, LyTreeHelperConfiguration>();
        this.worldConfigurations.clear();

        // Load the world Configurations
        for (World world : this.getServer().getWorlds()) {
            String worldName = world.getName();
            this.worldConfigurations.put(worldName, createWorldConfig(worldName));
        }

        // Setup the forest database
        this.database = new net.darklikally.LyTreeHelper.database.Database(this,
                new File(this.getDataFolder(), "db.yml"));
        
        // Setup the Timer for the timed apple drops
        // 25 ticks = about 1 second
        this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new TimedDropTimer(this), 1500, 1500);
        
        // BETA: Add Custom Tree Populator to each world
        for(World world : this.getServer().getWorlds()) {
            world.getPopulators().add(this.treePopulator);
        }

        
        // Tell the server admins that our plugin is enabled
        logger.info("[LyTreeHelper] LyTreeHelper " + this.getDescription().getVersion() + " enabled.");
    }

    /**
     * Called on plugin disable.
     */
    public void onDisable() {

        
        // BETA: Remove Custom Tree Populator to each world
        for(World world : this.getServer().getWorlds()) {
            world.getPopulators().remove(this.treePopulator);
        }
        
        this.economy = null;
        // Tell the server admins that our plugin is disabled
        logger.info("[LyTreeHelper] LyTreeHelper " + this.getDescription().getVersion() + " disabled.");
    }

    /**
     * Returns the permissions handler
     * @return
     */
    public LyTreeHelperPermissions getPermissions() {
        return this.Permissions;
    }

    /**
     * Returns the Register Method
     * @return
     */
    public Method getEconomy() {
        return this.economy;
    }

    /**
     * We can set the Register economy Method.
     * @param value
     */
    public void setEconomy(Method value) {
        this.economy = value;
    }
    
    /**
     * Returns the WorldGuardPlugin.
     * @return
     */
    public WorldGuardPlugin getWorldGuard() {
        return this.worldGuard;
    }
    
    /**
     * We can set the WorldGuardPlugin.
     * @param value
     */
    public void setWorldGuard(WorldGuardPlugin value) {
        this.worldGuard = value;
    }
    
    /**
     * Returns the command manager
     * @return
     */
    public net.darklikally.minecraft.utils.commands.CommandManager<Player> getCommandManager() {
        return this.commandManager;
    }
    
    /**
     * Returns the BOB2 Tree Populator
     * @return
     */
    public BOB2Populator getTreePopulator() {
        return this.treePopulator;
    }

    /**
     * Returns the LyTreeHelper (LTH) forest database
     * @return
     */
    public net.darklikally.LyTreeHelper.database.Database getLTHDatabase() {
        return this.database;
    }

    /**
     * This method creates a new LyTreeHelperConfiguration for the requested world. 
     * @param world
     * @return
     */
    private LyTreeHelperConfiguration createWorldConfig(String world) {
        return new LyTreeHelperConfiguration(this, world, new File(this.getDataFolder(), world + ".yml"));
    }

    /**
     * Returns the LyTreeHelperConfiguration for the given world name.
     * @param world
     * @return
     */
    public LyTreeHelperConfiguration getWorldConfig(String world) {
        LyTreeHelperConfiguration ret = this.worldConfigurations.get(world);
        if (ret == null) {
            ret = createWorldConfig(world);
            worldConfigurations.put(world, ret);
        }

        return ret;
    }

    /**
     * Creates an EditSession for a Player.
     * Note: You must set whether the plugin is enabled for this Player.
     * @param player
     * @param pluginEnabled
     * @return
     */
    public EditSession createEditSession(Player player, boolean pluginEnabled) {
        if(!this.editSessions.containsKey(player.getName())) {
            this.editSessions.put(player.getName(),
                    new EditSession(player, pluginEnabled));
        }
        return this.editSessions.get(player.getName());
    }

    /**
     * Removes the EditSession for a Player.
     * @param player
     */
    public void removeEditSession(Player player) {
        this.removeEditSession(player.getName());
    }

    /**
     * Removes the EditSession for a player.
     * @param playerName
     */
    public void removeEditSession(String playerName) {
        this.editSessions.remove(playerName);
    }

    /**
     * Returns the EditSession for a Player.
     * @param player
     * @return
     */
    public EditSession getEditSession(Player player) {
        return this.getEditSession(player.getName());
    }

    /**
     * Returns the EditSession for a player.
     * @param playerName
     * @return
     */
    public EditSession getEditSession(String playerName) {
        if(!this.editSessions.containsKey(playerName)) {
            Player player = this.getServer().getPlayer(playerName);
            return this.createEditSession(player, true);
        } else {
            return this.editSessions.get(playerName);
        }
    }

    /**
     * Returns true if all LyTreeHelper features are enabled for the player with the name playerName.
     * @param playerName
     * @return
     */
    public boolean isPluginEnabledFor(String playerName) {
        if(this.editSessions.containsKey(playerName)) {
            return this.editSessions.get(playerName).isPluginEnabled();
        }
        return true;
    }

    /**
     * Cuts off a float value (Return mask: [[0]0]0.00)
     * @param value
     * @return
     */
    public double cutOff(float value) {
        double newValue = (int)(value * 100.0);
        newValue /= 100.0;
        return newValue;
    }

    /**
     * Returns true if debugging is enabled for Player.
     * @param player
     * @return
     */
    public boolean isDebugging(Player player) {
        if (this.debugees.containsKey(player)) {
            return ((Boolean)this.debugees.get(player)).booleanValue();
        }
        return false;
    }

    /**
     * Sets the debugging for Player.
     * @param player
     * @param value
     */
    public void setDebugging(Player player, boolean value){
        this.debugees.put(player, Boolean.valueOf(value));
    }
}