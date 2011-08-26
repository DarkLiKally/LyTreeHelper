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

package net.darklikally.LyTreeHelper.commands;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.BlockVector;

import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;
import net.darklikally.LyTreeHelper.editor.EditSession;
import net.darklikally.minecraft.utils.TargetBlock;
import net.darklikally.minecraft.utils.commands.Command;
import net.darklikally.minecraft.utils.commands.CommandArgs;
import net.darklikally.minecraft.utils.commands.CommandNesting;
import net.darklikally.minecraft.utils.commands.CommandPermission;
import net.darklikally.minecraft.utils.generator.TreeGenerator;
import net.darklikally.minecraft.utils.generator.WorldGenMammothTree;
import net.darklikally.minecraft.utils.generator.TreeGenerator.TreeType;

/**
 * 
 * @author DarkLiKally
 */
public class ForestCommands {

    @Command(
            aliases = { "lytree", "lyt" },
            usage = "<tree|bigtree|birch|redwood|tallredwood|randomredwood|random|custom> [<in case of custom: BOB2 object name>]",
            desc = "Generates a tree at the position you are looking at.",
            minArgs = 1,
            maxArgs = 2
    )
    @CommandPermission({ "lytreehelper.generate.trees" })
    public static void generateTree(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player, EditSession editSession) {
        int[] ignoreBlockIds = { 8, 9, 20 };
        Location loc = new TargetBlock(player, 300, 0.2, ignoreBlockIds)
                .getTargetBlock().getLocation();
        loc.setY(loc.getBlockY() + 1);

        String typeName = args.getString(0);
        TreeType type = null;

        if (typeName.equalsIgnoreCase("tree")) {
            type = TreeType.TREE;
        } else if (typeName.equalsIgnoreCase("bigtree")) {
            type = TreeType.BIG_TREE;
        } else if (typeName.equalsIgnoreCase("birch")) {
            type = TreeType.BIRCH;
        } else if (typeName.equalsIgnoreCase("redwood")) {
            type = TreeType.REDWOOD;
        } else if (typeName.equalsIgnoreCase("tallredwood")) {
            type = TreeType.TALL_REDWOOD;
        } else if (typeName.equalsIgnoreCase("randomredwood")) {
            type = TreeType.RANDOM_REDWOOD;
        } else if (typeName.equalsIgnoreCase("random")) {
            type = TreeType.RANDOM;
        } else if (typeName.equalsIgnoreCase("mammoth")) {
            int treeWidth = (int) (Math.round(Math.random() * 4) + 2);
            @SuppressWarnings("unused")
            WorldGenMammothTree gen = new WorldGenMammothTree(player, loc,
                    treeWidth);
            return;
        } else if (typeName.equalsIgnoreCase("custom")) {
            
        }

        if (type != null) {
            TreeGenerator gen = new TreeGenerator(plugin, type);
            if (!gen.generator(player.getWorld().getName(), loc.toVector())) {
                player.sendMessage("A tree can't go there.");
            } else {
                player.sendMessage(ChatColor.YELLOW + type.getName()
                        + " created.");
            }
        } else {
            player.sendMessage(ChatColor.DARK_RED
                    + "That tree type does not exist.");
        }
    }

    @Command(
            aliases = { "lyforest", "lyf" },
            usage = "<radius> <normal|redwood|mixed> (density)",
            desc = "Generates a forest in the specified radius around your position.",
            minArgs = 2,
            maxArgs = 3
    )
    @CommandPermission({ "lytreehelper.generate.forests" })
    public static void generateForestC(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player, EditSession editSession) {

        Location loc = player.getLocation();

        double density = 0.03;

        if (args.argsLength() == 3) {
            try {
                density = args.getDouble(2);
            } catch (Exception e) {
                player.sendMessage(ChatColor.DARK_RED
                        + "Invalid density! Only doubles, for example: 0.03");
                return;
            }
        }

        int radius = 5;
        try {
            radius = args.getInteger(0);
        } catch (Exception e) {
            player.sendMessage(ChatColor.DARK_RED
                    + "Invalid radius! Only numbers.");
            return;
        }

        String typeName = args.getString(1);
        
        ForestCommands.generateForest(typeName, radius, density, loc, player.getWorld().getName(), plugin, player);
    }

    @Command(
            aliases = { "lynursery", "lyn" },
            usage = "<radius> (density)",
            desc = "Generates a tree nursery in the specified radius around your position consisting of saplings.",
            minArgs = 2,
            maxArgs = 3
    )
    @CommandPermission({ "lytreehelper.generate.nursery" })
    public static void generateNursery(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player, EditSession editSession) {
        Location loc = player.getLocation();

        double density = 0.03;

        if(args.argsLength() == 2) {
            try {
                density = args.getDouble(1);
            } catch(Exception e) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid density! Only doubles, for example: 0.03");
                return;
            }
        }

