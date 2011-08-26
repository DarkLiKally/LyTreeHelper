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
import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

/**
 * 
 * @author DarkLiKally
 */
public class BOB2Populator extends BlockPopulator {
    final int ChanceForTree = 20;
    final int ChanceForObject = 5;
    final boolean BailOutput = false;

    public void populate(World world, Random rng, Chunk source) {
        ArrayList<Block> blocks = getSurfaceBlocks(source, world);
        ArrayList<BOB2Object> trees = BOB2Manager.getTrees();

        for (int i = 0; i < blocks.size(); i++) {
            int roll = rng.nextInt(100);
            if (roll > 20) {
                continue;
            }
            for (int t = 0; t < trees.size(); t++) {
                Block b = (Block) blocks.get(i);
                BOB2Object obj = (BOB2Object) trees.get(t);

                if (!obj.canSpawnInBiome(b.getBiome())) {
                    BailError("wrong biome");
                } else if (!obj.canSpawnOnBlock(b.getTypeId())) {
                    BailError("wrong type");
                } else {
                    roll = rng.nextInt(100);
                    int ChanceToSpawn = obj.getRarity();
                    if (roll > ChanceToSpawn) {
                        BailError("fate decides no");
                    } else if (b.getY() < obj.getSpawnElevationMin()) {
                        BailError("Too low");
                    } else if (b.getY() >= obj.getSpawnElevationMax()) {
                        BailError("Too High");
                    } else if (b.getY() + obj.getMaxY() >= 127) {
                        BailError("Over the top");
                    } else if (b.getY() + obj.getMinY() <= 0) {
                        BailError("Out of the bottom");
                    } else {
                        BOB2Generator gen = new BOB2Generator();
                        gen.placeObjectInWorld(b, obj, world, rng);

                        break;
                    }
                }
            }
        }
        
        /*ArrayList<BOB2Object> objects = BOB2Manager.getNotTrees();

        if (objects.size() == 0)
            return;
        for (int i = 0; i < blocks.size(); i++) {
            int roll = rng.nextInt(100);
            if (roll > 5) {
                continue;
            }
            for (int t = 0; t < objects.size(); t++) {
                Block b = (Block) blocks.get(i);
                BOB2Object obj = (BOB2Object) objects.get(t);
                if (!obj.canSpawnInBiome(b.getBiome())) {
                    BailError("wrong biome");
                } else if (!obj.canSpawnOnBlock(b.getTypeId())) {
                    BailError("wrong type");
                } else if (b.getY() < obj.getSpawnElevationMin()) {
                    BailError("Too low");
                } else if (b.getY() >= obj.getSpawnElevationMax()) {
                    BailError("Too High");
                } else if (b.getY() + obj.getMaxY() >= 127) {
                    BailError("Over the top");
                } else if (b.getY() + obj.getMinY() <= 0) {
                    BailError("Out of the bottom");
                } else if ((b.getLightLevel() < 4) && (!obj.canSpawnDarkness())) {
                    BailError("Too Dark");
                } else if ((b.getLightLevel() >= 4)
                        && (!obj.canSpawnSunlight())) {
                    BailError("Too Light");
                } else {
                    roll = rng.nextInt(100);
                    int ChanceToSpawn = obj.getRarity();
                    if (roll > ChanceToSpawn) {
                        BailError("fate decides no");
                    } else {
                        placeObjectInWorld(b, obj, world, rng);

                        break;
                    }
                }
            }
        }*/
    }

    private void BailError(String reason) {
    }

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
