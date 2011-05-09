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

package net.darklikally.LyTreeHelper.command;

import java.util.HashSet;
import java.util.Random;

import net.darklikally.LyTreeHelper.LyTreeHelperCommands.*;
import net.darklikally.LyTreeHelper.LyTreeHelperCommands;
import net.darklikally.LyTreeHelper.LyTreeHelperConfiguration;
import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;
import net.darklikally.minecraft.utils.TreeGenerator.TreeType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkLiKally
 */
public class CommandLynursery extends LyTreeHelperCommand {
    @Override
    public boolean handle(CommandSender sender, String senderName,
            String command, String[] args, LyTreeHelperPlugin plugin,
            LyTreeHelperConfiguration worldConfig) throws CommandHandlingException {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command");
            return true;
        }
        Player player = (Player) sender;

        if(!plugin.hasPermission(player, "generate.nursery")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have sufficient permissions for this action!");
            return true;
        }

        LyTreeHelperCommands.checkArgs(args, 1, 2);
        
        Location loc = player.getLocation();

        double density = 0.04;

        if(args.length == 2) {
            try {
                density = Double.parseDouble(args[1]);
            } catch(Exception e) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid density! Only doubles, for example: 0.04");
                return false;
            }
        }

        int radius = 5;
        try{
            radius = Integer.parseInt(args[0]);
        } catch(Exception e) {
            player.sendMessage(ChatColor.DARK_RED + "Invalid radius! Only numbers.");
            return false;
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

        return true;
    }
}
