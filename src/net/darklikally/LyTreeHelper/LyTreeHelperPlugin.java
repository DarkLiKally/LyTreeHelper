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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 *
 * @author DarkLiKally
 */
public class LyTreeHelperPlugin extends JavaPlugin {

    private static final Logger logger = Logger.getLogger("Minecraft.LyTreeHelper");

    protected final LyTreeHelperCommands commandHandler = new LyTreeHelperCommands(this);

    private final LyTreeHelperBlockListener blockListener = new LyTreeHelperBlockListener(this);

    private Map<String, LyTreeHelperConfiguration> worldConfigurations;

    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

    String pluginDir = "plugins/LyTreeHelper/";

    public PermissionHandler Permissions;

    /**
     * Called on plugin enable.
     */    
    public void onEnable() {
        getDataFolder().mkdirs();

        setupPermissions();

        this.commandHandler.registerCommands();

        this.blockListener.registerEvents();

        this.worldConfigurations = new HashMap<String, LyTreeHelperConfiguration>();
        this.worldConfigurations.clear();

        for (World world : this.getServer().getWorlds()) {
            String worldName = world.getName();
            this.worldConfigurations.put(worldName, createWorldConfig(worldName));
        }

        logger.info("[LyTreeHelper] LyTreeHelper " + this.getDescription().getVersion() + " enabled.");
    }

    /**
     * Called on plugin disable.
     */
    public void onDisable() {
        logger.info("[LyTreeHelper] LyTreeHelper " + this.getDescription().getVersion() + " disabled.");
    }

    private LyTreeHelperConfiguration createWorldConfig(String world) {
        return new LyTreeHelperConfiguration(this, world, new File(this.getDataFolder(), world + ".yml"));
    }

    private void setupPermissions() {
        Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

        if (this.Permissions == null) {
            if (test != null) {
                this.Permissions = ((Permissions)test).getHandler();
                logger.log(Level.INFO, "[LyTreeHelper] Permission plugin detected, using Permissions plugin for permissions.");
                
            } else {
                logger.log(Level.INFO, "[LyTreeHelper] Permission plugin not detected.");
            }
        }
    }

    public boolean hasPermission(Player player, String permission) {
        try {
            if(this.Permissions != null) {
                if (!this.Permissions.has(player, "lytreehelper." + permission) && !player.isOp()) {
                    return false;
                } else return true;
            } else if (player.isOp()) {
                return true;
            } else {
                return true;
            }
        } catch (Throwable t) {
            //t.printStackTrace();
            return false;
        }
    }

    public boolean inGroup(String world, String player, String group) {
        try {
            return this.Permissions.inGroup(world, player, group);
        } catch (Throwable t) {
            //t.printStackTrace();
            return false;
        }
    }

    public String[] getGroups(String world, String player) {
        try {
            return this.Permissions.getGroups(world, player);
        } catch (Throwable t) {
            //t.printStackTrace();
            return new String[0];
        }
    }

    public LyTreeHelperConfiguration getWorldConfig(String world) {
        LyTreeHelperConfiguration ret = this.worldConfigurations.get(world);
        if (ret == null) {
            ret = createWorldConfig(world);
            worldConfigurations.put(world, ret);
        }

        return ret;
    }

    public double cutOff(float value) {
        double newValue = (int)(value * 100.0);
        newValue /= 100.0;
        return newValue;
    }

    public boolean isDebugging(Player player) {
        if (this.debugees.containsKey(player)) {
            return ((Boolean)this.debugees.get(player)).booleanValue();
        }
        return false;
    }

    public void setDebugging(Player player, boolean value){
        this.debugees.put(player, Boolean.valueOf(value));
    }
}