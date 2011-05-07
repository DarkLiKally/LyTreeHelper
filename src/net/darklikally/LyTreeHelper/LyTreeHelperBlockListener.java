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

package net.darklikally.LyTreeHelper;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 *
 * @author DarkLiKally
 */
public class LyTreeHelperBlockListener extends BlockListener {
    /**
     * Plugin.
     */
    private LyTreeHelperPlugin plugin;

    private List<Location> checkBlocks = new ArrayList<Location>();

    private List<Block> checkedBlocks;

    private int checkedBlocksCounter;

    /**
     * Construct the object;
     * 
     * @param plugin
     */
    public LyTreeHelperBlockListener(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;

        this.checkBlocks.add(new Location(null, 1.0, 0.0, 0.0));
        this.checkBlocks.add(new Location(null, 0.0, 1.0, 0.0));
        this.checkBlocks.add(new Location(null, 0.0, 0.0, 1.0));
        this.checkBlocks.add(new Location(null, -1.0, 0.0, 0.0));
        this.checkBlocks.add(new Location(null, 0.0, -1.0, 0.0));
        this.checkBlocks.add(new Location(null, 0.0, 0.0, -1.0));
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();

        pm.registerEvent(Event.Type.LEAVES_DECAY, this, Priority.High, plugin);
        pm.registerEvent(Event.Type.BLOCK_BREAK, this, Priority.High, plugin);
    }

    public void destroyTree(Block firstBlock) {
        LyTreeHelperConfiguration worldConfig = this.plugin.getWorldConfig(firstBlock.getWorld().getName());

        this.checkedBlocks = new ArrayList<Block>();
        this.checkedBlocksCounter = 0;
        boolean returnValue = true;

        if ((worldConfig.isDestroyAllWood() && !worldConfig.isDestroyAll())) {
            if (destroyTreeWoodOnly(firstBlock, returnValue, worldConfig)) {
                for (Block block : this.checkedBlocks) {
                    if(block.getType() == Material.LOG) {
                        block.getWorld().dropItemNaturally(
                                block.getLocation(), new ItemStack(Material.LOG, 1, (short)0,
                                        Byte.valueOf(block.getData())));
                        block.setType(Material.AIR);
                    }
                }
                if(worldConfig.getCreaturesToSpawn().size() > 0) {
                    this.spawnCreature(firstBlock);
                }
            }
        } else if (!worldConfig.isDestroyAllWood()) {
            if (destroyTreeLeaves(firstBlock, returnValue, worldConfig)) {
                for (Block block : this.checkedBlocks) {
                    if (block.getType() != Material.SNOW) {
                        dropLeaveItems(block);
                    }
                    block.setType(Material.AIR);
                }
                if(worldConfig.getCreaturesToSpawn().size() > 0) {
                    this.spawnCreature(firstBlock);
                }
            }
        } else if (destroyTreeWood(firstBlock, returnValue, worldConfig)) {
            for (Block block : this.checkedBlocks) {
                if (block.getType() == Material.LOG) {
                    block.getWorld().dropItemNaturally(
                            block.getLocation(), new ItemStack(Material.LOG, 1, (short)0,
                                    Byte.valueOf(block.getData())));
                }
                if (block.getType() != Material.SNOW) {
                    dropLeaveItems(block);
                }
                block.setType(Material.AIR);
            }
            if(worldConfig.getCreaturesToSpawn().size() > 0) {
                this.spawnCreature(firstBlock);
            }
        }
    }

    public boolean destroyTreeLeaves(Block currentBlock, boolean ret, LyTreeHelperConfiguration worldConfig) {
        return destroyTreeLeaves(currentBlock, ret, worldConfig, currentBlock);
    }

    public boolean destroyTreeLeaves(Block currentBlock, boolean ret, LyTreeHelperConfiguration worldConfig, Block startBlock) {
        if (this.checkedBlocks.size() > worldConfig.getMaxTreeSize()) {
            return false;
        }

        for (Location currCheckBlock : this.checkBlocks) {
            Block relative = currentBlock.getRelative((int)currCheckBlock.getX(), (int)currCheckBlock.getY(), (int)currCheckBlock.getZ());

            if(!this.checkTreeRadius(worldConfig.getMaxTreeRadius(), startBlock, relative)) {
                continue;
            }

            if ((relative.getType() == Material.LEAVES)
                    || (relative.getType() == Material.SNOW)) {
                if (!this.checkedBlocks.contains(relative)) {
                    this.checkedBlocksCounter++;
                    this.checkedBlocks.add(relative);
                    ret = destroyTreeLeaves(relative, ret, worldConfig, startBlock);
                }  
            } else if (relative.getType() != Material.AIR) {
                ret = false;
            }
        }
        
        return ret;
    }

    public boolean destroyTreeWood(Block currentBlock, boolean ret, LyTreeHelperConfiguration worldConfig) {
        return destroyTreeWood(currentBlock, ret, worldConfig, currentBlock);
    }

