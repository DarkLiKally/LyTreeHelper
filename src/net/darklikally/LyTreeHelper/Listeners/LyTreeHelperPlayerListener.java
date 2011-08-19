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

package net.darklikally.LyTreeHelper.Listeners;

import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author DarkLiKally
 */
public class LyTreeHelperPlayerListener extends PlayerListener {
    /**
     * Plugin.
     */
    private LyTreeHelperPlugin plugin;

    /**
     * Construct the object;
     * 
     * @param plugin
     */
    public LyTreeHelperPlayerListener(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();

        pm.registerEvent(Event.Type.PLAYER_JOIN, this, Priority.Normal, plugin);
        pm.registerEvent(Event.Type.PLAYER_QUIT, this, Priority.Normal, plugin);
        pm.registerEvent(Event.Type.PLAYER_KICK, this, Priority.Normal, plugin);
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.plugin.createEditSession(event.getPlayer(), true);
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.removeEditSession(event.getPlayer().getName());
    }

    @Override
    public void onPlayerKick(PlayerKickEvent event) {
        this.plugin.removeEditSession(event.getPlayer().getName());
    }
}