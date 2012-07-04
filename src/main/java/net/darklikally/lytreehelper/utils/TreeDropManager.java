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

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import net.darklikally.lytreehelper.bukkit.WorldConfiguration;

/**
 * 
 * @author DarkLiKally
 *
 */
public class TreeDropManager {
    
    public static boolean dropLeaveItems(Block block, WorldConfiguration wconfig) {
        
        Random gen = new Random();
        int maxItemsPerBlock = 3;;
        
        for(int i = 0; i < maxItemsPerBlock; i++) {
            int item = gen.nextInt(5);
            
            if(item < 3) {
                ItemStack stack = null;
                if(gen.nextDouble() * 100 <= wconfig.appleDropChance) {
                    stack = new ItemStack(Material.APPLE, 1);
                } else if(gen.nextDouble() * 100 <= wconfig.goldenAppleDropChance) {
                    stack = new ItemStack(Material.GOLDEN_APPLE, 1);
                } else if(gen.nextDouble() * 100 <= wconfig.leavesBlockDropChance) {
                    stack = new ItemStack(Material.LEAVES, 1, (short) 0, (byte)(block.getData() & ~0x8));
                } else if(gen.nextDouble() * 100 <= wconfig.saplingDropChance) {
                    stack = new ItemStack(Material.SAPLING, 1, (short) 0, (byte)(block.getData() & ~0x8));
                }
                
                // If we have a stack, drop it
                if(stack != null) {
                    TreeDropManager.dropItemNaturally(
                            block.getWorld(), block.getLocation(), stack);
                }
                continue;
            } else {
                if(wconfig.customDrops != null && wconfig.customDrops.size() != 0) {
                    Iterator<Map.Entry<String, Double>> iterator =
                        wconfig.customDrops.entrySet().iterator();
                    while(iterator.hasNext()) {
                        Map.Entry<String, Double> pair = (Map.Entry<String, Double>) iterator.next();
                        if(gen.nextDouble() * 100 <= pair.getValue()) {
                            String[] itemType = pair.getKey().split(",");
                            ItemStack stack =
                                new ItemStack(Material.getMaterial(Integer.parseInt(itemType[0])), 1);
                            
                            if(itemType.length > 1) {
                                stack.setDurability(Short.parseShort(itemType[1]));
                            }
                            
                            TreeDropManager.dropItemNaturally(block.getWorld(), block.getLocation(), stack);
                            
                            break;
                        }
                    }
                }
                continue;
            }
        }
        
        return true;
    }
    
    public static void dropItemNaturally(World world, Location loc, ItemStack stack) {
        world.dropItemNaturally(loc, stack);
    }
}
