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

import net.darklikally.LyTreeHelper.LyTreeHelperCommands.*;
import net.darklikally.LyTreeHelper.LyTreeHelperCommand;
import net.darklikally.LyTreeHelper.LyTreeHelperCommands;
import net.darklikally.LyTreeHelper.LyTreeHelperConfiguration;
import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author DarkLiKally
 */
public class CommandLyregenerate extends LyTreeHelperCommand {
    @Override
    public boolean handle(CommandSender sender, String senderName,
            String command, String[] args, LyTreeHelperPlugin plugin,
            LyTreeHelperConfiguration worldConfig) throws CommandHandlingException {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command");
            return true;
        }
        Player player = (Player) sender;

        if(!plugin.getPermissions().hasPermission(player, "db.regenerateforest")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have sufficient permissions for this action!");
            return true;
        }

        LyTreeHelperCommands.checkArgs(args, 1, 1);

        String name = args[0];

        if(name != null && name != "") {
            net.darklikally.LyTreeHelper.database.Database db = plugin.getLTHDatabase();

            if(db.getFromPath("forests." + name) != null) {
                String type = db.getString("forests." + name + ".type", "normal");
                int radius = db.getInt("forests." + name + ".radius", 5);
                double density = db.getDouble("forests." + name + ".density", 0.04);
                Vector locVec = db.getVector("forests." + name + ".location");
                String worldName = db.getString("forests." + name + ".world");

                CommandLyforest forestgen = new CommandLyforest();
                forestgen.handle(sender, senderName, command,
                        new String[]{String.valueOf(radius),type,String.valueOf(density)},
                        plugin, worldConfig, locVec.toLocation(plugin.getServer().getWorld(worldName)));
            } else {
                player.sendMessage(ChatColor.DARK_RED + "There is no forest " + name);
            }
        } else {
            player.sendMessage(ChatColor.DARK_RED + "That forest type does not exist.");
        }

        return true;
    }
}
