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
package net.darklikally.lytreehelper.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.darklikally.lytreehelper.bukkit.ConfigurationManager;
import net.darklikally.lytreehelper.bukkit.LyTreeHelperPlugin;
import net.darklikally.lytreehelper.bukkit.WorldConfiguration;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * 
 * @author DarkLiKally
 * 
 */
public class TreeDetector {

    private static List<Material> blocksToIgnore = Arrays.asList(
            Material.AIR,
            Material.SAPLING,
            Material.RED_ROSE,
            Material.YELLOW_FLOWER,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.LONG_GRASS,
            Material.DEAD_BUSH,
            Material.VINE,
            Material.WEB,
            Material.CACTUS,
            Material.SUGAR_CANE_BLOCK);

    private static List<Location> checkBlocks = Arrays.asList(
            new Location(null, 1.0, 0.0, 0.0),
            new Location(null, 0.0, 1.0, 0.0),
            new Location(null, 0.0, 0.0, 1.0),
            new Location(null, -1.0, 0.0, 0.0),
            new Location(null, 0.0, -1.0, 0.0),
            new Location(null, 0.0, 0.0, -1.0));

    /**
     * Counts the processed trees
     */
    private static int processCounter = 0;

    /**
     * Returns the number of processed trees
     * 
     * @return
     */
    public static int getProcessCount() {
        return processCounter;
    }

    /**
     * Detect the tree from the given source block and return all blocks of the
     * tree.
     * 
     * @see detectTree()
     * 
     * @param source
     *            The source block (in normal case the destroyed block)
     * @param config
     *            The LyTreeHelper Configuration Manager
     * @param plugin
     *            The LyTreeHelper plugin
     * @return
     */
    public static ArrayList<Block> detect(Block source,
            LyTreeHelperPlugin plugin) {

        ConfigurationManager config = plugin.getGlobalConfigurationManager();
        WorldConfiguration wconfig = config.getWorldConfig(source.getWorld());
        ArrayList<Block> blocks = new ArrayList<Block>();

        ArrayList<Block> returned = 
            detectTree(source, source, blocks, config, wconfig, plugin, false); 
        return returned != null ? returned : blocks;
    }

    /**
     * Detect the tree from the given source block using recursion and return
     * all the blocks.
     * 
     * @param source
     *            The source block
     * @param first
     *            The first block in the check
     * @param wconfig
     *            The world configuration
     * @param plugin
     *            The LyTreeHelper plugin
     * @param hasLeaves
     *            Whether the tree has leaves or not
     * @return
     */
    private static ArrayList<Block> detectTree(Block source, Block first,
            ArrayList<Block> blocks, ConfigurationManager config, WorldConfiguration wconfig,
            LyTreeHelperPlugin plugin, boolean hasLeaves) {

        if(blocks.size() > config.maxTreeSize) {
            return null;
        }
        
        for(Location checkBlock : checkBlocks) {
            Block relBlock = source.getRelative(
                    checkBlock.getBlockX(),
                    checkBlock.getBlockY(),
                    checkBlock.getBlockZ());
            
            if(!checkTreeRadius(config.maxTreeRadius, source, first)) {
                continue;
            }
            
            Material relBlockType = relBlock.getType();
            if(relBlockType == Material.LEAVES
                || relBlockType == Material.LOG
                || relBlockType == Material.SNOW) {
                    if(relBlockType == Material.LEAVES) {
                        hasLeaves = true;
                    }
                    
                    if(!blocks.contains(relBlock)) {
                        if(wconfig.onlyWoodDestruction) {
                            if(relBlockType == Material.LOG) {
                                blocks.add(relBlock);
                            }
                        } else {
                            blocks.add(relBlock);
                        }
                        
                        blocks = detectTree(source, first, blocks, config, wconfig, plugin, hasLeaves);
                    }
                } else if(blocksToIgnore.contains(relBlockType)) {
                    return null;
                }
        }

        return blocks;
    }
    
    private static boolean checkTreeRadius(int maxRadius, Block source, Block first) {
        if(maxRadius > 0) {
            if((source.getX() <= first.getX() + maxRadius)
                    && (source.getX() >= first.getX() - maxRadius)
                    && (source.getZ() <= first.getZ() + maxRadius)
                    && (source.getZ() >= first.getZ() - maxRadius)) {
                return true;
            }
            return false;
        }
        return true;
    }
}
