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

package net.darklikally.LyTreeHelper.BOB2;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Biome;

/**
 * 
 * @author DarkLiKally
 */
public class BOB2Object {
    private HashMap<String, String> metadata = new HashMap<String, String>();

    private int minX;
    private int minY;
    private int minZ;

    private int maxX;
    private int maxY;
    private int maxZ;

    private BOB2BlockData[] data;

    public BOB2Object() {
        this.metadata.put("name", "Undefined");
        this.metadata.put("version", "");
        
        this.metadata.put("spawnOnBlockType", "2");
        this.metadata.put("spawnSunlight", "true");
        this.metadata.put("spawnElevationMax", "128");
        this.metadata.put("spawnDarkness", "false");
        this.metadata.put("spawnWater", "false");
        this.metadata.put("spawnLava", "false");
        
        this.metadata.put("underFill", "true");
        this.metadata.put("dig", "true");
        this.metadata.put("rarity", "10");
        this.metadata.put("collisionPercentage", "2");
        
        this.metadata.put("spawnElevationMin", "0");
        this.metadata.put("spawnElevationMax", "128");
        
        this.metadata.put("randomRotation", "true");
        
        this.metadata.put("groupID", "");
        
        this.metadata.put("tree", "false");
        this.metadata.put("branch", "false");
        this.metadata.put("diggingBranch", "false");
        
        this.metadata.put("groupFrequencyMin", "1");
        this.metadata.put("groupFrequencyMax", "5");
        this.metadata.put("groupSeperationMin", "0");
        this.metadata.put("groupSeperationMax", "5");
        
        this.metadata.put("spawnInBiome", "All");
    }

    public String getVersion() {
        return (String) this.metadata.get("version");
    }

    public int getSpawnOnBlockType() {
        return Integer.parseInt((String) this.metadata.get("spawnOnBlockType"));
    }

    public boolean canSpawnOnBlock(int blockID) {
        boolean rv = ((String) this.metadata.get("spawnOnBlockType"))
                .contains(Integer.toString(blockID));
        return rv;
    }

    public boolean canSpawnSunlight() {
        return Boolean.valueOf((String) this.metadata.get("spawnSunlight"))
                .booleanValue();
    }

    public boolean canSpawnDarkness() {
        return Boolean.valueOf((String) this.metadata.get("spawnDarkness"))
                .booleanValue();
    }

    public boolean canSpawnWater() {
        return Boolean.valueOf((String) this.metadata.get("spawnWater"))
                .booleanValue();
    }

    public boolean canSpawnLava() {
        return Boolean.valueOf((String) this.metadata.get("spawnLava"))
                .booleanValue();
    }

    public boolean shouldUnderFill() {
        return Boolean.valueOf((String) this.metadata.get("underFill"))
                .booleanValue();
    }

    public boolean canDig() {
        return Boolean.valueOf((String) this.metadata.get("dig"))
                .booleanValue();
    }

    public int getRarity() {
        return Integer.parseInt((String) this.metadata.get("rarity"));
    }

    public int getCollisionPercentage() {
        return Integer.parseInt((String) this.metadata
                .get("collisionPercentage"));
    }

    public int getSpawnElevationMin() {
        return Integer
                .parseInt((String) this.metadata.get("spawnElevationMin"));
    }

    public int getSpawnElevationMax() {
        return Integer
                .parseInt((String) this.metadata.get("spawnElevationMax"));
    }

    public boolean canRandomRotation() {
        return Boolean.valueOf((String) this.metadata.get("randomRotation"))
                .booleanValue();
    }

    public String getGroupID() {
        return (String) this.metadata.get("groupID");
    }

    public boolean isTree() {
        return Boolean.valueOf((String) this.metadata.get("tree"))
                .booleanValue();
    }

    public boolean isBranch() {
        return Boolean.valueOf((String) this.metadata.get("branch"))
                .booleanValue();
    }

    public boolean isDiggingBranch() {
        return Boolean.valueOf((String) this.metadata.get("diggingBranch"))
                .booleanValue();
    }

    public int getGroupFrequencyMin() {
        return Integer
                .parseInt((String) this.metadata.get("groupFrequencyMin"));
    }

    public int getGroupFrequencyMax() {
        return Integer
                .parseInt((String) this.metadata.get("groupFrequencyMax"));
    }

    public int getGroupSeperationMin() {
        return Integer.parseInt((String) this.metadata
                .get("groupSeperationMin"));
    }

    public int getGroupSeperationMax() {
        return Integer.parseInt((String) this.metadata
                .get("groupSeperationMax"));
    }

    public String[] getSpawnInBiome() {
        return ((String) this.metadata.get("spawnInBiome")).split(",");
    }

    public boolean canSpawnInBiome(Biome biome) {
        if (((String) this.metadata.get("spawnInBiome"))
                .equalsIgnoreCase("all"))
            return true;

        String[] biomes = getSpawnInBiome();

        for (int i = 0; i < biomes.length; i++) {
            if (biomes[i].equalsIgnoreCase(biome.toString()))
                return true;
        }
        return false;
    }

    public BOB2BlockData[] getData() {
        return this.data;
    }

    public String getName() {
        return (String) this.metadata.get("name");
    }

    public void ParseMetadata(String[] data) {
        for (int i = 0; i < data.length; i++) {
            String[] kv = data[i].toLowerCase().split("=");
            this.metadata.put(kv[0].trim().toLowerCase(), kv[1].trim()
                    .toLowerCase());
        }
    }

    public void ParseBlockdata(String[] data) {
        ArrayList<BOB2BlockData> dat = new ArrayList<BOB2BlockData>();
        for (int i = 0; i < data.length; i++) {
            String loc = data[i].toLowerCase();

            int locToken = loc.indexOf(':');
            String pos = loc.substring(0, locToken);
            String[] poses = pos.split(",");
            int X = Integer.parseInt(poses[0]);
            if (this.minX > X) {
                this.minX = X;
            }
            if (this.maxX < X) {
                this.maxX = X;
            }
            int Z = Integer.parseInt(poses[1]);
            if (this.minZ > Z) {
                this.minZ = Z;
            }
            if (this.maxZ < Z) {
                this.maxZ = Z;
            }
            int Y = Integer.parseInt(poses[2]);
            if (this.minY > Y) {
                this.minY = Y;
            }
            if (this.maxY < Y) {
                this.maxY = Y;
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

            BOB2BlockData block = new BOB2BlockData();
            block.x = X;
            block.y = Y;
            block.z = Z;
            block.type = mat;
            block.data = BlockData;
            dat.add(block);
        }

        this.data = ((BOB2BlockData[]) dat.toArray(new BOB2BlockData[1]));
    }

    public HashMap<String, String> getMetadata() {
        return this.metadata;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMinZ() {
        return this.minZ;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMaxY() {
        return this.maxY;
    }

    public int getMaxZ() {
        return this.maxZ;
    }
}