        int radius = 5;
        try{
            radius = args.getInteger(0);
        } catch(Exception e) {
            player.sendMessage(ChatColor.DARK_RED + "Invalid radius! Only numbers.");
            return;
        }
        HashSet<TreeType> types = new HashSet<TreeType>();

        //if (typeName.equalsIgnoreCase("normal")) {
            types.add(TreeType.TREE);
            types.add(TreeType.BIRCH);
            types.add(TreeType.REDWOOD);
        //}

        if (types != null && types.size() != 0) {
            int affected = 0;

            for (int x = loc.getBlockX() - radius; x <= loc.getBlockX()+ radius; x++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ()+ radius; z++) {
                    if (player.getWorld().getBlockAt(x, loc.getBlockY(), z).getType() != Material.AIR)
                        continue;
    
                    if (Math.random() >= density) {
                        continue;
                    }
    
                    for (int y = loc.getBlockY(); y >= loc.getBlockY() - 10; y--) {
                        Material mat = player.getWorld().getBlockAt(x, y, z).getType();

                        if (mat == Material.DIRT || mat == Material.GRASS) {    
                            TreeType type = null;
                            Random generator = new Random();
                            int rand = generator.nextInt(types.size());

                            int typeNum = 0;
                            for (TreeType typeT : types) {
                                if(typeNum == Math.round(rand)) {
                                    type = typeT;
                                }
                                typeNum++;
                            }

                            Block saplingBlock = player.getWorld().getBlockAt(x, y + 1, z);
                            saplingBlock.setType(Material.SAPLING);

                            Byte saplingData = saplingBlock.getData();
                            if(type == TreeType.BIRCH) {
                                saplingData = (byte)(saplingData | (1 << 1));
                            } else if(type == TreeType.REDWOOD){
                                saplingData = (byte)(saplingData | (1 << 0));
                            }
                            saplingBlock.setData(saplingData);

                            affected++;
                            break;
                        } else if (mat != Material.AIR) {
                            break;
                        }
                    }
                }
            }
            player.sendMessage(ChatColor.YELLOW + "Tree nursery created with " + affected + " saplings.");
        } else {
            player.sendMessage(ChatColor.DARK_RED + "That nursery type does not exist.");
        }
    }

    @Command(
            aliases = {"lyforestdb", "lyfdb"},
            desc = "Gives access to the forest database."
    )
    @CommandNesting({ForestDBCommands.class})
    public static void forestDatabase(CommandArgs args, LyTreeHelperPlugin plugin, Player player, EditSession editSession) {
        
    }

    @Command(
            aliases = { "lyregenerate", "lyregen" },
            usage = "<forestname>",
            desc = "Regenerates a registered forest.",
            minArgs = 1,
            maxArgs = 1
    )
    @CommandPermission({ "lytreehelper.db.regenerateforest" })
    public static void regenerateForest(CommandArgs args, LyTreeHelperPlugin plugin, Player player, EditSession editSession) {
        ForestDBCommands.forestDBRegenerateForest(args, plugin, player, editSession);
    }

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
                        Material mat = plugin.getServer().getWorld(worldName).getBlockAt(x, y, z)
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

                            gen.generator(worldName,
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
    public static void generateRegionForest(String typeName, String regionName, double density, String worldName, LyTreeHelperPlugin plugin, Player player) {
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
            World world = plugin.getServer().getWorld(worldName);
            
            if(world == null) {
                player.sendMessage(ChatColor.DARK_RED + "The specified world " + worldName + " does not exist.");
                return;
            }
            
            com.sk89q.worldguard.protection.managers.RegionManager regionManager = plugin.getWorldGuard().getRegionManager(world);
            
            if(regionManager == null) {
                player.sendMessage(ChatColor.DARK_RED + "WorldGuard regions are disabled for the world called " + worldName);
                return;
            }
            
            com.sk89q.worldguard.protection.regions.ProtectedRegion region = regionManager.getRegion(regionName);
            
            if(region == null) {
                player.sendMessage(ChatColor.DARK_RED + "There is no WorldGuard region called " + regionName);
                return;
            }

            BlockVector minBlock = region.getMinimumPoint();
            BlockVector maxBlock = region.getMaximumPoint();
            
            int affected = 0;
            

            for (int x = minBlock.getBlockX(); x <= maxBlock.getBlockX(); x++) {
                for (int y = minBlock.getBlockY(); y <= maxBlock.getBlockY(); y--) {
                    for (int z = minBlock.getBlockZ(); z <= maxBlock.getBlockZ(); z++) {
                        if(region.contains(new com.sk89q.worldedit.Vector(x, y, z))) {
                            if (player.getWorld().getBlockAt(x, y, z)
                                    .getType() != Material.AIR)
                                continue;
        
                            if (Math.random() >= density) {
                                continue;
                            }
    
                            Material mat = plugin.getServer().getWorld(worldName).getBlockAt(x, y, z)
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
    
                                gen.generator(worldName,
                                        new Vector(x, y + 1, z));
    
                                affected++;
                                break;
                            } else if (mat != Material.AIR) {
                                break;
                            }
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
