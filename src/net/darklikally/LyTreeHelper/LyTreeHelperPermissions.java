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

import java.util.List;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 *
 * @author DarkLiKally
 */
public class LyTreeHelperPermissions {
    /**
     * Plugin.
     */
    private LyTreeHelperPlugin plugin;

    /**
     * Permissions Plugin
     */
    private PermissionHandler Permissions = null;

    /**
     * Default Constructor
     * @param plugin
     */
    public LyTreeHelperPermissions(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
        
        this.setupPermissions();
    }

    /**
     * Setups the Permissions handler
     */
    private void setupPermissions() {
        Plugin test = this.plugin.getServer().getPluginManager().getPlugin("Permissions");

        if (this.Permissions == null) {
            if (test != null) {
                this.Permissions = ((Permissions)test).getHandler();
                this.plugin.getLogger().log(Level.INFO, "[LyTreeHelper] Permission plugin detected, using Permissions plugin for permissions.");
            }
        }
    }

    /**
     * Returns true if the Player player has the permission String permission.
     * Note: You don't need to add the "lytreehelper." in front of the permission.
     * Note 2: isOPRequired is set to false!
     * @param player
     * @param permission
     * @return
     */
    public boolean hasPermission(Player player, String permission) {
        return this.hasPermission(player, permission, false, true);
    }

    /**
     * Returns true if the Player player has the permission String permission.
     * Note: You don't need to add the "lytreehelper." in front of the permission.
     * @param player
     * @param permission
     * @return
     */
    public boolean hasPermission(Player player, String permission, boolean isOPRequired) {
        return this.hasPermission(player, permission, isOPRequired, true);
    }

    /**
     * Returns true if the Player player has the permission String permission.
     * Note: If you don't want to check for a LyTreeHelper permission set isLTH to false.
     * @param player
     * @param permission
     * @param isLTH
     * @return
     */
    public boolean hasPermission(Player player, String permission, boolean isOPRequired, boolean isLTH) {
        // No one can access a null permission
        if(permission == null) {
            return false;
        }
        
        // But everyone can access an empty permission
        if(permission.equals("")) {
            return true;
        }

        // Build the permission node path
        String node = isLTH ? "LyTreeHelper." + permission : permission;

        // If Permissions is enabled, check against it.
        if(this.Permissions != null && this.Permissions.has(player, node)) {
            if(isOPRequired && !player.isOp()) {
                return false;
            }
            return true;
        } else if (player.hasPermission(node)) {
            if(isOPRequired && !player.isOp()) {
                return false;
            }
            return true;
        } else if (player.isOp()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Has the player the permission and is OP?
     * Note: isLTH is set to true!
     * @param player
     * @param permission
     * @return
     */
    public boolean hasOPPermission(Player player, String permission) {
        return this.hasPermission(player, permission, true, true);
    }
    
    /**
     * Has the player the permission and is OP?
     * @param player
     * @param permission
     * @param isLTH
     * @return
     */
    public boolean hasOPPermission(Player player, String permission, boolean isLTH) {
        return this.hasPermission(player, permission, true, isLTH);
    }

    /**
     * Returns true if the player is in the requested group in the specified world.
     * @param world
     * @param player
     * @param group
     * @return
     */
    public boolean inGroup(String world, String player, String group) {
        if(this.Permissions != null) {
            return this.Permissions.inGroup(world, player, group);
        }

        return false;
    }

    /**
     * Returns a String[] containing the group names of the player in the requested world.
     * @param world
     * @param player
     * @return
     */
    public String[] getGroups(String world, String player) {
        if(this.Permissions != null) {
            return this.Permissions.getGroups(world, player);
        }

        return new String[0];
    }

    public boolean hasAnyPermission(Player player, List<String> nodes, boolean isOpRequired) {
        for(String node : nodes) {
            if (this.hasPermission(player, node, isOpRequired)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllPermission(Player player, List<String> nodes, boolean isOpRequired) {
        for(String node : nodes) {
            if (!this.hasPermission(player, node, isOpRequired)) {
                return false;
            }
        }
        return true;
    }
}
