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

import org.bukkit.entity.Player;


/**
 * 
 * @author DarkLiKally
 */
public class TimedDropTimer implements Runnable {
    private LyTreeHelperPlugin plugin;

    public TimedDropTimer(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Player[] players = plugin.getServer().getOnlinePlayers();

        for (Player player : players) {
            //TODO Check the area around the player for leave blocks and calculate, for about 3 blocks, a apple drop (with a user specified drop chance)
            //TODO If the apple drops are deactivated for the current world of the player -> continue
        }
    }

}
