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
package net.darklikally.lytreehelper.generators;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 *
 * @author DarkLiKally
 * @author sk89q
 * 
 */
public class TreeGenerator {
    public enum TreeType {
        TREE("Regular tree", "tree", "regular"),
        BIG_TREE("Big tree", "big", "bigtree"),
        REDWOOD("Redwood", "redwood", "sequoia", "sequoioideae"),
        TALL_REDWOOD("Tall redwood", "tallredwood", "tallsequoia", "tallsequoioideae"),
        BIRCH("Birch", "birch", "white", "whitebark"),
        RANDOM_NORMAL("Random normal", "randnormal", "randomnormal", "anynormal") {
            public boolean generate(World world, Vector pos) {
                TreeType[] choices = new TreeType[] {
                        TreeType.TREE, TreeType.BIG_TREE, TreeType.BIRCH
                };
                return choices[rand.nextInt(choices.length)].generate(world, pos);
            }
        },
        RANDOM_REDWOOD("Random redwood", "randredwood", "randomredwood", "anyredwood" ) {
            public boolean generate(World world, Vector pos) {
                TreeType[] choices = new TreeType[] {
                        TreeType.REDWOOD, TreeType.TALL_REDWOOD
                };
                return choices[rand.nextInt(choices.length)].generate(world, pos);
            }
        },
        JUNGLE("Jungle", "jungle"),
        SHORT_JUNGLE("Short jungle", "shortjungle", "smalljungle"),
        JUNGLE_BUSH("Jungle bush", "junglebush", "jungleshrub"),
        RED_MUSHROOM("Red Mushroom", "redmushroom", "redgiantmushroom"),
        BROWN_MUSHROOM("Brown Mushroom", "brownmushroom", "browngiantmushroom"),
        SWAMP("Swamp", "swamp", "swamptree"),
        RANDOM("Random", "rand", "random" ) {
            public boolean generate(World world, Vector pos) {
                TreeType[] choices = new TreeType[] {
                        TreeType.TREE, TreeType.BIG_TREE, TreeType.BIRCH,
                        TreeType.REDWOOD, TreeType.TALL_REDWOOD
                };
                return choices[rand.nextInt(choices.length)].generate(world, pos);
            }
        };

        /**
         * Stores a map of the names for fast access.
         */
        private static final Map<String, TreeType> lookup = new HashMap<String, TreeType>();

        private final String name;
        private final String[] lookupKeys;

        static {
            for (TreeType type : EnumSet.allOf(TreeType.class)) {
                for (String key : type.lookupKeys) {
                    lookup.put(key, type);
                }
            }
        }

        TreeType(String name, String... lookupKeys) {
            this.name = name;
            this.lookupKeys = lookupKeys;
        }

        public boolean generate(World world, Vector pos) {
            return TreeGenerator.generateTree(this, world, pos);
        }

        /**
         * Get user-friendly tree type name.
         *
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         * Return type from name. May return null.
         *
         * @param name
         * @return
         */
        public static TreeType lookup(String name) {
            return lookup.get(name.toLowerCase());
        }
    };
    
    /**
     * An EnumMap that stores which TreeGenerator.TreeTypes apply to which Bukkit TreeTypes
     */
    private static final EnumMap<TreeGenerator.TreeType, org.bukkit.TreeType> treeTypeMapping =
            new EnumMap<TreeGenerator.TreeType, org.bukkit.TreeType>(TreeGenerator.TreeType.class);

    static {
        // Mappings for new TreeType values not yet in Bukkit
        treeTypeMapping.put(TreeGenerator.TreeType.SWAMP, org.bukkit.TreeType.TREE);
        treeTypeMapping.put(TreeGenerator.TreeType.JUNGLE_BUSH, org.bukkit.TreeType.TREE);
        try {
            treeTypeMapping.put(TreeGenerator.TreeType.SHORT_JUNGLE, org.bukkit.TreeType.valueOf("SMALL_JUNGLE"));
        } catch (IllegalArgumentException e) {
            treeTypeMapping.put(TreeGenerator.TreeType.SHORT_JUNGLE, org.bukkit.TreeType.TREE);
        }
        for (TreeGenerator.TreeType type : TreeGenerator.TreeType.values()) {
            try {
                org.bukkit.TreeType bukkitType = org.bukkit.TreeType.valueOf(type.name());
                treeTypeMapping.put(type, bukkitType);
            } catch (IllegalArgumentException e) {
                // Unhandled TreeType
            }
        }
        // Other mappings for TreeGenerator-specific values
        treeTypeMapping.put(TreeGenerator.TreeType.RANDOM, org.bukkit.TreeType.TREE);
        treeTypeMapping.put(TreeGenerator.TreeType.RANDOM_NORMAL, org.bukkit.TreeType.TREE);
        treeTypeMapping.put(TreeGenerator.TreeType.RANDOM_REDWOOD, org.bukkit.TreeType.REDWOOD);
        for (TreeGenerator.TreeType type : TreeGenerator.TreeType.values()) {
            if (treeTypeMapping.get(type) == null) {
                // No TreeType mapping for TreeGenerator.TreeType
            }
        }
    }

    private static Random rand = new Random();
    
    private TreeType type;

    @SuppressWarnings("unused")
    private JavaPlugin plugin;

    public TreeGenerator(JavaPlugin plugin, TreeType type) {
        this.type = type;
        this.plugin = plugin;
    }

    public boolean generate(World world, Vector pos) {
        return type.generate(world, pos);
    }
    
    public static org.bukkit.TreeType toBukkitTreeType(TreeGenerator.TreeType type) {
        return treeTypeMapping.get(type);
    }

    public static boolean generateTree(TreeGenerator.TreeType type, World world, Vector pt) {
        org.bukkit.TreeType bukkitType = toBukkitTreeType(type);
        return type != null && world.generateTree(new Location(world, pt.getX(), pt.getY(), pt.getZ()), bukkitType);
    }
}
