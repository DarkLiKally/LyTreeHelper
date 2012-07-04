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
package net.darklikally.lytreehelper.utils;

import net.darklikally.lytreehelper.bukkit.LyTreeHelperPlugin;
import net.darklikally.lytreehelper.bukkit.WorldConfiguration;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 * 
 * @author DarkLiKally
 *
 */
public class CreatureSpawner {
    public static LivingEntity spawn(Location loc, EntityType type,
            WorldConfiguration wconfig, LyTreeHelperPlugin plugin) {
        
        LivingEntity livingEntity = spawnCreature(loc, type, wconfig, plugin);
        
        return livingEntity;
    }
    
    private static LivingEntity spawnCreature(Location loc, EntityType type,
            WorldConfiguration wconfig, LyTreeHelperPlugin plugin) {
        
        LivingEntity creature = loc.getWorld().spawnCreature(loc, type);
        
        creature.setHealth(creature.getMaxHealth());
        
        return creature;
    }
}
