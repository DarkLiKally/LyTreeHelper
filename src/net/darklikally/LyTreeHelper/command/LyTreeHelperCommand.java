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

import net.darklikally.LyTreeHelper.LyTreeHelperConfiguration;
import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;
import net.darklikally.LyTreeHelper.LyTreeHelperCommands.*;

import org.bukkit.command.CommandSender;

/**
 *
 * @author DarkLiKally
 */
public abstract class LyTreeHelperCommand {

    public abstract boolean handle(CommandSender sender, String senderName,
            String command, String[] args, LyTreeHelperPlugin plugin, LyTreeHelperConfiguration worldConfig)
            throws CommandHandlingException;

}
