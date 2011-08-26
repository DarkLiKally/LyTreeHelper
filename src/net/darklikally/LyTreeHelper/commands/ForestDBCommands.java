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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;
import net.darklikally.LyTreeHelper.editor.EditSession;
import net.darklikally.minecraft.utils.commands.Command;
import net.darklikally.minecraft.utils.commands.CommandArgs;
import net.darklikally.minecraft.utils.commands.CommandPermission;

/**
 * 
 * @author DarkLiKally
 */
public class ForestDBCommands {

    @Command(
            aliases = { "version", "ver" },
            usage = "",
            desc = "Returns the forest database version.",
            minArgs = 0,
            maxArgs = 0
    )
    public static void forestDBVersion(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player, EditSession editSession) {
        player.sendMessage(ChatColor.AQUA + "LyTreeHelper Forest Database Version 1.0");
    }

    @Command(
            aliases = { "forestlist", "list", "fl" },
            usage = "(page)",
            desc = "Returns the forest database version.",
            minArgs = 0,
            maxArgs = 1
    )
    @CommandPermission({ "lytreehelper.db.forestlist" })
    public static void forestDBList(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player, EditSession editSession) {
        int page = 0;
        if(args.argsLength() == 1) {
            try{
                page = Math.max(0, args.getInteger(0) - 1);
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

        if(pages == 0) {
            player.sendMessage(ChatColor.YELLOW + "There are no registered forests.");
        } else if(page < pages) {
            for(int i = page * listRows; i < page * listRows + listRows; i++) {
                if(i >= listRows) {
                    break;
                }
                player.sendMessage(ChatColor.YELLOW.toString() + (i + 1) + ". " + forestDbKeys[i]);
            }
        }
    }

    @Command(
            aliases = { "addforest", "add", "register", "reg" },
            usage = "<forestname> <forestradius> <foresttype> (density)",
            desc = "Allows you to register a new forest in the database.",
            minArgs = 3,
            maxArgs = 4
    )
    @CommandPermission({ "lytreehelper.db.registerforest" })
    public static void forestDBAddForest(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player, EditSession editSession) {
        Location loc = player.getLocation();

        String name = args.getString(0);

        double density = 0.03;

        if(args.argsLength() == 3) {
            try {
                density = args.getDouble(3);
            } catch(Exception e) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid density! Only doubles, for example: 0.03");
                return;
            }
        }

        int radius = 5;
        try{
            radius = args.getInteger(1);
        } catch(Exception e) {
            player.sendMessage(ChatColor.DARK_RED + "Invalid radius! Only numbers.");
            return;
        }

        String typeName = args.getString(2);
        if (typeName.equalsIgnoreCase("normal") || typeName.equalsIgnoreCase("redwood") || typeName.equalsIgnoreCase("mixed")) {
            net.darklikally.LyTreeHelper.database.Database db = plugin.getLTHDatabase();

            if(db.getFromPath("forests." + name) == null) {
                db.setForPath("forests." + name + "isRegion", "no");
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
                    return;
                }

                player.sendMessage(ChatColor.YELLOW + "Forest " + name + " with radius " + radius + " registered at your position.");
            } else {
                player.sendMessage(ChatColor.DARK_RED + "Forest " + name + " already exists.");
            }
        } else {
            player.sendMessage(ChatColor.DARK_RED + "That forest type does not exist.");
        }
    }

    @Command(
            aliases = { "addwgregionforest", "addwgregion", "addregion", "registerwg", "registerregion", "regwg", "regregion" },
            usage = "<forestname> <regionname> <worldname> <foresttype> (density)",
            desc = "Allows you to register a WorldGuard region as a forest in the database.",
            minArgs = 4,
            maxArgs = 5
    )
    @CommandPermission({ "lytreehelper.db.registerforest" })
    public static void forestDBAddRegionForest(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player, EditSession editSession) {
        Location loc = player.getLocation();

        if(plugin.getWorldGuard() == null) {
            player.sendMessage(ChatColor.DARK_RED + "WorldGuard is not installed on this server or not enabled.");
            return;
        }

        String name = args.getString(0);

        double density = 0.03;

        if(args.argsLength() == 4) {
            try {
                density = args.getDouble(4);
            } catch(Exception e) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid density! Only doubles, for example: 0.03");
                return;
            }
        }

        String regionName = args.getString(1);
        String worldName = args.getString(2);
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
        