    public boolean destroyTreeWood(Block currentBlock, boolean ret, LyTreeHelperConfiguration worldConfig, Block startBlock) {
        if (this.checkedBlocks.size() > worldConfig.getMaxTreeSize()) {
            return false;
        }

        for (Location currCheckBlock : this.checkBlocks) {
            Block relative = currentBlock.getRelative((int)currCheckBlock.getX(), (int)currCheckBlock.getY(), (int)currCheckBlock.getZ());

            if(!this.checkTreeRadius(worldConfig.getMaxTreeRadius(), startBlock, relative)) {
                continue;
            }

            if ((relative.getType() == Material.LEAVES)
                    || (relative.getType() == Material.LOG)
                    || (relative.getType() == Material.SNOW)) {
                if (!this.checkedBlocks.contains(relative)) {
                    this.checkedBlocksCounter++;
                    this.checkedBlocks.add(relative);
                    ret = destroyTreeWood(relative, ret, worldConfig, startBlock);
                }
            }
            else if (relative.getType() != Material.AIR) {
                ret = false;
            }
        }
        
        return ret;
    }

    public boolean destroyTreeWoodOnly(Block currentBlock, boolean ret, LyTreeHelperConfiguration worldConfig) {
        return destroyTreeWoodOnly(currentBlock, ret, worldConfig, currentBlock);
    }

    public boolean destroyTreeWoodOnly(Block currentBlock, boolean ret, LyTreeHelperConfiguration worldConfig, Block startBlock) {
        if (this.checkedBlocks.size() > worldConfig.getMaxTreeSize()) {
            return false;
        }

        for (Location currCheckBlock : this.checkBlocks) {
            Block relative = currentBlock.getRelative((int)currCheckBlock.getX(), (int)currCheckBlock.getY(), (int)currCheckBlock.getZ());

            if(!this.checkTreeRadius(worldConfig.getMaxTreeRadius(), startBlock, relative)) {
                continue;
            }

            if ((relative.getType() == Material.LEAVES)
                    || (relative.getType() == Material.LOG)
                    || (relative.getType() == Material.SNOW)) {
                if (!this.checkedBlocks.contains(relative)) {
                    this.checkedBlocksCounter++;
                    if(relative.getType() == Material.LOG) {
                        this.checkedBlocks.add(relative);
                    }
                    ret = destroyTreeWoodOnly(relative, ret, worldConfig, startBlock);
                }
            }
        }
        
        return ret;
    }

