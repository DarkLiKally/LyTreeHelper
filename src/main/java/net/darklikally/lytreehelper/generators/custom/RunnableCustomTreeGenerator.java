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
package net.darklikally.lytreehelper.generators.custom;

import org.bukkit.Location;
import org.bukkit.World;

import net.darklikally.lytreehelper.bukkit.LyTreeHelperPlugin;
import net.darklikally.lytreehelper.bukkit.WorldConfiguration;

/**
 * 
 * @author DarkLikally
 * 
 */
public class RunnableCustomTreeGenerator extends CustomTreeGenerator implements
        Runnable {
    private boolean isRunning;

    @SuppressWarnings("unused")
    private final LyTreeHelperPlugin plugin;

    @SuppressWarnings("unused")
    private WorldConfiguration wconfig;

    private Location loc;

    private World world;

    private String[] args;

    public RunnableCustomTreeGenerator(Location loc, World world,
            LyTreeHelperPlugin plugin, WorldConfiguration wconfig, String[] args) {
        setRunning(false);

        this.plugin = plugin;
        this.wconfig = wconfig;
        this.args = args;
    }

    /**
     * Returns the name of the custom tree generator
     * 
     * @return
     */
    @Override
    public String getName() {
        return "RunnableCustomTreeGenerator";
    }

    /**
     * Returns the version of the custom tree generator
     * 
     * @return
     */
    @Override
    public String getVersion() {
        return "1.0";
    }

    /**
     * Returns the description of the custom tree generator
     * 
     * @return
     */
    @Override
    public String getDescription() {
        return "Custom runnable tree generator description";
    }

    /**
     * Called by the Java environment to start the generator.
     */
    @Override
    public void run() {
        setRunning(true);
        if (!this.generate()) {
            // Could not create the tree
        }
    }

    /**
     * Generate the tree
     * 
     * @see generate(Location loc, World world, String[] args)
     * 
     * @return
     */
    public boolean generate() {
        return this.generate(loc, world, args);
    }

    /**
     * Generate the tree
     * 
     * @param loc
     * @param world
     * @param args
     * @return
     */
    @Override
    public boolean generate(Location loc, World world, String[] args) {
        return true;
    }

    /**
     * Pauses the generator for the given amount of milliseconds
     * 
     * @param time
     */
    protected void pause(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            // Cannot create the thread to generate the tree
        }
    }

    /**
     * Returns whether the generator is running or nor
     * 
     * @return
     */
    public boolean isRunning() {
        return this.isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

}
