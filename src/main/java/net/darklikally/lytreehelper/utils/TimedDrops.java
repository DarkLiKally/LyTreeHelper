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
package net.darklikally.lytreehelper.utils;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.darklikally.lytreehelper.bukkit.ConfigurationManager;
import net.darklikally.lytreehelper.bukkit.LyTreeHelperPlugin;
import net.darklikally.lytreehelper.bukkit.WorldConfiguration;

/**
 * 
 * @author DarkLiKally
 * 
 */
public class TimedDrops implements Runnable {

    private final LyTreeHelperPlugin plugin;

    public TimedDrops(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        ConfigurationManager config = plugin.getGlobalConfigurationManager();
        WorldConfiguration wconfig;

        for (Player player : players) {
            wconfig = config.getWorldConfig(player.getWorld());

            if (wconfig.enableDropsOverTime) {
                Location playerLoc = player.getLocation();

                for (int x = playerLoc.getBlockX() - 4; x <= playerLoc
                        .getBlockX() + 4; x++) {
                    for (int y = playerLoc.getBlockY() + 5; y >= playerLoc
                            .getBlockY() - 1; y--) {
                        for (int z = playerLoc.getBlockZ() - 4; z <= playerLoc
                                .getBlockZ() + 4; z++) {
                            Block block = player.getWorld().getBlockAt(x, y, z);

                            if (block.getType() == Material.LEAVES) {
                                if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {

                                    TreeDropManager.dropTimedDrops(block.getRelative(BlockFace.DOWN), wconfig);
                                    
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

}