    private boolean checkTreeRadius(int maxRadius, Block startBlock, Block currentBlock) {
        if(maxRadius > 0) {
            if ((currentBlock.getX() <= startBlock.getX() + maxRadius)
                    && (currentBlock.getX() >= startBlock.getX() - maxRadius)
                    && (currentBlock.getZ() <= startBlock.getZ() + maxRadius)
                    && (currentBlock.getZ() >= startBlock.getZ() - maxRadius)) {
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public void onLeavesDecay(LeavesDecayEvent event) {
        if(event.isCancelled()) {
            return;
        }

        LyTreeHelperConfiguration worldConfig = this.plugin.getWorldConfig(event.getBlock().getWorld().getName());

        if (worldConfig.isDestroyAll() && !worldConfig.isDecay()) {
            event.setCancelled(true);
            destroyTree(event.getBlock());
        } else if (worldConfig.isDecay()) {
            if (worldConfig.isFasterDecay()) {
                fasterDecay(event.getBlock());
            }
            dropLeaveItems(event.getBlock());
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled()) {
            return;
        }

        LyTreeHelperConfiguration worldConfig = this.plugin.getWorldConfig(event.getBlock().getWorld().getName());

        if (event.getBlock().getType() == Material.LEAVES) {
            if (worldConfig.isDestroyAll()) {
                if(!this.plugin.hasPermission(event.getPlayer(), "destroyall")) {
                    return;
                }

                if (worldConfig.getDestructionTools().size() > 0) {
                    for (int destructionTool : worldConfig.getDestructionTools()) {
                        if (event.getPlayer().getItemInHand().getTypeId() == destructionTool) {
                            destroyTree(event.getBlock());
                        }
                    }
                } else {
                    destroyTree(event.getBlock());
                }
            } else if (worldConfig.isDestroyFaster()) {
                fasterDecay(event.getBlock());
            }
            if (worldConfig.getHarvestTools().size() > 0) {
                for (int harvestTool : worldConfig.getHarvestTools()) {
                    if (event.getPlayer().getItemInHand().getTypeId() == harvestTool) {
                        dropLeaveItems(event.getBlock());
                        break;
                    }
                }
            } else {
                dropLeaveItems(event.getBlock());
            }
        } else if ((event.getBlock().getType() == Material.LOG)
                && (worldConfig.isDestroyAll()) && (worldConfig.isDestroyAllWood())) {
            if(!this.plugin.hasPermission(event.getPlayer(), "destroyall")) {
                return;
            }

            boolean destructionAllowed = false;

            if (worldConfig.getDestructionTools().size() > 0) {
                for (int destructionTool : worldConfig.getDestructionTools()) {
                    if (event.getPlayer().getItemInHand().getTypeId() == destructionTool) {
                        destructionAllowed = true;
                    }
                }
            } else {
                destructionAllowed = true;
            }
            
            if(destructionAllowed) {
                event.setCancelled(true);
                ItemStack stack = new ItemStack(Material.LOG, 1, (short)0, Byte.valueOf(event.getBlock().getData()));
                event.getBlock().setType(Material.AIR);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stack);
                destroyTree(event.getBlock().getRelative(0, 1, 0));
            }
        } else if ((event.getBlock().getType() == Material.LOG)
                && (worldConfig.isDestroyAllWood())) {
            if(!this.plugin.hasPermission(event.getPlayer(), "destroyall")) {
                return;
            }

            boolean destructionAllowed = false;

            if (worldConfig.getDestructionTools().size() > 0) {
                for (int destructionTool : worldConfig.getDestructionTools()) {
                    if (event.getPlayer().getItemInHand().getTypeId() == destructionTool) {
                        destructionAllowed = true;
                    }
                }
            } else {
                destructionAllowed = true;
            }
            
            if(destructionAllowed) {
                event.setCancelled(true);
                ItemStack stack = new ItemStack(Material.LOG, 1, (short)0, Byte.valueOf(event.getBlock().getData()));
                event.getBlock().setType(Material.AIR);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stack);
                destroyTree(event.getBlock().getRelative(0, 1, 0));
            }
        }
    }

    public void dropLeaveItems(Block block) {
        LyTreeHelperConfiguration worldConfig = this.plugin.getWorldConfig(block.getWorld().getName());

        if(worldConfig.isOnlyTopDown()) {
            if(worldConfig.isDestroyAll() && !isGroundConnection(block)) {
                return;
            } else if(block.getFace(BlockFace.UP).getType() != Material.AIR) {
                return;
            }
        }

        Random generator = new Random();
        int rand = generator.nextInt(10000);
        
        if (rand >= (10000.0 - (worldConfig.getAppleChance() * 100.0))) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.APPLE, 1));
        }
        if (rand >= (10000.0 - (worldConfig.getGoldenAppleChance() * 100.0))) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.GOLDEN_APPLE, 1));
        }
        if (rand >= (10000.0 - (worldConfig.getLeavesChance() * 100.0))) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.LEAVES, 1, (short)0, (byte)(block.getData() & ~0x8)));
        }
        if (rand >= (10000.0 - (worldConfig.getSaplingChance() * 100.0))) {
        	block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SAPLING, 1, (short)0, (byte)(block.getData() & ~0x8)));
        }

        Iterator<Map.Entry<String,Double>> iterator = worldConfig.getCustomDrops().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,Double> pair = (Map.Entry<String,Double>)iterator.next();
            if (rand >= (10000.0 - (pair.getValue() * 100.0))) {
            	//Split the value from the metadata
        		String customDrop[] = pair.getKey().split(",");
        		ItemStack custDropItem = new ItemStack(Material.getMaterial(Integer.parseInt(customDrop[0])), 1);
        		//If we have Metadata, set it.
        		if(customDrop.length > 1) {
            		custDropItem.setDurability(Short.parseShort(customDrop[1]));
        		}
        		block.getWorld().dropItemNaturally(block.getLocation(), custDropItem);
            }
        }
    }

    private void spawnCreature(Block firstBlock) {
    }

    public boolean isGroundConnection(Block block) {
        return isGroundConnection(block, false, new ArrayList<Block>());
    }
    /*public boolean isGroundConnection(Block block, boolean def, List<Block> alreadyChecked) {
        for (Location iterator : this.checkBlocks) {
            Block relative = block.getRelative((int)iterator.getX(), (int)iterator.getY(), (int)iterator.getZ());
            if ((relative.getType() == Material.LEAVES)
                    || (relative.getType() == Material.LOG)
                    || (relative.getType() == Material.SNOW)) {
                if (!alreadyChecked.contains(relative)) {
                    alreadyChecked.add(relative);
                    def = isGroundConnection(relative, def, alreadyChecked);
                }  
            } else if (relative.getType() != Material.AIR) {
                def = true;
            }
        }
        return def;
    }*/
    public boolean isGroundConnection(Block block, boolean def, List<Block> alreadyChecked) {
        if(alreadyChecked.size() >= this.plugin.getWorldConfig(block.getWorld().getName()).getMaxTreeSize()) {
            return true;
        }

        for (Location iterator : this.checkBlocks) {
            Block relative = block.getRelative((int)iterator.getX(), (int)iterator.getY(), (int)iterator.getZ());
            if ((relative.getType() == Material.LEAVES)
                    || (relative.getType() == Material.LOG)
                    || (relative.getType() == Material.SNOW)) {
                if (!alreadyChecked.contains(relative)) {
                    alreadyChecked.add(relative);
                    def = isGroundConnection(relative, def, alreadyChecked);
                }  
            } else if (relative.getType() != Material.AIR) {
                def = true;
            }
        }
        return def;
    }

    public void fasterDecay(Block firstBlock) {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    if (!(x == y && x == z)) {
                        if(firstBlock.getRelative(x, y, z).getType() == Material.LEAVES) {
                            dropLeaveItems(firstBlock.getRelative(x, y, z));
                            firstBlock.getRelative(x, y, z).setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
}