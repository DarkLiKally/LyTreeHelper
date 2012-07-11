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
package net.darklikally.lytreehelper.schematic.bo2;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.util.Vector;

/**
 * 
 * @author DarkLiKally
 */
public class Bo2Object {
    /**
     * Contains all the metadata delivered with the bo2 file
     */
    private HashMap<String, String> metadata = new HashMap<String, String>();

    /*
     * The minimum block of the cuboid area containing the object. Mainly used
     * to calculate the size of the area.
     */
    private int minX;
    private int minY;
    private int minZ;

    /*
     * The maximum block of the cuboid area containing the object. Mainly used
     * to calculate the size of the area.
     */
    private int maxX;
    private int maxY;
    private int maxZ;

    /**
     * This array holds all the block informations for all blocks of the bo2
     * object.
     */
    private Bo2BlockData[] data;

    /**
     * Default constructor Construct a basic bo2 object with some default
     * values.
     */
    public Bo2Object() {
        metadata.put("name", "Undefined");
        metadata.put("version", "");

        metadata.put("spawnOnBlockType", "2");
        metadata.put("spawnSunlight", "true");
        metadata.put("spawnElevationMax", "128");
        metadata.put("spawnDarkness", "false");
        metadata.put("spawnWater", "false");
        metadata.put("spawnLava", "false");

        metadata.put("underFill", "true");
        metadata.put("dig", "true");
        metadata.put("rarity", "10");
        metadata.put("collisionPercentage", "2");

        metadata.put("spawnElevationMin", "0");
        metadata.put("spawnElevationMax", "128");

        metadata.put("randomRotation", "true");

        metadata.put("groupID", "");

        metadata.put("tree", "false");
        metadata.put("branch", "false");
        metadata.put("diggingBranch", "false");

        metadata.put("groupFrequencyMin", "1");
        metadata.put("groupFrequencyMax", "5");
        metadata.put("groupSeperationMin", "0");
        metadata.put("groupSeperationMax", "5");

        metadata.put("spawnInBiome", "All");
    }

    /**
     * Returns the version of the bo2 object.
     * 
     * @return
     */
    public String getVersion() {
        return (String) metadata.get("version");
    }

    /**
     * Get the id of the block on which the object can spawn.
     * 
     * @return
     */
    public int getSpawnOnBlockType() {
        return Integer.parseInt((String) metadata.get("spawnOnBlockType"));
    }

    /**
     * Returns whether the object can spawn on top of the given Material.
     * 
     * @param blockType
     * @return
     */
    public boolean canSpawnOnBlock(Material blockType) {
        boolean rv = ((String) metadata.get("spawnOnBlockType"))
                .contains(Integer.toString(blockType.getId()));
        return rv;
    }

    /**
     * Returns whether the object can spawn in sunlight or not.
     * 
     * @return
     */
    public boolean canSpawnSunlight() {
        return Boolean.valueOf((String) metadata.get("spawnSunlight"))
                .booleanValue();
    }

    /**
     * Returns whether the object can spawn in darkness or not.
     * 
     * @return
     */
    public boolean canSpawnDarkness() {
        return Boolean.valueOf((String) metadata.get("spawnDarkness"))
                .booleanValue();
    }

    /**
     * Returns whether the object can spawn in water or not.
     * 
     * @return
     */
    public boolean canSpawnWater() {
        return Boolean.valueOf((String) metadata.get("spawnWater"))
                .booleanValue();
    }

    /**
     * Returns whether the object can spawn in lava or not.
     * 
     * @return
     */
    public boolean canSpawnLava() {
        return Boolean.valueOf((String) metadata.get("spawnLava"))
                .booleanValue();
    }

    /**
     * Returns whether the object should get under filled with some material.
     * 
     * @return
     */
    public boolean shouldUnderFill() {
        return Boolean.valueOf((String) metadata.get("underFill"))
                .booleanValue();
    }

    public boolean canDig() {
        return Boolean.valueOf((String) metadata.get("dig")).booleanValue();
    }

    /**
     * Returns the rarity of the bo2 object.
     * 
     * @return
     */
    public int getRarity() {
        return Integer.parseInt((String) metadata.get("rarity"));
    }

    public int getCollisionPercentage() {
        return Integer.parseInt((String) metadata.get("collisionPercentage"));
    }

    /**
     * Returns the minimum elevation of the object which it should have when
     * spawning.
     * 
     * @return
     */
    public int getSpawnElevationMin() {
        return Integer.parseInt((String) metadata.get("spawnElevationMin"));
    }

    /**
     * Returns the maximum elevation of the object which it should have when
     * spawning.
     * 
     * @return
     */
    public int getSpawnElevationMax() {
        return Integer.parseInt((String) metadata.get("spawnElevationMax"));
    }

    /**
     * Returns whether the object can randomly rotated before spawning.
     * 
     * @return
     */
    public boolean canRandomRotation() {
        return Boolean.valueOf((String) metadata.get("randomRotation"))
                .booleanValue();
    }

