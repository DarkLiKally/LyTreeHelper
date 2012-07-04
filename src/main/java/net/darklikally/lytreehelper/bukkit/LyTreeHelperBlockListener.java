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

import net.darklikally.lytreehelper.utils.TreeDestroyer;
import net.darklikally.lytreehelper.utils.TreeDropManager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

/**
* @author DarkLiKally
*/
public class LyTreeHelperBlockListener implements Listener {

    private final LyTreeHelperPlugin plugin;

    public LyTreeHelperBlockListener(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled()) {
            return;
        }
        
        Block block = event.getBlock();
        Player player = event.getPlayer();
        WorldConfiguration wconfig = plugin.getGlobalConfigurationManager()
            .getWorldConfig(block.getWorld());
        boolean destructionAllowed = false;

        // Abort the checks if we have no tree
        if(block.getType() != Material.LOG && block.getType() != Material.LEAVES) {
            return;
        }

        // Check whether we have full destruction enabled
        if(wconfig.enableFullTreeDestruction) {
            destructionAllowed = true;
            
            if(!plugin.hasPermission(event.getPlayer(), "lytreehelper.fulldestruction")) {
                destructionAllowed = false;
            }
        // If we have no full destruction, check whether we have faster leaves destruction
        } else if (block.getType() == Material.LEAVES
                && wconfig.enableFasterLeavesDestruction
                && !player.isSneaking()) {
            fasterLeavesDestruction(block);
        }
        
        // See if we have a leaves block and cannot destroy the whole tree, check for harvest tools
        if(!destructionAllowed && block.getType() == Material.LEAVES) {
            harvestingLeaves(player, block, wconfig, plugin);
        }
        
        // If the full tree destruction is allowed... go on 
        if(destructionAllowed) {
            boolean destruct = false;

            if(wconfig.fullDestructionTools.size() > 0) {
                if(wconfig.fullDestructionTools.contains(player.getItemInHand().getTypeId())) {
                    destruct = true;
                }
            } else {
                destruct = true;
            }
            
            // Are we finally allowed to destroy the whole tree?
            if(destruct) {
                Material blockType = block.getType();
                
                // Drop the block only if it is a Log, because leaves blocks will drop randomly
                if(blockType == Material.LOG) {                    
                    ItemStack stack = new ItemStack(blockType, 1, (short)0, block.getData());
                    TreeDropManager.dropItemNaturally(
                            block.getWorld(), block.getLocation(), stack);
                } else {
                    // We have a leaves block, so harvest the leaves block
                    harvestingLeaves(player, block, wconfig, plugin);
                    
                    // We can also trigger the faster leaves destruction if necessary
                    if(wconfig.enableFasterLeavesDestruction && !player.isSneaking()) {
                        fasterLeavesDestruction(block);
                    }
                    
                }
                
                block.setType(Material.AIR);
                
                if(!player.isSneaking()) {
                    TreeDestroyer.destroy(player, block, wconfig, plugin);
                }
            }
        }
    }
    
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if(event.isCancelled()) {
            return;
        }
        
        Block block = event.getBlock();
        WorldConfiguration wconfig = plugin.getGlobalConfigurationManager()
            .getWorldConfig(block.getWorld());
        
        if(wconfig.enableLeavesDecay) {
            if(wconfig.enableFasterLeavesDecay) {
                fasterLeavesDestruction(block);
            }
            TreeDropManager.dropLeaveItems(block, wconfig);
        } else {
            event.setCancelled(true);
        }
    }
    
    private void fasterLeavesDestruction(Block block) {
        for(int x = -1; x < 2; x++) {
            for(int y = -1; y < 2; y++) {
                for(int z = -1; z < 2; z++) {
                    if(!(x == y && x == z)) {
                        Block relBlock = block.getRelative(x, y, z); 
                        if(relBlock.getType() == Material.LEAVES) {
                            relBlock.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
    
    private void harvestingLeaves(Player player, Block block,
            WorldConfiguration wconfig, LyTreeHelperPlugin plugin) {

        boolean canDrop = true;
        // Stop.. first check whether we can only harvest top to bottom
        if(wconfig.enableOnlyTopDownDrops
                && block.getRelative(BlockFace.UP).getType() != Material.AIR) {
            canDrop = false;
        }
        
        if(canDrop) {
            if(wconfig.harvestTools.size() > 0) {
                if(wconfig.harvestTools.contains(player.getItemInHand().getTypeId())) {
                    TreeDropManager.dropLeaveItems(block, wconfig);
                }
            } else {
                TreeDropManager.dropLeaveItems(block, wconfig);
            }
        }
    }
}