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
import net.darklikally.minecraft.utils.TreeGenerator;
import net.darklikally.minecraft.utils.TreeGenerator.TreeType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
        
        //TODO

        return true;
    }
}
