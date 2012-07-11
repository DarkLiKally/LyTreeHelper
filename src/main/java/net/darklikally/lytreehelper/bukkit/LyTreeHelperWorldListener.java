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

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.PluginManager;

/**
 * @author DarkLiKally
 */
public class LyTreeHelperWorldListener implements Listener {

    private final LyTreeHelperPlugin plugin;

    public LyTreeHelperWorldListener(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        initializeWorld(event.getWorld());
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
    }

    /**
     * Initialize the settings for the specified world
     * 
     * @param world
     *            The specified world
     */
    public void initializeWorld(World world) {
        ConfigurationManager config = plugin.getGlobalConfigurationManager();
        WorldConfiguration wconfig = config.getWorldConfig(world);
        
        if(wconfig.enableCustomPopulator) {
            plugin.getPopulator().registerPopulator(world);
        }
    }
}