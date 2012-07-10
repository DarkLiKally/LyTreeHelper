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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;

import net.darklikally.lytreehelper.bukkit.LyTreeHelperPlugin;

/**
 * 
 * @author DarkLiKally
 */
public class Bo2Manager {
    private static boolean inited = false;
    /**
     * Plugin
     */
    static LyTreeHelperPlugin plugin;

    /**
     * BOB2 Objects list BOB Editor: http://faskerstudio.com/minecraft/BBOB
     */
    private static HashMap<String, Bo2Object> objects = new HashMap<String, Bo2Object>();

    /**
     * Init method to load all bo2 files
     * 
     * @return
     */
    public static void init(LyTreeHelperPlugin plugin, String path) {
        Bo2Manager.plugin = plugin;

        if (!Bo2Manager.inited) {
            try {
                Bo2Manager.ReadBo2Files(path);

                Bo2Manager.inited = true;
            } catch (FileNotFoundException e) {
                // e.printStackTrace();
                plugin.getLogger().log(Level.INFO,
                        "[LyTreeHelper] Could not find any BOB2 files.");
            }
        }
    }

    /**
     * Read a Bo2 File
     * 
     * @param file
     * @throws FileNotFoundException
     */
    private static Bo2Object ReadBo2File(File file)
            throws FileNotFoundException {
        // Check whether we have a bo2 file
        if (!file.getName().contains(".bo2")) {
            return null;
        }

        Scanner scanner = new Scanner(file);
        Bo2Object object = new Bo2Object();
        ArrayList<String> data = new ArrayList<String>();

        boolean fromMeta = false;
        boolean fromData = false;

        while (scanner.hasNext()) {
            String line = scanner.nextLine();

            // Check if we're in the meta data category
            if (line.equalsIgnoreCase("[META]")) {
                // If we come from the data category, first, clear the data
                // array, because we need an empty array to collect our meta
                // data
                if (fromData) {
                    // But we should scan our block data first
                    object.ParseBlockdata((String[]) data
                            .toArray(new String[1]));

                    data.clear();
                    fromData = false;
                }
                fromMeta = true;

                // Add the bo2 file name to the data
                data.add("name=" + file.getName());

                continue;
            }

            // Check if we're in the block data category
            if (line.equalsIgnoreCase("[DATA]")) {
                // If we come from the meta category, clear the data array,
                // because we need an empty array to collect our block data
                if (fromMeta) {
                    // But we should scan our meta data first
                    object.ParseMetadata((String[]) data.toArray(new String[1]));

                    data.clear();
                    fromMeta = false;
                }
                fromData = true;

                continue;
            }

            // If we have a data line ...
            // If we're in the meta data block, we're catching
            data.add(line);
        }

        // Finally we're done with file scanning, now parse the last data set
        // and return

        if (fromMeta) {
            object.ParseMetadata((String[]) data.toArray(new String[1]));
        }
        if (fromData) {
            object.ParseBlockdata((String[]) data.toArray(new String[1]));
        }

        return object;
    }

    /**
     * Reads the Bo2 Files in the specified path.
     * 
     * @param path
     * @throws FileNotFoundException
     */
    private static void ReadBo2Files(String path) throws FileNotFoundException {
        File dir = new File(path);
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {

            Bo2Object object = ReadBo2File(files[i]);

            if(object != null) {
                objects.put(files[i].getName(), object);
            }
        }
    }
    
    public static Bo2Object getObjectFromFile(File file) {
        if(objects.get(file.getName()) == null) {
            try {
                Bo2Object object = ReadBo2File(file);
                
                if(object != null) {
                    objects.put(file.getName(), object);
                }
            } catch (FileNotFoundException e) {
                return null;
            }
        }
        
        return objects.get(file.getName()); 
    }

    public static ArrayList<Bo2Object> getTrees() {
        ArrayList<Bo2Object> retObjects = new ArrayList<Bo2Object>();

        for (int i = 0; i < objects.size(); i++) {
            if (!((Bo2Object) objects.get(i)).isTree())
                continue;
            retObjects.add((Bo2Object) objects.get(i));
        }

        return retObjects;
    }

    public static ArrayList<Bo2Object> getNotTrees() {
        ArrayList<Bo2Object> retObjects = new ArrayList<Bo2Object>();

        for (int i = 0; i < objects.size(); i++) {
            if (((Bo2Object) objects.get(i)).isTree())
                continue;
            retObjects.add((Bo2Object) objects.get(i));
        }

        return retObjects;
    }
}