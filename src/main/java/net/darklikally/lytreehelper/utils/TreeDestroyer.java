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
import java.util.Random;

import net.darklikally.lytreehelper.bukkit.LyTreeHelperPlugin;
import net.darklikally.lytreehelper.bukkit.WorldConfiguration;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
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
        
        // Cancel destruction if the player cannot use the full destruction
        if(plugin.hasPermission(player, "lytreehelper.destruction.nofulldestruction")) {
            return null;
        }
        
        ArrayList<Block> blocks = new ArrayList<Block>();
        
        blocks = TreeDetector.detect(block, plugin);
        
        // We have a connection to something, so don't destroy
        if(blocks == null) {
            return null;
        }
        
        Byte saplingData = getSaplingData(block);
        
        boolean destroyed = destroyTree(blocks, wconfig, plugin);
        
        if(wconfig.enableAutoPlantSapling
                && blocks.size() > 8
                && !plugin.hasPermission(player, "lytreehelper.noautoplantsapling")) {
            plantSapling(block, saplingData);
        }
        
        if(wconfig.creaturesToSpawnInTrees.size() > 0
                && blocks.size() > 8
                && !plugin.hasPermission(player, "lytreehelper.creatures.nospawn")) {
            spawnCreature(block, wconfig, plugin);
        }
        
        if(wconfig.enableEconomySupport
                && blocks.size() > 8
                && !plugin.hasPermission(player, "lytreehelper.economy.freechopping")) {
            handleEconomy(player, wconfig, plugin);
        }
        
        return  destroyed ? blocks : null;
    }

    private static boolean destroyTree(ArrayList<Block> blocks,
            WorldConfiguration wconfig, LyTreeHelperPlugin plugin) {
        
        for(Block block : blocks) {
            Material blockType = block.getType();
            if(blockType == Material.LOG || blockType == Material.LEAVES) {
                // If we have only wood destruction and a leave block, continue to the next block
                if(wconfig.onlyWoodDestruction && blockType == Material.LEAVES) {
                    continue;
                }
                
                if(blockType == Material.LEAVES) {
                    // We have a leaves block, so drop the leaves items
                    TreeDropManager.dropLeaveItems(block, wconfig);
                } else {
                    // Only drop the block if it's a log, because the leaves drop randomly
                    ItemStack stack = new ItemStack(block.getType(), 1, (short) 0, block.getData());

                    TreeDropManager.dropItemNaturally(
                            block.getWorld(), block.getLocation(), stack);
                }
                block.setType(Material.AIR);
            }
        }
        
        return true;
    }
    
    private static void plantSapling(Block block, Byte saplingData) {
        block.setType(Material.AIR);
        Block plantHere = getSurfaceBlockBelow(block);
        
        if(plantHere == null) {
            return;
        }
        
        if(plantHere.getType() != Material.AIR) {
            plantHere = plantHere.getRelative(BlockFace.UP);
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
    
    private static void handleEconomy(Player player,
            WorldConfiguration wconfig, LyTreeHelperPlugin plugin) {
            
        Economy economy = plugin.getEconomy();
        
        if(economy == null) {
            return;
        }
        
        EconomyResponse response = economy.withdrawPlayer(player.getName(), wconfig.costsOnFullDestruction);
        
        if(response.transactionSuccess()) {
            player.sendMessage(ChatColor.YELLOW + "You have paid " + wconfig.costsOnFullDestruction + " for chopping this tree.");
        }
    }
    
    private static byte getSaplingData(Block block) {
        return (byte) (block.getData() & ~0x8);
    }
    
    private static Block getSurfaceBlockBelow(Block block) {
        List<Material> blocksToIgnore = Arrays.asList(
                Material.AIR,
                Material.LOG,
                Material.VINE,
                Material.LEAVES);
        
        Block surfaceBlock = 
            block.getWorld().getHighestBlockAt(block.getLocation());
        
        if(surfaceBlock.getY() > block.getY()) {
            for(int y = 0; y <= 30; y++) {
                if(y > 0) {
                    surfaceBlock = block.getRelative(BlockFace.DOWN);
                }
                if((surfaceBlock.getType() == Material.DIRT
                        || surfaceBlock.getType() == Material.GRASS)
                        && blocksToIgnore.contains(surfaceBlock.getRelative(BlockFace.UP).getType())) {
                    return surfaceBlock;
                }
            }
        } else {
            return surfaceBlock;
        }
        return null;
    }
}
