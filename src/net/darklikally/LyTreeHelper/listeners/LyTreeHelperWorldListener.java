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

import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author DarkLiKally
 */
public class LyTreeHelperWorldListener extends WorldListener {
    /**
     * Plugin.
     */
    private LyTreeHelperPlugin plugin;

    /**
     * Construct the object;
     * 
     * @param plugin
     */
    public LyTreeHelperWorldListener(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();

        pm.registerEvent(Event.Type.WORLD_LOAD, this, Priority.Monitor, plugin);
        pm.registerEvent(Event.Type.WORLD_INIT, this, Priority.Monitor, plugin);
    }

    @Override
    public void onWorldLoad(WorldLoadEvent event) {
        this.addPopulator(event.getWorld());
    }
    
    @Override
    public void onWorldInit(WorldInitEvent event) {
        this.addPopulator(event.getWorld());
    }   
    
    private void addPopulator(World world) {
        // BETA: Add Custom Tree Populator to each world
        if(!world.getPopulators().contains(this.plugin.getTreePopulator())) {
            world.getPopulators().add(this.plugin.getTreePopulator());
        }
    }
}