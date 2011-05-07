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

import java.util.logging.Level;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.iConomy.*;

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
     * Construct the object;
     * 
     * @param plugin
     */
    public LyTreeHelperServerListener(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();

        pm.registerEvent(Event.Type.PLUGIN_ENABLE, this, Priority.Monitor, plugin);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, this, Priority.Monitor, plugin);
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        if (plugin.getiConomy() != null) {
            if (event.getPlugin().getDescription().getName().equals("iConomy")) {
                plugin.setiConomy(null);
                plugin.getLogger().log(Level.INFO, "[LyTreeHelper] Un-hooked from iConomy.");
            }
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (plugin.getiConomy() == null) {
            Plugin iConomy = plugin.getServer().getPluginManager().getPlugin("iConomy");

            if (iConomy != null) {
                if (iConomy.isEnabled() && iConomy.getClass().getName().equals("com.iConomy.iConomy")) {
                    plugin.setiConomy( (iConomy)iConomy );
                    plugin.getLogger().log(Level.INFO, "[LyTreeHelper] Hooked into iConomy.");
                }
            }
        }
    }

}