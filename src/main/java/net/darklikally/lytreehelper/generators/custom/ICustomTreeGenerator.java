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
package net.darklikally.lytreehelper.generators.custom;

import java.util.ArrayList;

import net.darklikally.lytreehelper.blocks.BaseBlock;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * 
 * @author DarkLikally
 *
 */
public interface ICustomTreeGenerator {
    
    /**
     * Returns the name of the custom tree generator
     * 
     * @return
     */
    public String getName();
    
    /**
     * Returns the version of the custom tree generator
     * @return
     */
    public String getVersion();
    
    /**
     * Returns the description of the custom tree generator
     * @return
     */
    public String getDescription();
    
    /**
     * Generate the tree
     * 
     * @param loc
     * @param world
     * @param args
     * @return
     */
    public boolean generate(Location loc, World world, String[] args);
    
    /**
     * Returns all blocks which are affected by the custom tree generator.
     * Note: The block's data should be that before the change!
     * @return
     */
    public ArrayList<BaseBlock> getChangedBlocks();
}
