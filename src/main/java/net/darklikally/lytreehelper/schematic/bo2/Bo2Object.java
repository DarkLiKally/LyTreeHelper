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
    private HashMap<String, String> metadata = new HashMap<String, String>();

    private int minX;
    private int minY;
    private int minZ;

    private int maxX;
    private int maxY;
    private int maxZ;

    private Bo2BlockData[] data;

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

    public String getVersion() {
        return (String) metadata.get("version");
    }

    public int getSpawnOnBlockType() {
        return Integer.parseInt((String) metadata.get("spawnOnBlockType"));
    }

    public boolean canSpawnOnBlock(int blockID) {
        boolean rv = ((String) metadata.get("spawnOnBlockType"))
                .contains(Integer.toString(blockID));
        return rv;
    }

    public boolean canSpawnSunlight() {
        return Boolean.valueOf((String) metadata.get("spawnSunlight"))
                .booleanValue();
    }

    public boolean canSpawnDarkness() {
        return Boolean.valueOf((String) metadata.get("spawnDarkness"))
                .booleanValue();
    }

    public boolean canSpawnWater() {
        return Boolean.valueOf((String) metadata.get("spawnWater"))
                .booleanValue();
    }

    public boolean canSpawnLava() {
        return Boolean.valueOf((String) metadata.get("spawnLava"))
                .booleanValue();
    }

    public boolean shouldUnderFill() {
        return Boolean.valueOf((String) metadata.get("underFill"))
                .booleanValue();
    }

    public boolean canDig() {
        return Boolean.valueOf((String) metadata.get("dig")).booleanValue();
    }

    public int getRarity() {
        return Integer.parseInt((String) metadata.get("rarity"));
    }

    public int getCollisionPercentage() {
        return Integer.parseInt((String) metadata.get("collisionPercentage"));
    }

    public int getSpawnElevationMin() {
        return Integer.parseInt((String) metadata.get("spawnElevationMin"));
    }

    public int getSpawnElevationMax() {
        return Integer.parseInt((String) metadata.get("spawnElevationMax"));
    }

    public boolean canRandomRotation() {
        return Boolean.valueOf((String) metadata.get("randomRotation"))
                .booleanValue();
    }

    public String getGroupID() {
        return (String) metadata.get("groupID");
    }

    public boolean isTree() {
        return Boolean.valueOf((String) metadata.get("tree")).booleanValue();
    }

    public boolean isBranch() {
        return Boolean.valueOf((String) metadata.get("branch")).booleanValue();
    }

    public boolean isDiggingBranch() {
        return Boolean.valueOf((String) metadata.get("diggingBranch"))
                .booleanValue();
    }

    public int getGroupFrequencyMin() {
        return Integer.parseInt((String) metadata.get("groupFrequencyMin"));
    }

    public int getGroupFrequencyMax() {
        return Integer.parseInt((String) metadata.get("groupFrequencyMax"));
    }

    public int getGroupSeperationMin() {
        return Integer.parseInt((String) metadata.get("groupSeperationMin"));
    }

    public int getGroupSeperationMax() {
        return Integer.parseInt((String) metadata.get("groupSeperationMax"));
    }

    public String[] getSpawnInBiome() {
        return ((String) metadata.get("spawnInBiome")).split(",");
    }

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

    public Bo2BlockData[] getData() {
        return data;
    }

    public String getName() {
        return (String) metadata.get("name");
    }

    public void ParseMetadata(String[] data) {
        for (int i = 0; i < data.length; i++) {
            String[] kv = data[i].toLowerCase().split("=");
            metadata.put(kv[0].trim().toLowerCase(), kv[1].trim().toLowerCase());
        }
    }

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

    public Vector getSize() {
        return new Vector(maxX - minX, maxY - minY, maxZ - minZ);
    }
}