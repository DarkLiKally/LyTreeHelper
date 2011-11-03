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

package net.darklikally.minecraft.utils.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.darklikally.LyTreeHelper.LyTreeHelperPlugin;

/**
 * 
 * @author DarkLiKally, original by sk89q
 */
public abstract class CommandManager<CPlayer> {
    /**
     * Plugin
     */
    protected LyTreeHelperPlugin plugin;
    
    /**
     * Logger for general errors.
     */
    protected static final Logger logger = Logger
            .getLogger(CommandManager.class.getCanonicalName());

    /**
     * Mapping of commands (including aliases) with a description. Root commands
     * are stored under a key of null, whereas child commands are cached under
     * their respective {@link Method}. The child map has the key of the command
     * name (one for each alias) with the method.
     */
    protected Map<Method, Map<String, Method>> commands = new HashMap<Method, Map<String, Method>>();

    /**
     * Used to store the instances associated with a method.
     */
    protected Map<Method, Object> instances = new HashMap<Method, Object>();

    /**
     * Mapping of commands (not including aliases) with a description. This is
     * only for top level commands.
     */
    protected Map<String, String> descs = new HashMap<String, String>();

    /**
     * Stores the injector used to getInstance.
     */
    protected CommandInjector injector;

    /**
     * Standard Constructor
     */
    public CommandManager(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Register an class that contains commands (denoted by {@link Command}. If
     * no dependency injector is specified, then the methods of the class will
     * be registered to be called statically. Otherwise, new instances will be
     * created of the command classes and methods will not be called statically.
     * 
     * @param cl
     */
    public void register(Class<?> cl) {
        registerMethods(cl, null);
    }

    /**
     * Register the methods of a class as commands. This will automatically
     * construct new instances if they are necessary.
     * 
     * @param cl
     * @param parent
     */
    private void registerMethods(Class<?> cl, Method parent) {
        try {
            if (this.getInjector() == null) {
                registerMethods(cl, parent, null);
            } else {
                Object obj = null;
                obj = this.getInjector().getInstance(cl);
                registerMethods(cl, parent, obj);
            }
        } catch (InvocationTargetException e) {
            logger.log(Level.SEVERE, "Failed to register commands", e);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Failed to register commands", e);
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Failed to register commands", e);
        }
    }

    /**
     * Register the methods of a class as commands.
     * 
     * @param cl
     * @param parent
     */
    private void registerMethods(Class<?> cl, Method parent, Object obj) {
        Map<String, Method> map;

        // Make a new hash map to cache the commands for this class
        // as looking up methods via reflection is fairly slow
        if (commands.containsKey(parent)) {
            map = commands.get(parent);
        } else {
            map = new HashMap<String, Method>();
            commands.put(parent, map);
        }

        for (Method method : cl.getMethods()) {
            if (!method.isAnnotationPresent(Command.class)) {
                continue;
            }

            boolean isStatic = Modifier.isStatic(method.getModifiers());

            Command cmd = method.getAnnotation(Command.class);

            // Cache the aliases too
            for (String alias : cmd.aliases()) {
                map.put(alias, method);
            }

            // We want to be able invoke with an instance
            if (!isStatic) {
                // Can't register this command if we don't have an instance
                if (obj == null) {
                    continue;
                }

                instances.put(method, obj);
            }

            // Build a list of commands and their usage details, at least for
            // root level commands
            if (parent == null) {
                if (cmd.usage().length() == 0) {
                    descs.put(cmd.aliases()[0], cmd.desc());
                } else {
                    descs.put(cmd.aliases()[0],
                            cmd.usage() + " - " + cmd.desc());
                }
            }

            // Look for nested commands -- if there are any, those have
            // to be cached too so that they can be quickly looked
            // up when processing commands
            if (method.isAnnotationPresent(CommandNesting.class)) {
                CommandNesting nestedCmd = method
                        .getAnnotation(CommandNesting.class);

                for (Class<?> nestedCl : nestedCmd.value()) {
                    registerMethods(nestedCl, method);
                }
            }
        }
    }

    /**
     * Checks to see whether there is a command named such at the root level.
     * This will check aliases as well.
     * 
     * @param command
     * @return
     */
    public boolean hasCommand(String command) {
        return commands.get(null).containsKey(command.toLowerCase());
    }

    /**
     * Get a list of command descriptions. This is only for root commands.
     * 
     * @return
     */
    public Map<String, String> getCommands() {
        return descs;
    }

    /**
     * Get the usage string for a command.
     * 
     * @param args
     * @param level
     * @param cmd
     * @return
     */
    protected String getUsage(String[] args, int level, Command cmd) {
        StringBuilder command = new StringBuilder();

        command.append("/");

        for (int i = 0; i <= level; ++i) {
            command.append(args[i] + " ");
        }

        command.append(cmd.flags().length() > 0 ? "[-" + cmd.flags() + "] "
                : "");
        command.append(cmd.usage());

        return command.toString();
    }

    /**
     * Get the usage string for a nested command.
     * 
     * @param args
     * @param level
     * @param method
     * @param player
     * @return
     * @throws CommandException
     */
    protected String getNestedUsage(String[] args, int level, Method method,
            CPlayer player) throws CommandException {

        StringBuilder command = new StringBuilder();

        command.append("/");

        for (int i = 0; i <= level; ++i) {
            command.append(args[i] + " ");
        }

        Map<String, Method> map = commands.get(method);
        boolean found = false;

        command.append("<");

        Set<String> allowedCommands = new HashSet<String>();

        for (Map.Entry<String, Method> entry : map.entrySet()) {
            Method childMethod = entry.getValue();
            found = true;

            if (hasPermission(childMethod, player)) {
                Command childCmd = childMethod.getAnnotation(Command.class);

                allowedCommands.add(childCmd.aliases()[0]);
            }
        }

        if (allowedCommands.size() > 0) {
            command.append(CommandManager.joinString(allowedCommands.toArray(),
                    "|", 0));
        } else {
            if (!found) {
                command.append("?");
            } else {
                throw new CommandNotEnoughPermissionsException();
            }
        }

        command.append(">");

        return command.toString();
    }

    /**
     * Converts an array of strings to one string.
     * 
     * @param objects
     * @param delimiter
     * @param initialIndex
     * @return
     */
    public static String joinString(Object[] objects, String delimiter,
            int initialIndex) {
        if (objects.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(
                objects[initialIndex].toString());
        for (int i = initialIndex + 1; i < objects.length; ++i) {
            buffer.append(delimiter).append(objects[i].toString());
        }
        return buffer.toString();
    }

    /**
     * Attempt to execute a command. This version takes a separate command name
     * (for the root command) and then a list of following arguments.
     * 
     * @param cmd
     *            command to run
     * @param args
     *            arguments
     * @param player
     *            command source
     * @param methodArgs
     *            method arguments
     * @throws CommandException
     */
    public void execute(String cmd, String[] args, CPlayer player,
            Object... methodArgs) throws CommandException {

        String[] newArgs = new String[args.length + 1];
        System.arraycopy(args, 0, newArgs, 1, args.length);
        newArgs[0] = cmd;
        Object[] newMethodArgs = new Object[methodArgs.length + 1];
        System.arraycopy(methodArgs, 0, newMethodArgs, 1, methodArgs.length);

        executeMethod(null, newArgs, player, newMethodArgs, 0);
    }

    /**
     * Attempt to execute a command.
     * 
     * @param args
     * @param player
     * @param methodArgs
     * @throws CommandException
     */
    public void execute(String[] args, CPlayer player, Object... methodArgs)
            throws CommandException {

        Object[] newMethodArgs = new Object[methodArgs.length + 1];
        System.arraycopy(methodArgs, 0, newMethodArgs, 1, methodArgs.length);
        executeMethod(null, args, player, newMethodArgs, 0);
    }

    /**
     * Attempt to execute a command.
     * 
     * @param parent
     * @param args
     * @param player
     * @param methodArgs
     * @param level
     * @throws CommandException
     */
    public void executeMethod(Method parent, String[] args, CPlayer player,
            Object[] methodArgs, int level) throws CommandException {

        String cmdName = args[level];

        Map<String, Method> map = commands.get(parent);
        Method method = map.get(cmdName.toLowerCase());

        if (method == null) {
            if (parent == null) { // Root
                throw new CommandUnhandledException();
            } else {
                throw new CommandMissingCommandNestingException(
                        "Unknown command: " + cmdName, getNestedUsage(args,
                                level - 1, parent, player));
            }
        }

        if (!hasPermission(method, player)) {
            throw new CommandNotEnoughPermissionsException();
        }

        int argsCount = args.length - 1 - level;

        if (method.isAnnotationPresent(CommandNesting.class)) {
            if (argsCount == 0) {
                throw new CommandMissingCommandNestingException(
                        "Sub-command required.", getNestedUsage(args, level,
                                method, player));
            } else {
                executeMethod(method, args, player, methodArgs, level + 1);
            }
        } else {
            Command cmd = method.getAnnotation(Command.class);

            String[] newArgs = new String[args.length - level];
            System.arraycopy(args, level, newArgs, 0, args.length - level);

            CommandArgs context = new CommandArgs(newArgs);

            if (context.argsLength() < cmd.minArgs()) {
                throw new CommandWrongUsageException("Too few arguments.",
                        getUsage(args, level, cmd));
            }

            if (cmd.maxArgs() != -1 && context.argsLength() > cmd.maxArgs()) {
                throw new CommandWrongUsageException("Too many arguments.",
                        getUsage(args, level, cmd));
            }

            for (char flag : context.getFlags()) {
                if (cmd.flags().indexOf(String.valueOf(flag)) == -1) {
                    throw new CommandWrongUsageException("Unknown command flag: "
                            + flag, getUsage(args, level, cmd));
                }
            }

            methodArgs[0] = context;

            Object instance = instances.get(method);

            invokeMethod(parent, args, player, method, instance, methodArgs,
                    argsCount);
        }
    }

    public void invokeMethod(Method parent, String[] args, CPlayer player,
            Method method, Object instance, Object[] methodArgs, int level)
            throws CommandException, CommandWrappingException {
        try {
            method.invoke(instance, methodArgs);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Failed to execute command", e);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Failed to execute command", e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof CommandException) {
                throw (CommandException) e.getCause();
            }

            throw new CommandWrappingException(e.getCause());
        }
    }

    /**
     * Returns whether a player has access to a command.
     * 
     * @param method
     * @param player
     * @return
     */
    protected boolean hasPermission(Method method, CPlayer player) {
        CommandPermissions perms = method
                .getAnnotation(CommandPermissions.class);
        if (perms == null) {
            return true;
        }

        for (String perm : perms.value()) {
            if (hasPermission(player, perm)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether a player permission..
     * 
     * @param player
     * @param perm
     * @return
     */
    public abstract boolean hasPermission(CPlayer player, String perm);

    /**
     * Get the injector used to create new instances. This can be null, in which
     * case only classes will be registered statically.
     */
    public CommandInjector getInjector() {
        return injector;
    }

    /**
     * Set the injector for creating new instances.
     * 
     * @param injector
     *            injector or null
     */
    public void setInjector(CommandInjector injector) {
        this.injector = injector;
    }
}