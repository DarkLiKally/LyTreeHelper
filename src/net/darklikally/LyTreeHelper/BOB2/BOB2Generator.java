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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * 
 * @author DarkLiKally
 */
public class BOB2Generator {
    private boolean bailOutput;
    
    public BOB2Generator() {
        this.bailOutput = false;
    }
    
    public BOB2Generator(boolean bailOutput) {
        this.bailOutput = bailOutput;
    }

    public void BailError(String reason) {
        if(this.bailOutput) {
            //plugin.getLogger().log(Level.INFO, reason);
        }
    }

    public void placeObjectInWorld(Block center, BOB2Object object, World world, Random rng) {
        int X = center.getX();
        int Y = center.getY();
        int Z = center.getZ();

        int xrot = 1;
        int zrot = 1;
        if (object.canRandomRotation()) {
            int rot = rng.nextInt() % 4;

            if (rot == 1) {
                xrot = -1;
            }
            if (rot == 2) {
                zrot = -1;
            }
            if (rot == 3) {
                xrot = -1;
                zrot = -1;
            }
        }

        BOB2BlockData[] data = object.getData();

        for (int i = 0; i < data.length; i++) {
            int nX = (X + data[i].x) * xrot;
            @SuppressWarnings("unused")
            int nY = Y + data[i].y;
            int nZ = (Z + data[i].z) * zrot;
            Chunk c = center.getChunk();
            if (!isCoordInChunk(nX, nZ, c)) {
                BailError("Not in chunk");
                return;
            }
            Material mat = data[i].type;
            if (mat == Material.BEDROCK) {
                BailError("Replacing bedrock");
                return;
            }

        }

        for (int i = 0; i < data.length; i++) {
            int nX = (X + data[i].x) * xrot;
            int nY = Y + data[i].y;
            int nZ = (Z + data[i].z) * zrot;

            Material mat = data[i].type;
            int dat = data[i].data;

            Block modify = world.getBlockAt(nX, nY, nZ);
            modify.setTypeId(mat.getId());
            modify.setData((byte) dat);
        }
    }

    public boolean isCoordInChunk(int x, int z, Chunk check) {
        int cX = check.getX() * 16;
        int cZ = check.getZ() * 16;
        int cXmax = cX + 15;
        int cZmax = cZ + 15;

        return (x >= cX) && (cXmax >= x) && (z >= cZ) && (cZmax >= z);
    }

    public ArrayList<Block> getSurfaceBlocks(Chunk source, World world) {
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

    public ArrayList<Block> getOpenBlocks(Chunk source, World world) {
        ArrayList<Block> blocks = new ArrayList<Block>();

        for (int x = source.getX() * 16; x < source.getX() * 16 + 16; x++) {
            for (int z = source.getZ() * 16; z < source.getZ() * 16 + 16; z++) {
                for (int y = world.getHighestBlockYAt(x, z) - 1; y > 0; y--) {
                    Block candidate = world.getBlockAt(x, y, z);
                    if (candidate.getType() != Material.AIR) {
                        Material mat = candidate.getRelative(BlockFace.UP)
                                .getType();

                        if ((mat != Material.AIR) && (mat != Material.WATER)
                                && (mat != Material.LAVA)) {
                            continue;
                        }
                        blocks.add(candidate);
                    }

                }

            }

        }

        return blocks;
    }
}
