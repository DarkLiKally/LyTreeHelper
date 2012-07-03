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
package net.darklikally.lytreehelper.generators;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.darklikally.lytreehelper.bukkit.LyTreeHelperPlugin;
import net.darklikally.lytreehelper.generators.TreeGenerator.TreeType;

public class ForestGenerators {
    /**
     * Generates a new forest at the given location with the specified parameters.
     * @param typeName
     * @param radius
     * @param density
     * @param loc
     * @param worldName
     * @param plugin
     * @param player
     */
    public static void generateForest(String typeName, int radius, double density, Location loc, String worldName, LyTreeHelperPlugin plugin, Player player) {
        HashSet<TreeType> types = new HashSet<TreeType>();

        if (typeName.equalsIgnoreCase("normal")) {
            types.add(TreeType.TREE);
            types.add(TreeType.BIG_TREE);
            types.add(TreeType.BIRCH);
        } else if (typeName.equalsIgnoreCase("redwood")) {
            types.add(TreeType.REDWOOD);
            types.add(TreeType.TALL_REDWOOD);
        } else if (typeName.equalsIgnoreCase("mixed")) {
            types.add(TreeType.TREE);
            types.add(TreeType.BIG_TREE);
            types.add(TreeType.BIRCH);
            types.add(TreeType.REDWOOD);
            types.add(TreeType.TALL_REDWOOD);
        }

        if (types != null && types.size() != 0) {
            int affected = 0;

            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX()
                    + radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ()
                        + radius; z++) {
                    if (player.getWorld().getBlockAt(x, loc.getBlockY(), z)
                            .getType() != Material.AIR)
                        continue;

                    if (Math.random() >= density) {
                        continue;
                    }

                    for (int y = loc.getBlockY(); y >= loc.getBlockY() - 10; y--) {
                        World world = plugin.getServer().getWorld(worldName);
                        Material mat = world.getBlockAt(x, y, z)
                                .getType();

                        if (mat == Material.DIRT || mat == Material.GRASS) {
                            TreeType type = null;
                            Random generator = new Random();
                            int rand = generator.nextInt(types.size());

                            int typeNum = 0;
                            for (TreeType typeT : types) {
                                if (typeNum == Math.round(rand)) {
                                    type = typeT;
                                }
                                typeNum++;
                            }

                            TreeGenerator gen = new TreeGenerator(plugin, type);

                            gen.generate(world,
                                    new Vector(x, y + 1, z));

                            affected++;
                            break;
                        } else if (mat != Material.AIR) {
                            break;
                        }
                    }
                }
            }
            player.sendMessage(ChatColor.YELLOW + "Forest " + typeName
                    + " created with " + affected + " trees.");
        } else {
            player.sendMessage(ChatColor.DARK_RED
                    + "That forest type does not exist.");
        }
    }
}
