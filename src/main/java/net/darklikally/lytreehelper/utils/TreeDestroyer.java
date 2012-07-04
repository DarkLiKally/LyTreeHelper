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
import java.util.Random;

import net.darklikally.lytreehelper.bukkit.LyTreeHelperPlugin;
import net.darklikally.lytreehelper.bukkit.WorldConfiguration;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author DarkLiKally
 *
 */
public class TreeDestroyer {
    
    public static ArrayList<Block> destroy(Player player, Block block,
            WorldConfiguration wconfig, LyTreeHelperPlugin plugin) {
        ArrayList<Block> blocks = new ArrayList<Block>();
        
        blocks = TreeDetector.detect(block, plugin);
        
        Byte saplingData = getSaplingData(block);
        
        boolean destroyed = destroyTree(blocks, wconfig, plugin);
        
        if(wconfig.enableAutoPlantSapling) {
            plantSapling(block, saplingData);
        }
        
        if(wconfig.creaturesToSpawnInTrees.size() > 0) {
            spawnCreature(block, wconfig, plugin);
        }
        
        return  destroyed ? blocks : null;
    }
    
    private static boolean destroyTree(ArrayList<Block> blocks,
            WorldConfiguration wconfig, LyTreeHelperPlugin plugin) {
        
        for(Block block : blocks) {
            Material blockType = block.getType();
            if(blockType == Material.LOG || blockType == Material.LEAVES) {
                ItemStack stack = new ItemStack(block.getType(), 1, (short) 0, block.getData());
                block.setType(Material.AIR);
                TreeDropManager.dropItemNaturally(
                        block.getWorld(), block.getLocation(), stack);
            }
        }
        
        return true;
    }
    
    private static void plantSapling(Block block, Byte saplingData) {
        Block plantHere = getSurfaceBlockBelow(block).getRelative(BlockFace.UP);
        
        if(plantHere == null) {
            return;
        }
        
        plantHere.setType(Material.SAPLING);
        plantHere.setData(saplingData);
    }
    
    private static void spawnCreature(Block block,
            WorldConfiguration wconfig, LyTreeHelperPlugin plugin) {
        
        double rand = new Random().nextDouble() * 100;
        
        // Is a monster going to spawn? Continue..
        if(rand <= wconfig.creatureSpawnChance) {
            int size = wconfig.creaturesToSpawnInTrees.size();
            EntityType type = (EntityType) wconfig.creaturesToSpawnInTrees
                .toArray()[new Random().nextInt(size)];
            
            @SuppressWarnings("unused")
            LivingEntity creature = CreatureSpawner.spawn(
                    block.getLocation(), type, wconfig, plugin);
        }
    }
    
    private static byte getSaplingData(Block block) {
        return (byte) (block.getData() & ~0x8);
    }
    
    private static Block getSurfaceBlockBelow(Block block) {
        Block surfaceBlock = 
            block.getWorld().getHighestBlockAt(block.getLocation());
        
        if(surfaceBlock.getY() > block.getY()) {
            for(int y = 0; y <= 30; y++) {
                surfaceBlock = block.getRelative(BlockFace.DOWN);
                if((surfaceBlock.getType() == Material.DIRT
                        || surfaceBlock.getType() == Material.GRASS)
                        && surfaceBlock.getRelative(BlockFace.UP).getType() == Material.AIR) {
                    return surfaceBlock;
                }
            }
        }
        return null;
    }
}
