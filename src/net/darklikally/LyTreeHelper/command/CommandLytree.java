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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.darklikally.LyTreeHelper.LyTreeHelperCommands.*;
import net.darklikally.LyTreeHelper.LyTreeHelperCommands;
import net.darklikally.LyTreeHelper.LyTreeHelperConfiguration;
import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;
import net.darklikally.minecraft.utils.TargetBlock;
import net.darklikally.minecraft.utils.TreeGenerator;
import net.darklikally.minecraft.utils.TreeGenerator.TreeType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkLiKally
 */
public class CommandLytree extends LyTreeHelperCommand {
    @Override
    public boolean handle(CommandSender sender, String senderName,
            String command, String[] args, LyTreeHelperPlugin plugin,
            LyTreeHelperConfiguration worldConfig) throws CommandHandlingException {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command");
            return true;
        }
        Player player = (Player) sender;

        LyTreeHelperCommands.checkArgs(args, 1, 1);

        if(!plugin.hasPermission(player, "generate.trees")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have sufficient permissions for this action!");
            return true;
        }

        int[] ignoreBlockIds = {8, 9};
        Location loc = new TargetBlock(player, 300, 0.2, ignoreBlockIds).getTargetBlock().getLocation(); //player.getLocation();
        loc.setY(loc.getBlockY() + 1);

        //loc.setX(loc.getBlockX() + 3);

        String typeName = args[0];
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
        } 

        if (type != null) {
            TreeGenerator gen = new TreeGenerator(plugin, type);
            if (!gen.generator(player.getWorld().getName(), loc.toVector())) {
                player.sendMessage("A tree can't go there.");
            } else {
                player.sendMessage(ChatColor.YELLOW + type.getName() + " created.");
            }
        } else {
            player.sendMessage(ChatColor.DARK_RED + "That tree type does not exist.");
        }

        return true;
    }
}
