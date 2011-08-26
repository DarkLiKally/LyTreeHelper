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

package net.darklikally.LyTreeHelper.listeners;

import java.util.logging.Level;

import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.nijikokun.register.payment.Methods;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/**
 *
 * @author DarkLiKally
 */
public class LyTreeHelperServerListener extends ServerListener {
    /**
     * Plugin.
     */
    private LyTreeHelperPlugin plugin;
    
    /**
     * The Register economy Methods
     */
    private Methods Methods = null;

    /**
     * Construct the object;
     * 
     * @param plugin
     */
    public LyTreeHelperServerListener(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
        
        this.Methods = new Methods();
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();

        pm.registerEvent(Event.Type.PLUGIN_ENABLE, this, Priority.Monitor, plugin);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, this, Priority.Monitor, plugin);
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        // Check to see if the plugin thats being disabled is the one we are using
        if (this.Methods != null && this.Methods.hasMethod()) {
            Boolean check = this.Methods.checkDisabled(event.getPlugin());

            if(check) {
                this.plugin.setEconomy(null);
                System.out.println("[LyTreeHelper] Payment method was disabled. No longer accepting payments.");
            }
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        // Check to see if we need a payment method
        if (!this.Methods.hasMethod()) {
            if(this.Methods.setMethod(event.getPlugin())) {
                // You might want to make this a public variable inside your MAIN class public Method Method = null;
                // then reference it through this.plugin.Method so that way you can use it in the rest of your plugin ;)
                this.plugin.setEconomy(this.Methods.getMethod());
                System.out.println("[LyTreeHelper] Payment method found (" + this.plugin.getEconomy().getName() + " version: " + this.plugin.getEconomy().getVersion() + ")");
            }
        }
        
        if(this.plugin.getWorldGuard() != null) {
            Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("WorldGuard");
            
            // WorldGuard may not be loaded
            if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
                return; // Maybe you want throw an exception instead
            } else {
                this.plugin.setWorldGuard(((WorldGuardPlugin) plugin));
                this.plugin.getLogger().log(Level.INFO, "[LyTreeHelper] Hooked into WorldGuard.");
            }
        }
    }

}