        String typeName = args.getString(3);
        if (typeName.equalsIgnoreCase("normal") || typeName.equalsIgnoreCase("redwood") || typeName.equalsIgnoreCase("mixed")) {
            net.darklikally.LyTreeHelper.database.Database db = plugin.getLTHDatabase();

            if(db.getFromPath("forests." + name) == null) {
                db.setForPath("forests." + name + "isRegion", "yes");
                db.setForPath("forests." + name + ".type", typeName);
                db.setForPath("forests." + name + ".name", name);
                db.setForPath("forests." + name + ".regionName", regionName);
                db.setForPath("forests." + name + ".density", density);
                db.setVector("forests." + name + ".location", loc.toVector());
                db.setForPath("forests." + name + ".world", worldName);

                try {
                    db.save();
                } catch(IOException e) {
                    player.sendMessage("Failed saving database.");
                    return;
                }

                player.sendMessage(ChatColor.YELLOW + "Forest " + name + " for WorldGuard region " + regionName + " registered.");
            } else {
                player.sendMessage(ChatColor.DARK_RED + "Forest " + name + " already exists.");
            }
        } else {
            player.sendMessage(ChatColor.DARK_RED + "That forest type does not exist.");
        }
    }

    @Command(
            aliases = { "removeforest", "remove", "delete", "del" },
            usage = "<forestname>",
            desc = "Allows you to remove a registered forest from the database.",
            minArgs = 0,
            maxArgs = 0
    )
    @CommandPermission({ "lytreehelper.db.deleteforest" })
    public static void forestDBRemoveForest(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player, EditSession editSession) {
        String name = args.getString(0);

        if(name != null && name != "") {
            net.darklikally.LyTreeHelper.database.Database db = plugin.getLTHDatabase();

            if(db.getFromPath("forests." + name) != null) {
                db.removePath("forests." + name);

                try {
                    db.save();
                } catch(IOException e) {
                    player.sendMessage("Failed saving database.");
                    return;
                }

                player.sendMessage(ChatColor.YELLOW + "Forest " + name + " deleted.");
            } else {
                player.sendMessage(ChatColor.DARK_RED + "There is no forest called " + name);
            }
        } else {
            player.sendMessage(ChatColor.DARK_RED + "That forest does not exist.");
        }
    }

    @Command(
            aliases = { "regenerateforest", "regenerate", "regen", "reforest" },
            usage = "<forestname>",
            desc = "Regenerates a registered forest.",
            minArgs = 1,
            maxArgs = 1
    )
    @CommandPermission({ "lytreehelper.db.regenerateforest" })
    public static void forestDBRegenerateForest(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player, EditSession editSession) {
        String name = args.getString(0);

        if(name != null && name != "") {
            net.darklikally.LyTreeHelper.database.Database db = plugin.getLTHDatabase();

            if(db.getFromPath("forests." + name) != null) {
                String type = db.getString("forests." + name + ".type", "normal");
                String isRegion = db.getString("forests." + name + ".isRegion", "no");
                int radius = db.getInt("forests." + name + ".radius", 5);
                String regionName = db.getString("forests." + name + ".regionName", "");
                double density = db.getDouble("forests." + name + ".density", 0.04);
                Vector locVec = db.getVector("forests." + name + ".location");
                String worldName = db.getString("forests." + name + ".world");

                if(isRegion != "yes") {
                    ForestCommands.generateForest(type, radius, density, locVec.toLocation(plugin.getServer().getWorld(worldName)), worldName, plugin, player);
                } else {
                    ForestCommands.generateRegionForest(type, regionName, density, worldName, plugin, player);
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED + "There is no forest called " + name);
            }
        } else {
            player.sendMessage(ChatColor.DARK_RED + "That forest does not exist.");
        }
    }

    @Command(
            aliases = { "help", "h", "?" },
            usage = "",
            desc = "Shows you the help for the LyForestDatabase.",
            minArgs = 0,
            maxArgs = 0
    )
    @CommandPermission({ "lytreehelper.db.forestlist" })
    public static void forestDBHelp(CommandArgs args,
            LyTreeHelperPlugin plugin, Player player, EditSession editSession) {
        player.sendMessage(ChatColor.GREEN + "LyTreeHelper LyForestDatabase Help");
        player.sendMessage(ChatColor.YELLOW + "************************************************");
        player.sendMessage(ChatColor.GREEN + "/lyforestdb list (page) - Shows you a list of the registered forests");
        player.sendMessage(ChatColor.GREEN + "/lyforestdb add <forestname> <forestradius> <foresttype> (density) - Registers a new forest");
        player.sendMessage(ChatColor.GREEN + "/lyforestdb remove <forestname> - Removes an existing forest");
        player.sendMessage(ChatColor.GREEN + "/lyforestdb regen <forestname> - Regenerates a registered forest");
        player.sendMessage(ChatColor.GREEN + "/lyforestdb help - Shows you the help for the LyForestDatabase");
    }
}
