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

package net.darklikally.LyTreeHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import net.darklikally.LyTreeHelper.command.*;

/**
 *
 * @author DarkLiKally
 */
public class LyTreeHelperCommands implements CommandExecutor {
    /**
     * Plugin.
     */
    private LyTreeHelperPlugin plugin;

    private Map<String, LyTreeHelperCommand> commands;

    public LyTreeHelperCommands(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;

        this.commands = new HashMap<String, LyTreeHelperCommand>();


        this.commands.put("lytree", new CommandLytree());
        this.commands.put("lyforest", new CommandLyforest());
        this.commands.put("lynursery", new CommandLynursery());
        this.commands.put("lyregisterforest", new CommandLyregisterforest());
        this.commands.put("lydeleteforest", new CommandLydeleteforest());
        this.commands.put("lyregenerate", new CommandLyregenerate());
    }

    public void registerCommands() {
        for(String cmd : this.commands.keySet()) {
            PluginCommand command = this.plugin.getCommand(cmd);
            if(command != null) {
                command.setExecutor(this);
            }
        }
     }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            String cmdName = cmd.getName().toLowerCase();
            LyTreeHelperCommand command = commands.get(cmdName);
            if (command == null) {
                return false;
            }

            String senderName = sender instanceof Player ? ((Player)sender).getName() : "Console";
            Player senderPlayer = (Player) sender;
            LyTreeHelperConfiguration worldConfig = this.plugin.getWorldConfig(senderPlayer.getWorld().getName());

            if(worldConfig.isShowCommandsInLog()) {
                this.plugin.getLogger().log(Level.INFO, "[LyTreeHelper] Player "
                        + senderName + " issued command: " + cmdName);
            }

            command.handle(sender, senderName, cmdName, args, this.plugin, worldConfig);
            return true;

        } catch (InsufficientArgumentsException e) {
            if (e.getHelp() != null) {
                sender.sendMessage(ChatColor.RED + e.getHelp());
                return true;
            } else {
                return false;
            }
        } catch (InsufficientPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have sufficient permission for this command.");
            return true;
        } catch (CommandHandlingException e) {
            return true;
        } catch (Throwable t) {
            sender.sendMessage(ChatColor.RED + "ERROR: " + t.getMessage());
            t.printStackTrace();
            return true;
        }
    }

    public static void checkArgs(String[] args, int min, int max)
            throws InsufficientArgumentsException {
        if (args.length < min || (max != -1 && args.length > max)) {
            throw new InsufficientArgumentsException();
        }
    }

    public static void checkArgs(String[] args, int min, int max, String help)
            throws InsufficientArgumentsException {
        if (args.length < min || (max != -1 && args.length > max)) {
            throw new InsufficientArgumentsException(help);
        }
    }

    /**
     * Thrown when command handling has raised an exception.
     *
     * @author sk89q
     */
    public static class CommandHandlingException extends Exception {
        private static final long serialVersionUID = 6912130636812036780L;
    }

    /**
     * Thrown when a player has insufficient permissions.
     *
     * @author sk89q
     */
    public static class InsufficientPermissionsException extends CommandHandlingException {
        private static final long serialVersionUID = 8087662707619954750L;
    }

    /**
     * Thrown when a command wasn't given sufficient arguments.
     *
     * @author sk89q
     */
    public static class InsufficientArgumentsException extends CommandHandlingException {
        private static final long serialVersionUID = 3153597953889773788L;
        private final String help;

        public InsufficientArgumentsException() {
            help = null;
        }

        public InsufficientArgumentsException(String msg) {
            this.help = msg;
        }

        public String getHelp() {
            return help;
        }
    }
}
