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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.darklikally.LyTreeHelper.LyTreeHelperCommands;
import net.darklikally.LyTreeHelper.LyTreeHelperCommands.CommandHandlingException;
import net.darklikally.LyTreeHelper.LyTreeHelperConfiguration;
import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkLiKally
 */
public class CommandLyforestdb extends LyTreeHelperCommand {
    @Override
    public boolean handle(CommandSender sender, String senderName,
            String command, String[] args, LyTreeHelperPlugin plugin,
            LyTreeHelperConfiguration worldConfig) throws CommandHandlingException {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command");
            return true;
        }
        Player player = (Player) sender;

        if(!plugin.hasPermission(player, "db.blockaccess")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have sufficient permissions for this action!");
            return true;
        }

        if(args.length == 0) {
            player.sendMessage("No parameters given.");
            return false;
        }

        if(args[0].equalsIgnoreCase("list")) {
            if(!plugin.hasPermission(player, "db.forestlist")) {
                sender.sendMessage(ChatColor.DARK_RED + "You don't have sufficient permissions for this action!");
                return true;
            }
    
            LyTreeHelperCommands.checkArgs(args, 1, 2);

            int page = 0;
            if(args.length == 2) {
                try{
                    page = Math.max(0, Integer.parseInt(args[1]) - 1);
                } catch(Exception e) {
                    page = 0;
                }
            }

            int listRows = 10;

            net.darklikally.LyTreeHelper.database.Database db = plugin.getLTHDatabase();

            List<String> forestDbList = db.getKeys("forests");
            int forestDbSize = forestDbList.size();
            int pages = (int)Math.ceil(forestDbSize / (float) listRows);

            String[] forestDbKeys = (String[]) forestDbList.toArray();
            Arrays.sort(forestDbKeys);

            player.sendMessage(ChatColor.YELLOW + "Forests (page " + (page + 1) + " of " + pages + "):");

            if(page < pages) {
                for(int i = page * listRows; i < page * listRows + listRows; i++) {
                    if(i >= listRows) {
                        break;
                    }
                    player.sendMessage(ChatColor.YELLOW.toString() + (i + 1) + ". " + forestDbKeys[i]);
                }
            }
            
            return true;
        } else if(args[0].equalsIgnoreCase("register")) {
            if(!plugin.hasPermission(player, "db.registerforest")) {
                sender.sendMessage(ChatColor.DARK_RED + "You don't have sufficient permissions for this action!");
                return true;
            }

            LyTreeHelperCommands.checkArgs(args, 4, 5);
            
            Location loc = player.getLocation();

            String name = args[1];

            double density = 0.04;

            if(args.length == 3) {
                try {
                    density = Double.parseDouble(args[4]);
                } catch(Exception e) {
                    player.sendMessage(ChatColor.DARK_RED + "Invalid density! Only doubles, for example: 0.04");
                    return false;
                }
            }

            int radius = 5;
            try{
                radius = Integer.parseInt(args[2]);
            } catch(Exception e) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid radius! Only numbers.");
                return false;
            }

            String typeName = args[3];
            if (typeName.equalsIgnoreCase("normal") || typeName.equalsIgnoreCase("redwood") || typeName.equalsIgnoreCase("mixed")) {
                net.darklikally.LyTreeHelper.database.Database db = plugin.getLTHDatabase();

                if(db.getFromPath("forests." + name) == null) {
                    db.setForPath("forests." + name + ".type", typeName);
                    db.setForPath("forests." + name + ".name", name);
                    db.setForPath("forests." + name + ".radius", radius);
                    db.setForPath("forests." + name + ".density", density);
                    db.setVector("forests." + name + ".location", loc.toVector());
                    db.setForPath("forests." + name + ".world", player.getWorld().getName());

                    try {
                        db.save();
                    } catch(IOException e) {
                        player.sendMessage("Failed saving database.");
                        return false;
                    }

                    player.sendMessage(ChatColor.YELLOW + "Forest " + name + " with radius " + radius + " registered at your position.");
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "Forest " + name + " already exists.");
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED + "That forest type does not exist.");
            }

            return true;
            
        } else if(args[0].equalsIgnoreCase("delete")) {
            if(!plugin.hasPermission(player, "db.deleteforest")) {
                sender.sendMessage(ChatColor.DARK_RED + "You don't have sufficient permissions for this action!");
                return true;
            }

            LyTreeHelperCommands.checkArgs(args, 2, 2);

            String name = args[1];

            if(name != null && name != "") {
                net.darklikally.LyTreeHelper.database.Database db = plugin.getLTHDatabase();

                if(db.getFromPath("forests." + name) != null) {
                    db.removePath("forests." + name);

                    try {
                        db.save();
                    } catch(IOException e) {
                        player.sendMessage("Failed saving database.");
                        return false;
                    }

                    player.sendMessage(ChatColor.YELLOW + "Forest " + name + " deleted.");
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "There is no forest " + name);
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED + "That forest type does not exist.");
            }

            return true;
        } else {
            player.sendMessage(ChatColor.DARK_RED + "Usage(s):");
            player.sendMessage(ChatColor.DARK_RED + "/lyforestdb register <forestname> <radius> <normal|redwood|mixed> (density)");
            player.sendMessage(ChatColor.DARK_RED + "/lyforestdb delete <forestname>");
            player.sendMessage(ChatColor.DARK_RED + "/lyforestdb list (page)");
        }

        return false;
    }
}
