//$Id$
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import net.darklikally.lytreehelper.bukkit.commands.ForestCommands;
import net.darklikally.lytreehelper.populator.LyTreeHelperPopulator;
import net.darklikally.sk89q.minecraft.util.commands.CommandException;
import net.darklikally.sk89q.minecraft.util.commands.CommandManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/**
 * The main class for LyTreeHelper as a Bukkit plugin.
 * 
 * @author DarkLiKally
 */
public class LyTreeHelperPlugin extends JavaPlugin {
    /**
     * The command handler
     */
    protected final LyTreeHelperCommands commandHandler;

    /**
     * Manager for commands. This automatically handles nested commands,
     * permissions checking, and a number of other fancy command things. We just
     * set it up and register commands against it.
     */
    private final CommandManager<Player> commands;

    /**
     * Handles all configuration.
     */
    private final ConfigurationManager configuration;

    /**
     * Vault Economy Handler
     */
    private static Economy economy = null;

    /**
     * Vault Permission Handler
     */
    private static Permission permissions = null;

    /**
     * Vault Chat Handler
     */
    private static Chat chat = null;

    /**
     * Construct objects. Actual loading occurs when the plugin is enabled, so
     * this merely instantiates the objects.
     */
    public LyTreeHelperPlugin() {
        configuration = new ConfigurationManager(this);

        final LyTreeHelperPlugin plugin = this;
        commands = new CommandManager<Player>(this) {
            @Override
            public boolean hasPermission(Player player, String perm) {
                return plugin.hasPermission(player, perm);
            }
        };
        commandHandler = new LyTreeHelperCommands(this);
    }

