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

import java.io.File;
import java.io.IOException;

import net.darklikally.lytreehelper.blocks.BaseBlock;
import net.darklikally.lytreehelper.schematic.bo2.Bo2BlockData;
import net.darklikally.lytreehelper.schematic.bo2.Bo2Manager;
import net.darklikally.lytreehelper.schematic.bo2.Bo2Object;

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

/**
 * 
 * @author DarkLiKally
 * 
 */
public class Bo2Schematic extends MCSchematic {

    @Override
    public CuboidObject load(File file) throws IOException, Exception {
        Bo2Object object = Bo2Manager.getObjectFromFile(file);

        if (object == null) {
            return null;
        }

        // The size of the loaded bo2 object
        Vector size = object.getSize();

        Vector offset = new Vector(
                Math.abs(object.getMinX()),
                Math.abs(object.getMinY()),
                Math.abs(object.getMinZ())
                );
        CuboidObject clipboard = new CuboidObject(size, offset);

        Bo2BlockData[] blocks = object.getData();

        for (int i = 0; i < blocks.length; i++) {
            Bo2BlockData bo2Block = blocks[i];
            BlockVector pt = new BlockVector(
                bo2Block.x + offset.getBlockX(),
                bo2Block.y + offset.getBlockY(),
                bo2Block.z + offset.getBlockZ()
                );
            BaseBlock block = getBlockForId(bo2Block.type.getId(), (short) bo2Block.data);
            
            clipboard.setBlock(pt, block);
        }

        return clipboard;
    }
}