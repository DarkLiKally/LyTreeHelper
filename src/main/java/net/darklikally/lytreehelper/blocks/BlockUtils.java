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
package net.darklikally.lytreehelper.blocks;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * 
 * @author DarkLiKally
 *
 */
public class BlockUtils {
    /**
     * Sets a block.
     *
     * @param loc
     * @param block
     * @return Whether the block changed
     */
    public static boolean setBlock(Location loc, BaseBlock block) {
        World world = loc.getWorld();
        
        final int y = loc.getBlockY();
        final int type = block.getType();
        if (y < 0 || y > world.getMaxHeight()) {
            return false;
        }
        
        world.getBlockAt(loc).setData((byte) block.getData());
        
        return world.getBlockAt(loc).setTypeId(type);
    }
}