    /**
     * Called on plugin enable.
     */
    public void onEnable() {
        // Register Commands
        (new ForestCommands(this)).registerCommands();
        
        // Set the command executor to the commandHandler
        commandHandler.registerCommands();
        
        // Setup Vault
        if (!setupEconomy()) {
            getLogger()
                    .severe(String
                            .format("[%s] - Disabled due to no Vault dependency found!",
                                    getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();

        // Need to create the plugins/LyTreeHelper folder
        getDataFolder().mkdirs();

        try {
            // Load the configuration
            configuration.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Register events
        (new LyTreeHelperServerListener(this)).registerEvents();
        (new LyTreeHelperPlayerListener(this)).registerEvents();
        (new LyTreeHelperBlockListener(this)).registerEvents();

        // first initialize already loaded worlds then register the events
        LyTreeHelperWorldListener worldListener = new LyTreeHelperWorldListener(
                this);
        for (World world : getServer().getWorlds()) {
            worldListener.initializeWorld(world);
        }
        worldListener.registerEvents();

        // Register custom world populator
        (new LyTreeHelperPopulator(this)).initialize();
    }

    /**
     * Called on plugin disable.
     */
    public void onDisable() {
        configuration.unload();
        this.getServer().getScheduler().cancelTasks(this);
    }

    /**
     * Setup the Vault Economy Handler
     * 
     * @return
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    /**
     * Setup the Vault Permission Handler
     * 
     * @return
     */
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer()
                .getServicesManager().getRegistration(Permission.class);
        permissions = rsp.getProvider();
        return permissions != null;
    }

    /**
     * Setup the Vault Chat Handler
     * 
     * @return
     */
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager()
                .getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
    
    /**
     * Get the command manager
     * 
     * @return
     */
    public CommandManager<Player> getCommandManager() { 
        return commands;
    }

    /**
     * Get the global ConfigurationManager. Use this to access global
     * configuration values and per-world configuration values.
     * 
     * @return The global ConfigurationManager
     */
    public ConfigurationManager getGlobalConfigurationManager() {
        return configuration;
    }

    /**
     * Get the Configuration for the requested world
     * 
     * @param world
     *            The world
     * @return
     */
    public WorldConfiguration getWorldConfiguration(World world) {
        return getGlobalConfigurationManager().getWorldConfig(world);
    }

    /**
     * Checks to see if the sender is a player, otherwise throw an exception.
     * 
     * @param sender
     *            The {@link CommandSender} to check
     * @return {@code sender} casted to a player
     * @throws CommandException
     *             if {@code sender} isn't a {@link Player}
     */
    public Player checkPlayer(CommandSender sender) throws CommandException {
        if (sender instanceof Player) {
            return (Player) sender;
        } else {
            throw new CommandException("A player is expected.");
        }
    }

    /**
     * Gets a copy of the WorldEdit plugin.
     * 
     * @return The WorldEditPlugin instance
     * @throws CommandException
     *             If there is no WorldEditPlugin available
     */
    public WorldEditPlugin getWorldEdit() throws CommandException {
        Plugin worldEdit = getServer().getPluginManager()
                .getPlugin("WorldEdit");
        if (worldEdit == null) {
            throw new CommandException(
                    "WorldEdit does not appear to be installed.");
        }

        if (worldEdit instanceof WorldEditPlugin) {
            return (WorldEditPlugin) worldEdit;
        } else {
            throw new CommandException("WorldEdit detection failed.");
        }
    }

    /**
     * Gets a copy of the WorldEdit plugin.
     * 
     * @return The WorldEditPlugin instance
     * @throws CommandException
     *             If there is no WorldEditPlugin available
     */
    public WorldGuardPlugin getWorldGuard() throws CommandException {
        Plugin worldGuard = getServer().getPluginManager().getPlugin(
                "WorldGuard");
        if (worldGuard == null) {
            throw new CommandException(
                    "WorldGuard does not appear to be installed.");
        }

        if (worldGuard instanceof WorldGuardPlugin) {
            return (WorldGuardPlugin) worldGuard;
        } else {
            throw new CommandException("WorldGuard detection failed.");
        }
    }

    /**
     * Check whether the player has the sufficient permission or not.
     * 
     * @param player
     *            The player
     * @param perm
     *            The permission
     * @return
     */
    public boolean hasPermission(Player player, String perm) {
        return permissions.has(player, perm);
    }

    /**
     * Check whether the player has the sufficient permission in the specified
     * world or not.
     * 
     * @param player
     *            The player
     * @param world
     *            The world
     * @param perm
     *            The permission
     * @return
     */
    public boolean hasPermission(Player player, World world, String perm) {
        return permissions.has(world, player.getName(), perm);
    }

    /**
     * @see hasGroup(Player player, String group)
     * 
     * @param player
     *            The player
     * @param group
     *            The group
     * @return
     */
    public boolean inGroup(Player player, String group) {
        return hasGroup(player, group);
    }

    /**
     * Check if the player is member of the specified group.
     * 
     * @param player
     * @param group
     * @return
     */
    public boolean hasGroup(Player player, String group) {
        return permissions.playerInGroup(player, group);
    }

    /**
     * Create a default configuration file from the .jar.
     * 
     * @param actual
     *            The destination file
     * @param defaultName
     *            The name of the file inside the jar's defaults folder
     */
    public void createDefaultConfiguration(File actual, String defaultName) {

        // Make parent directories
        File parent = actual.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (actual.exists()) {
            return;
        }

        InputStream input = null;
        try {
            JarFile file = new JarFile(getFile());
            ZipEntry copy = file.getEntry("defaults/" + defaultName);
            if (copy == null)
                throw new FileNotFoundException();
            input = file.getInputStream(copy);
        } catch (IOException e) {
            getLogger().severe(
                    "Unable to read default configuration: " + defaultName);
        }

        if (input != null) {
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(actual);
                byte[] buf = new byte[8192];
                int length = 0;
                while ((length = input.read(buf)) > 0) {
                    output.write(buf, 0, length);
                }

                getLogger().info(
                        "Default configuration file written: "
                                + actual.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException ignore) {
                }

                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException ignore) {
                }
            }
        }
    }
}
