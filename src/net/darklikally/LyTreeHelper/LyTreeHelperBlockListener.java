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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.darklikally.minecraft.utils.SpawnMob;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.iConomy.iConomy;

/**
 * Rewritten on 2011-05-20-12-30-00+01-00
 * @author DarkLiKally
 * @version 2.0
 */
public class LyTreeHelperBlockListener extends BlockListener {
    /**
     * Plugin.
     */
    private LyTreeHelperPlugin plugin;

    private List<Location> checkBlocks = new ArrayList<Location>();

    private HashMap<Integer,List<Block>> checkedBlocks = new HashMap<Integer,List<Block>>();

    private HashMap<Integer,Boolean> hasLeaves = new HashMap<Integer,Boolean>();

    private List<Material> blocksToIgnore = Arrays.asList(
            Material.AIR,
            Material.SAPLING,
            Material.RED_ROSE,
            Material.YELLOW_FLOWER,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.LONG_GRASS,
            Material.DEAD_BUSH);

    private int processCoutner = 0;

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

        pm.registerEvent(Event.Type.LEAVES_DECAY, this, Priority.Highest, plugin);
        pm.registerEvent(Event.Type.BLOCK_BREAK, this, Priority.Highest, plugin);
    }

    private void clearProcess(int processNumber) {
        this.checkedBlocks.remove(processNumber);
        this.hasLeaves.remove(processNumber);
        this.processCoutner--;
    }

    private boolean hasLeaves(int processNumber) {
        return this.hasLeaves.get(processNumber);
    }

    public void destroyTree(Block firstBlock) {
        destroyTree(null, firstBlock);
    }

    public void destroyTree(Player player, Block firstBlock) {
        LyTreeHelperConfiguration worldConfig = this.plugin.getWorldConfig(firstBlock.getWorld().getName());

        this.processCoutner++;
        int processNumber = this.processCoutner;
        this.checkedBlocks.put(processNumber, new ArrayList<Block>());
        this.hasLeaves.put(processNumber, false);
        boolean stdRetVal = true;
        boolean doAutoplantSapling = false;

        if(!worldConfig.isDestroyAll() && worldConfig.isDestroyAllWood()) {
            if(detectTreeWood(firstBlock, stdRetVal, worldConfig, processNumber)) {
                if(!this.hasLeaves(processNumber)) {
                    this.clearProcess(processNumber);
                    return;
                }

                for(Block block : this.checkedBlocks.get(processNumber)) {
                    if(block.getType() == Material.LOG) {
                        block.getWorld().dropItemNaturally(
                                block.getLocation(),
                                new ItemStack(Material.LOG, 1, (short)0,
                                        Byte.valueOf(block.getData())));
                        block.setType(Material.AIR);
                    }
                }

                doAutoplantSapling = true;
            }
        } else if(worldConfig.isDestroyAll() && !worldConfig.isDestroyAllWood()) {
            if(detectTreeWoodLeaves(firstBlock, stdRetVal, worldConfig, processNumber)) {
                if(!this.hasLeaves(processNumber)) {
                    this.clearProcess(processNumber);
                    return;
                }

                for(Block block : this.checkedBlocks.get(processNumber)) {
                    if(block.getType() != Material.SNOW) {
                        this.dropLeaveItems(block);
                    }
                    block.setType(Material.AIR);
                }
            }
        } else if(worldConfig.isDestroyAll() && worldConfig.isDestroyAllWood()) {
            if(detectTreeWoodLeaves(firstBlock, stdRetVal, worldConfig, processNumber)) {
                if(!this.hasLeaves(processNumber)) {
                    this.clearProcess(processNumber);
                    return;
                }
    
                // iConomy stuff
                if(worldConfig.isiConomySupport() && this.plugin.getiConomy() != null) {
                    if(player != null) {
                        com.iConomy.system.Account account = iConomy.getAccount(player.getName());
    
                        if(account != null) {
                            com.iConomy.system.Holdings balance = account.getHoldings();
    
                            if(balance.hasEnough(worldConfig.getiConomyMoneyOnFullDest())) {
                                balance.subtract(worldConfig.getiConomyMoneyOnFullDest());
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "You have not enough money to full destruct this tree.");
                                return;
                            }
                        }
                    }
                }
    
                for(Block block : this.checkedBlocks.get(processNumber)) {
                    if(block.getType() == Material.LOG) {
                        block.getWorld().dropItemNaturally(
                                block.getLocation(),
                                new ItemStack(Material.LOG, 1, (short)0,
                                        Byte.valueOf(block.getData())));
                    } else if(block.getType() != Material.SNOW) {
                        this.dropLeaveItems(block);
                    }

                    block.setType(Material.AIR);
                }

                doAutoplantSapling = true;
            }
        } else { // if !worldConfig.isDestroyAll() and !worldConfig.isDestroyAllWood()
            // Do nothing
        }

        if(doAutoplantSapling) {
            if(worldConfig.isAutoplantSapling()) {
                //TODO Check for the last block before ground block, check if it's type is
                //TODO dirt/grass and start the plantSapling method (this.plantSapling())
                //TODO ONLY PLANT IF THE REALLY ALL BLOCKS OF THE TYPES ARE DESTROYED
            }
        }

        if(worldConfig.getCreaturesToSpawn().size() > 0) {
            this.spawnCreature(firstBlock);
        }

        this.clearProcess(processNumber);
    }

    public boolean detectTreeWoodLeaves(Block currentBlock, boolean retVal, LyTreeHelperConfiguration worldConfig, int processNumber) {
        return detectTreeWoodLeaves(currentBlock, retVal, worldConfig, processNumber, currentBlock);
    }

    public boolean detectTreeWoodLeaves(Block currentBlock, boolean retVal, LyTreeHelperConfiguration worldConfig, int processNumber, Block startBlock) {
        if(this.checkedBlocks.get(processNumber).size() > worldConfig.getMaxTreeSize()) {
            return false;
        }

        for(Location checkBlock : this.checkBlocks) {
            Block relBlock = currentBlock.getRelative(
                    (int)checkBlock.getX(),
                    (int)checkBlock.getY(),
                    (int)checkBlock.getZ());

            if(!this.checkTreeRadius(worldConfig.getMaxTreeRadius(), startBlock, currentBlock)) {
                continue;
            }

            if((relBlock.getType() == Material.LEAVES)
                    || (relBlock.getType() == Material.LOG)
                    || (relBlock.getType() == Material.SNOW)) {
                if(relBlock.getType() == Material.LEAVES) {
                    this.hasLeaves.put(processNumber, true);
                }

                if(!this.checkedBlocks.get(processNumber).contains(relBlock)) {
                    this.checkedBlocksCounter++;
                    this.checkedBlocks.get(processNumber).add(relBlock);

                    retVal = detectTreeWoodLeaves(relBlock, retVal, worldConfig, processNumber, startBlock);
                }
            } else if(!this.blocksToIgnore.contains(relBlock.getType())) {
                retVal = false;
            }
        }

        return retVal;
    }

    public boolean detectTreeWood(Block currentBlock, boolean ret, LyTreeHelperConfiguration worldConfig, int processNumber) {
        return detectTreeWood(currentBlock, ret, worldConfig, processNumber, currentBlock);
    }

    public boolean detectTreeWood(Block currentBlock, boolean retVal, LyTreeHelperConfiguration worldConfig, int processNumber, Block startBlock) {
        if(this.checkedBlocks.get(processNumber).size() > worldConfig.getMaxTreeSize()) {
            return false;
        }

        for(Location checkBlock : this.checkBlocks) {
            Block relBlock = currentBlock.getRelative(
                    (int)checkBlock.getX(),
                    (int)checkBlock.getY(),
                    (int)checkBlock.getZ());

            if(!this.checkTreeRadius(worldConfig.getMaxTreeRadius(), startBlock, relBlock)) {
                continue;
            }

            if((relBlock.getType() == Material.LEAVES)
                    || (relBlock.getType() == Material.SNOW)
                    || (relBlock.getType() == Material.LOG)) {
                if(relBlock.getType() == Material.LEAVES) {
                    this.hasLeaves.put(processNumber, true);
                }

                if(!this.checkedBlocks.get(processNumber).contains(relBlock)) {
                    this.checkedBlocksCounter++;

                    if(relBlock.getType() == Material.LOG) {
                        this.checkedBlocks.get(processNumber).add(relBlock);
                    }

                    retVal = detectTreeWood(relBlock, retVal, worldConfig, processNumber, startBlock);
                }
            }
        }
        return retVal;
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

        boolean destrAllowed = false;

        if(event.getBlock().getType() == Material.LEAVES) {
            if(worldConfig.isDestroyAll()) {
                if(!this.plugin.hasPermission(event.getPlayer(), "destroyall")) {
                    return;
                }

                if(worldConfig.getDestructionTools().size() > 0) {
                    for(int destructionTool : worldConfig.getDestructionTools()) {
                        if(event.getPlayer().getItemInHand().getTypeId() == destructionTool) {
                            this.destroyTree(event.getPlayer(), event.getBlock());
                        }
                    }
                } else {
                    this.destroyTree(event.getPlayer(), event.getBlock());
                }
            } else if(worldConfig.isDestroyFaster()) {
                this.fasterDecay(event.getBlock());
            }

            if(worldConfig.getHarvestTools().size() > 0) {
                for(int harvestTool : worldConfig.getHarvestTools()) {
                    if(event.getPlayer().getItemInHand().getTypeId() == harvestTool) {
                        this.dropLeaveItems(event.getBlock());
                        break;
                    }
                }
            } else {
                this.dropLeaveItems(event.getBlock());
            }
        } else if ((event.getBlock().getType() == Material.LOG)
                && (worldConfig.isDestroyAll())
                && (worldConfig.isDestroyAllWood())) {
            if(!this.plugin.hasPermission(event.getPlayer(), "destroyall")) {
                return;
            }

            if(worldConfig.getDestructionTools().size() > 0) {
                for(int destructionTool : worldConfig.getDestructionTools()) {
                    if(event.getPlayer().getItemInHand().getTypeId() == destructionTool) {
                        destrAllowed = true;
                    }
                }
            } else {
                destrAllowed = true;
            }
        } else if((event.getBlock().getType() == Material.LOG)
                && (worldConfig.isDestroyAllWood())) {
            if(!this.plugin.hasPermission(event.getPlayer(), "destroyall")) {
                return;
            }

            if(worldConfig.getDestructionTools().size() > 0) {
                for(int destructionTool : worldConfig.getDestructionTools()) {
                    if(event.getPlayer().getItemInHand().getTypeId() == destructionTool) {
                        destrAllowed = true;
                    }
                }
            } else {
                destrAllowed = true;
            }
        }

        if(destrAllowed) {
            // event.setCancelled(true);
            ItemStack stack = new ItemStack(Material.LOG, 1, (short)0,
                    Byte.valueOf(event.getBlock().getData()));
            event.getBlock().setType(Material.AIR);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stack);
            this.destroyTree(event.getPlayer(), event.getBlock().getRelative(0, 1, 0));
        }
    }

    private void dropLeaveItems(Block block) {
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
        LyTreeHelperConfiguration worldConfig = this.plugin.getWorldConfig(firstBlock.getWorld().getName());
        Random generator = new Random();
        int rand = generator.nextInt(10000);
        //Is a monster going to spawn?
        if (rand >= (10000.0 - (worldConfig.getCreatureSpawnChance() * 100.0))) {
            Object[] creatures = worldConfig.getCreaturesToSpawn().toArray();
            //Pick the monster to spawn.
            String creature = (String)creatures[generator.nextInt(creatures.length)];
            //System.out.println("Spawning mob: " + creature);
            @SuppressWarnings("unused")
            SpawnMob themob = new SpawnMob(this.plugin.getServer(), firstBlock, creature);
        }
    }

    /**
     * Type has to be 0, 1 or 2: 0 -> usual sapling, 1 -> spruce sapling, 2 -> birch sapling)
     * (NOT !!! data has to be da data of the log block ( block.getData() )
     * Location must be the location of the sapling, NOT the grass/dirt block below it.
     * 
     * @param location
     * @param type
     */
    private void plantSapling(Location location, short type ) {
    //private void plantSapling(Location location, Byte data) {
        Block block = location.getBlock();

        if(block.getRelative(BlockFace.DOWN).getType() == Material.DIRT
                || block.getRelative(BlockFace.DOWN).getType() == Material.GRASS) {
            type = (type < 0 || type > 2) ? 0 : type;

            block.setType(Material.SAPLING);

            Byte saplingData = block.getData();
            switch(type) {
            case 1:
                saplingData = (byte)(saplingData | (1 << 0));
                break;
            case 2:
                saplingData = (byte)(saplingData | (1 << 1));
                break;
            }
            block.setData(saplingData);
            //block.setData(data);
        }
    }
    
    public boolean isGroundConnection(Block block) {
        return isGroundConnection(block, false, new ArrayList<Block>());
    }

    public boolean isGroundConnection(Block block, boolean def, List<Block> alreadyChecked) {
        if(alreadyChecked.size() >= this.plugin.getWorldConfig(block.getWorld().getName()).getMaxTreeSize()) {
            return true;
        }

        for (Location iterator : this.checkBlocks) {
            Block relBlock = block.getRelative((int)iterator.getX(), (int)iterator.getY(), (int)iterator.getZ());
            if ((relBlock.getType() == Material.LEAVES)
                    || (relBlock.getType() == Material.LOG)
                    || (relBlock.getType() == Material.SNOW)) {
                if (!alreadyChecked.contains(relBlock)) {
                    alreadyChecked.add(relBlock);
                    def = isGroundConnection(relBlock, def, alreadyChecked);
                }  
            } else if (!this.blocksToIgnore.contains(relBlock.getType())) {
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