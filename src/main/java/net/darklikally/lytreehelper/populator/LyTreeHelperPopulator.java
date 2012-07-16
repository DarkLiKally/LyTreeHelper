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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;

import net.darklikally.lytreehelper.bukkit.ConfigurationManager;
import net.darklikally.lytreehelper.bukkit.LyTreeHelperPlugin;
import net.darklikally.lytreehelper.bukkit.WorldConfiguration;
import net.darklikally.lytreehelper.schematic.CuboidObject;
import net.darklikally.lytreehelper.schematic.MCSchematic;
import net.darklikally.lytreehelper.schematic.SchematicInformation;
import net.darklikally.lytreehelper.schematic.bo2.Bo2Manager;
import net.darklikally.lytreehelper.schematic.bo2.Bo2Object;

/**
 * This is LyTreeHelper's custom world populator
 * 
 * @author DarkLiKally
 * 
 */
public class LyTreeHelperPopulator extends BlockPopulator {
    
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
        Material.LEAVES);

    private final LyTreeHelperPlugin plugin;

    public LyTreeHelperPopulator(LyTreeHelperPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize the custom populator
     */
    public void initialize() {
        ConfigurationManager config = plugin.getGlobalConfigurationManager();

        for (World world : plugin.getServer().getWorlds()) {
            if (config.getWorldConfig(world).enableCustomPopulator) {
                registerPopulator(world);
            }
        }
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

    public boolean hasPopulator(World world) {
        return world.getPopulators().contains(this);
    }

    /**
     * Populate a chunk
     */
    public void populate(World world, Random rng, Chunk source) {
        // We need only the surface blocks to grow our trees
        ArrayList<Block> blocks = getSurfaceBlocks(source, world);

        WorldConfiguration wconfig = plugin.getGlobalConfigurationManager()
                .getWorldConfig(world);

        // Choose a random block
        Random rand = new Random();
        Block block = blocks.get(rand.nextInt(blocks.size()));
        Biome biome = block.getBiome();
        Material blockType = block.getType();
        
        SchematicInformation schematicInfo = selectSchematic(wconfig, block);
        
        if(schematicInfo == null) {
            // We have no schematic selected
            return;
        }

        // Get the schematic file
        File schematicFile = new File(new File(plugin.getDataFolder(), "schematics"), schematicInfo.file);
        
        if(!schematicFile.exists()) {
            // The schematic file does not exist
            return;
        }
        
        // Load the schematic file into a cuboid object
        CuboidObject object = null;
        Bo2Object bo2Object = null;
        if(schematicInfo.type.equalsIgnoreCase("mcedit")) {
            try {
                object = MCSchematic.getFormat("mcedit").load(schematicFile);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if(schematicInfo.offset != null) {
                object.setOffset(schematicInfo.offset);
            }
            
            if(!schematicInfo.allBlockTypes) {
                if(schematicInfo.spawnOnBlockTypes.size() != 0
                        && !schematicInfo.spawnOnBlockTypes.contains(block.getTypeId())) {
                    return;
                }
            }
            
        } else if(schematicInfo.type.equalsIgnoreCase("bo2")) {
            bo2Object = Bo2Manager.getObjectFromFile(schematicFile);

            if(!bo2Object.isTree()) {
                // Our object isn't a tree, but we have a tree/forest plugin...
                
                // A little clean-up
                Bo2Manager.forgetObject(schematicFile);
                
                return;
            }
            
            if(!bo2Object.canSpawnInBiome(biome)
                    || !bo2Object.canSpawnOnBlock(blockType)
                    || block.getY() < bo2Object.getSpawnElevationMin()
                    || block.getY() > bo2Object.getSpawnElevationMax()
                    || (block.getY() + bo2Object.getSize().getBlockY()) < 0
                    || (block.getY() + bo2Object.getSize().getBlockY()) > world.getMaxHeight()) {
                // One or more bo2 object checks failed, we should cancel the population process
                return;
            }
            
            try {
                object = MCSchematic.getFormat("bo2").load(schematicFile);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Maybe rotate the object if it's allowed
        boolean rotationAllowed = true;
        
        if(schematicInfo.type.equalsIgnoreCase("bo2")) {
            rotationAllowed = bo2Object.canRandomRotation();
        }
        
        if(rotationAllowed) {
            int rotationTimes = rand.nextInt(3);
            
            switch(rotationTimes) {
            case 0:
                object.rotate2D(90);
                break;
            case 1:
                object.rotate2D(180);
                break;
            case 2:
                object.rotate2D(270);
                break;
            }
        }
        
        // Check if we have enough space for the object to be placed
        Vector offset = object.getOffset();
        
        Vector minBlock = new Vector(
                block.getX() - offset.getBlockX(),
                block.getY() - offset.getBlockY(),
                block.getZ() - offset.getBlockZ()
                );
        Vector maxBlock = new Vector(
                block.getX() + object.getWidth() - offset.getBlockX(),
                block.getY() + object.getHeight() - offset.getBlockY(),
                block.getZ() + object.getLength() - offset.getBlockZ()
                );
        
        if(!schematicInfo.forceSpawn) {
            // If we won't force the object to spawn, check if we have enough space for it
            for(int x = minBlock.getBlockX(); x <= maxBlock.getBlockX(); x++) {
                for(int y = minBlock.getBlockY() + 1; y <= maxBlock.getBlockY(); y++) {
                    for(int z = minBlock.getBlockZ(); z <= maxBlock.getBlockZ(); z++) {
                        Block cBlock = world.getBlockAt(new Location(world, x, y, z));
                        
                        if(!blocksToIgnore.contains(cBlock.getType())) {
                            // We have not enough space for our object
                            if(schematicInfo.type.equalsIgnoreCase("bo2")) {
                                // A bo2 object, so check if it can dig into the ground
                                if(!bo2Object.canDig() && y < block.getY()) {
                                    continue;
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
        
        // Can the object dig into the ground? e.g. for branches
        // This is only for bo2 objects
        if(schematicInfo.type.equalsIgnoreCase("bo2")) {
            if(!bo2Object.canDig() && offset.getBlockY() < 0) {
                return;
            }
        }
        
        
        // Finally, we have all checks done, it's time to place the object! 
        object.place(block.getRelative(BlockFace.UP).getLocation(), true);
    }

    private SchematicInformation selectSchematic(WorldConfiguration wconfig, Block block) {
        if (wconfig.schematics != null && wconfig.schematics.size() != 0) {
            Random rand = new Random();

            SchematicInformation[] schematics = wconfig.schematics.toArray(new SchematicInformation[1]);
            for(int i = 0; i < schematics.length; i++) {
                SchematicInformation schematicInfo = schematics[i];
                
                if(schematicInfo.biomes.contains(block.getBiome())) {
                    if (rand.nextDouble() * 100 <= schematicInfo.chance) {
                        return schematicInfo;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Receive error messages from the populator and handle them.
     * 
     * @param reason
     */
    @SuppressWarnings("unused")
    private void BailError(String reason) {
        // plugin.getLogger().severe(reason);
    }

    /**
     * Checks whether the coordinates of the location are inside the chunk
     * 
     * @see isCoordInChunk(intx, int z, Chunk check)
     * 
     * @param loc
     * @param check
     * @return
     */
    @SuppressWarnings("unused")
    private boolean isCoordInChunk(Location loc, Chunk check) {
        return isCoordInChunk(loc.getBlockX(), loc.getBlockZ(), check);
    }

    /**
     * Checks whether the coordinates are inside the chunk
     * 
     * @param x
     * @param z
     * @param check
     * @return
     */
    private boolean isCoordInChunk(int x, int z, Chunk check) {
        int cX = check.getX() * 16;
        int cZ = check.getZ() * 16;
        int cXmax = cX + 15;
        int cZmax = cZ + 15;

        return (x >= cX) && (cXmax >= x) && (z >= cZ) && (cZmax >= z);
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
