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

import org.bukkit.Material;

/**
 * 
 * @author DarkLiKally
 * 
 */
public class Bo2BlockData {
    public int x = 0;
    public int y = 0;
    public int z = 0;

    public Material type = null;
    public int data = 0;

    public Bo2BlockData() {
        type = Material.AIR;
    }
}