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
package net.darklikally.lytreehelper.bukkit;

import net.darklikally.sk89q.minecraft.util.commands.CommandManager;
import net.darklikally.sk89q.minecraft.util.commands.CommandMissingCommandNestingException;
import net.darklikally.sk89q.minecraft.util.commands.CommandNotEnoughPermissionsException;
import net.darklikally.sk89q.minecraft.util.commands.CommandUnhandledException;
import net.darklikally.sk89q.minecraft.util.commands.CommandWrappingException;
import net.darklikally.sk89q.minecraft.util.commands.CommandWrongUsageException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

/**
 * 
 * @author DarkLiKally
 * 
 */
public class LyTreeHelperCommands implements CommandExecutor {

    private final LyTreeHelperPlugin plugin;

    public LyTreeHelperCommands(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        for (String cmd : plugin.getCommandManager().getCommands().keySet()) {
            PluginCommand command = plugin.getCommand(cmd);
            if (command != null) {
                command.setExecutor(this);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
            String commandLabel, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        String[] split = new String[args.length + 1];
        System.arraycopy(args, 0, split, 1, args.length);
        split[0] = "/" + cmd.getName();

        return handleCommand((Player) sender, split);
    }

    public boolean handleCommand(Player player, String[] split) {
        try {
            split[0] = split[0].substring(1);

            String searchCmd = split[0].toLowerCase();

            CommandManager<Player> commands = plugin.getCommandManager();
            // Try to find the command
            if (commands.hasCommand(searchCmd)) {
            } else if (split[0].length() >= 2 && split[0].charAt(0) == '/'
                    && commands.hasCommand(searchCmd.substring(1))) {
                split[0] = split[0].substring(1);
            }

            // No command found
            if (!commands.hasCommand(split[0])) {
                return false;
            }

            try {
                commands.execute(split, player, plugin, player); // TODO:
                                                                      // maybe
                                                                      // if
                                                                      // needed
                                                                      // add
                                                                      // edit
                                                                      // session
                                                                      // to the
                                                                      // arg
                                                                      // list
            } catch (CommandNotEnoughPermissionsException e) {
                player.sendMessage(ChatColor.RED
                        + "You don't have the permission to do this.");
            } catch (CommandMissingCommandNestingException e) {
                player.sendMessage(ChatColor.RED + "" + e.getUsage());
            } catch (CommandWrongUsageException e) {
                player.sendMessage(ChatColor.RED + "" + e.getUsage());
            } catch (CommandWrappingException e) {
                player.sendMessage(ChatColor.RED + "" + e.getCause());
            } catch (CommandUnhandledException e) {
                return false;
            } finally {

            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Number expected; string given.");
        } catch (Throwable e) {
            player.sendMessage(ChatColor.RED
                    + "An error occured: [See console]");
            player.sendMessage(ChatColor.DARK_RED + e.getClass().getName()
                    + ": " + e.getMessage());
            e.printStackTrace();
        }

        return true;
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
    public static class InsufficientPermissionsException extends
            CommandHandlingException {
        private static final long serialVersionUID = 8087662707619954750L;
    }

    /**
     * Thrown when a command wasn't given sufficient arguments.
     * 
     * @author sk89q
     */
    public static class InsufficientArgumentsException extends
            CommandHandlingException {
        private static final long serialVersionUID = 3153597953889773788L;
        private final String help;

        public InsufficientArgumentsException() {
            help = null;
        }

        public InsufficientArgumentsException(String msg) {
            help = msg;
        }

        public String getHelp() {
            return help;
        }
    }
}
