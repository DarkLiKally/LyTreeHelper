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
package net.darklikally.lytreehelper.bukkit.commands;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.darklikally.bukkit.util.TargetBlock;
import net.darklikally.lytreehelper.bukkit.LyTreeHelperPlugin;
import net.darklikally.lytreehelper.generators.ForestGenerators;
import net.darklikally.lytreehelper.generators.TreeGenerator;
import net.darklikally.lytreehelper.generators.TreeGenerator.TreeType;
import net.darklikally.sk89q.minecraft.util.commands.Command;
import net.darklikally.sk89q.minecraft.util.commands.CommandArgs;
import net.darklikally.sk89q.minecraft.util.commands.CommandPermission;

/**
 * 
 * @author DarkLiKally
 *
 */
public class ForestCommands {

    private final LyTreeHelperPlugin plugin;

    public ForestCommands(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void registerCommands() {
        plugin.getCommandManager().register(this.getClass());
    }
    
    /**
     * Generate a tree
     * 
     * @param args
     * @param plugin
     * @param player
     */
    @Command(
            aliases = { "lytree", "lyt" },
            usage = "<tree|bigtree|birch|redwood|tallredwood|smalljungle|jungle|junglebush|redmushroom|brownmushroom|swamp|randomredwood|randomnormal|random>",
            desc = "Generates a tree at the position you are looking at.",
            minArgs = 1,
            maxArgs = 2
    )
    @CommandPermission({ "lytreehelper.generate.trees" })
    public static void generateTree(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player) { //TODO: add edit session if neccessary @see LyTreeHelperCommands todo
        
        int[] ignoreBlockIds = { 8, 9, 20 };
        Location loc = new TargetBlock(player, 300, 0.2, ignoreBlockIds)
                .getTargetBlock().getLocation();
        loc.setY(loc.getBlockY() + 1);

        String typeName = args.getString(0);
        TreeType type = null;

        type = TreeType.lookup(typeName);
        
        if(type == null) {
            player.sendMessage(ChatColor.DARK_RED
                    + "The tree type " + typeName + " is unknown.");
            return;
        } else {
            TreeGenerator gen = new TreeGenerator(plugin, type);
            if (!gen.generate(player.getWorld(), loc.toVector())) {
                player.sendMessage("That tree can't go there.");
            } else {
                player.sendMessage(ChatColor.YELLOW + type.getName()
                        + " created.");
            }
        }
    }
    
    /**
     * Generate a forest
     * 
     * @param args
     * @param plugin
     * @param player
     */
    @Command(
            aliases = { "lyforest", "lyf" },
            usage = "<radius> <normal|redwood|mixed|jungle> (density)",
            desc = "Generates a forest in the specified radius around your position.",
            minArgs = 2,
            maxArgs = 3
    )
    @CommandPermission({ "lytreehelper.generate.forests" })
    public static void generateForestC(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player) { //TODO: add edit session if neccessary @see LyTreeHelperCommands todo

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
        
        ForestGenerators.generateForest(typeName, radius, density, loc, player.getWorld().getName(), plugin, player);
    }
    
    /**
     * Generate a tree nursery
     * 
     * @param args
     * @param plugin
     * @param player
     * @param editSession
     */
    @Command(
            aliases = { "lynursery", "lyn" },
            usage = "<radius> (density)",
            desc = "Generates a tree nursery in the specified radius around your position consisting of saplings.",
            minArgs = 2,
            maxArgs = 3
    )
    @CommandPermission({ "lytreehelper.generate.nursery" })
    public static void generateNursery(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player) { //TODO: add edit session if neccessary @see LyTreeHelperCommands todo
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
}