    /**
     * Get the group id of the group which contains the object.
     * 
     * @return
     */
    public String getGroupID() {
        return (String) metadata.get("groupID");
    }

    /**
     * Returns whether the object is a tree or not.
     * 
     * @return
     */
    public boolean isTree() {
        return Boolean.valueOf((String) metadata.get("tree")).booleanValue();
    }

    /**
     * Return whether the object is a branch or not.
     * 
     * @return
     */
    public boolean isBranch() {
        return Boolean.valueOf((String) metadata.get("branch")).booleanValue();
    }

    /**
     * Returns whether the object's branches can dig into the earth.
     * 
     * @return
     */
    public boolean isDiggingBranch() {
        return Boolean.valueOf((String) metadata.get("diggingBranch"))
                .booleanValue();
    }

    /**
     * Returns the minimum frequency in which the object occurs in a group.
     * 
     * @return
     */
    public int getGroupFrequencyMin() {
        return Integer.parseInt((String) metadata.get("groupFrequencyMin"));
    }

    /**
     * Returns the maximum frequency in which the object occurs in a group.
     * 
     * @return
     */
    public int getGroupFrequencyMax() {
        return Integer.parseInt((String) metadata.get("groupFrequencyMax"));
    }

    public int getGroupSeperationMin() {
        return Integer.parseInt((String) metadata.get("groupSeperationMin"));
    }

    public int getGroupSeperationMax() {
        return Integer.parseInt((String) metadata.get("groupSeperationMax"));
    }

    /**
     * Returns the names of the biomes in which the object can spawn.
     * 
     * @return
     */
    public String[] getSpawnInBiome() {
        return ((String) metadata.get("spawnInBiome")).split(",");
    }

    /**
     * Checks whether the object can spawn in the requested biome or not.
     * 
     * @param biome
     * @return
     */
    public boolean canSpawnInBiome(Biome biome) {
        if (((String) metadata.get("spawnInBiome")).equalsIgnoreCase("all"))
            return true;

        String[] biomes = getSpawnInBiome();

        for (int i = 0; i < biomes.length; i++) {
            if (biomes[i].equalsIgnoreCase(biome.toString()))
                return true;
        }
        return false;
    }

    /**
     * Get all the block data.
     * 
     * @return
     */
    public Bo2BlockData[] getData() {
        return data;
    }

    /**
     * Get the name of the bo2 object
     * 
     * @return
     */
    public String getName() {
        return (String) metadata.get("name");
    }

    /**
     * Parses the meta data of the object from an string array.
     * 
     * @param data
     */
    public void ParseMetadata(String[] data) {
        for (int i = 0; i < data.length; i++) {
            String[] kv = data[i].toLowerCase().split("=");
            metadata.put(kv[0].trim().toLowerCase(), kv[1].trim().toLowerCase());
        }
    }

    /**
     * Parses the block data of the object from an string array.
     * 
     * @param source
     */
    public void ParseBlockdata(String[] source) {
        ArrayList<Bo2BlockData> parsedData = new ArrayList<Bo2BlockData>();
        for (int i = 0; i < source.length; i++) {
            String loc = source[i].toLowerCase();

            int locToken = loc.indexOf(':');
            String position = loc.substring(0, locToken);
            String[] coords = position.split(",");
            int X = Integer.parseInt(coords[0]);
            if (minX > X) {
                minX = X;
            }
            if (maxX < X) {
                maxX = X;
            }
            int Z = Integer.parseInt(coords[1]);
            if (minZ > Z) {
                minZ = Z;
            }
            if (maxZ < Z) {
                maxZ = Z;
            }
            int Y = Integer.parseInt(coords[2]);
            if (minY > Y) {
                minY = Y;
            }
            if (maxY < Y) {
                maxY = Y;
            }

            int MatID = 0;

            if (loc.contains(".")) {
                MatID = Integer.parseInt(loc.substring(locToken + 1,
                        loc.indexOf('.')));
            } else {
                MatID = Integer.parseInt(loc.substring(locToken + 1));
            }
            Material mat = Material.getMaterial(MatID);

            int BlockData = 0;
            if (loc.contains(".")) {
                if (loc.contains("#")) {
                    BlockData = Integer.parseInt(loc.substring(
                            loc.indexOf('.') + 1, loc.indexOf('#')));
                } else {
                    BlockData = Integer
                            .parseInt(loc.substring(loc.indexOf('.') + 1));
                }

            }

            Bo2BlockData block = new Bo2BlockData();
            block.x = X;
            block.y = Y;
            block.z = Z;
            block.type = mat;
            block.data = BlockData;
            parsedData.add(block);
        }

        data = ((Bo2BlockData[]) parsedData.toArray(new Bo2BlockData[1]));
    }

    /**
     * Returns all the metadata of the object.
     * 
     * @return
     */
    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    /**
     * Returns the size of the object (cuboid area).
     * 
     * @return
     */
    public Vector getSize() {
        return new Vector(maxX - minX, maxY - minY, maxZ - minZ);
    }
}