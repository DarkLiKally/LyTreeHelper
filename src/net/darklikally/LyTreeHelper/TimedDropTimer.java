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

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


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
            LyTreeHelperConfiguration worldConfig = this.plugin.getWorldConfig(player.getWorld().getName());

            if(worldConfig.isAppleDropOverTime()) {
                Location playerLoc = player.getLocation();

                for(int x = playerLoc.getBlockX() - 4; x <= playerLoc.getBlockX() + 4; x++) {
                    for(int y = playerLoc.getBlockY() + 5; y >= playerLoc.getBlockY() - 1; y--) {
                        for(int z = playerLoc.getBlockZ() - 4; z <= playerLoc.getBlockZ() + 4; z++) {
                            Block block = player.getWorld().getBlockAt(x, y, z);

                            if(block.getType() == Material.LEAVES) {
                                if(block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                                    Random generator = new Random();
                                    int rand = generator.nextInt(10000);

                                    if (rand >= (10000.0 - (worldConfig.getAppleDropOverTimeChance() * 100.0))) {
                                        player.getWorld().dropItemNaturally(
                                                block.getRelative(BlockFace.DOWN).getLocation(),
                                                new ItemStack(Material.APPLE, 1));
                                        player.sendMessage(ChatColor.GREEN + "An apple dropped near your position!");
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            } else {
                continue;
            }
        }
    }

}
