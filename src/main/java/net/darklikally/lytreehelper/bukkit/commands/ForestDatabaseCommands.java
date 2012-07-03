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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.darklikally.bukkit.util.TargetBlock;
import net.darklikally.lytreehelper.bukkit.LyTreeHelperPlugin;
import net.darklikally.lytreehelper.generators.MammothTreeGenerator;
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
public class ForestDatabaseCommands {

    private final LyTreeHelperPlugin plugin;

    public ForestDatabaseCommands(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void registerCommands() {
        plugin.getCommandManager().register(this.getClass());
    }

}
