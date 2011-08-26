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

package net.darklikally.minecraft.utils.generator;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

/**
 *
 * @author DarkLiKally
 */
public class TreeGenerator {
    public enum TreeType {
        BIG_TREE("Big tree"),
        BIRCH("Birch"),
        RANDOM("Random"),
        RANDOM_REDWOOD("Random redwood"),
        REDWOOD("Redwood"),
        TREE("Regular tree"),
        TALL_REDWOOD("Tall redwood");

        private final String name;
        
        TreeType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    };
    
    private static Random rand = new Random();
    
    private TreeType type;

    private JavaPlugin plugin;

    public TreeGenerator(JavaPlugin plugin, TreeType type) {
        this.type = type;
        this.plugin = plugin;
    }

    public boolean generator(String world, Vector pos) {
        return generator(this.type, world, pos);
    }

    private boolean generator(TreeType type, String worldName, Vector pos) {
        World world = this.plugin.getServer().getWorld(worldName);

        TreeType[] choices;
        TreeType realType;
        
        switch (type) {
            case TREE:
                return world.generateTree(
                        new BlockVector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()).toLocation(world),
                        org.bukkit.TreeType.TREE
                );
            case BIG_TREE:
                return world.generateTree(
                        new BlockVector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()).toLocation(world),
                        org.bukkit.TreeType.BIG_TREE
                );
            case BIRCH:
                return world.generateTree(
                        new BlockVector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()).toLocation(world),
                        org.bukkit.TreeType.BIRCH
                );
            case REDWOOD:
                return world.generateTree(
                        new BlockVector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()).toLocation(world),
                        org.bukkit.TreeType.REDWOOD
                );
            case TALL_REDWOOD:
                return world.generateTree(
                        new BlockVector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()).toLocation(world),
                        org.bukkit.TreeType.TALL_REDWOOD
                );
            case RANDOM_REDWOOD:
                choices = 
                    new TreeType[] {
                        TreeType.REDWOOD, TreeType.TALL_REDWOOD
                        };
                realType = choices[rand.nextInt(choices.length)];
                return generator(realType, world.getName(), pos);
            case RANDOM:
                choices = 
                    new TreeType[] {
                        TreeType.TREE, TreeType.BIG_TREE, TreeType.BIRCH,
                        TreeType.REDWOOD, TreeType.TALL_REDWOOD
                        };
                realType = choices[rand.nextInt(choices.length)];
                return generator(realType, world.getName(), pos);
        }
        
        return false;
    }
}
