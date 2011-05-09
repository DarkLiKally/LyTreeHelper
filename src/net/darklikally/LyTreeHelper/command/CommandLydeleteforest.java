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

import net.darklikally.LyTreeHelper.LyTreeHelperCommands.*;
import net.darklikally.LyTreeHelper.LyTreeHelperCommands;
import net.darklikally.LyTreeHelper.LyTreeHelperConfiguration;
import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkLiKally
 */
public class CommandLydeleteforest extends LyTreeHelperCommand {
    @Override
    public boolean handle(CommandSender sender, String senderName,
            String command, String[] args, LyTreeHelperPlugin plugin,
            LyTreeHelperConfiguration worldConfig) throws CommandHandlingException {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command");
            return true;
        }
        Player player = (Player) sender;

        if(!plugin.hasPermission(player, "db.deleteforest")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have sufficient permissions for this action!");
            return true;
        }

        LyTreeHelperCommands.checkArgs(args, 1, 1);

        String name = args[0];

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
    }
}
