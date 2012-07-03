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
package net.darklikally.lytreehelper.populator;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import net.darklikally.lytreehelper.bukkit.LyTreeHelperPlugin;

/**
 * This is LyTreeHelper's custom world populator
 * 
 * @author DarkLiKally
 * 
 */
public class LyTreeHelperPopulator extends BlockPopulator {

    private final LyTreeHelperPlugin plugin;

    public LyTreeHelperPopulator(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {

    }

    /**
     * Register the populator for all worlds
     */
    public void registerPopulators() {
        for (World world : plugin.getServer().getWorlds()) {
            registerPopulator(world);
        }
    }

    /**
     * Register the populator for the specified world.
     * 
     * @param world
     *            The world
     */
    public void registerPopulator(World world) {
        if (!world.getPopulators().contains(this)) {
            world.getPopulators().add(this);
        }
    }

    /**
     * Unregister the populator for all worlds
     */
    public void unregisterPopulators() {
        for (World world : plugin.getServer().getWorlds()) {
            unregisterPopulator(world);
        }
    }

    /**
     * Unregister the populator for the specified world
     * 
     * @param world
     *            The world
     */
    public void unregisterPopulator(World world) {
        if (world.getPopulators().contains(this)) {
            world.getPopulators().remove(this);
        }
    }

    /**
     * Populate a chunk
     */
    public void populate(World world, Random rng, Chunk source) {
        ArrayList<Block> blocks = getSurfaceBlocks(source, world);

    }

    /**
     * Receive error messages from the populator and handle them.
     * 
     * @param reason
     */
    private void BailError(String reason) {
        // plugin.getLogger().severe(reason);
    }

    /**
     * Return an ArrayList containing the surface blocks of the source chunk.
     * 
     * @param source
     *            The source Chunk
     * @param world
     *            The source World
     * @return
     */
    private ArrayList<Block> getSurfaceBlocks(Chunk source, World world) {
        ArrayList<Block> blocks = new ArrayList<Block>();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Block b = source.getBlock(x, 64, z);
                int y = world.getHighestBlockYAt(b.getX(), b.getZ());
                b = source.getBlock(x, y, z);

                blocks.add(b.getRelative(BlockFace.DOWN));
            }

        }

        return blocks;
    }
}
