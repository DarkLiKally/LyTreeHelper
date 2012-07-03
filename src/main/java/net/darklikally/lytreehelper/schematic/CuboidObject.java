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
package net.darklikally.lytreehelper.schematic;

import net.darklikally.lytreehelper.blocks.BaseBlock;
import net.darklikally.lytreehelper.blocks.BlockUtils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * 
 * @author DarkLiKally
 * @author sk89q
 * 
 */
public class CuboidObject {
    /**
     * Flip direction.
     */
    public enum FlipDirection {
        NORTH_SOUTH, WEST_EAST, UP_DOWN
    }

    private BaseBlock[][][] data;
    private Vector offset;
    private Vector size;

    /**
     * Constructs the cuboid object
     * 
     * @param size
     */
    public CuboidObject(Vector size) {
        this.size = size;
        data = new BaseBlock[size.getBlockX()][size.getBlockY()][size
                .getBlockZ()];
        offset = new Vector();
    }

    /**
     * Constructs the cuboid object
     * 
     * @param size
     * @param offset
     */
    public CuboidObject(Vector size, Vector offset) {
        this.size = size;
        data = new BaseBlock[size.getBlockX()][size.getBlockY()][size
                .getBlockZ()];
        this.offset = offset;
    }

    /**
     * Get the width (X-direction) of the cuboid object.
     * 
     * @return width
     */
    public int getWidth() {
        return size.getBlockX();
    }

    /**
     * Get the height (Y-direction) of the cuboid object.
     * 
     * @return height
     */
    public int getHeight() {
        return size.getBlockY();
    }

    /**
     * Get the length (Z-direction) of the cuboid object.
     * 
     * @return length
     */
    public int getLength() {
        return size.getBlockZ();
    }
    
    /**
     * Get one point in the cuboid object.
     *
     * @param pos
     * @return null
     * @throws ArrayIndexOutOfBoundsException
     */
    public BaseBlock getPoint(Vector pos) throws ArrayIndexOutOfBoundsException {
        return data[pos.getBlockX()][pos.getBlockY()][pos.getBlockZ()];
    }

    /**
     * Set one point in the cuboid object.
     *
     * @param pos
     * @return null
     * @throws ArrayIndexOutOfBoundsException
     */
    public void setBlock(Vector pt, BaseBlock block) {
        data[pt.getBlockX()][pt.getBlockY()][pt.getBlockZ()] = block;
    }

    /**
     * Get the size of the cuboid object.
     *
     * @return
     */
    public Vector getSize() {
        return size;
    }

    /**
     * Get the offset of the cuboid object.
     * 
     * @return the offset
     */
    public Vector getOffset() {
        return offset;
    }

