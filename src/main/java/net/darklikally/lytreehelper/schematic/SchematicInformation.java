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

import java.util.Set;

import org.bukkit.block.Biome;
import org.bukkit.util.Vector;

/**
 * 
 * @author DarkLiKally
 *
 */
public class SchematicInformation {
    public String name;
    public Set<Biome> biomes;
    public boolean allBiomes;
    public double chance;
    public String type;
    public boolean forceSpawn;
    public Vector offset;
    public Set<Integer> spawnOnBlockTypes;
    public boolean allBlockTypes;
    public String file;
}