    /**
     * Set the offset of the cuboid object
     * 
     * @param offset
     */
    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    /**
     * Rotate the cuboid object in 2D. It can only rotate by angles divisible by
     * 90.
     * 
     * @param angle
     *            in degrees
     */
    public void rotate2D(int angle) {
        angle = angle % 360;
        if (angle % 90 != 0) { // Can only rotate 90 degrees at the moment
            return;
        }
        boolean reverse = angle < 0;
        int numRotations = Math.abs((int) Math.floor(angle / 90.0));

        int width = getWidth();
        int length = getLength();
        int height = getHeight();

        net.darklikally.lytreehelper.blocks.Vector sizeEdit = new net.darklikally.lytreehelper.blocks.Vector(
                size.getBlockX(), size.getBlockY(), size.getBlockZ());
        net.darklikally.lytreehelper.blocks.Vector sizeRotated = sizeEdit
                .transform2D(angle, 0, 0, 0, 0);

        int shiftX = sizeRotated.getX() < 0 ? -sizeRotated.getBlockX() - 1 : 0;
        int shiftZ = sizeRotated.getZ() < 0 ? -sizeRotated.getBlockZ() - 1 : 0;

        BaseBlock newData[][][] = new BaseBlock[Math.abs(sizeRotated
                .getBlockX())][Math.abs(sizeRotated.getBlockY())][Math
                .abs(sizeRotated.getBlockZ())];

        for (int x = 0; x < width; ++x) {
            for (int z = 0; z < length; ++z) {
                net.darklikally.lytreehelper.blocks.Vector v = 
                    (new net.darklikally.lytreehelper.blocks.Vector(x, 0, z)).transform2D(angle, 0, 0, 0, 0);
                int newX = v.getBlockX();
                int newZ = v.getBlockZ();
                for (int y = 0; y < height; ++y) {
                    BaseBlock block = data[x][y][z];
                    newData[shiftX + newX][y][shiftZ + newZ] = block;

                    if (reverse) {
                        for (int i = 0; i < numRotations; ++i) {
                            block.rotate90Reverse();
                        }
                    } else {
                        for (int i = 0; i < numRotations; ++i) {
                            block.rotate90();
                        }
                    }
                }
            }
        }

        data = newData;
        size = new Vector(Math.abs(sizeRotated.getBlockX()),
                Math.abs(sizeRotated.getBlockY()), Math.abs(sizeRotated
                        .getBlockZ()));
        
        net.darklikally.lytreehelper.blocks.Vector offsetEdit =
            new net.darklikally.lytreehelper.blocks.Vector(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
        
        offset = offsetEdit.transform2D(angle, 0, 0, 0, 0).subtract(shiftX, 0, shiftZ).toBukkitVector();
    }

    /**
     * Flip the cuboid object.
     * 
     * @param dir
     *            direction to flip
     */
    public void flip(FlipDirection dir) {
        final int width = getWidth();
        final int length = getLength();
        final int height = getHeight();

        switch (dir) {
        case NORTH_SOUTH:
            final int wid = (int) Math.ceil(width / 2.0f);
            for (int xs = 0; xs < wid; ++xs) {
                for (int z = 0; z < length; ++z) {
                    for (int y = 0; y < height; ++y) {
                        BaseBlock old = data[xs][y][z].flip(dir);
                        if (xs == width - xs - 1)
                            continue;
                        data[xs][y][z] = data[width - xs - 1][y][z].flip(dir);
                        data[width - xs - 1][y][z] = old;
                    }
                }
            }

            break;

        case WEST_EAST:
            final int len = (int) Math.ceil(length / 2.0f);
            for (int zs = 0; zs < len; ++zs) {
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        BaseBlock old = data[x][y][zs].flip(dir);
                        if (zs == length - zs - 1)
                            continue;
                        data[x][y][zs] = data[x][y][length - zs - 1].flip(dir);
                        data[x][y][length - zs - 1] = old;
                    }
                }
            }

            break;

        case UP_DOWN:
            final int hei = (int) Math.ceil(height / 2.0f);
            for (int ys = 0; ys < hei; ++ys) {
                for (int x = 0; x < width; ++x) {
                    for (int z = 0; z < length; ++z) {
                        BaseBlock old = data[x][ys][z].flip(dir);
                        if (ys == height - ys - 1)
                            continue;
                        data[x][ys][z] = data[x][height - ys - 1][z].flip(dir);
                        data[x][height - ys - 1][z] = old;
                    }
                }
            }

            break;
        }
    }

    /**
     * Places the blocks in a position from the minimum corner.
     *
     * @param editSession
     * @param pos
     * @param noAir
     */
    public void place(Location pos, boolean noAir) {
        for (int x = 0; x < size.getBlockX(); ++x) {
            for (int y = 0; y < size.getBlockY(); ++y) {
                for (int z = 0; z < size.getBlockZ(); ++z) {
                    if (noAir && data[x][y][z].isAir()) {
                        continue;
                    }

                    Vector newPos = new Vector(x + pos.getBlockX(), y + pos.getBlockY(), z + pos.getBlockZ());
                    
                    BlockUtils.setBlock(new Location(pos.getWorld(),
                            newPos.getBlockX(), newPos.getBlockY(), newPos.getBlockZ()), data[x][y][z]);
                }
            }
        }
    }
}
